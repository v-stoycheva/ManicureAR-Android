package com.viktoriastoycheva.manicurear.network

import com.viktoriastoycheva.manicurear.models.Appointment
import com.viktoriastoycheva.manicurear.models.ArDesign
import com.viktoriastoycheva.manicurear.models.Service
import com.viktoriastoycheva.manicurear.models.StaffAvailability
import com.viktoriastoycheva.manicurear.models.User
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
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
    fun getAllServices(): Call<List<Service>>

    @GET("api/users/manicurists")
    fun getManicurists(): Call<List<User>>

    @GET("api/appointments/manicurist/{id}")
    fun getManicuristAppointments(@Path("id") id: Long): Call<List<Appointment>>

    @GET("api/staff-availability/manicurist/{id}")
    fun getManicuristAvailability(@Path("id") id: Long): Call<List<StaffAvailability>>

    @POST("api/appointments")
    fun createAppointment(@Body appointment: Appointment): Call<Appointment>

    @GET("api/users/{id}")
    fun getUserById(@Path("id") id: Long): Call<User>

    @GET("api/appointments/client/{clientId}")
    fun getClientHistory(@Path("clientId") clientId: Long): Call<List<Appointment>>

    @PUT("api/appointments/{id}/cancel")
    fun cancelAppointment(@Path("id") id: Long): Call<Void>
}