package com.example.glucosetracker.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ReadingDao {
    @Delete
    suspend fun deleteReading(reading: Reading)

    // for safe updates for existing data
    @Update
    suspend fun updateReading(reading: Reading)

    // when you need to insert or overwrite existing data
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: Reading)

    @Query("SELECT * FROM readings ORDER BY id ASC")
    suspend fun getAllReadings(): List<Reading>

    @Query("SELECT * FROM readings WHERE id LIKE :id")
    suspend fun getAllReadingsById(id:Int): Reading

}