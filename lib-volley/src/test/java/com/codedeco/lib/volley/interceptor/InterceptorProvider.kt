package com.codedeco.lib.volley.interceptor

import androidx.annotation.CallSuper
import com.android.volley.NetworkResponse
import org.junit.Assert

object InterceptorProvider {

    open class AssertInterceptor : Interceptor {
        private var invoked = false

        @CallSuper
        override fun intercept(chain: Interceptor.Chain): NetworkResponse? {
            invoked = true
            return null
        }

        fun assertInvoked() {
            Assert.assertEquals(true, invoked)
        }

        fun assertNotInvoked() {
            Assert.assertEquals(false, invoked)
        }

        companion object {
            /**
             * Lambda support
             * val interceptor = Interceptor { chain ->
             *     chain.proceed(chain.request())
             * }
             */
            inline operator fun invoke(crossinline block: (chain: Interceptor.Chain) -> NetworkResponse?) =
                    object : AssertInterceptor() {
                        override fun intercept(chain: Interceptor.Chain): NetworkResponse? {
                            super.intercept(chain)
                            return block(chain)
                        }
                    }
        }
    }

    /**
     * Interceptors for testing
     */
    fun doNothingInterceptor(): AssertInterceptor {
        return AssertInterceptor { chain ->
            println("doNothingInterceptor")
            chain.proceed(chain.request())
        }
    }

    fun errorInterceptor(): AssertInterceptor {
        return AssertInterceptor {
            println("errorInterceptor")
            throw IllegalStateException()
        }
    }

    fun hasResponseInterceptor(response: NetworkResponse): AssertInterceptor {
        return AssertInterceptor {
            println("hasResponseInterceptor")
            response
        }
    }

    fun noResponseInterceptor(): AssertInterceptor {
        return AssertInterceptor {
            println("noResponseInterceptor")
            null
        }
    }

    fun assertRequestHeaderInterceptor(map: Map<String, String>): AssertInterceptor {
        return AssertInterceptor { chain ->
            println("assertRequestHeaderInterceptor")
            val headers = chain.request().headers
            headers.forEach { entry ->
                if (map[entry.key] != entry.value) {
                    throw AssertionError("${entry.key} should not be exists")
                }
            }
            map.forEach { entry ->
                if (headers[entry.key] != entry.value) {
                    throw AssertionError("${entry.key} should be exists")
                }
            }
            chain.proceed(chain.request())
        }
    }

    fun modifyRequestHeaderInterceptor(key: String, value: String): AssertInterceptor {
        println("modifyRequestHeaderInterceptor")
        return AssertInterceptor { chain ->
            chain.request().headers[key] = value
            chain.proceed(chain.request())
        }
    }

    fun assertResponseHeaderInterceptor(map: Map<String, String>): AssertInterceptor {
        return AssertInterceptor { chain ->
            println("assertResponseHeaderInterceptor")
            val response = chain.proceed(chain.request())
            val headers = response?.headers
            headers?.forEach { entry ->
                if (map[entry.key] != entry.value) {
                    throw AssertionError("${entry.key} should not be exists")
                }
            }
            map.forEach { entry ->
                if (headers?.get(entry.key) != entry.value) {
                    throw AssertionError("${entry.key} should be exists")
                }
            }
            response
        }
    }

    fun modifyResponseHeaderInterceptor(key: String, value: String): AssertInterceptor {
        println("modifyResponseHeaderInterceptor")
        return AssertInterceptor { chain ->
            chain.proceed(chain.request())?.apply {
                this@apply.headers[key] = value
            }
        }
    }
}