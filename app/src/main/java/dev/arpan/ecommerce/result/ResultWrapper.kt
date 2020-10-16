/*
 * Copyright 2020 Arpan Sarkar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.arpan.ecommerce.result

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class GenericError(
        val code: Int? = null,
        val error: Any? = null
    ) : ResultWrapper<Nothing>()

    object NetworkError : ResultWrapper<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$value]"
            is GenericError -> "GenericError[code=$code, error=$error]"
            NetworkError -> "NetworkError"
        }
    }
}

/*
suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T
): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            ResultWrapper.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> ResultWrapper.NetworkError
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse =
                        convertErrorBody(throwable)
                    ResultWrapper.GenericError(
                        code,
                        errorResponse
                    )
                }
                else -> {
                    ResultWrapper.GenericError(
                        null,
                        null
                    )
                }
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
    return try {
        throwable.response()
            ?.errorBody()
            ?.source()
            ?.let {
                val moshiAdapter = Moshi.Builder()
                    .build()
                    .adapter(ErrorResponse::class.java)
                moshiAdapter.fromJson(it)
            }
    } catch (exception: Exception) {
        null
    }
}
*/
