package com.lorbke.ft_hangouts.data

// Phone numbers get written differently in different places - "+49 151
// 00000001" vs "015100000001" vs "4915100000001". A plain string comparison
// would almost never match, so instead we strip everything down to just the
// digits and compare from the end (country codes are the part most likely
// to be missing on one side).
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
