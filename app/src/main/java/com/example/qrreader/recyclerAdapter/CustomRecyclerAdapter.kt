package com.example.qrreader.recyclerAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qrreader.R
import com.example.qrreader.singletones.MySingleton

class CustomRecyclerAdapter(
    var context: Context, itemListener: OnItemListener
) :  RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>() {


    private var mItemListener: OnItemListener = itemListener

    class MyViewHolder(itemView: View, onItemListener: OnItemListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var documentFormatField: TextView? = null
        var numberOfOrderField: TextView? = null
        var image: ImageView? = null
        var time: TextView
        var day: TextView
        var status: ImageView

        var onItemListener: OnItemListener = onItemListener

        init {

            itemView.setOnClickListener(this)
            documentFormatField = itemView.findViewById(R.id.txt_name)
            numberOfOrderField = itemView.findViewById(R.id.txt_number)
            time = itemView.findViewById(R.id.textViewDateTime)
            day = itemView.findViewById(R.id.textViewDateDay)
            status = itemView.findViewById(R.id.imageViewStatus)

        }

        override fun onClick(v: View?) {
            onItemListener.onItemClick(adapterPosition)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.history_item, parent, false)
        return MyViewHolder(itemView, mItemListener)
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (MySingleton.arrayListOfBundlesOfDocuments!!.size != 0) {
            var pageCount = 0
            var remember = 0
            for (x in 0 until MySingleton.arrayListOfBundlesOfDocuments!![position]!!.documentFormatField.size)
                if (MySingleton.arrayListOfBundlesOfDocuments!![position]!!.documentFormatField[x] != null) {
                    pageCount++
                    remember = x
                }
            holder.documentFormatField?.text =
                MySingleton.arrayListOfBundlesOfDocuments!![position]!!.documentFormatField[remember]!!.split(
                    ","
                )[0] + ", стр. " + pageCount + " из " + MySingleton.arrayListOfBundlesOfDocuments!![position]!!.documentFormatField.size
            holder.numberOfOrderField?.text =
                MySingleton.arrayListOfBundlesOfDocuments!![position]!!.numberOfOrderField.toString()
            holder.day.text = MySingleton.arrayListOfBundlesOfDocuments!![position]!!.day[remember]
            holder.time.text =
                MySingleton.arrayListOfBundlesOfDocuments!![position]!!.time[remember]
            Log.d(
                "MyLog",
                MySingleton.arrayListOfBundlesOfDocuments!![position]!!.status[0].toString()
            )
            var check = false
            for (x in 0 until MySingleton.arrayListOfBundlesOfDocuments!![position]!!.day.size)
                if (MySingleton.arrayListOfBundlesOfDocuments!![position]!!.day[x] == null) {
                    holder.status.setImageResource(R.drawable.ic_uncomplete)
                    check = true
                    break
                }
            if (!check)
                if (MySingleton.arrayListOfBundlesOfDocuments!![position]!!.status[0] == "no") {
                    holder.status.setImageResource(R.drawable.history_status_no)

                } else if (MySingleton.arrayListOfBundlesOfDocuments!![position]!!.status[0] == "yes") {
                    Log.d(
                        "MyLog",
                        "status" + MySingleton.arrayListOfBundlesOfDocuments!![position]!!.status[0].toString()
                    )
                    holder.status.setImageResource(R.drawable.ic_submite)
                }

        }
    }

    override fun getItemCount(): Int {


        return MySingleton.arrayListOfBundlesOfDocuments!!.size

    }

    interface OnItemListener {
        fun onItemClick(position: Int) {

        }
    }

}