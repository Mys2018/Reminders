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

class MainActivity : AppCompatActivity(), SupplementDialog.DataListener {

    private val channel0ID = "channel_for_0"
    private val channel1ID = "channel_for_1"
    private val channel2ID = "channel_for_2"
    private val notificationCode = 1

    var notificationAdapter: Notification_RecyclerView? = null

    var categoryFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as? CategoryFragment



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        items = FileUtils.loadNotifications(this).toMutableList()

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
                        .replace(R.id.fragmentContainerView, InfoFragment())
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
        FileUtils.saveNotifications(this, items)
        super.onStop()
    }

    fun setAdapter(adapter: Notification_RecyclerView) {
        this.notificationAdapter = adapter
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


        val categoryFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as? CategoryFragment
        categoryFragment?.adapter_important?.updateData()
        notificationAdapter?.notifyDataSetChanged()
        setNotification(this, name, description, importance, time, date)
        FileUtils.saveNotifications(this, items)
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


        FileUtils.saveNotifications(context, items)
        sendNotification(context, name, description, importance)

        val notificationToRemove = items.firstOrNull {
            it.name == name && it.description == description && it.importance == importance
        }
        if (notificationToRemove != null) {
            val index = items.indexOf(notificationToRemove)
            (context as? MainActivity)?.notificationAdapter?.removeItem(index)
            (context as? MainActivity)?.notificationAdapter?.notifyDataSetChanged()
            items.remove(notificationToRemove)
        }
        FileUtils.saveNotifications(context, items)
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
