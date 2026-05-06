package com.viktoriastoycheva.manicurear.network

import com.viktoriastoycheva.manicurear.ar.ArDesign
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

    @GET("api/ar-designs") // Провери пътя в твоя Controller в IntelliJ
    fun getAllArDesigns(): Call<List<ArDesign>>

    @POST("api/users/{userId}/favorites/{designId}")
    fun toggleFavorite(@Path("userId") userId: Long, @Path("designId") designId: Long): Call<Void>

    @GET("api/users/{userId}/favorites")
    fun getFavoriteDesigns(@Path("userId") userId: Long): Call<List<ArDesign>>

    @GET("api/services")
    fun getServices(): Call<List<Service>>
}