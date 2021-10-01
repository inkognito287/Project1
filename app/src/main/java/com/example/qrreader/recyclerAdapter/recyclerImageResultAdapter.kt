package com.example.qrreader.recyclerAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qrreader.R
import com.example.qrreader.singletones.MySingleton

class recyclerImageResultAdapter(var Number:Int,var currentPage:Int,itemListener: OnItemListener): RecyclerView.Adapter<recyclerImageResultAdapter.MyViewHolder>() {

    private var mItemListener: OnItemListener = itemListener
    class  MyViewHolder(itemView: View, var onItemListener: OnItemListener): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var numberOfPageTextView: TextView? = null
        var background:View? = null

    init {
        itemView.setOnClickListener(this)
        numberOfPageTextView   =   itemView.findViewById(R.id.numberOfPageTextView)
        background = itemView.findViewById(R.id.view12)
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

        if (MySingleton.completedPages[position]){

            holder.background?.setBackgroundResource(R.drawable.ic_complete_image)
            holder.numberOfPageTextView?.text=""
            holder.numberOfPageTextView?.text = (position+1).toString()
        }
        else {
            holder.background?.setBackgroundResource(R.drawable.ic_uncomplete_image)
            holder.numberOfPageTextView?.text = (position + 1).toString()
        }
    }

    override fun getItemCount(): Int {
      return Number
    }

    interface OnItemListener {
        fun onItemClick(position: Int) {

        }
    }


}