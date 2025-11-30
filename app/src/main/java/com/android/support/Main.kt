package com.android.support

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
            val menu = Menu(context)
        }
    }

}