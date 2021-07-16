package com.novelijk.numberlog.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DateFormat

@Entity(tableName = "data_table")
data class DataPoint(
    @ColumnInfo(name = "value") val value: Float,
    @ColumnInfo(name = "time") val time: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Int = 0
) {
    val timeFormattedToDisplay: String
        get() = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM).format(time)
    val timeFormattedToExport: String
        get() = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(time)
    val dateFormattedToExport: String
        get() = DateFormat.getDateInstance(DateFormat.SHORT).format(time)


}