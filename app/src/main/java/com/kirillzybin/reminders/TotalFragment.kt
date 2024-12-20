package com.kirillzybin.reminders

import SpaceItemDecoration
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kirillzybin.reminders.NotificationRepository.items

class TotalFragment : Fragment(R.layout.total_fragment){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = Notification_RecyclerView(items)

        val recyclerView = view.findViewById<RecyclerView>(R.id.notif_rv)

        recyclerView.adapter = adapter

        recyclerView.setLayoutManager(LinearLayoutManager(requireContext()))

        recyclerView.addItemDecoration(SpaceItemDecoration(5))

        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT + ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                items.removeAt(pos)
                adapter.notifyItemRemoved(pos)
            }
        }
        val swipeHelper = ItemTouchHelper(swipeCallback)
        swipeHelper.attachToRecyclerView(recyclerView)

        (activity as? MainActivity)?.setAdapter(adapter)

        NotificationRepository.sortItems_time()
        adapter.updateData()
    }

}