package com.kirillzybin.reminders

import SpaceItemDecoration
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kirillzybin.reminders.NotificationRepository.items_complited

class CompletedFragment : Fragment(R.layout.completed_fragment) {

    val adapter = Notification_RecyclerView(items_complited)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val completedFragment = view.findViewById<RecyclerView>(R.id.comp_rv)
        completedFragment.addItemDecoration(SpaceItemDecoration(5))

        completedFragment.adapter = adapter
        completedFragment.setLayoutManager(LinearLayoutManager(requireContext()))

        val swipeCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT + ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                items_complited.removeAt(pos)
                adapter.notifyItemRemoved(pos)
            }
        }

        val swipeHelper = ItemTouchHelper(swipeCallback)
        swipeHelper.attachToRecyclerView(completedFragment)

        (activity as? MainActivity)?.setAdapterCompleted(adapter)

        NotificationRepository.sortItems_time()
        adapter.updateData()
    }
}