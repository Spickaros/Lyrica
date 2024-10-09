package com.example.lyrica

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class FileAdapter(context: Context, private val items: List<FileConversionResponseItem>) :
    ArrayAdapter<FileConversionResponseItem>(context, R.layout.file_item, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.file_item, parent, false)
        val item = getItem(position)

        val fileNameView = view.findViewById<TextView>(R.id.fileNameTextView)
        val vocalsUrlView = view.findViewById<TextView>(R.id.vocalsUrlTextView)
        val instrumentalsUrlView = view.findViewById<TextView>(R.id.instrumentalsUrlTextView)
        val sourceUrlView = view.findViewById<TextView>(R.id.sourceUrlTextView)

        fileNameView.text = item?.fileName ?: "Unknown"
        vocalsUrlView.text = item?.output?.vocals?.url ?: "No Vocals URL"
        instrumentalsUrlView.text = item?.output?.instrumentals?.url ?: "No Instrumentals URL"
        sourceUrlView.text = item?.output?.source?.url ?: "No Source URL"

        return view
    }
}
