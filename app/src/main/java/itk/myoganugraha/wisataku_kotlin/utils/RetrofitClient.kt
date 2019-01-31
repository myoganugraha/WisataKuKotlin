package itk.myoganugraha.wisataku_kotlin.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    val BASE_URL_API = "http://10f15a73.ngrok.io/"

    private lateinit var baseAPIService: BaseAPIService

    fun getMovieRepository(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_API + "api/wisataku/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getInitInstance(): BaseAPIService{
        return getMovieRepository().create(BaseAPIService::class.java)
    }
}