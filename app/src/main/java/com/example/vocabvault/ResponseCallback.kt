package com.example.vocabvault

interface ResponseCallback {
    fun onSuccess(result: ResultOutput)
    fun onFailure(error: String)
}