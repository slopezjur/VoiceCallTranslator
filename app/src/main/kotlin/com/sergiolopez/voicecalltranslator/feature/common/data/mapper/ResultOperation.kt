package com.sergiolopez.voicecalltranslator.feature.common.data.mapper

object ResultOperation {

    suspend fun <R> performSimpleApiOperation(apiOperation: suspend () -> R): Boolean {
        return runCatching {
            apiOperation()
        }.fold(
            onSuccess = {
                true
            },
            onFailure = {
                false
                // TODO : Map Firebase exceptions, FirebaseAuthWeakPasswordException, 1FirebaseAuthInvalidCredentialsException, etc
            }
        )
    }

    /*fun <T> Result<T>.foldResult(): Boolean {
        return fold(
            onSuccess = {
                true
            },
            onFailure = {
                false
            }
        )
    }*/
}