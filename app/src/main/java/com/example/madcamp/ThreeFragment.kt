package com.example.madcamp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aallam.openai.api.http.Timeout
import com.example.madcamp.databinding.FragmentThreeBinding
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.CustomViewTarget
import com.example.madcamp.OpenAIRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.Date
import kotlin.time.Duration.Companion.seconds
import com.bumptech.glide.request.transition.Transition


class ChatAdapter(private val chatMessages: MutableList<Message>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView =  itemView.findViewById(R.id.chat_image_view)
        val messageText: TextView = itemView.findViewById(R.id.chat_message_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layout = if (viewType == 0) R.layout.chat_item_user else R.layout.chat_item_ai
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun getItemCount(): Int = chatMessages.size

    override fun getItemViewType(position: Int): Int {
        return if (chatMessages[position].isFromUser) 0 else 1
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = chatMessages[position]

        // Load the image if there is a URL
        if (!message.imageUrl.isNullOrEmpty()) {
            holder.messageText.visibility=View.GONE
            holder.imageView.visibility = View.VISIBLE
            Glide.with(holder.itemView)
                .asBitmap()  // Request image as Bitmap
                .load(message.imageUrl)
                .into(object : CustomTarget<Bitmap>(200, 200) {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val bitmapDrawable = BitmapDrawable(holder.itemView.resources, resource)
                        holder.imageView.setImageDrawable(bitmapDrawable)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        holder.imageView.setImageDrawable(placeholder)
                    }
                })
        } else {
            holder.messageText.text = message.text
            holder.messageText.visibility=View.VISIBLE
            holder.imageView.visibility = View.GONE
        }
    }
}
class ThreeFragment : Fragment() {
    private var _binding: FragmentThreeBinding? = null
    private val binding get() = _binding!!

