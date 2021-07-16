package com.novelijk.numberlog.data

import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface DAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dataPoint: DataPoint)

    @Delete
    suspend fun delete(dataPoint: DataPoint)

    @Query("DELETE from data_table")
    suspend fun deleteAll()

    @Query("SELECT * from data_table order by id DESC")
    fun getHistoryPagingSource(): PagingSource<Int, DataPoint>

    @Query("SELECT COUNT(id) from data_table")
    suspend fun getValueCount(): Int

    @Query("SELECT * from data_table order by id DESC")
    suspend fun getHistory(): List<DataPoint>
}