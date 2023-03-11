package com.wassallni.data.model

import android.content.Context
import android.util.Log
import com.wassallni.R
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

class DoHomeWork (private  val context: Context) {

    private  val TAG = "DoHomeWork"
    init {
        Log.e(TAG, context.getString(R.string.app_name))

    }
}
