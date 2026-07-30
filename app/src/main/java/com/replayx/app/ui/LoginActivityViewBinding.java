package com.replayx.app.ui;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.replayx.app.R;

/**
 * ViewBinding manual para LoginActivity.
 * Original: C1150j (p000) - gerado automaticamente pelo build
 */
public final class LoginActivityViewBinding implements ViewBinding {

    public final FrameLayout splashScreen;
    public final ParticleView particleView;
    public final EditText etKey;
    public final SwitchMaterial switchHideStreamLogin;
    public final SwitchMaterial switchRemember;
    public final Button btnLogin;
    public final TextView tvError;
    public final ProgressBar progressBar;
    public final LinearLayout layoutTimer;
    public final TextView tvTimer;
    public final TextView tvKeyUser;

    private LoginActivityViewBinding(
            FrameLayout splashScreen,
            ParticleView particleView,
            EditText etKey,
            SwitchMaterial switchHideStreamLogin,
            SwitchMaterial switchRemember,
            Button btnLogin,
            TextView tvError,
            ProgressBar progressBar,
            LinearLayout layoutTimer,
            TextView tvTimer,
            TextView tvKeyUser) {
        this.splashScreen = splashScreen;
        this.particleView = particleView;
        this.etKey = etKey;
        this.switchHideStreamLogin = switchHideStreamLogin;
        this.switchRemember = switchRemember;
        this.btnLogin = btnLogin;
        this.tvError = tvError;
        this.progressBar = progressBar;
        this.layoutTimer = layoutTimer;
        this.tvTimer = tvTimer;
        this.tvKeyUser = tvKeyUser;
    }

    public static LoginActivityViewBinding bind(View rootView) {
        return new LoginActivityViewBinding(
            rootView.findViewById(R.id.splashScreen),
            rootView.findViewById(R.id.particleView),
            rootView.findViewById(R.id.etKey),
            rootView.findViewById(R.id.switchHideStreamLogin),
            rootView.findViewById(R.id.switchRemember),
            rootView.findViewById(R.id.btnLogin),
            rootView.findViewById(R.id.tvError),
            rootView.findViewById(R.id.progressBar),
            rootView.findViewById(R.id.layoutTimer),
            rootView.findViewById(R.id.tvTimer),
            rootView.findViewById(R.id.tvKeyUser)
        );
    }

    @Override
    public View getRoot() {
        return splashScreen;
    }
}
