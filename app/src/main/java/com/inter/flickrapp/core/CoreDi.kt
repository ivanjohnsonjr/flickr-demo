package com.inter.flickrapp.core

import com.google.gson.GsonBuilder
import com.inter.flickrapp.BuildConfig
import com.inter.flickrapp.core.CoreConstants.API_KEY
import com.inter.flickrapp.core.CoreConstants.BASE_URL
import com.inter.flickrapp.core.repo.FlickrRepository
import com.inter.flickrapp.core.repo.FlickrRepositoryImpl
import com.inter.flickrapp.core.repo.FlickrServiceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object CoreDi {
    private val retrofitBuilder: Retrofit
        get() {
            val interceptor = HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) {
                    this.level = HttpLoggingInterceptor.Level.BODY
                }
            }
            val client = OkHttpClient.Builder().apply {
                this.addInterceptor(interceptor)
                    .addInterceptor(object : Interceptor {
                        override fun intercept(chain: Interceptor.Chain): Response {
                            val request = chain.request()
                            val httpUrl = request.url

                            // Add all the static query parameter information
                            val newHttpUrl = httpUrl.newBuilder()
                                .addQueryParameter("format", "json")
                                .addQueryParameter("nojsoncallback", "1")
                                .addQueryParameter("api_key", API_KEY)
                                .build()

                            val newRequest = request.newBuilder().url(newHttpUrl).build()

                            return chain.proceed(newRequest)
                        }
                    })
                    // time out setting
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(20,TimeUnit.SECONDS)
                    .writeTimeout(25,TimeUnit.SECONDS)


            }.build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
        }

    @Provides
    fun provideFlickService(): FlickrServiceApi {
        return retrofitBuilder.create(FlickrServiceApi::class.java)
    }

    @Provides
    fun provideFlickrRepository(
        api: FlickrServiceApi
    ): FlickrRepository {
        return FlickrRepositoryImpl(api)
    }
}