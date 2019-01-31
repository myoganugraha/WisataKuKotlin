package itk.myoganugraha.wisataku_kotlin.model

import javax.annotation.Generated
import com.google.gson.annotations.SerializedName

@Generated("com.robohorse.robopojogenerator")
data class WisataResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("results")
	val results: List<Wisata?>? = null
)