package com.example.qrreader

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.qrreader.Interfaces.UpdateAdapter
import com.example.qrreader.fragment.HistoryItem
import com.example.qrreader.Pojo.DocumentsItem
import com.example.qrreader.Pojo.Response
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import kotlin.collections.ArrayList

class CustomRecyclerAdapter(
    var names1: ArrayList<DocumentsItem>, var context:Context
) :  UpdateAdapter,RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener{
        var largeTextView: TextView? = null
        var smallTextView: TextView? = null
        var image: ImageView? = null
        var time: TextView
        var day: TextView
        var status: ImageView
        init {
            itemView.setOnClickListener(this)
            largeTextView = itemView.findViewById(R.id.txt_name)
            smallTextView = itemView.findViewById(R.id.txt_number)
            time = itemView.findViewById(R.id.textViewDateTime)
            day = itemView.findViewById(R.id.textViewDateDay)
            status = itemView.findViewById(R.id.imageViewStatus)
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
        if(names1.get(position).status=="no") {
            holder.status.setImageResource(R.drawable.history_status_no)

        }
        else     holder.status.setImageResource(R.drawable.submitted)

        holder.itemView.setOnClickListener(){
           var bundle= Bundle()
            bundle.putInt("position",position)
            var fragment = HistoryItem()
            fragment.arguments=bundle

            var myFragmentTransaction = MyFragmentTransaction(context)
            myFragmentTransaction.fragmentTransactionReplace(fragment)



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

    override fun update() {
       // Thread(){

         val gson= Gson()
        val result= gson.fromJson(readToFile(context),Response::class.java)

        names1.clear()
        for (x in 0..result.documents!!.size-1)
        names1.add(result.documents!![x]!!)
           // (context as AppCompatActivity).runOnUiThread(){
                notifyDataSetChanged()
            //}
       // }


    }

    override fun clear() {
        try {
            val outputStreamWriter = OutputStreamWriter(
                context?.openFileOutput(
                    "single.json",
                    AppCompatActivity.MODE_PRIVATE
                )
            )
            outputStreamWriter.close()
        }catch (e:Exception){}

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

}