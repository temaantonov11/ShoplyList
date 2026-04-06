package com.example.shoplylist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PurchaseDAO {

    @Query("SELECT * FROM purchases")
    fun getAll(): List<Purchase>

    @Insert
    fun insert(purchase: Purchase): Long

    @Delete
    fun delete(purchase: Purchase)

    @Update
    fun update(purchase: Purchase)
}