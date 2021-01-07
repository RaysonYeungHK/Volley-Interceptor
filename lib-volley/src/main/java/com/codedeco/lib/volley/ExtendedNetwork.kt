package com.codedeco.lib.volley

import androidx.annotation.VisibleForTesting
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.toolbox.BaseHttpStack
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.ByteArrayPool
import com.android.volley.toolbox.HttpStack
import com.codedeco.lib.volley.interceptor.Interceptor
import com.codedeco.lib.volley.interceptor.InterceptorChain

open class ExtendedNetwork : BasicNetwork {
    constructor(httpStack: HttpStack?) : super(httpStack)
    constructor(httpStack: HttpStack?, pool: ByteArrayPool?) : super(httpStack, pool)
    constructor(httpStack: BaseHttpStack?) : super(httpStack)
    constructor(httpStack: BaseHttpStack?, pool: ByteArrayPool?) : super(httpStack, pool)

    @VisibleForTesting
    val interceptors = ArrayList<Interceptor>()

    @VisibleForTesting
    open fun realCallInterceptor(): Interceptor {
        return Interceptor { chain ->
            this@ExtendedNetwork.realCall(chain.request())
        }
    }

    fun addInterceptor(interceptor: Interceptor) = apply {
        interceptors.add(interceptor)
    }

    fun addInterceptors(interceptors: Collection<Interceptor>) = apply {
        this.interceptors.addAll(interceptors)
    }

    override fun performRequest(request: Request<*>): NetworkResponse {
        val interceptors = ArrayList(interceptors)
        interceptors.add(realCallInterceptor())
        val chain = InterceptorChain(request, interceptors, 0)
        return chain.proceed(request)!!
    }

    private fun realCall(request: Request<*>?): NetworkResponse {
        return super.performRequest(request)
    }
}