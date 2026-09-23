package io.github.mrsimkin.dndcustomaid.android.pdf

import android.content.Context
import java.io.InputStream

internal object AndroidPcSheetAssetLoader {
    @Volatile
    private var applicationContext: Context? = null

    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }

    fun open(path: String): InputStream? {
        val context = applicationContext ?: return null
        return runCatching { context.assets.open(path) }.getOrNull()
    }
}
