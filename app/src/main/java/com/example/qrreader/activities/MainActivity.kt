package com.example.qrreader.activities


import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.qrreader.Fragment.*
import com.example.qrreader.Interfaces.UpdateAdapter
import com.example.qrreader.MyFragmentTransaction
import com.example.qrreader.R


class MainActivity : AppCompatActivity() {



    lateinit var text: String
    lateinit var historyFragment: HistoryFragment
    //lateinit var updateAdapter:UpdateAdapter
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       // historyFragment= HistoryFragment()
       findViewById<ConstraintLayout>(R.id.mainConstraint).isEnabled=false
        fragmentTransactionReplace(HistoryFragment())

    }

    fun history(v: View) {
        fragmentTransactionReplace(HistoryFragment())
    }

    fun camera(v: View) {
        val intent = Intent(this, BarcodeScanActivity::class.java)
        startActivityForResult(intent,8)
    }

    fun setting(v: View) {

        fragmentTransactionReplace(SettingFragment())
    }

    fun finish(v: View) {
        var sharedPreferences=getSharedPreferences("user",Context.MODE_PRIVATE)
        val editor=sharedPreferences.edit()
        editor.clear().apply()
        var intent=Intent(this,Authorization::class.java)
        startActivity(intent)
        finish()
    }

    fun back(v: View) {
        onBackPressed()
    }

    fun secure(v: View) {
        val secure = SecureFragment()
        fragmentTransactionReplace(secure)
    }

    fun data(v: View) {
        val data = DataFragment()
        fragmentTransactionReplace(data)
    }

    fun historBack(v: View) {
        finish()
    }

    fun fragmentTransactionReplace(fragment: Fragment) {

        var myFragmentTransaction=MyFragmentTransaction(this)
        myFragmentTransaction.fragmentTransactionReplace(fragment)

    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount != 1)
            supportFragmentManager.popBackStack()
        else finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1) {
            if (data?.getIntExtra("fragment", 1) == 1)
                fragmentTransactionReplace(historyFragment)
            else if (data?.getIntExtra("fragment", 1) == 2)
                fragmentTransactionReplace(SettingFragment())
        }
        if (resultCode == 28) {

          //  updateAdapter=HistoryFragment()
            var z=HistoryFragment()
            fragmentTransactionReplace( z)
            z.update()
          //  updateAdapter.update()

        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        applicationContext.cacheDir.deleteRecursively()
        super.onDestroy()

    }
}




