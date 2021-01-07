package com.codedeco.lib.volley.interceptor

import com.android.volley.NetworkResponse
import com.android.volley.Request

/**
 * Interception of request and response
 */
interface Interceptor {
    @Throws(Exception::class)
    fun intercept(chain: Chain): NetworkResponse?

    companion object {
        /**
         * Lambda support
         * val interceptor = Interceptor { chain ->
         *     chain.proceed(chain.request())
         * }
         */
        inline operator fun invoke(crossinline block: (chain: Interceptor.Chain) -> NetworkResponse?) =
                object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain) = block(chain)
                }
    }

    interface Chain {
        /**
         * Return the request instance in this interceptor chain
         */
        fun request(): Request<*>

        /**
         * Align with com.android.volley.Network#performRequest(Request<?> request)
         */
        @Throws(Exception::class)
        fun proceed(request: Request<*>): NetworkResponse?
    }
}
