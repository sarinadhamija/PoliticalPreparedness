package com.example.android.politicalpreparedness.representative.adapter

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.data.remote.models.Channel
import com.example.android.politicalpreparedness.data.remote.models.Official
import com.example.android.politicalpreparedness.databinding.LayoutMyRepresentativeItemBinding
import com.example.android.politicalpreparedness.representative.RepresentativeViewModel
import com.example.android.politicalpreparedness.representative.model.Representative

class RepresentativeListAdapter(private val viewModel: RepresentativeViewModel): ListAdapter<Official, RepresentativeViewHolder>(RepresentativeDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepresentativeViewHolder {
        return RepresentativeViewHolder.from(parent, viewModel)
    }

    override fun onBindViewHolder(holder: RepresentativeViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class RepresentativeViewHolder(private val binding: LayoutMyRepresentativeItemBinding, private val viewModel: RepresentativeViewModel): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Official) {
        binding.representative = item
        binding.viewModel = viewModel
        if (!item.channels.isNullOrEmpty()){
            binding.representativeFacebookImage.visibility = View.VISIBLE
            binding.representativeTwitterImage.visibility = View.VISIBLE
            showSocialLinks(item.channels)
        } else {
            binding.representativeFacebookImage.visibility = View.GONE
            binding.representativeTwitterImage.visibility = View.GONE
        }

        if (!item.urls.isNullOrEmpty()){
            binding.representativeWebsiteImage.visibility = View.GONE
            showWWWLinks(urls = item.urls)
        } else {
            binding.representativeWebsiteImage.visibility = View.GONE
        }

        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup, viewModel : RepresentativeViewModel): RepresentativeViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = LayoutMyRepresentativeItemBinding.inflate(layoutInflater, parent, false)

            return RepresentativeViewHolder(binding, viewModel)
        }
    }

    private fun showSocialLinks(channels: List<Channel>) {
        val facebookUrl = getFacebookUrl(channels)
        if (!facebookUrl.isNullOrBlank()) { enableLink(binding.representativeFacebookImage, facebookUrl) }

        val twitterUrl = getTwitterUrl(channels)
        if (!twitterUrl.isNullOrBlank()) { enableLink(binding.representativeTwitterImage, twitterUrl) }
    }

    private fun showWWWLinks(urls: List<String>) {
        enableLink(binding.representativeWebsiteImage, urls.first())
    }

    private fun getFacebookUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Facebook" }
                .map { channel -> "https://www.facebook.com/${channel.id}" }
                .firstOrNull()
    }

    private fun getTwitterUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Twitter" }
                .map { channel -> "https://www.twitter.com/${channel.id}" }
                .firstOrNull()
    }

    private fun enableLink(view: ImageView, url: String) {
        view.visibility = View.VISIBLE
        view.setOnClickListener { setIntent(url) }
    }

    private fun setIntent(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(ACTION_VIEW, uri)
        itemView.context.startActivity(intent)
    }

}

class RepresentativeDiffCallback : DiffUtil.ItemCallback<Official>() {
    override fun areItemsTheSame(oldItem: Official, newItem: Official): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Official, newItem: Official): Boolean {
        return oldItem == newItem
    }
}