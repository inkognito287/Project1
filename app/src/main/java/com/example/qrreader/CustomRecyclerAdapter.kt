package com.example.qrreader

import android.content.SharedPreferences
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomRecyclerAdapter(private val names1: List<String>,private val names2: List<String>,private val text: String) :
    RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>(){
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var largeTextView: TextView? = null
        var smallTextView: TextView? = null
        var image: ImageView?=null

        init {
            largeTextView = itemView.findViewById(R.id.txt_name)
            smallTextView = itemView.findViewById(R.id.txt_number)
            image= itemView.findViewById(R.id.imageViewStatus)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.history_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.largeTextView?.text=names1[position]
        holder.smallTextView?.text=text
        holder.image?.setImageURI(Uri.parse(text))
    }

    override fun getItemCount()= names1.size

}