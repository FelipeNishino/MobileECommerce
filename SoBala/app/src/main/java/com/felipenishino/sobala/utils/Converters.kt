package com.felipenishino.sobala.utils

import androidx.room.TypeConverter
import com.felipenishino.sobala.model.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


public class Converters {
    @TypeConverter
    open fun stringToProductSet(data: String?): MutableSet<Product?>? {
        if (data == null) {
            return Collections.emptySet()
        }
        var gson = Gson()

        return gson.fromJson(data, object : TypeToken<Set<Product?>?>() {}.type)
    }

    @TypeConverter
    fun productSetToString(someObjects: Set<Product?>?): String? {
        return Gson().toJson(someObjects)
    }

    @TypeConverter
    open fun stringToProductMap(data: String?): MutableMap<Int, Int>? {
        if (data == null) {
            return Collections.emptyMap<Int, Int>()
        }
        var gson = Gson()

        return gson.fromJson(data, object : TypeToken<Map<Int, Int>?>() {}.type)
    }

    @TypeConverter
    fun productMapToString(someObjects: MutableMap<Int, Int>?): String? {
        return Gson().toJson(someObjects)
    }
}