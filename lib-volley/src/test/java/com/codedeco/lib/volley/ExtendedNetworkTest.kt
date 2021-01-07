package com.codedeco.lib.volley

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.toolbox.HurlStack
import com.codedeco.lib.volley.interceptor.InterceptorProvider
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.spyk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExtendedNetworkTest {
    private lateinit var sut: ExtendedNetwork

    @MockK
    private lateinit var request: Request<String>

    @MockK
    private lateinit var response: NetworkResponse

    private lateinit var mockCallInterceptor: InterceptorProvider.AssertInterceptor

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        sut = spyk(ExtendedNetwork(HurlStack()))
        mockCallInterceptor = InterceptorProvider.hasResponseInterceptor(response)
        every { sut.realCallInterceptor() } returns mockCallInterceptor
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun addInterceptor() {
        // Before
        Assert.assertEquals(0, sut.interceptors.size)

        // After
        sut.addInterceptor(InterceptorProvider.doNothingInterceptor())
        val actual = sut.interceptors
        Assert.assertEquals(1, actual.size)
    }

    @Test
    fun addInterceptors() {
        // Before
        Assert.assertEquals(0, sut.interceptors.size)

        // After
        val interceptors = listOf(
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.doNothingInterceptor()
        )
        sut.addInterceptors(interceptors)
        val actual = sut.interceptors
        Assert.assertEquals(2, actual.size)
    }

    @Test
    fun performRequest_noInterceptor() {
        // Assert response
        val actual = sut.performRequest(request)
        Assert.assertEquals(response, actual)
        // Assert real call interceptor
        mockCallInterceptor.assertInvoked()
    }

    @Test
    fun performRequest_1_interceptor() {
        val interceptors = listOf(
            InterceptorProvider.doNothingInterceptor()
        )
        sut.addInterceptors(interceptors)
        // Assert response
        val actual = sut.performRequest(request)
        Assert.assertEquals(response, actual)
        // Assert all interceptors
        interceptors.forEach {
            it.assertInvoked()
        }
        // Assert real call interceptor
        mockCallInterceptor.assertInvoked()
    }

    @Test
    fun performRequest_2_interceptos() {
        val interceptors = listOf(
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.doNothingInterceptor()
        )
        sut.addInterceptors(interceptors)
        // Assert response
        val actual = sut.performRequest(request)
        Assert.assertEquals(response, actual)
        // Assert all interceptors
        interceptors.forEach {
            it.assertInvoked()
        }
        // Assert real call interceptor
        mockCallInterceptor.assertInvoked()
    }

    @Test
    fun performRequest_multiple_interceptors() {
        val interceptors = listOf(
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.doNothingInterceptor(),
            InterceptorProvider.doNothingInterceptor()
        )
        sut.addInterceptors(interceptors)
        // Assert response
        val actual = sut.performRequest(request)
        Assert.assertEquals(response, actual)
        // Assert all interceptors
        interceptors.forEach {
            it.assertInvoked()
        }
        // Assert real call interceptor
        mockCallInterceptor.assertInvoked()
    }

    @Test(expected = NullPointerException::class)
    fun noResponse() {
        every { sut.realCallInterceptor() } returns InterceptorProvider.noResponseInterceptor()
        sut.performRequest(request)
    }

    @Test(expected = IllegalStateException::class)
    fun interceptor_error() {
        val interceptors = listOf(
            InterceptorProvider.errorInterceptor()
        )
        sut.addInterceptors(interceptors)
        sut.performRequest(request)
    }
}