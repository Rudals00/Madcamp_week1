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

data class ChatMessage(val content: String, val isUser: Boolean)

class ChatAdapter(private val chatMessages: MutableList<ChatMessage>) :
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
        return if (chatMessages[position].isUser) 0 else 1
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.messageText.text = chatMessages[position].content
    }
}
class ThreeFragment : Fragment() {
    private var _binding: FragmentThreeBinding? = null
    private val binding get() = _binding!!

    private val chatMessages: MutableList<ChatMessage> = mutableListOf()
    private lateinit var chatAdapter: ChatAdapter

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

        sendButton.setOnClickListener {
            val userInput = inputChat.text.toString().trim()
            if (userInput.isNotEmpty()) {
                // Add user message to the chat
                chatMessages.add(ChatMessage(userInput, true))
                // Get GPT-4 response (fake response for now)
                val aiResponse = "AI Response to '$userInput'"
                // Add AI response to the chat
                chatMessages.add(ChatMessage(aiResponse, false))
                // Notify adapter about changes
                chatAdapter.notifyDataSetChanged()
                // Scroll to the bottom
                binding.chatRecyclerview.scrollToPosition(chatMessages.size - 1)
                // Clear the input field
                inputChat.text.clear()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }}
