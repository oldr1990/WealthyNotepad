package com.example.walthynotepad.util


import java.text.SimpleDateFormat

fun String.millisToDate(formatPattern: String):String{
      return SimpleDateFormat(formatPattern).format(this.toLong())
}