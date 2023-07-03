package com.example.madcamp

import com.example.madcamp.ConversationRepository
import com.example.madcamp.Message
import com.example.madcamp.MessageStatus
import com.example.madcamp.OpenAIRepository
import kotlinx.coroutines.delay

class SendChatRequestUseCase(
    private val openAIRepository: OpenAIRepository,
    private val conversationRepository: ConversationRepository
) {

    suspend operator fun invoke(
        prompt: String
    ) {
        val message = Message(
            text = prompt,
            isFromUser = true,
            messageStatus = MessageStatus.Sending
        )
        val conversation = conversationRepository.addMessage(message)

        try {
            val reply = openAIRepository.sendChatRequest(conversation)
            conversationRepository.setMessageStatusToSent(message.id)
            conversationRepository.addMessage(reply)
        } catch (exception: Exception) {
            conversationRepository.setMessageStatusToError(message.id)
        }
    }
}