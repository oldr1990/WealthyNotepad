package com.example.wealthynotepad.util


import java.text.SimpleDateFormat

fun String.millisToDate(formatPattern: String):String{
      return SimpleDateFormat(formatPattern).format(this.toLong())
}