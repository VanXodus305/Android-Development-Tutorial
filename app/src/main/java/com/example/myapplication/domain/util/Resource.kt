package com.example.myapplication.domain.util

sealed interface Resource<out T> {
	data class Success<out T>(val data: T) : Resource<T>
	data class Error(val message: String) : Resource<Nothing>
}