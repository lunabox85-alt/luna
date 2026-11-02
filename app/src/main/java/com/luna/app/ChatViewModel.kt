package com.luna.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class ChatViewModel : ViewModel() {

    data class Message(val text: String, val isUser: Boolean)

    private val _messages = MutableLiveData<MutableList<Message>>(mutableListOf())
    val messages: LiveData<MutableList<Message>> = _messages

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun send(text: String) {
        addMessage(text, true)
        scope.launch {
            val reply = try {
                LocalAI.answer(text)
            } catch (e: Exception) {
                "Ой, что-то сломалось у меня внутри: ${e.message}"
            }
            addMessage(reply, false)
        }
    }

    private fun addMessage(text: String, fromUser: Boolean) {
        val list = _messages.value ?: mutableListOf()
        list.add(Message(text, fromUser))
        _messages.postValue(list)
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}
