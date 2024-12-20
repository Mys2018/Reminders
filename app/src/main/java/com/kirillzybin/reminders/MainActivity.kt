package com.kirillzybin.reminders

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kirillzybin.reminders.NotificationRepository.items
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.icu.util.Calendar
import android.util.Log
import android.widget.Toast
import com.kirillzybin.reminders.NotificationRepository.items_complited

class MainActivity : AppCompatActivity(), SupplementDialog.DataListener {

    private val channel0ID = "channel_for_0"
    private val channel1ID = "channel_for_1"
    private val channel2ID = "channel_for_2"
    private val notificationCode = 1

    companion object{
        var notificationAdapterTotal: Notification_RecyclerView? = null
        var notificationAdapterCategory: Notification_RecyclerView? = null
        var notificationAdapterCompleted: Notification_RecyclerView? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        items = FileUtils.loadNotifications_active(this).toMutableList()
        items_complited = FileUtils.loadNotifications_completed(this).toMutableList()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val existingChannel0 = notificationManager.getNotificationChannel(channel0ID)
        val existingChannel1 = notificationManager.getNotificationChannel(channel1ID)
        val existingChannel2 = notificationManager.getNotificationChannel(channel2ID)
        if (existingChannel0 == null)
            createChannel0()
        if (existingChannel1 == null)
            createChannel1()
        if (existingChannel2 == null)
            createChannel2()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                notificationCode
            )
        }


        val addButton = findViewById<FloatingActionButton>(R.id.add_button)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainerView, TotalFragment())
                .commit()
        }

        val navBar = findViewById<BottomNavigationView>(R.id.bottomNav)
        navBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.total -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView, TotalFragment())
                        .commit()
                    true
                }

                R.id.сategory -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView, CategoryFragment())
                        .commit()
                    true
                }

                R.id.info -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView, CompletedFragment())
                        .commit()
                    true
                }

                else -> false
            }
        }

        addButton.setOnClickListener {
            val dialog = SupplementDialog()
            dialog.show(supportFragmentManager, "my_dialog")
        }
    }

    override fun onStop() {
        FileUtils.saveNotifications_active(this, items)
        FileUtils.saveNotifications_completed(this, items_complited)
        super.onStop()
    }

    fun setAdapterTotal(adapter: Notification_RecyclerView) {
        notificationAdapterTotal = adapter
    }
    fun setAdapterCategory(adapter: Notification_RecyclerView) {
        notificationAdapterTotal = adapter
    }
    fun setAdapterCompleted(adapter: Notification_RecyclerView) {
        notificationAdapterCompleted = adapter
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onDialogData(
        name: String,
        description: String,
        importance: Int,
        time: String,
        date: String
    ) {
        NotificationRepository.addNotification(
            Notification(
                name,
                description,
                importance,
                time,
                date
            )
        )
        notificationAdapterTotal?.notifyDataSetChanged()
        notificationAdapterCategory?.notifyDataSetChanged()
        notificationAdapterCompleted?.notifyDataSetChanged()
        setNotification(this, name, description, importance, time, date)
        FileUtils.saveNotifications_active(this, items)
        FileUtils.saveNotifications_completed(this, items_complited)
        Toast.makeText(this, "Уведомление \"$name\" добавлено", Toast.LENGTH_SHORT).show()
    }

    private fun setNotification(
        context: Context,
        name: String,
        description: String,
        importance: Int,
        time: String,
        date: String
    ) {
        val dateParts = date.split(".")
        val timeParts = time.split(":")
        val year = dateParts[2].toInt()
        val month = dateParts[1].toInt() - 1
        val day = dateParts[0].toInt()
        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("NAME", name)
            putExtra("DESCRIPTION", description)
            putExtra("IMPORTANCE", importance)
        }
        val requestCode =
            System.currentTimeMillis().toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val calendar: Calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
    private fun createChannel0() {
        val name = getString(R.string.channel_name_0)
        val descriptionText = getString(R.string.channel_description_0)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(channel0ID, name, importance).apply {
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500, 200, 500)
        }
        mChannel.description = descriptionText
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
    private fun createChannel1() {
        val name = getString(R.string.channel_name_1)
        val descriptionText = getString(R.string.channel_description_1)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(channel1ID, name, importance)
        mChannel.description = descriptionText
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
    private fun createChannel2() {
        val name = getString(R.string.channel_name_2)
        val descriptionText = getString(R.string.channel_description_2)
        val importance = NotificationManager.IMPORTANCE_LOW
        val mChannel = NotificationChannel(channel2ID, name, importance)
        mChannel.description = descriptionText
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
}

class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("NotifyDataSetChanged")
    override fun onReceive(context: Context, intent: Intent) {
        val name = intent.getStringExtra("NAME") ?: "Default Title"
        val description = intent.getStringExtra("DESCRIPTION") ?: "Default Message"
        val importance = intent.getIntExtra("IMPORTANCE", 0)


        FileUtils.saveNotifications_active(context, items)
        sendNotification(context, name, description, importance)

        val notificationToRemove = items.firstOrNull {
            it.name == name && it.description == description && it.importance == importance
        }
        if (notificationToRemove != null) {
            items.remove(notificationToRemove)
            val updatedNotification = notificationToRemove.copy(importance = 3)
            items_complited.add(updatedNotification)
            MainActivity.notificationAdapterTotal?.updateData()
            MainActivity.notificationAdapterCategory?.updateData()
            MainActivity.notificationAdapterCompleted?.updateData()




        }
        Log.d("Save list 1", items.toString())
        FileUtils.saveNotifications_active(context, items)
        Log.d("Save list 2", items_complited.toString())
        FileUtils.saveNotifications_completed(context, items_complited)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun sendNotification(
        context: Context,
        name: String,
        description: String,
        importance: Int
    ) {

        val channelId = "channel_for_$importance"

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, channelId)
        builder
            .setSmallIcon(R.drawable.add)
            .setContentTitle(name)
            .setContentText(description)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle())

        val uniqueId = System.currentTimeMillis().toInt()
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            notify(uniqueId, builder.build())
        }

    }

}
