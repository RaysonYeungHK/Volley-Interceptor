package com.codedeco.lib.volley.interceptor

import com.android.volley.NetworkResponse
import com.android.volley.Request
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class InterceptorChainTest {
    @MockK
    private lateinit var request: Request<String>

    private val response = NetworkResponse(
        0,
        ByteArray(0),
        HashMap(),
        true,
        0
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun emptyInterceptor() {
        val sut = InterceptorChain(request, listOf(), 0)
        sut.proceed(request)
    }

    @Test
    fun negativeIndex() {
        val interceptors = listOf(
            InterceptorProvider.doNothingInterceptor()
        )
        val sut = InterceptorChain(request, interceptors, -1)
        try {
            sut.proceed(request)
        } catch (e: Exception) {
            Assert.assertEquals(IndexOutOfBoundsException::class, e::class)
        }
        interceptors.forEach {
            it.assertNotInvoked()
        }
    }

    @Test
    fun excessIndex() {
        val interceptors = listOf(
            InterceptorProvider.doNothingInterceptor()
        )
        val sut = InterceptorChain(request, interceptors, 999)
        try {
            sut.proceed(request)
        } catch (e: Exception) {
            Assert.assertEquals(IndexOutOfBoundsException::class, e::class)
        }
        interceptors.forEach {
            it.assertNotInvoked()
        }
    }

    @Test
    fun middleIndex() {
        val interceptors = listOf(
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.hasResponseInterceptor(response)
        )
        val sut = InterceptorChain(request, interceptors, 2)
        try {
            sut.proceed(request)
        } catch (e: Exception) {
            Assert.assertEquals(IndexOutOfBoundsException::class, e::class)
        }
        interceptors.forEachIndexed { index, interceptor ->
            if (index < 2) {
                interceptor.assertNotInvoked()
            } else {
                interceptor.assertInvoked()
            }
        }
    }

    @Test
    fun noResponse() {
        val interceptors = listOf(
            InterceptorProvider.noResponseInterceptor()
        )
        val sut = InterceptorChain(request, interceptors, 0)
        try {
            sut.proceed(request)
        } catch (e: Exception) {
            Assert.assertEquals(NullPointerException::class, e::class)
        }
        interceptors.forEach {
            it.assertInvoked()
        }
    }

    @Test
    fun hasResponse() {
        val interceptors = listOf(
            InterceptorProvider.hasResponseInterceptor(response)
        )
        val sut = InterceptorChain(request, interceptors, 0)
        val actual = sut.proceed(request)
        Assert.assertEquals(this.response, actual)
        interceptors.forEach {
            it.assertInvoked()
        }
    }

    @Test
    fun failInMiddle() {
        val interceptors = listOf(
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.errorInterceptor(),
            InterceptorProvider.hasResponseInterceptor(response)
        )
        val sut = InterceptorChain(request, interceptors, 0)
        try {
            sut.proceed(request)
        } catch (e: Exception) {
            Assert.assertEquals(IllegalStateException::class, e::class)
        }
        interceptors.forEachIndexed { index, interceptor ->
            if (index < 2) {
                interceptor.assertInvoked()
            } else {
                interceptor.assertNotInvoked()
            }
        }
    }

    @Test
    fun modifyRequest() {
        val actual = HashMap<String, String>()
        every { request.headers } returns actual

        val interceptors = listOf(
            InterceptorProvider.assertRequestHeaderInterceptor(mapOf()),
            InterceptorProvider.modifyRequestHeaderInterceptor("user-agent", "myagent"),
            InterceptorProvider.assertRequestHeaderInterceptor(
                mapOf(
                    "user-agent" to "myagent"
                )
            ),
            InterceptorProvider.modifyRequestHeaderInterceptor(
                "cache-control",
                "no-store no-cache"
            ),
            InterceptorProvider.assertRequestHeaderInterceptor(
                mapOf(
                    "user-agent" to "myagent",
                    "cache-control" to "no-store no-cache"
                )
            ),
            InterceptorProvider.hasResponseInterceptor(response)
        )
        val sut = InterceptorChain(request, interceptors, 0)
        sut.proceed(request)

        Assert.assertEquals(2, actual.size)
        Assert.assertEquals("myagent", actual["user-agent"])
        Assert.assertEquals("no-store no-cache", actual["cache-control"])

        interceptors.forEach {
            it.assertInvoked()
        }
    }

    @Test
    fun modifyResponse() {
        val interceptors = listOf(
            InterceptorProvider.assertResponseHeaderInterceptor(
                mapOf(
                    "user-agent" to "myagent",
                    "cache-control" to "no-store no-cache"
                )
            ),
            InterceptorProvider.modifyResponseHeaderInterceptor(
                "cache-control",
                "no-store no-cache"
            ),
            InterceptorProvider.assertResponseHeaderInterceptor(
                mapOf(
                    "user-agent" to "myagent"
                )
            ),
            InterceptorProvider.modifyResponseHeaderInterceptor("user-agent", "myagent"),
            InterceptorProvider.assertResponseHeaderInterceptor(mapOf()),

            InterceptorProvider.hasResponseInterceptor(response)
        )
        val sut = InterceptorChain(request, interceptors, 0)
        val responseHeaders = sut.proceed(request)?.headers!!

        Assert.assertEquals(2, responseHeaders.size)
        Assert.assertEquals("myagent", responseHeaders["user-agent"])
        Assert.assertEquals("no-store no-cache", responseHeaders["cache-control"])

        interceptors.forEach {
            it.assertInvoked()
        }
    }

    @Test
    fun largeNumberInterceptors() {
        val requestHeaders = HashMap<String, String>()
        every { request.headers } returns requestHeaders
        val interceptors = listOf(
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("0", "0"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("1", "1"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("2", "2"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("3", "3"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("4", "4"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("5", "5"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("6", "6"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("7", "7"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("8", "8"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("9", "9"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("10", "10"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("11", "11"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("12", "12"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("13", "13"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("14", "14"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("15", "15"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("16", "16"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("17", "17"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("18", "18"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("19", "19"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("20", "20"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("21", "21"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("22", "22"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("23", "23"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("24", "24"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("25", "25"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("26", "26"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("27", "27"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("28", "28"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("29", "29"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("30", "30"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("31", "31"),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.modifyRequestHeaderInterceptor("32", "32"),
            InterceptorProvider.hasResponseInterceptor(response)
        )
        val sut = InterceptorChain(request, interceptors, 0)
        val actual = sut.proceed(request)
        Assert.assertEquals(this.response, actual)

        interceptors.forEach {
            it.assertInvoked()
        }
    }
}