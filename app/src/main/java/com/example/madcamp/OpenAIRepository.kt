package com.example.madcamp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.file.FileUpload
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageEdit
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.image.ImageVariation
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.Audio
import com.aallam.openai.client.OpenAI
import com.example.madcamp.Conversation
import com.example.madcamp.Message
import com.example.madcamp.MessageStatus
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.Source
import java.io.File

@OptIn(BetaOpenAI::class)
class OpenAIRepository(private val openAI: OpenAI) {

    @Throws(NoChoiceAvailableException::class)
    suspend fun sendChatRequest(
        conversation: Conversation
    ) : Message {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = conversation.toChatMessages()
        )

        val chatMessage = openAI.chatCompletion(chatCompletionRequest).choices.first().message
            ?: throw NoChoiceAvailableException()

        return Message(
            text = chatMessage.content,
            isFromUser = chatMessage.role == ChatRole.User,
            messageStatus = MessageStatus.Sent
        )
    }
    suspend fun sendVoiceRequest(fileName: String): String {
        val file = File("/data/data/com.example.madcamp/files")
        if(!file.exists()) {
            file.mkdirs()
        }
        val filePath = "/data/data/com.example.madcamp/files/"
        val path = (filePath + fileName).toPath()
        val audioSource = FileSystem.SYSTEM.source(path)
        val request = TranscriptionRequest(
            model = ModelId("whisper-1"),
            audio = FileSource(name = fileName, source = audioSource),
        )
        val transcription = openAI.transcription(request)
        Log.d("RESULT","${transcription.text}")
        return transcription.text
    }
    suspend fun sendImageRequest(prompt : String): String {
        val images = openAI.imageURL( // or openAI.imageJSON
            creation = ImageCreation(
                prompt = prompt,
                n = 1,
                size = ImageSize.is1024x1024
            )
        )
        Log.d("RESULT","${images.first().url}")
        return images.first().url
    }

    suspend fun sendImageVarRequest(prompt : String): String {
        val path = ("/data/data/com.example.madcamp/files/test.png").toPath()
        val imageSource = FileSystem.SYSTEM.source(path)
        val images = openAI.imageURL( // or openAI.imageJSON
            variation = ImageVariation(
                image = FileSource(name = "test.png", source = imageSource),
                n = 1,
                size = ImageSize.is1024x1024
            )
        )
        Log.d("RESULT","${images.first().url}")
        return images.first().url
    }

    private fun Conversation.toChatMessages() = this.list
        .filterNot { it.messageStatus == MessageStatus.Error }
        .map {
            ChatMessage(
                content = it.text,
                role = if (it.isFromUser) { ChatRole.User } else { ChatRole.Assistant }
            )
        }
}

class NoChoiceAvailableException: Exception()