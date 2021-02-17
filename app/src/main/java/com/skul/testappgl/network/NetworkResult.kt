package com.skul.testappgl.network

sealed class NetworkResult<out T>

object Loading: NetworkResult<Nothing>()

data class SuccessfulResult<out T>(val data: T): NetworkResult<T>()

data class NetworkError(val exception: Throwable): NetworkResult<Nothing>()