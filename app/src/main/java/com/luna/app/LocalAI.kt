package com.luna.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object LocalAI {

    suspend fun answer(text: String): String = withContext(Dispatchers.Default) {
        val t = text.lowercase().trim()

        when {
            t.contains("привет") || t.contains("здравств") || t == "hi" ->
                "Привет! Я Луна. Спроси меня о чём-нибудь 🙂"
            t.contains("как дела") || t.contains("как ты") ->
                "Работаю как часы — только что проснулась. А у тебя как?"
            t.contains("кто ты") || t.contains("как тебя зовут") ->
                "Меня зовут Луна, я твой локальный помощник."
            t.contains("спасибо") ->
                "Всегда пожалуйста!"
            t.contains("что умеешь") || t.contains("помощь") ->
                "Пока я молодая: умею здороваться, считать и болтать. Скоро научусь большему!"
            isMath(t) -> solveMath(t)
            else -> listOf(
                    "Интересная мысль! Расскажи подробнее.",
                    "Хм, об этом стоит подумать.",
                    "Я запомнила. Что-нибудь ещё?"
                ).random()
        }
    }

    private fun isMath(t: String): Boolean {
        return t.any { it.isDigit() } && listOf('+', '-', '*', '/').any { it in t }
    }

    private fun solveMath(t: String): String {
        return try {
            val expr = t.replace("х", "*").replace("x", "*").replace(",", ".")
                .filter { it.isDigit() || it in "+-*/.() " }
            val result = evalSimple(expr)
            if (result.isFinite()) "Получается $result" else "Хм, тут деление на ноль — так у меня не выйдет."
        } catch (e: Exception) {
            "Не смогла посчитать. Напиши проще, например: 2+2"
        }
    }

    private fun evalSimple(expr: String): Double {
        val s = expr.replace(" ", "")
        var i = 0
        fun number(): Double {
            var start = i
            while (i < s.length && (s[i].isDigit() || s[i] == '.')) i++
            return s.substring(start, i).toDouble()
        }
        fun term(): Double {
            var v = if (s[i] == '(') { i++; val r = expr(); if (i < s.length) i++; r } else number()
            while (i < s.length && (s[i] == '*' || s[i] == '/')) {
                val op = s[i]; i++
                val r = if (s[i] == '(') { i++; val x = expr(); if (i < s.length) i++; x } else number()
                v = if (op == '*') v * r else v / r
            }
            return v
        }
        fun expr(): Double {
            var v = term()
            while (i < s.length && (s[i] == '+' || s[i] == '-')) {
                val op = s[i]; i++
                val r = term()
                v = if (op == '+') v + r else v - r
            }
            return v
        }
        return expr()
    }
}
