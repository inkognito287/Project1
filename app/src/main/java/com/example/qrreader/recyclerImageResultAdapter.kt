package com.example.qrreader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class recyclerImageResultAdapter(var Number:Int,itemListener: OnItemListener): RecyclerView.Adapter<recyclerImageResultAdapter.MyViewHolder>() {

    private var mItemListener: OnItemListener = itemListener
    class  MyViewHolder(itemView: View,onItemListener: OnItemListener): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var numberOfPageTextView: TextView? = null
        var onItemListener: OnItemListener = onItemListener

    init {
        itemView.setOnClickListener(this)
        numberOfPageTextView   =   itemView.findViewById(R.id.numberOfPageTextView)
    }

        override fun onClick(p0: View?) {
            onItemListener.onItemClick(adapterPosition)
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_image_item, parent, false)
        return MyViewHolder(itemView,mItemListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.numberOfPageTextView?.text = (position+1).toString()

    }

    override fun getItemCount(): Int {
      return Number
    }

    interface OnItemListener {
        fun onItemClick(position: Int) {

        }
    }


}