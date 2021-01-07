package com.codedeco.lib.volley.example.original

import com.android.volley.toolbox.HurlStack

class NetworkProvider {
    private val httpStack = HurlStack()
    val network = ExtendedNetwork(httpStack)
}