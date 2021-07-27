package com.example.qrreader

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.qrreader.Fragment.HistoryItem
import com.example.qrreader.Pojo.DocumentsItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CustomRecyclerAdapter(
    var names1: ArrayList<DocumentsItem>, var context:Context
) :
    RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener{
        var largeTextView: TextView? = null
        var smallTextView: TextView? = null
        var image: ImageView? = null
        var time: TextView
        var day: TextView
        init {
            itemView.setOnClickListener(this)
            largeTextView = itemView.findViewById(R.id.txt_name)
            smallTextView = itemView.findViewById(R.id.txt_number)
            image = itemView.findViewById(R.id.imageViewStatus)
            time = itemView.findViewById(R.id.textViewDateTime)
            day = itemView.findViewById(R.id.textViewDateDay)
        }

        override fun onClick(v: View?) {
            TODO("Not yet implemented")
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.history_item, parent, false)
        return MyViewHolder(itemView)
    }

    //E MMM dd HH:mm:ss z yyyy
    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.largeTextView?.text = names1?.get(position)?.code.toString()
        holder.smallTextView?.text = names1?.get(position)?.date.toString()
        holder.day.text = names1?.get(position)?.day
        holder.time.text = names1?.get(position)?.time

        holder.itemView.setOnClickListener(){
           var bundle= Bundle()
            bundle.putInt("position",position)
            var fragment= HistoryItem()
            fragment.arguments=bundle

            var myFragmentTransaction = MyFragmentTransaction(context)
            myFragmentTransaction.fragmentTransactionReplace(fragment)
            //(context as AppCompatActivity).supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView,fragment ).commit()

        }


        //holder.image?.setImageURI(Uri.parse(text))
    }

    override fun getItemCount(): Int {

        return names1.size
    }

    fun clearRecyclerView() {
        names1.clear()
        notifyDataSetChanged()
    }

    fun updateRecyclerView(names1: ArrayList<DocumentsItem>) {
        this.names1 = names1
        notifyDataSetChanged()
    }
    fun kek(){
    }
}