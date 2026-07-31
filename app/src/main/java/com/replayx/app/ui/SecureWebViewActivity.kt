package com.replayx.app.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.replayx.app.databinding.ActivitySecureWebviewBinding
import com.replayx.app.security.HtmlVault

/**
 * Renderiza o sensi.html com o maior nível de proteção possível dentro de
 * uma WebView nativa:
 *
 *  - HTML nunca fica em disco: vem cifrado (AES-128) do assets/sensi.enc e
 *    só é decifrado em memória, na hora de renderizar.
 *  - FLAG_SECURE bloqueia screenshot, gravação de tela e a miniatura do
 *    app na tela de "recentes".
 *  - Debug remoto da WebView desativado (não dá pra inspecionar via
 *    chrome://inspect).
 *  - Seleção de texto, menu de contexto (copiar/salvar imagem) e
 *    long-press desativados.
 *  - Download, upload de arquivo e acesso a file:// desativados.
 *  - Navegação para fora do HTML carregado é bloqueada (evita redirecionar
 *    a WebView pra uma página externa via JS).
 *  - Cache em disco desligado; cache/histórico limpos ao sair da tela.
 *
 * Isso cobre o cenário realista de alguém tentando extrair o arquivo
 * abrindo o APK ou usando as ferramentas normais da WebView. Não existe
 * proteção 100% à prova de um atacante com o celular *rooteado* fazendo
 * dump de memória em tempo de execução — isso é uma limitação de qualquer
 * conteúdo renderizado localmente, não só deste app.
 */
class SecureWebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecureWebviewBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        // Bloqueia screenshot/gravação de tela e esconde da lista de recentes
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        super.onCreate(savedInstanceState)
        binding = ActivitySecureWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WebView.setWebContentsDebuggingEnabled(false)

        val webView = binding.webViewSensi
        val s: WebSettings = webView.settings
        s.javaScriptEnabled = true
        s.domStorageEnabled = false
        s.databaseEnabled = false
        s.allowFileAccess = false
        s.allowContentAccess = false
        s.cacheMode = WebSettings.LOAD_NO_CACHE
        s.setGeolocationEnabled(false)
        s.saveFormData = false
        s.setSupportZoom(false)

        // Bloqueia menu de contexto (copiar texto / salvar imagem / etc.)
        webView.setOnLongClickListener { true }
        webView.isLongClickable = false
        registerForContextMenu(webView)

        // Bloqueia qualquer navegação pra fora do HTML já carregado
        webView.webViewClient = object : WebViewClient() {
            private var loaded = false
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return loaded
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                loaded = true
            }
        }

        val html = HtmlVault.loadHtml(this)
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)

        binding.btnVoltarWeb.setOnClickListener { finish() }
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        // Consome o menu de contexto sem exibir nada (bloqueia "salvar imagem", etc.)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean = true

    override fun onDestroy() {
        binding.webViewSensi.apply {
            clearHistory()
            clearCache(true)
            loadUrl("about:blank")
            destroy()
        }
        super.onDestroy()
    }
}
