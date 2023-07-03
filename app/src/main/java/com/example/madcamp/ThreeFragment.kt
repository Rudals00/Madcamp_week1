package com.example.madcamp

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aallam.openai.api.http.Timeout
import com.example.madcamp.databinding.FragmentThreeBinding
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.example.madcamp.OpenAIRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.Date
import kotlin.time.Duration.Companion.seconds


class ChatAdapter(private val chatMessages: MutableList<Message>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
        holder.messageText.text = chatMessages[position].text
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

        binding.startButton.setOnClickListener {
            checkPermission()
        }
        binding.stopButton.setOnClickListener {
            stopRecording()
        }

        sendButton.setOnClickListener {
            val userInput = inputChat.text.toString().trim()
            if (userInput.isNotEmpty()) {
                // Add user message to the chat
                inputChat.text.clear()
                binding.sendButton.isEnabled=false
                chatMessages.add(Message("dd",userInput, true,MessageStatus.Sent))
                chatMessages.add(Message("dd","AI is sending.", false,MessageStatus.Sending))
                chatAdapter.notifyDataSetChanged()
                binding.chatRecyclerview.scrollToPosition(chatMessages.size - 1)

                // art a new coroutine for asynchronous work
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val file = File("/data/data/com.example.madcamp/files")
                        if(!file.exists()) {
                            file.mkdirs()
                        }
                        val test = openAIRepository.sendVoiceRequest("/data/data/com.example.madcamp/files/","test.m4a")
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
                            binding.sendButton.isEnabled=true
                        }
                    } catch (e: Exception) {
                        // Handle the exception (e.g., show an error message)
                        withContext(Dispatchers.Main) {
                            chatMessages[chatMessages.size - 1].text = "ERROR"
                            chatMessages[chatMessages.size - 1].messageStatus = MessageStatus.Error
                            chatAdapter.notifyDataSetChanged()
                            binding.chatRecyclerview.scrollToPosition(chatMessages.size - 1)
                            binding.sendButton.isEnabled=true
                        }
                    }
                }
            }
        }
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