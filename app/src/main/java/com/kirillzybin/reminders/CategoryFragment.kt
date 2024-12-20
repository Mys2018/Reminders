package com.kirillzybin.reminders

import SpaceItemDecoration
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kirillzybin.reminders.NotificationRepository.items

class CategoryFragment : Fragment(R.layout.category_fragment) {

    val adapter = Notification_RecyclerView(items)

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val importance_0 = view.findViewById<RecyclerView>(R.id.rec_0)
        importance_0.addItemDecoration(SpaceItemDecoration(5))

        importance_0.adapter = adapter
        importance_0.setLayoutManager(LinearLayoutManager(requireContext()))


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
                items.removeAt(pos)
                adapter.notifyItemRemoved(pos)
            }
        }

        val swipeHelper = ItemTouchHelper(swipeCallback)
        swipeHelper.attachToRecyclerView(importance_0)

        (activity as? MainActivity)?.setAdapterCategory(adapter)

        NotificationRepository.sortItems_importance()
        adapter.updateData()
    }
}
