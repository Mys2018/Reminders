package com.kirillzybin.reminders

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object FileUtils {
    private const val FILE_NAME = "notifications.json"
    private const val FILE_NAME_COMPLETED = "complited.json"

    fun saveNotifications_active(context: Context, notifications: List<Notification>) {
        val gson = Gson()
        val jsonString = gson.toJson(notifications)
        val file = File(context.filesDir, FILE_NAME)
        file.writeText(jsonString)
    }

    fun loadNotifications_active(context: Context): List<Notification> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return emptyList()

        val jsonString = file.readText()
        val gson = Gson()
        val type = object : TypeToken<List<Notification>>() {}.type
        return gson.fromJson(jsonString, type)
    }

    fun saveNotifications_completed(context: Context, notifications: List<Notification>) {
        val gson = Gson()
        val jsonString = gson.toJson(notifications)
        val file = File(context.filesDir, FILE_NAME_COMPLETED)
        file.writeText(jsonString)
    }

    fun loadNotifications_completed(context: Context): List<Notification> {
        val file = File(context.filesDir, FILE_NAME_COMPLETED)
        if (!file.exists()) return emptyList()

        val jsonString = file.readText()
        val gson = Gson()
        val type = object : TypeToken<List<Notification>>() {}.type
        return gson.fromJson(jsonString, type)
    }
}