package com.c0d3rraze.sukli.utils

import android.content.Context
import com.c0d3rraze.sukli.data.model.Customer
import com.c0d3rraze.sukli.data.model.Item
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object JsonHelper {
    private val gson = Gson()

    fun loadItems(context: Context): List<Item> {
        return try {
            val json = context.assets.open("items.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<Item>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun loadCustomers(context: Context): List<Customer> {
        return try {
            val json = context.assets.open("customers.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<Customer>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
