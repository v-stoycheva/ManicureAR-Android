package com.viktoriastoycheva.manicurear.network

import com.viktoriastoycheva.manicurear.models.Service
import com.viktoriastoycheva.manicurear.models.User
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("api/services")
    fun getAllServices(): Call<List<Service>>

    @POST("api/users/login")
    fun login(@Query("email") email: String, @Query("password") password: String): Call<User>

    @POST("api/users/register")
    fun register(@Body user: User): Call<User>

    @PUT("api/users/{id}")
    fun updateUser(
        @Path("id") userId: Long,
        @Body user: User
    ): Call<User>

    @FormUrlEncoded
    @POST("api/users/{id}/change-password")
    fun changePassword(
        @Path("id") userId: Long,
        @Field("currentPassword") current: String,
        @Field("newPassword") new: String
    ): Call<Void>
}