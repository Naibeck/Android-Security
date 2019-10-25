package com.naibeck.encryptedsharedpreferences.extensions

import android.widget.TextView
import androidx.annotation.NonNull
import io.noties.markwon.Markwon

fun TextView.markDown(@NonNull markdownText: String) {
    Markwon.create(this.context).also {
        it.setMarkdown(this, markdownText)
    }
}
