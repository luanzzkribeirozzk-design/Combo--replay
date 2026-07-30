package com.replayx.app.ui;

import android.webkit.JavascriptInterface;

/**
 * Interface JavaScript exposta na WebView da SensiActivity.
 * Original: C1819zc (p000)
 * Permite que o HTML/JS chame métodos do Android, como fechar a activity.
 */
public final class SensiJsInterface {

    private final SensiActivity activity;

    public SensiJsInterface(SensiActivity activity) {
        this.activity = activity;
    }

    /**
     * Chamado pelo JavaScript para fechar a activity.
     * Usado pelo botão "voltar" na sensi.html.
     */
    @JavascriptInterface
    public void finish() {
        activity.runOnUiThread(activity::finish);
    }
}
