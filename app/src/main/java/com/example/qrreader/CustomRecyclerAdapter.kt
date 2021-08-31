package com.example.qrreader

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.qrreader.interfaces.UpdateAdapter
import com.example.qrreader.model.ItemForHistory
import com.example.qrreader.singletones.MySingleton
import java.io.OutputStreamWriter

class CustomRecyclerAdapter(
    var context: Context, itemListener: OnItemListener
) : UpdateAdapter, RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>() {


    private var mItemListener: OnItemListener = itemListener
    var myFunctions: Functions = Functions(context.applicationContext)

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

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if( MySingleton.arrayList!!.size!=0) {
            var pageCount = 0
            var remember = 0
            for (x in 0 until MySingleton.arrayList!![position]!!.documentFormatField.size)
                if (MySingleton.arrayList!![position]!!.documentFormatField[x] != null) {
                    pageCount++
                    remember = x
                }
            holder.documentFormatField?.text =
                MySingleton.arrayList!![position]!!.documentFormatField[remember]!!.split(
                    ","
                )[0] + ", стр. " + pageCount + " из " + MySingleton.arrayList!![position]!!.documentFormatField.size
            holder.numberOfOrderField?.text =
                MySingleton.arrayList!![position]!!.numberOfOrderField.toString()
            holder.day.text = MySingleton.arrayList!![position]!!.day[remember]
            holder.time.text = MySingleton.arrayList!![position]!!.time[remember]
            Log.d("MyLog",MySingleton.arrayList!![position]!!.status[0].toString())
            var check=false
            for (x in 0 until MySingleton.arrayList!![position]!!.day.size)
            if (MySingleton.arrayList!![position]!!.day[x]==null) {
                holder.status.setImageResource(R.drawable.ic_uncomplete)
                check=true
                break
            }
            if(!check)
                if (MySingleton.arrayList!![position]!!.status[0] == "no") {
                    holder.status.setImageResource(R.drawable.history_status_no)

                } else if (MySingleton.arrayList!![position]!!.status[0] == "yes"){
                    Log.d("MyLog","status" + MySingleton.arrayList!![position]!!.status[0].toString())
                    holder.status.setImageResource(R.drawable.ic_submite)
                }

        }
    }

    override fun getItemCount(): Int {


        return MySingleton.arrayList!!.size

    }

    override fun update() {
        (context as AppCompatActivity).runOnUiThread() {
            notifyDataSetChanged()

        }


    }

    override fun clear() {

        try {
            val outputStreamWriter = OutputStreamWriter(
                context.openFileOutput(
                    "single.json",
                    AppCompatActivity.MODE_PRIVATE
                )
            )
            MySingleton.arrayList?.clear()
            outputStreamWriter.write("")
            outputStreamWriter.close()
            notifyDataSetChanged()
        } catch (e: Exception) {
        }

    }


    interface OnItemListener {
        fun onItemClick(position: Int) {

        }
    }

}