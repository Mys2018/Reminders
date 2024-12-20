package com.kirillzybin.reminders

object NotificationRepository {
    var items = mutableListOf<Notification>()
    var items_complited = mutableListOf<Notification>()

    fun addNotification(notification: Notification) {
        items.add(notification)
        sortItems_time()
    }

    fun sortItems_time() {
        items.sortBy {
            val dateParts = it.date.split(".")
            val timeParts = it.time.split(":")
            dateParts[2] + dateParts[1] + dateParts[0] + timeParts[0] + timeParts[1]
        }
    }
    fun sortItems_importance() {
        items.sortBy { it.importance }
    }
}