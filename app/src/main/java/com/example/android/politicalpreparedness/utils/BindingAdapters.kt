package com.example.android.politicalpreparedness.utils

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.remote.models.Election
import com.example.android.politicalpreparedness.data.remote.models.Official
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.model.Representative
import com.squareup.picasso.Picasso

@BindingAdapter("visibilityStatus")
fun bindStatus(progressBar: ProgressBar, status: Boolean) {
    progressBar.visibility = if (status) View.VISIBLE else View.GONE
}

/**
 * [BindingAdapter]s for the [Task]s list.
 */
@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<Election>?) {
    items?.let {
        (listView.adapter as ElectionListAdapter).submitList(items)
    }
}

@BindingAdapter("app:representatives")
fun setRepresentatives(listView: RecyclerView, items: List<Representative>?) {
    items?.let {
        (listView.adapter as RepresentativeListAdapter).submitList(items)
    }
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Picasso
            .with(imgView.context)
            .load(imgUri)
            .error(ContextCompat.getDrawable(imgView.context, R.drawable.ic_profile))
            .into(imgView)

    }
}
