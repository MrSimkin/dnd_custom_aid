package io.github.mrsimkin.dndcustomaid.android

import android.app.Application
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import io.github.mrsimkin.dndcustomaid.android.pdf.AndroidPcSheetAssetLoader

class DndCustomAidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PDFBoxResourceLoader.init(this)
        AndroidPcSheetAssetLoader.initialize(this)
        initializeHostedDevelopmentAuthentication(this)
    }
}
