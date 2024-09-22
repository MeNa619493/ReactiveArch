package com.example.reactivearch.domain.error

interface ErrorHandler {
    fun getError(throwable: Throwable) : Failure
}