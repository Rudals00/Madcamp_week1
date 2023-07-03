package com.example.madcamp

import com.example.madcamp.ConversationRepository

class ObserveMessagesUseCase(
    private val conversationRepository: ConversationRepository
) {

    operator fun invoke() = conversationRepository.conversationFlow

}