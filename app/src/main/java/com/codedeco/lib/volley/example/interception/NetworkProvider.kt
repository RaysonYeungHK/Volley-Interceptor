package com.codedeco.lib.volley.example.interception

import com.android.volley.toolbox.HurlStack
import com.codedeco.lib.volley.ExtendedNetwork
import com.codedeco.lib.volley.example.interception.interceptor.LogInterceptor
import com.codedeco.lib.volley.example.interception.interceptor.RequestInterceptor
import com.codedeco.lib.volley.example.interception.interceptor.ResponseInterceptor

class NetworkProvider {
    private val httpStack = HurlStack()
    val network = ExtendedNetwork(httpStack).apply {
        addInterceptor(LogInterceptor())
        addInterceptor(RequestInterceptor())
        addInterceptor(ResponseInterceptor())
    }
}