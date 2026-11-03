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
        val s = expr.replace(" ", "").replace(",", ".").replace("×", "*").replace("÷", "/")
        val nums = mutableListOf<Double>()
        val ops = mutableListOf<Char>()
        var i = 0
        var neg = false
        while (i < s.length) {
            val c = s[i]
            if (c.isDigit() || c == '.') {
                var j = i
                while (j < s.length && (s[j].isDigit() || s[j] == '.')) j++
                val v = s.substring(i, j).toDouble()
                nums.add(if (neg) -v else v)
                neg = false
                i = j
            } else if (c == '(') {
                var depth = 1
                var j = i + 1
                while (j < s.length && depth > 0) {
                    if (s[j] == '(') depth++
                    if (s[j] == ')') depth--
                    j++
                }
                val v = evalSimple(s.substring(i + 1, j - 1))
                nums.add(if (neg) -v else v)
                neg = false
                i = j
            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                if (c == '-' && nums.size == ops.size) {
                    neg = !neg
                } else {
                    ops.add(c)
                }
                i++
            } else {
                i++
            }
        }
        if (nums.isEmpty()) throw Exception("пусто")
        var k = 0
        while (k < ops.size) {
            val op = ops[k]
            if (op == '*' || op == '/') {
                val a = nums.removeAt(k)
                val b = nums.removeAt(k)
                nums.add(k, if (op == '*') a * b else a / b)
                ops.removeAt(k)
            } else {
                k++
            }
        }
        var result = nums.removeAt(0)
        for (op in ops) {
            val b = nums.removeAt(0)
            result = if (op == '+') result + b else result - b
        }
        return result
    }
}

