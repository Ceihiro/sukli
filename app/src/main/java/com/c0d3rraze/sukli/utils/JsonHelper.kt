package com.c0d3rraze.sukli.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.c0d3rraze.sukli.data.model.Customer
import com.c0d3rraze.sukli.data.model.Item

object JsonHelper {
    private val gson = Gson()

    fun loadItems(context: Context): List<Item> {
        val json = context.assets.open("items.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Item>>() {}.type
        return gson.fromJson(json, type)
    }

    fun loadCustomers(context: Context): List<Customer> {
        val json = context.assets.open("customers.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Customer>>() {}.type
        return gson.fromJson(json, type)
    }
}