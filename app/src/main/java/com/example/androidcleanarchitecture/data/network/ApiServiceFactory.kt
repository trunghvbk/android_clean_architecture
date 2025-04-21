package com.example.androidcleanarchitecture.data.network

import android.util.Log
import com.example.androidcleanarchitecture.data.model.UserNetworkModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Factory class for creating API service instances with mock support for testing.
 * Handles the configuration of Retrofit and OkHttpClient.
 */
object ApiServiceFactory {
    
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"
    private const val TIMEOUT = 30L
    private const val TAG = "ApiServiceFactory"
    
    // Mock web server for testing
    private var mockWebServer: MockWebServer? = null
    
    /**
     * Creates an OkHttpClient with logging and timeout configuration
     * @return Configured OkHttpClient instance
     */
    private fun createOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Creates a Retrofit instance with the configured OkHttpClient
     * @param baseUrl The base URL for the API
     * @return Configured Retrofit instance
     */
    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * Creates an instance of the UserApiService
     * @return UserApiService instance
     */
    fun createUserApiService(): UserApiService {
        // If mock web server is running, use it for testing
        val baseUrl = if (mockWebServer != null) {
            mockWebServer!!.url("/").toString()
        } else {
            BASE_URL
        }
        
        return createRetrofit(baseUrl).create(UserApiService::class.java)
    }
    
    /**
     * Starts a mock web server for testing
     * @return The started MockWebServer instance
     */
    fun startMockWebServer(): MockWebServer {
        mockWebServer = MockWebServer()
        try {
            mockWebServer!!.start()
            Log.d(TAG, "Mock server started at ${mockWebServer!!.url("/")}")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting mock server: ${e.message}")
            throw e
        }
        return mockWebServer!!
    }
    
    /**
     * Stops the mock web server
     */
    fun stopMockWebServer() {
        try {
            mockWebServer?.shutdown()
            mockWebServer = null
            Log.d(TAG, "Mock server stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping mock server: ${e.message}")
        }
    }
    
    /**
     * Enqueues a mock response for testing
     * @param jsonResponse The JSON response to return
     * @param responseCode The HTTP response code
     */
    fun enqueueMockResponse(jsonResponse: String, responseCode: Int = 200) {
        mockWebServer?.enqueue(
            MockResponse()
                .setResponseCode(responseCode)
                .setBody(jsonResponse)
        )
    }
}
