package itk.myoganugraha.wisataku_kotlin.model

import com.google.gson.annotations.SerializedName

data class Login (
    @field:SerializedName("username")
    val username: String? = null,

    @field:SerializedName("password")
    val password: String? = null
    )