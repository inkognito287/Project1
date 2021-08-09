package com.example.qrreader

import android.app.Dialog


import android.widget.TextView

import android.app.Activity
import android.view.View
import android.view.Window
import android.widget.Button


class CustomDialog {


    fun showDialog(activity: Activity?, msg: String?) {
        val dialog = Dialog(activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_error)
        val text = dialog.findViewById<View>(R.id.text_dialog) as TextView
        text.text = msg
        val dialogButton: Button = dialog.findViewById<View>(R.id.btn_ok) as Button
        dialogButton.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}