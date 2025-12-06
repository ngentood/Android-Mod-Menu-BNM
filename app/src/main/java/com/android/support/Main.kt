package com.android.support

import android.app.Activity
import android.content.Context
import android.widget.Toast

class Main {
    companion object {
        // Used to load the 'support' library on application startup.
        init {
            System.loadLibrary("support")
        }

        @JvmStatic
        fun start(context: Context) {
            //Check if context is an Activity.
            if (context is Activity) {
                Menu(context)
            } else {
                Toast.makeText(context, "Failed to launch the mod menu\n", Toast.LENGTH_LONG).show()
            }
        }
    }

}