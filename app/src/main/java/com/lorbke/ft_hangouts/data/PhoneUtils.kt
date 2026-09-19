package com.lorbke.ft_hangouts.data

object PhoneUtils {

    fun normalize(number: String): String {
        return number.filter { it.isDigit() }
    }

    fun matches(a: String, b: String): Boolean {
        val digitsA = normalize(a)
        val digitsB = normalize(b)
        if (digitsA.isEmpty() || digitsB.isEmpty()) {
            return false
        }
        return digitsA.endsWith(digitsB) || digitsB.endsWith(digitsA)
    }
}
