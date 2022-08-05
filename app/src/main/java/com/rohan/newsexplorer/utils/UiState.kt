package com.rohan.newsexplorer.utils

import java.lang.Exception


sealed class UiState<out R> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
//    data class Error : UiState<Exception>(val exception:Exception):String()
    data class Error<T>(val data: String) : UiState<T>()

}
