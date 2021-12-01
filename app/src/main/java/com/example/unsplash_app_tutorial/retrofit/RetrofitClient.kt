package com.example.unsplash_app_tutorial.retrofit

import android.util.Log
import com.example.unsplash_app_tutorial.utils.API
import com.example.unsplash_app_tutorial.utils.Constants.TAG
import com.example.unsplash_app_tutorial.utils.isJsonArray
import com.example.unsplash_app_tutorial.utils.isJsonObject
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.concurrent.TimeUnit

//싱글턴
// Kotlin 에서 object는 싱글턴 -> 메모리 하나만 씀
object RetrofitClient {
    //레트로핏 클랑이언트 선언
    private var retrofitClient: Retrofit? = null
    //private lateinit var retrofitClient: Retrofit

    //레트로핏 클라이언트 가져오기
    fun getClient(baseUrl : String): Retrofit? {
        Log.d(TAG, "RetrofitClient - getClient: called")


        // okhttp 인스턴스 생성
        val client = OkHttpClient.Builder()

        // 로그를 찍기 위해 로깅 인터셉터 설정
        val loggingInterceptor = HttpLoggingInterceptor(object: HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
//                Log.d(TAG, "RetrofitClient - log() called / message: $message")
                when {
                    message.isJsonObject() ->
                        Log.d(TAG, JSONObject(message).toString(4))
                    message.isJsonArray() ->
                        Log.d(TAG, JSONObject(message).toString(4))

                    else -> {
                        try {
                            Log.d(TAG, JSONObject(message).toString(4))
                        } catch (e: Exception) {
                            Log.d(TAG, message)
                        }
                    }
                }
            }

        })

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        // 위에서 설정한 로깅 인터셉터를 okhttp 클라이언트에 추가한다
        client.addInterceptor(loggingInterceptor)

        // 커넥션 타임아웃
        client.connectTimeout(timeout = 10, unit = TimeUnit.SECONDS)
        client.readTimeout(10, TimeUnit.SECONDS)
        client.writeTimeout(10, TimeUnit.SECONDS)
        client.retryOnConnectionFailure(true)

        // 기본 파라미터 추가
        val baseParameterInterceptor : Interceptor = (object : Interceptor{
            override fun intercept(chain: Interceptor.Chain): Response {
                Log.d(TAG, "RetrofitClient - intercept() called")
                //오리지날 리퀘스트
                val originalRequest = chain.request()

                //쿼리 파라미터 추가하기
                val addedUrl = originalRequest.url.newBuilder().addQueryParameter("client_id", API.CLIENT_ID).build()
                val finalRequest = originalRequest.newBuilder()
                    .url(addedUrl)
                    .method(originalRequest.method,originalRequest.body)
                    .build()

                return chain.proceed(finalRequest)
            }

        })

        //위에서 설정한 로깅 인터셉터를 okhttp 클라이언트에 추가한다.
        client.addInterceptor(baseParameterInterceptor)


        if(retrofitClient == null) {
            //레트로핏 빌더
            retrofitClient = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                //위에서 설정한 클라이언트로 Retrofit 클라이언트를 설정
                .client(client.build())
                .build()
        }

        return retrofitClient
    }
}