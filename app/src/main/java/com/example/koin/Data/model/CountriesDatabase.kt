package com.example.koin.Data.model

import androidx.databinding.adapters.Converters
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.koin.model.CountriesData
import java.lang.annotation.Native
import java.util.jar.Attributes

@Database(
    entities = [CountriesData::class, Languages::class, Attributes.Name::class, Native::class, Translations::class],
    version = 1, exportSchema = false
)

@TypeConverters(Converters::class, LanguagesTypeConverter::class, NameConverter::class, NativeConverter::class, TranslationsConverter::class)
abstract class CountriesDatabase : RoomDatabase() {
    abstract val countriesDao: CountriesDao
}