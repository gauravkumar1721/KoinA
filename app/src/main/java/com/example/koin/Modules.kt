package com.example.koin

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.koin.Data.model.CountriesDao
import com.example.koin.Data.model.CountriesDatabase
import okhttp3.OkHttpClient
import retrofit2.Retrofit

val apiModule = module {

    fun provideCountriesApi(retrofit: Retrofit): CountriesApi {
        return retrofit.create(CountriesApi::class.java)
    }
    single { provideCountriesApi(get()) }

}

val databaseModule = module {

    fun provideDatabase(application: Application): CountriesDatabase {
        return Room.databaseBuilder(application, CountriesDatabase::class.java, "countries")
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideCountriesDao(database: CountriesDatabase): CountriesDao {
        return  database.countriesDao
    }

    single { provideDatabase(androidApplication()) }
    single { provideCountriesDao(get()) }
}

val networkModule = module {
    val connectTimeout : Long = 40// 20s
    val readTimeout : Long  = 40 // 20s

    fun provideHttpClient(): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
        if (DEBUG) {
            val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            okHttpClientBuilder.addInterceptor(httpLoggingInterceptor)
        }
        okHttpClientBuilder.build()
        return okHttpClientBuilder.build()
    }

    fun provideRetrofit(client: OkHttpClient, baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
    }

    single { provideHttpClient() }
    single {
        val baseUrl = androidContext().getString(R.string.BASE_URL)
        provideRetrofit(get(), baseUrl)
    }
}

val repositoryModule = module {

    fun provideCountryRepository(api: CountriesApi, context: Context, dao : CountriesDao): CountriesRepository {
        return CountriesRepositoryImpl(api, context, dao)
    }
    single { provideCountryRepository(get(), androidContext(), get()) }

}


val viewModelModule = module {

    // Specific viewModel pattern to tell Koin how to build CountriesViewModel
    viewModel {
        CountriesViewModel(repository = get())
    }

}