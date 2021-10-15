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

     var mySingleton=MySingleton()
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
        if (mySingleton.arrayListOfBundlesOfDocuments!!.size != 0) {
            var pageCount = 0
            var remember = 0
            for (x in 0 until mySingleton.arrayListOfBundlesOfDocuments!![position]!!.documentFormatField.size)
                if (mySingleton.arrayListOfBundlesOfDocuments!![position]!!.documentFormatField[x] != null) {
                    pageCount++
                    remember = x
                }
            holder.documentFormatField?.text =
                mySingleton.arrayListOfBundlesOfDocuments!![position]!!.documentFormatField[remember]!!.split(
                    ","
                )[0] + ", стр. " + pageCount + " из " + mySingleton.arrayListOfBundlesOfDocuments!![position]!!.documentFormatField.size
            holder.numberOfOrderField?.text =
                mySingleton.arrayListOfBundlesOfDocuments!![position]!!.numberOfOrderField.toString()
            holder.day.text = mySingleton.arrayListOfBundlesOfDocuments!![position]!!.day[remember]
            holder.time.text =
                mySingleton.arrayListOfBundlesOfDocuments!![position]!!.time[remember]
            Log.d(
                "MyLog",
                mySingleton.arrayListOfBundlesOfDocuments!![position]!!.status[0].toString()
            )
            var check = false
            for (x in 0 until mySingleton.arrayListOfBundlesOfDocuments!![position]!!.day.size)
                if (mySingleton.arrayListOfBundlesOfDocuments!![position]!!.day[x] == null) {
                    holder.status.setImageResource(R.drawable.ic_uncomplete)
                    check = true
                    break
                }
            if (!check)
                if (mySingleton.arrayListOfBundlesOfDocuments!![position]!!.status[0] == "no") {
                    holder.status.setImageResource(R.drawable.history_status_no)

                } else if (mySingleton.arrayListOfBundlesOfDocuments!![position]!!.status[0] == "yes") {
                    Log.d(
                        "MyLog",
                        "status" + mySingleton.arrayListOfBundlesOfDocuments!![position]!!.status[0].toString()
                    )
                    holder.status.setImageResource(R.drawable.ic_submite)
                }

        }
    }

    override fun getItemCount(): Int {

        if(mySingleton.arrayListOfBundlesOfDocuments==null)
            return 0
        return mySingleton.arrayListOfBundlesOfDocuments!!.size

    }

    interface OnItemListener {
        fun onItemClick(position: Int) {

        }
    }

}