package com.mask.sliceviewdemo.ext

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.slice.SliceMetadata
import androidx.slice.widget.SliceLiveData
import androidx.slice.widget.SliceView

/**
 * Created by Manokar on 5/19/18.
 */
val TAG = "SliceKtx"

fun SliceView.bind(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        uri: Uri,
        onSliceActionListener: SliceView.OnSliceActionListener = SliceView.OnSliceActionListener { _, _ -> },
        onClickListener: View.OnClickListener = View.OnClickListener { },
        onLongClickListener: View.OnLongClickListener = View.OnLongClickListener { false },
        scrollable: Boolean = false
) {
    setOnSliceActionListener(onSliceActionListener)
    setOnClickListener(onClickListener)
    setScrollable(scrollable)
    setOnLongClickListener(onLongClickListener)
    if (uri.scheme == null) {
        Log.w(TAG, "Scheme is null for URI $uri")
        return
    }
    // If someone accidentally prepends the "slice-" prefix to their scheme, let's remove it.
    val scheme =
            if (uri.scheme.startsWith("slice-")) {
                uri.scheme.replace("slice-", "")
            }
            else {
                uri.scheme
            }
    if (scheme == ContentResolver.SCHEME_CONTENT ||
            scheme.equals("https", true) ||
            scheme.equals("http", true)
            ) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        val sliceLiveData = SliceLiveData.fromIntent(context, intent)
        sliceLiveData?.removeObservers(lifecycleOwner)
        try {
            sliceLiveData?.observe(lifecycleOwner, Observer { updatedSlice ->
                if (updatedSlice == null) return@Observer
                slice = updatedSlice
                val expiry = SliceMetadata.from(context, updatedSlice).expiry
                if (expiry != 1L) {
                    // Shows the updated text after the TTL expires.
                    postDelayed(
                            { slice = updatedSlice },
                            expiry - System.currentTimeMillis() + 15
                    )
                }
                Log.d(TAG, "Update Slice: $updatedSlice")
            })
        } catch (e: Exception) {
            Log.e(
                    TAG,
                    "Failed to find a valid ContentProvider for authority: $uri"
            )
        }
    } else {
        Log.w(TAG, "Invalid uri, skipping slice: $uri")
    }
}