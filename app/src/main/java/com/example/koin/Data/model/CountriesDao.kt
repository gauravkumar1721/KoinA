package com.example.koin.Data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.koin.model.CountriesData

@Dao
interface CountriesDao {

    @Query("SELECT * FROM Countries")
    fun findAll(): List<CountriesData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(users: List<CountriesData>)
}