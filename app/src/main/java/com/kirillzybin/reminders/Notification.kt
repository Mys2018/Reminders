package com.kirillzybin.reminders

data class Notification(
    val name : String,
    val description : String,
    val importance : Int,
    val time: String,
    val date : String
)
