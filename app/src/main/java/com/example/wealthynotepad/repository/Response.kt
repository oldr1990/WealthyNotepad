package com.example.wealthynotepad.repository

sealed class Response<T>(val message: String?, val data: T?) {
    class Success<T>(data: T) : Response<T>(null, data)
    class Failure<T>(message: String) : Response<T>(message, null)
}