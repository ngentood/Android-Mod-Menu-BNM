package com.android.support

import android.app.Activity
import android.content.Context
import android.widget.Toast

class Main {
    companion object {
        // When you change the lib name, change also on CMakeLists.txt file
        // Both must have same name
        init {
            System.loadLibrary("ModBNM")
        }

        @JvmStatic
        fun start(context: Context) {
            //Check if context is an Activity.
            if (context is Activity) {
                val menu = Menu(context)
                menu.start()
            } else {
                Toast.makeText(context, "Failed to launch the mod menu\n", Toast.LENGTH_LONG).show()
            }
        }
    }

}