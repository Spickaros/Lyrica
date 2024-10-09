package com.example.lyrica

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Lyrics(val lyrics: String = "", val timestamp: Long = 0)

class LyricsAdapter(private val lyricsList: List<Lyrics>) :
    RecyclerView.Adapter<LyricsAdapter.LyricsViewHolder>() {

    class LyricsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lyricsTextView: TextView = view.findViewById(R.id.lyrics_text_view)
        val timestampTextView: TextView = view.findViewById(R.id.timestamp_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LyricsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lyrics_item, parent, false)
        return LyricsViewHolder(view)
    }

    override fun onBindViewHolder(holder: LyricsViewHolder, position: Int) {
        val lyrics = lyricsList[position]
        holder.lyricsTextView.text = lyrics.lyrics

        // Format and display timestamp
        val formattedTimestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date(lyrics.timestamp))
        holder.timestampTextView.text = formattedTimestamp
    }

    override fun getItemCount(): Int {
        return lyricsList.size
    }
}
