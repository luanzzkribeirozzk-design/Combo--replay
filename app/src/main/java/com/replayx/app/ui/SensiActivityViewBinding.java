package com.replayx.app.ui;

import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import androidx.viewbinding.ViewBinding;
import com.replayx.app.R;

/**
 * ViewBinding manual para SensiActivity.
 */
public final class SensiActivityViewBinding implements ViewBinding {

    public final WebView webViewSensi;
    public final Button btnSensiVoltar;

    private SensiActivityViewBinding(WebView webViewSensi, Button btnSensiVoltar) {
        this.webViewSensi = webViewSensi;
        this.btnSensiVoltar = btnSensiVoltar;
    }

    public static SensiActivityViewBinding bind(View rootView) {
        return new SensiActivityViewBinding(
            rootView.findViewById(R.id.webViewSensi),
            rootView.findViewById(R.id.btnSensiVoltar)
        );
    }

    @Override
    public View getRoot() {
        return webViewSensi;
    }
}
