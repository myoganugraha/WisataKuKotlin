package itk.myoganugraha.wisataku_kotlin.Activity

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import itk.myoganugraha.wisataku_kotlin.R
import itk.myoganugraha.wisataku_kotlin.model.Login
import itk.myoganugraha.wisataku_kotlin.utils.BaseAPIService
import itk.myoganugraha.wisataku_kotlin.utils.RetrofitClient
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit


class LoginActivity : AppCompatActivity() {

    private lateinit var baseAPIService : BaseAPIService
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(itk.myoganugraha.wisataku_kotlin.R.layout.activity_login)

        baseAPIService = RetrofitClient().getInitInstance()

        initRX()
    }

    private fun initLogin() {

        btnProcedLogin.setOnClickListener{
            progressDialog = ProgressDialog.show(this, null, "Please Wait ...", true, false)
            if(usernameLogin.length() > 4 && passwordLogin.length() > 4){
                baseAPIService.userLogin(
                    Login(usernameLogin.text.toString(), passwordLogin.text.toString())
                ).enqueue(object: Callback<ResponseBody>{
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        progressDialog.dismiss()
                        loadFailed()
                    }

                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        progressDialog.dismiss()
                       try{
                           var jsonObject = JSONObject(response.body()?.string())
                           loginSuccess(jsonObject.getString("message"))
                       }
                       catch (e: JSONException) {
                           e.printStackTrace()
                       }
                    }

                })
            }
        }
    }

    private fun initRX(){

        var usernameChangeObservable = RxTextView.textChangeEvents(usernameLogin)
        var passwordChangeObservable = RxTextView.textChangeEvents(passwordLogin)

        usernameChangeObservable
            .skipInitialValue()
            .map {
                usernameLoginWrapper.error = null
                it.view().text.toString()
            }
            .debounce(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
            .compose(usernameCharacterLengthCheck)
            .compose(retryWhenError {
                usernameLoginWrapper.error = it.message

            })
            .subscribe()

        passwordChangeObservable
            .skipInitialValue()
            .map{
                passwordLoginWrapper.error = null
                it.view().text.toString()
            }
            .debounce(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
            .compose(passwordCharacterLengthCheck)
            .compose(retryWhenError {
                passwordLoginWrapper.error = it.message

            })
            .subscribe()

        initLogin()
    }

    private inline fun retryWhenError(crossinline onError: (ex: Throwable) -> Unit) : ObservableTransformer<String, String> = ObservableTransformer {
        observable -> observable.retryWhen { errors ->
            errors.flatMap {
                onError(it)
                Observable.just(it)
            }
        }
    }

    private val usernameCharacterLengthCheck = ObservableTransformer<String, String>{
        observable -> observable.flatMap {
        Observable.just(it).map { it.trim() }
            .filter { it.length > 4 }
            .singleOrError()
            .onErrorResumeNext {
                when{
                    it is NoSuchElementException -> Single.error(Exception(
                        "Username too short"
                    ))
                    else -> Single.error(it)
                }
            }
            .toObservable()
        }
    }

    private val passwordCharacterLengthCheck = ObservableTransformer<String, String>{
            observable -> observable.flatMap {
        Observable.just(it).map { it.trim() }
            .filter { it.length > 5 }
            .singleOrError()
            .onErrorResumeNext {
                when{
                    it is NoSuchElementException -> Single.error(Exception(
                        "Password too short"
                    ))
                    else -> Single.error(it)
                }
            }
            .toObservable()
        }
    }

    private fun loginSuccess(data : String?) {
        Toast.makeText(this, data, Toast.LENGTH_LONG).show()
    }

    private fun loadFailed() {
        Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
    }

    private fun enableSigIn(){
        btnProcedLogin.isEnabled = true
        btnProcedLogin.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        btnProcedLogin.setTextColor(resources.getColor(R.color.white))
    }

    private fun disableSigIn(){
        btnProcedLogin.isEnabled = false
        btnProcedLogin.setBackgroundColor(resources.getColor(R.color.material_grey_300))
        btnProcedLogin.setTextColor(resources.getColor(R.color.material_grey_850))
    }


}
