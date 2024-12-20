package com.kirillzybin.reminders

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class Notification_RecyclerView(var notific: MutableList<Notification>): RecyclerView.Adapter<Notification_RecyclerView.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val name : TextView
        val description : TextView
        val time : TextView
        val date : TextView
        val root: View = itemView

        init {
            name = itemView.findViewById(R.id.name_rv)
            description = itemView.findViewById(R.id.description_rv)
            time = itemView.findViewById(R.id.time_rv)
            date = itemView.findViewById(R.id.date_rv)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_notification, parent, false)
        val holder = ViewHolder(view)
        return holder
    }

    override fun getItemCount(): Int {
        return notific.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData() {
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        notific.removeAt(position)
        updateData()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateViewHolder(newData: MutableList<Notification>) {
        val oldItems = notific // Сохраняем старый список
        notific.clear() // Очищаем старый список
        notific = oldItems // Заменяем старый список
        notifyDataSetChanged() // Уведомляем адаптер
    }

//    @SuppressLint("NotifyDataSetChanged")
//    fun updateData(newNotifications: List<Notification>) {
//        notific = newNotifications.toMutableList()
//        notifyDataSetChanged()
//    }




    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.name.text = notific[position].name
        holder.description.text = notific[position].description
        holder.time.text = notific[position].time
        holder.date.text = notific[position].date

        if (notific[position].importance == 0) {
            holder.root.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.background_for_0)
        } else if (notific[position].importance == 1) {
            holder.root.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.background_for_1)
        } else if (notific[position].importance == 2) {
            holder.root.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.background_for_2)
        } else if (notific[position].importance == 3) {
            holder.root.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.background_for_3)
        }
    }



}