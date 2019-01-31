package itk.myoganugraha.wisataku_kotlin.utils

import itk.myoganugraha.wisataku_kotlin.model.Login
import itk.myoganugraha.wisataku_kotlin.model.UserResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BaseAPIService {

    @GET("{username}")
    fun getProfile(
        @Path("username") username : String
    ) : Call<UserResponse>

    @POST("login")
    fun userLogin(
        @Body login: Login
    ) : Call<ResponseBody>



}