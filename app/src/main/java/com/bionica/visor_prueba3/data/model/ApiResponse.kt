package com.bionica.visor_prueba3.data.model

//class ApiResponse {
    data class ApiResponse<T>(
        val success: Boolean,
        val message: String,
        val data: T?
    )

//}