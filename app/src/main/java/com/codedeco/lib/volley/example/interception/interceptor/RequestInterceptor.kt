package com.codedeco.lib.volley.example.interception.interceptor

import com.android.volley.NetworkResponse
import com.codedeco.lib.volley.interceptor.Interceptor

class RequestInterceptor : Interceptor {
    companion object {
        private const val TAG = "LogInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): NetworkResponse? {
        chain.request().apply {
            headers["cache-control"] = "no-store no-cache"
        }
        return chain.proceed(chain.request())
    }
}