package com.replayx.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.replayx.app.R;

/**
 * SensiActivity — Tela do configurador de sensibilidade.
 * Carrega sensi.html do assets como WebView e fornece interface JavaScript
 * para comunicação com o app Android.
 * 
 * Package original: com.replayx.app.p005ui.SensiActivity
 */
public final class SensiActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensi);

        webView = findViewById(R.id.webViewSensi);

        // Botão voltar
        Button btnVoltar = findViewById(R.id.btnSensiVoltar);
        btnVoltar.setOnClickListener(v -> finish());

        // Configurar WebView
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(false);
        settings.setDisplayZoomControls(false);

        // Interface JavaScript para comunicação Android <-> WebView
        webView.addJavascriptInterface(new SensiJsInterface(this), "Android");

        webView.setWebViewClient(new WebViewClient());

        // Carregar sensi.html dos assets
        webView.loadUrl("file:///android_asset/sensi.html");
    }

    @Override
    public void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}
