package com.bionica.visor_prueba3.network

import com.bionica.visor_prueba3.data.model.User
import com.bionica.visor_prueba3.data.model.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("usuarios.php")                      //busca el endpoint de usuarios.php
    suspend fun createUser(@Body user: User): ApiResponse<User>

    @GET("users")
    suspend fun getByEmail(@Query("email") email: String): ApiResponse<User?> //17/10/25 04:20

    //buscar el endpoint del login
    @POST("login.php")
    suspend fun login(@Body request: Map<String, String>): ApiResponse<User>  //user es el data class en el dat.model

    @GET("users")
    suspend fun listUsers(): ApiResponse<List<User>>
    fun getUserByEmail(correo: String)

    //@POST("users")
    //suspend fun createUser(@Body user: User): ApiResponse<User>

    companion object {
        fun create(): ApiService {
            return ApiClient.retrofit.create(ApiService::class.java)
        }
    }
}


/**internal class {
    internal interface ApiService {
        fun listUsers()
        fun createUser(
        )

        companion object {
            val `fun`: suspend ? = null
            val `fun`: suspend ? = null
        }
    }

    var api: `val`? = null
    fun create()
}***/