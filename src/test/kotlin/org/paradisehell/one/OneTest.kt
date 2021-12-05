package org.paradisehell.one

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.InputStream
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class BaseAPIThrowableResolverFactory : ThrowableResolver.Factory<BaseAPIResult<*>> {
    override fun create(type: Type): ThrowableResolver<BaseAPIResult<*>>? {
        return (type as? ParameterizedType)?.rawType
            ?.takeIf { it == BaseAPIResult::class.java }
            ?.let { Resolver() }
    }

    inner class Resolver : ThrowableResolver<BaseAPIResult<*>> {
        override fun resolve(throwable: Throwable): BaseAPIResult<*> {
            return BaseAPIResult<Any>(-1, null, message = throwable.message)
        }
    }
}


class WanAndroidResponseTransformerFactory : ResponseTransformer.Factory {
    companion object {
        private val gson = Gson()
    }

    override fun create(type: Type): ResponseTransformer? {
        return (type as? ParameterizedType)?.rawType
            ?.takeIf { it == BaseAPIResult::class.java }
            ?.let { Transformer() }
    }

    inner class WanAndroidResult<T> {
        @SerializedName("code", alternate = ["errorCode"])
        private val errorCode: Int = -1

        @SerializedName("data")
        private val data: T? = null

        @SerializedName("message", alternate = ["errorMsg"])
        private val errorMsg: String? = null
    }

    inner class Transformer : ResponseTransformer {
        override fun transform(original: InputStream): InputStream {
            val response = gson.fromJson<WanAndroidResult<JsonElement>>(
                original.reader(), object : TypeToken<WanAndroidResult<JsonElement>>() {}.type
            )
            return gson.toJson(response).byteInputStream()
        }
    }
}

data class BaseAPIResult<T>(
    @SerializedName("code") val code: Int,
    @SerializedName("data") override val data: T? = null,
    @SerializedName("message") val message: String? = null
) : APIResult<T, BaseAPIResult.Failure> {

    data class Failure(val code: Int, val message: String?) : APIResult.Failure

    override val isSuccess: Boolean get() = code == 0

    override val failure: Failure? get() = if (isFailure) Failure(code, message) else null
}

interface WanAndroidService {
    @GET("/test")
    suspend fun test(): BaseAPIResult<JsonElement>

    @GET("/user/lg/userinfo/json")
    suspend fun userInfo(): BaseAPIResult<JsonElement>

    @GET("/banner/json")
    suspend fun banner(): BaseAPIResult<JsonElement>
}

class Test {
    private val retrofit by lazy {
        Retrofit.Builder().baseUrl("https://www.wanandroid.com/")
            .addConverterFactory(TransformConverterFactory())
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    init {
        FactoryRegistry.registerThrowableResolver(BaseAPIThrowableResolverFactory())
        FactoryRegistry.registerResponseTransformer(WanAndroidResponseTransformerFactory())
    }

    @Test
    fun test() {
        val service = retrofit.create(WanAndroidService::class.java).proxyRetrofit()
        runBlocking {
            // test
            service.test()
                .onSuccess { println("execute test success ==> $it") }
                .onFailure { println("execute test() failure ==> $it") }
                // userInfo
                .onFailureThen { service.userInfo() }
                ?.onSuccess { println("execute userInfo success ==> $it") }
                ?.onFailure { println("execute userInfo() failure ==> $it") }
                // banner
                ?.onFailureThen { service.banner() }
                ?.onSuccess { println("execute banner() success ==> $it") }
                ?.onFailure { println("execute banner() failure ==> $it") }
        }
    }
}