package com.example.myapplication.data.repository

import com.example.myapplication.data.api.UserApi
import com.example.myapplication.data.models.LoginRequest
import com.example.myapplication.data.models.SignUpRequest

import com.google.gson.Gson
import retrofit2.HttpException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AuthRepository(private val userApi: UserApi) {

    suspend fun login(email: String, password: String): String = withContext(Dispatchers.IO){

        val response = userApi.login(LoginRequest(email, password))
        return@withContext if (response.isSuccessful) {
            val userResponse = response.body()
            if (userResponse != null && userResponse.user != null) {
                userResponse.user.id
            } else {
                throw Exception("Invalid login response: User is null")
            }
        } else {
            val errorBody = response.errorBody()?.string()
            val errorJson = try {
                Gson().fromJson(errorBody, ErrorResponse::class.java)
            } catch (e: Exception) {
                null
            }
            throw HttpException(response)
        }
    }

    suspend fun signUp(name: String, email: String, password: String): String = withContext(Dispatchers.IO){
        val response = userApi.signUp(SignUpRequest(name, email, password))
        return@withContext if (response.isSuccessful) {


            val userId = response.body()?.userId?.toString()


            if (userId != null) {
                userId
            } else {
                throw Exception("Invalid sign-up response: userId is null")
            }

        } else {
            val errorBody = response.errorBody()?.string()
            val errorJson = try {
                Gson().fromJson(errorBody, ErrorResponse::class.java)
            } catch (e: Exception) {
                null
            }
            val errorMessage = errorJson?.message ?: "Sign-up failed"
            throw Exception(errorMessage)
        }
    }
}

data class ErrorResponse(val message: String?)