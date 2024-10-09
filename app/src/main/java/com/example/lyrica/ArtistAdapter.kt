package com.example.lyrica

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ArtistAdapter(private var artistList: List<Artist>) : RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {

    inner class ArtistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.text_artist_name)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.text_artist_description)
        private val locationTextView: TextView = itemView.findViewById(R.id.text_artist_location)
        private val imageView: ImageView = itemView.findViewById(R.id.image_artist) // ImageView for artist image

        fun bind(artist: Artist) {
            nameTextView.text = artist.artist
            descriptionTextView.text = artist.desc
            locationTextView.text = artist.location

            // Function to convert gs:// URL to HTTP URL
            fun getHttpUrl(gsUrl: String): String {
                val path = gsUrl.substringAfter("gs://login-register-ded34.appspot.com/") // Change to your bucket name
                return "https://firebasestorage.googleapis.com/v0/b/login-register-ded34.appspot.com/o/${Uri.encode(path)}?alt=media"
            }


            val httpUrl = getHttpUrl(artist.image) // Get the HTTP URL
            Log.d("ArtistAdapter", "Loading image from URL: $httpUrl")

            // Use Glide to load the image
            Glide.with(itemView.context)
                .load(httpUrl) // Use the correct httpUrl
                .into(imageView) // Load image into ImageView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.artist_item, parent, false)
        return ArtistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.bind(artistList[position]) // Bind the data to the ViewHolder
    }

    override fun getItemCount(): Int = artistList.size // Return the size of the artist list

    fun updateData(newArtistList: List<Artist>) {
        artistList = newArtistList // Update the artist list
        notifyDataSetChanged() // Notify the adapter of data changes
    }
}