    private val chatMessages: MutableList<Message> = mutableListOf()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var openAIRepository: OpenAIRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThreeBinding.inflate(inflater, container, false)
//        var permission = Manifest.permission.READ_EXTERNAL_STORAGE
//        var result = ContextCompat.checkSelfPermission(requireContext(), permission)
//        Log.d("chan","MANAGE_PERMISSION : ${result != PackageManager.PERMISSION_GRANTED}")
//        if (result != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), 1)
//        }
        return binding.root
    }

    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false

    private fun startRecording(){
        //config and create MediaRecorder Object
//        val fileName: String = Date().getTime().toString() + ".mp3"
        val fileName = "test.m4a"
        output = "/data/data/com.example.madcamp/files/" + fileName //내장메모리 밑에 위치
        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource((MediaRecorder.AudioSource.MIC))
        mediaRecorder?.setOutputFormat((MediaRecorder.OutputFormat.MPEG_4))
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setOutputFile(output)

        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            state = true
            Toast.makeText(requireContext(), "레코딩 시작되었습니다.", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalStateException){
            e.printStackTrace()
        } catch (e: IOException){
            e.printStackTrace()
        }
    }

    private fun stopRecording(){
        if(state){
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            mediaRecorder?.release()
            state = false
            Toast.makeText(requireContext(), "중지 되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "레코딩 상태가 아닙니다.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun checkPermission() {
        val permission = Manifest.permission.RECORD_AUDIO
        val result = ContextCompat.checkSelfPermission(requireContext(), permission)
        if (result == PackageManager.PERMISSION_GRANTED) {
            startRecording()
        } else {
            requestPermission()
        }
    }
    private fun requestPermission() {
        val permission = Manifest.permission.RECORD_AUDIO
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), 1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputChat: EditText = binding.inputChat
        val sendButton: Button = binding.sendButton
        val imageButton: Button = binding.imageButton

        val config = OpenAIConfig(
            token = "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" ,
            timeout = Timeout(socket = 20.seconds)
        )
        val openAI = OpenAI(config)
        openAIRepository = OpenAIRepository(openAI)

        chatMessages.add(Message("ddd","Hi, how can I help?",false,MessageStatus.Sent))

        chatAdapter = ChatAdapter(chatMessages)
        binding.chatRecyclerview.layoutManager = LinearLayoutManager(context)
        binding.chatRecyclerview.adapter = chatAdapter

        imageButton.setOnClickListener{
            val userInput = inputChat.text.toString().trim()
            if(userInput.isNotEmpty()){
                inputChat.text.clear()
                button_off(binding)
                chatMessages.add(Message("dd",userInput, true,MessageStatus.Sent))
                chatMessages.add(Message("dd","AI is sending.", false,MessageStatus.Sending))
                chatAdapter.notifyDataSetChanged()
                binding.chatRecyclerview.scrollToPosition(chatMessages.size - 1)
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val imageurl = openAIRepository.sendImageRequest(userInput)
                        val message_for_image = Message("ai","\n",false,imageUrl=imageurl)

                        // Add AI response to the chat
                        withContext(Dispatchers.Main) {
                            chatMessages[chatMessages.size-1].text = message_for_image.text
                            chatMessages[chatMessages.size - 1].messageStatus = MessageStatus.Sent
                            chatMessages[chatMessages.size - 1].imageUrl = message_for_image.imageUrl
                            chatAdapter.notifyDataSetChanged()
                            binding.chatRecyclerview.scrollToPosition(chatMessages.size - 1)
                            button_on(binding)
                        }
                    } catch (e: Exception) {
                        // Handle the exception (e.g., show an error message)
                        withContext(Dispatchers.Main) {
                            chatMessages[chatMessages.size - 1].text = "ERROR"
                            chatMessages[chatMessages.size - 1].messageStatus = MessageStatus.Error
                            chatAdapter.notifyDataSetChanged()
                            binding.chatRecyclerview.scrollToPosition(chatMessages.size - 1)
                            button_on(binding)
                        }
                    }
                }
            }
        }
        binding.voiceButton.setOnClickListener {
            if (binding.voiceButton.text == "0") {
                checkPermission()
                binding.voiceButton.text = "1"
                binding.voiceButton.foreground = ContextCompat.getDrawable(requireContext(), R.drawable.stop_button)
            } else {
                stopRecording()
                binding.voiceButton.text = "0"
                binding.voiceButton.foreground = ContextCompat.getDrawable(requireContext(), R.drawable.mic_button)
                button_off(binding)
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val userInput = openAIRepository.sendVoiceRequest("test.m4a")
                        chatMessages.add(Message("dd",userInput, true,MessageStatus.Sent))
                        chatMessages.add(Message("dd","AI is sending.", false,MessageStatus.Sending))
                        withContext(Dispatchers.Main) {
                            chatAdapter.notifyDataSetChanged()
                            binding.chatRecyclerview.scrollToPosition(chatMessages.size - 1)
                        }
                        val conversation = Conversation(listOf(Message(text = userInput, isFromUser = true)))

                        // Send chat request and get AI response
                        val aiResponse = openAIRepository.sendChatRequest(conversation)

                        // Add AI response to the chat
                        withContext(Dispatchers.Main) {
                            chatMessages[chatMessages.size - 1].text = aiResponse.text
                            chatMessages[chatMessages.size - 1].messageStatus = MessageStatus.Sent
                            chatAdapter.notifyDataSetChanged()
                            binding.chatRecyclerview.scrollToPosition(chatMessages.size - 1)
                            button_on(binding)
                        }
                    } catch (e: Exception) {
                        // Handle the exception (e.g., show an error message)
                        withContext(Dispatchers.Main) {
                            chatMessages[chatMessages.size - 1].text = "ERROR"
                            chatMessages[chatMessages.size - 1].messageStatus = MessageStatus.Error
                            chatAdapter.notifyDataSetChanged()
                            binding.chatRecyclerview.scrollToPosition(chatMessages.size - 1)
                            button_on(binding)
                        }
                    }
                }
            }
        }
        sendButton.setOnClickListener {
            val userInput = inputChat.text.toString().trim()
            if (userInput.isNotEmpty()) {
                // Add user message to the chat
                inputChat.text.clear()
                button_off(binding)
                chatMessages.add(Message("dd",userInput, true,MessageStatus.Sent))
                chatMessages.add(Message("dd","AI is sending.", false,MessageStatus.Sending))
                chatAdapter.notifyDataSetChanged()
                binding.chatRecyclerview.scrollToPosition(chatMessages.size - 1)

                // art a new coroutine for asynchronous work
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Create a new conversation object
                        val conversation = Conversation(listOf(Message(text = userInput, isFromUser = true)))

                        // Send chat request and get AI response
                        val aiResponse = openAIRepository.sendChatRequest(conversation)

                        // Add AI response to the chat
                        withContext(Dispatchers.Main) {
                            chatMessages[chatMessages.size - 1].text = aiResponse.text
                            chatMessages[chatMessages.size - 1].messageStatus = MessageStatus.Sent
                            chatAdapter.notifyDataSetChanged()
                            binding.chatRecyclerview.scrollToPosition(chatMessages.size - 1)
                            button_on(binding)
                        }
                    } catch (e: Exception) {
                        // Handle the exception (e.g., show an error message)
                        withContext(Dispatchers.Main) {
                            chatMessages[chatMessages.size - 1].text = "ERROR"
                            chatMessages[chatMessages.size - 1].messageStatus = MessageStatus.Error
                            chatAdapter.notifyDataSetChanged()
                            binding.chatRecyclerview.scrollToPosition(chatMessages.size - 1)
                            button_on(binding)
                        }
                    }
                }
            }
        }
    }

    private fun button_off(binding: FragmentThreeBinding) {
        binding.sendButton.isEnabled=false
        binding.imageButton.isEnabled=false
        binding.voiceButton.isEnabled=false
    }
    private fun button_on(binding: FragmentThreeBinding) {
        binding.sendButton.isEnabled=true
        binding.imageButton.isEnabled=true
        binding.voiceButton.isEnabled=true
    }

    private fun List<Message>.toMessages(): List<Message> {
        return this.map {
            Message(
                text = it.text,
                isFromUser = it.isFromUser,
                messageStatus = MessageStatus.Sent
            )
        }
    }}