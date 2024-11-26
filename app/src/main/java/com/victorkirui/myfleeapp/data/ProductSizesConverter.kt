package com.victorkirui.myfleeapp.data

import androidx.room.TypeConverter

class ProductSizesConverter {

    @TypeConverter
    fun fromListToString(sizes: List<String>): String {
        return sizes.joinToString(",") // Convert list to a single comma-separated string
    }

    @TypeConverter
    fun fromStringToList(data: String): List<String> {
        return data.split(",").map { it.trim() } // Convert string back to list
    }
}
