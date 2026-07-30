package com.devwilltech.otimizacao.ui.screens

import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.devwilltech.otimizacao.MainViewModel

@Composable
fun SensiScreen(viewModel: MainViewModel) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    builtInZoomControls = false
                    displayZoomControls = false
                    cacheMode = WebSettings.LOAD_NO_CACHE
                }
                loadUrl("file:///android_asset/sensi.html")
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp)
    )
}
