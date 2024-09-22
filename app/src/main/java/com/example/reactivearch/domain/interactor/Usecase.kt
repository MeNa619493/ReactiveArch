package com.example.reactivearch.domain.interactor

interface Usecase<T,R> {
    fun execute(param : T) : R
}