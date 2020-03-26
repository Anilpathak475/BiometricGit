package com.anilpathak475.staxter.extension

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import io.reactivex.disposables.Disposable

fun String.showToast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_LONG).show()
}

fun Disposable?.isNotDisposed(): Boolean {
    return this != null && !isDisposed
}

fun Context.openSecuritySettings() {
    val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
    startActivity(intent)
}