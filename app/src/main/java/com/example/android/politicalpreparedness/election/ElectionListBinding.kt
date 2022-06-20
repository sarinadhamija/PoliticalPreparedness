package com.example.android.politicalpreparedness.election

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.data.remote.models.Election
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter

/**
 * [BindingAdapter]s for the [Task]s list.
 */
@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<Election>?) {
    items?.let {
        (listView.adapter as ElectionListAdapter).submitList(items)
    }
}