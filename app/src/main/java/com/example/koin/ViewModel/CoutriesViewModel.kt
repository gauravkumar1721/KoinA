package com.example.koin.ViewModel

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.koin.Repository.CountriesRepository
import com.example.koin.model.CountriesData

class CountriesViewModel(private val repository : CountriesRepository) : ViewModel() {

    val showLoading = ObservableBoolean()
    val countriesList = MutableLiveData<List<CountriesData>>()
    val showError = SingleLiveEvent<String>()

    fun getAllCountries() {
        showLoading.set(true)
        viewModelScope.launch {
            val result =  repository.getAllCountries()

            showLoading.set(false)
            when (result) {
                is AppResult.Success -> {
                    countriesList.value = result.successData
                    showError.value = null
                }
                is AppResult.Error -> showError.value = result.exception.message
            }
        }
    }
}