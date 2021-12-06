package com.oukachkosnt.coins.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import com.oukachkosnt.coins.R

private object IntentsBuilder {
    private const val MIME_PLAIN_TEXT = "text/plain"

    fun browserIntent(url: String) = Intent(Intent.ACTION_VIEW, Uri.parse(url))

    fun shareIntent(messageTitle: String, message: String)
        = shareTextDataIntent().apply {
            putExtra(Intent.EXTRA_SUBJECT, messageTitle)
            putExtra(Intent.EXTRA_TEXT, message)
        }

    private fun shareTextDataIntent() = Intent(Intent.ACTION_SEND).apply { type = MIME_PLAIN_TEXT }
}


fun viewInBrowser(context: Context, url: String, onCannotResolve: () -> Unit) {
    IntentsBuilder.browserIntent(url).startActivityIfCanResolve(context, onCannotResolve)
}

@SuppressLint("QueryPermissionsNeeded")
private fun Intent.startActivityIfCanResolve(context: Context, onCannotResolve: () -> Unit) {
    if (resolveActivity(context.packageManager) != null) {
        context.startActivity(this)
    } else {
        onCannotResolve()
    }
}

private fun Intent.startActivityViaChooser(context: Context, @StringRes chooserTitleResId: Int) {
    context.startActivity(Intent.createChooser(this, context.getString(chooserTitleResId)))
}