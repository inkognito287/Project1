package com.example.qrreader

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class MyFragmentTransaction (var context: Context){


    fun fragmentTransactionReplace(fragment: Fragment) {

        val backStateName: String = fragment.javaClass.name
        val fragmentPopped = (context as AppCompatActivity).supportFragmentManager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) {

            var fragmentTransaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainerView, fragment)


            if ((context as AppCompatActivity).supportFragmentManager.backStackEntryCount == 0){
                fragmentTransaction.addToBackStack(backStateName)
                fragmentTransaction.commit()}
            if ((context as AppCompatActivity).supportFragmentManager.backStackEntryCount != 0 && (context as AppCompatActivity).supportFragmentManager.getBackStackEntryAt(
                    (context as AppCompatActivity).supportFragmentManager.backStackEntryCount-1
                ).name != backStateName
            ) {
                fragmentTransaction.addToBackStack(backStateName)
                fragmentTransaction.commit()
            }


        }

    }

}