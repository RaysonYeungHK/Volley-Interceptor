package com.codedeco.lib.volley.interceptor

import com.android.volley.NetworkResponse
import com.android.volley.Request

/**
 * Chain of interceptors and actual network call
 */
class InterceptorChain(
        /**
         * API request
         */
        private val request: Request<*>,
        /**
         * All interceptors, including actual network call
         */
        private val interceptors: List<Interceptor>,
        /**
         * Index of current interceptor to proceed
         */
        private val index: Int,
) : Interceptor.Chain {

    override fun request(): Request<*> {
        return request
    }

    @Throws(Exception::class)
    override fun proceed(request: Request<*>): NetworkResponse? {
        if (0 > index || index >= interceptors.size) {
            throw IndexOutOfBoundsException()
        }

        val next = InterceptorChain(request, interceptors, index + 1)
        val interceptor = interceptors[index]

        return interceptor.intercept(next)
                ?: throw NullPointerException("interceptor $interceptor returned null")
    }
}