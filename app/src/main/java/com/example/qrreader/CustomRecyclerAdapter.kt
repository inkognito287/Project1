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
import com.example.qrreader.Interfaces.UpdateAdapter
import com.example.qrreader.Pojo.DocumentsItem
import com.example.qrreader.Pojo.Response
import com.example.qrreader.fragment.array
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import kotlin.collections.ArrayList

class CustomRecyclerAdapter(
    var names1: ArrayList<DocumentsItem>, var context: Context, var itemListener: OnItemListener
) : UpdateAdapter, RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>() {

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

    //E MMM dd HH:mm:ss z yyyy
    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.documentFormatField?.text = array[position].documentFormatField .toString()
        holder.numberOfOrderField?.text = array[position].numberOfOrderField .toString()
        holder.day.text = array[position].day
        holder.time.text = array[position].time
        if (array[position].status == "no") {
            holder.status.setImageResource(R.drawable.history_status_no)

        } else holder.status.setImageResource(R.drawable.submitted)


    }

    override fun getItemCount(): Int {

        return names1.size
    }

    fun clearRecyclerView() {
        names1.clear()
        notifyDataSetChanged()
    }

    fun updateRecyclerView(names1:  ArrayList<DocumentsItem>) {
        this.names1 = names1
        notifyDataSetChanged()
    }

    fun kek() {
    }

    override fun update() {
        // Thread(){

        val gson = Gson()
        val result = gson.fromJson(readToFile(context), Response::class.java)

        names1.clear()
        for (element in result.documents!!)
            names1.add(element!!)
        // (context as AppCompatActivity).runOnUiThread(){
        notifyDataSetChanged()
        //}
        // }


    }

    override fun clear() {
        //names1.clear()
        try {
            val outputStreamWriter = OutputStreamWriter(
                context.openFileOutput(
                    "single.json",
                    AppCompatActivity.MODE_PRIVATE
                )
            )
            outputStreamWriter.write("")
            outputStreamWriter.close()
        } catch (e: Exception) {
        }

    }

    private fun readToFile(context: Context?): String {

        return try {
            val reader =
                BufferedReader(InputStreamReader(context?.openFileInput("single.json")))
            val text = reader.readText()
            reader.close()
            text
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
            "ERROR"
        }

    }

    interface OnItemListener {
        fun onItemClick(position: Int) {

        }
    }

}