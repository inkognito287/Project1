package com.example.qrreader

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qrreader.Pojo.DocumentsItem
import java.text.SimpleDateFormat

class CustomRecyclerAdapter(
    var names1: ArrayList<DocumentsItem>,
) :
    RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>() {
 class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var largeTextView: TextView? = null
        var smallTextView: TextView? = null
        var image: ImageView? = null
        var time:TextView
        var day:TextView
        init {
            largeTextView = itemView.findViewById(R.id.txt_name)
            smallTextView = itemView.findViewById(R.id.txt_number)
            image = itemView.findViewById(R.id.imageViewStatus)
            time=itemView.findViewById(R.id.textViewDateTime)
            day=itemView.findViewById(R.id.textViewDateDay)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.history_item, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var dateFormatTime = SimpleDateFormat(" HH:mm");
        var dateFormatDay = SimpleDateFormat("MM/dd/yyyy");
        holder.largeTextView?.text = names1?.get(position)?.code.toString()
        holder.smallTextView?.text = names1?.get(position)?.date.toString()
        holder.time.text= dateFormatTime.parse(names1?.get(position)?.date.toString()).toString()
        holder.day.text= dateFormatDay.parse(names1?.get(position)?.date.toString()).toString()


    //holder.image?.setImageURI(Uri.parse(text))
    }

    override fun getItemCount(): Int {

        return  names1!!.size
    }

    fun clearRecyclerView(){
        names1.clear()
        notifyDataSetChanged()
    }
    fun updateRecyclerView(names1: ArrayList<DocumentsItem>){
        this.names1=names1
        notifyDataSetChanged()
    }
}