package com.example.walthynotepad.util

import java.util.regex.Pattern

fun isItEmail(toCheck: String): Boolean {
    val emailCheckPattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
    return emailCheckPattern.matcher(toCheck).matches()
}
