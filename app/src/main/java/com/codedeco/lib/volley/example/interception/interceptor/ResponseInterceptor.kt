package com.codedeco.lib.volley.example.interception.interceptor

import com.android.volley.NetworkResponse
import com.codedeco.lib.volley.interceptor.Interceptor

class ResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): NetworkResponse? {
        val response = chain.proceed(chain.request())
        if (response?.statusCode != 200) {
            throw IllegalStateException()
        }
        if (response.data.isEmpty()) {
            throw NullPointerException()
        }
        return response
    }
}