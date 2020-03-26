package com.anilpathak475.staxter.base

import android.content.Context
import com.anilpathak475.staxter.viewmodel.MainViewModel
import dagger.android.support.DaggerFragment

abstract class BaseFragment : DaggerFragment() {

    lateinit var mainViewModel: MainViewModel
    lateinit var activityGeneralMessagesUtils: GeneralMessages

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            activityGeneralMessagesUtils = context as GeneralMessages
        } catch (castException: java.lang.ClassCastException) {
        }
    }

}