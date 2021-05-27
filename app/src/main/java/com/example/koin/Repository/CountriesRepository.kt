package com.example.koin.Repository

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.example.koin.CountriesApi
import com.example.koin.Data.model.CountriesDao
import com.example.koin.model.CountriesData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

interface CountriesRepository {
    suspend fun getAllCountries() : AppResult<List<CountriesData>>
    fun isOnline(context: Context): Boolean
    fun handleSuccess(response: Response<List<CountriesData>>): AppResult<List<CountriesData>>
    fun handleApiError(response: Response<List<CountriesData>>): AppResult<List<CountriesData>>
}

class AppResult<T> {

}

class CountriesRepositoryImpl(private val api: CountriesApi, private val context: Context, private val dao: CountriesDao) :
    CountriesRepository {

    override suspend fun getAllCountries(): AppResult<List<CountriesData>> {
        if (isOnline(context)) {
            return try {
                val response = api.getAllCountries()
                if (response.isSuccessful) {
                    //save the data
                    response.body()?.let {
                        withContext(Dispatchers.IO) { dao.add(it) }
                    }
                    handleSuccess(response)
                } else {
                    handleApiError(response)
                }
            } catch (e: Exception) {
                AppResult.Error(e)
            }
        } else {
            //check in db if the data exists
            val data = getCountriesDataFromCache()
            return if (data.isNotEmpty()) {
                Log.d(TAG, "from db")
                AppResult.Success(data)
            } else
            //no network
                context.noNetworkConnectivityError()
        }
    }

    private suspend fun getCountriesDataFromCache(): List<CountriesData> {
        return withContext(Dispatchers.IO) {
            dao.findAll()
        }
    }
}

private fun Context.noNetworkConnectivityError(): AppResult<List<CountriesData>> {

}
