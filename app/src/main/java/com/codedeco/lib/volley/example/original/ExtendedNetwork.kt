package com.codedeco.lib.volley.example.original

import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.toolbox.BaseHttpStack
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.ByteArrayPool
import com.android.volley.toolbox.HttpStack

class ExtendedNetwork : BasicNetwork {
    companion object {
        private const val TAG = "ExtendedNetwork"
    }

    constructor(httpStack: HttpStack?) : super(httpStack)
    constructor(httpStack: HttpStack?, pool: ByteArrayPool?) : super(httpStack, pool)
    constructor(httpStack: BaseHttpStack?) : super(httpStack)
    constructor(httpStack: BaseHttpStack?, pool: ByteArrayPool?) : super(httpStack, pool)

    override fun performRequest(request: Request<*>?): NetworkResponse {
        // example for intercepting the request
        /**
         * Please refer to com.codedeco.lib.volley.example.interception.interceptor.LogInterceptor
         */
        when (request?.method) {
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
        /**
         * Please refer to com.codedeco.lib.volley.example.interception.interceptor.RequestInterceptor
         */
        request?.headers?.apply {
            put("Cache-Control", "no-cache no-store")
        }
        // Original API call
        val response = super.performRequest(request)
        /**
         * Please refer to com.codedeco.lib.volley.example.interception.interceptor.ResponseInterceptor
         */
        // example for intercepting the response
        if (response.statusCode != 200) {
            throw IllegalStateException()
        }
        if (response.data.isEmpty()) {
            throw NullPointerException()
        }
        return response
    }
}