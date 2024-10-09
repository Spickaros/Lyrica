package com.example.lyrica

import SongRecognition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SongRecognitionAdapter(private val songList: List<SongRecognition>) :
    RecyclerView.Adapter<SongRecognitionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songNameTextView: TextView = view.findViewById(R.id.song_name_text_view)
        val artistNameTextView: TextView = view.findViewById(R.id.artist_name_text_view) // Add artist TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_recognition_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Set the song title and artist
        holder.songNameTextView.text = songList[position].title
        holder.artistNameTextView.text = songList[position].artist // Set the artist name
    }

    override fun getItemCount(): Int {
        return songList.size
    }
}
