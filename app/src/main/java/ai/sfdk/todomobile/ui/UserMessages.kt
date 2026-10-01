package ai.sfdk.todomobile.ui

import retrofit2.HttpException
import java.io.IOException

fun Throwable.toUserMessage(): String = when (this) {
    is HttpException -> "The server answered with an error (${code()})."
    is IOException -> "Could not reach the server."
    else -> message ?: "Something went wrong."
}
