package com.rajangarg.linkpreviewdemo

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

fun Context.loadURL(url: String?) {
    val params = CustomTabColorSchemeParams.Builder()
        .setToolbarColor(
            ContextCompat.getColor(
                this,
                R.color.purple_500
            )
        )
        .build()
    val customTabsIntent = CustomTabsIntent.Builder().apply {
        setShowTitle(true)
        setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_LIGHT, params)
        setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, params)
        setShareState(CustomTabsIntent.SHARE_STATE_OFF)
    }.build()
    customTabsIntent.launchUrl(this, Uri.parse(url))
}

@BindingAdapter("imageUrl")
fun ImageView.bindImageUrl(url: String?) {
    if (!url.isNullOrBlank()) {
        Glide.with(this).load(url).into(this)
    }
}

@BindingAdapter("visible")
fun View.setVisibility(status: Boolean) {
    visibility = if (status) View.VISIBLE else View.GONE
}

@BindingAdapter("textVisible")
fun View.setTextVisibility(string: String?) {
    visibility = if (string.isNullText()) View.GONE else View.VISIBLE
}

fun String?.isNullText(): Boolean {
    return isNullOrBlank() || trim() == "null"
}