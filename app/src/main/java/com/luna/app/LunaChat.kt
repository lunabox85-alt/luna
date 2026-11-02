package com.luna.app

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class LunaChat(context: Context, vm: ChatViewModel) : LinearLayout(context) {

    init {
        orientation = VERTICAL
        setPadding(32, 32, 32, 32)

        val log = TextView(context).apply {
            textSize = 16f
            setPadding(0, 0, 0, 24)
        }

        val scroll = ScrollView(context).apply {
            addView(log)
        }

        val input = EditText(context).apply {
            hint = "Спроси что-нибудь..."
        }

        val send = Button(context).apply {
            text = "Отправить"
        }

        addView(scroll, LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f))
        addView(input, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        addView(send, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))

        vm.messages.observeForever { list ->
            log.text = list.joinToString("\n\n") { msg ->
                if (msg.isUser) "Ты: ${msg.text}" else "Луна: ${msg.text}"
            }
            scroll.post { scroll.fullScroll(View.FOCUS_DOWN) }
        }

        send.setOnClickListener {
            val text = input.text.toString().trim()
            if (text.isNotEmpty()) {
                input.setText("")
                vm.send(text)
            }
        }
    }
}
