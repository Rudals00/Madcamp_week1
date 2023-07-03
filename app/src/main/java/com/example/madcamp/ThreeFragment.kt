package com.example.madcamp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp.databinding.FragmentThreeBinding
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.example.madcamp.OpenAIRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputChat: EditText = binding.inputChat
        val sendButton: Button = binding.sendButton

        // Initialize chat adapter
        chatAdapter = ChatAdapter(chatMessages)
        binding.chatRecyclerview.layoutManager = LinearLayoutManager(context)
        binding.chatRecyclerview.adapter = chatAdapter
        val config = OpenAIConfig(
            token = "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"
        )

        val openAI = OpenAI(config)
        openAIRepository = OpenAIRepository(openAI)
        sendButton.setOnClickListener {
            val userInput = inputChat.text.toString().trim()
            if (userInput.isNotEmpty()) {
                // Add user message to the chat
                chatMessages.add(Message("dd",userInput, true,MessageStatus.Sent))
                chatAdapter.notifyDataSetChanged()
                inputChat.text.clear()
                    //버튼 비활성화, ai sending 중 처리
                // art a new coroutine for asynchronous work
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Create a new conversation object
                        val conversation = Conversation(listOf(Message(text = userInput, isFromUser = true)))

                        // Send chat request and get AI response
                        val aiResponse = openAIRepository.sendChatRequest(conversation)

                        // Add AI response to the chat
                        withContext(Dispatchers.Main) {
                            chatMessages.add(Message("ai",aiResponse.text, false,MessageStatus.Sent))
                            // Notify adapter about changes
                            chatAdapter.notifyDataSetChanged()
                            //adapter에서 seding일 경우에 ai sending 띄워놓기
                            // Scroll to the bottom
                            binding.chatRecyclerview.scrollToPosition(chatMessages.size - 1)
                            // Clear the input field
                        }
                    } catch (e: NoChoiceAvailableException) {
                            //에러처리              // Handle the exception (e.g., show an error message)
                        withContext(Dispatchers.Main) {
                            // TODO: Handle the exception
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