package com.codedeco.lib.volley.example.interception.interceptor

import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.codedeco.lib.volley.interceptor.Interceptor

class LogInterceptor : Interceptor {
    companion object {
        private const val TAG = "LogInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): NetworkResponse? {
        when (chain.request().method) {
            Request.Method.HEAD,
            Request.Method.GET -> {
                Log.d(TAG, "HEAD / GET is detected")
            }
            Request.Method.POST,
            Request.Method.PUT,
            Request.Method.DELETE -> {
                Log.d(TAG, "POST / PUT / DELETE is detected")
            }
            else -> {
                Log.d(TAG, "other method is detected")
            }
        }
        return chain.proceed(chain.request())
    }
}