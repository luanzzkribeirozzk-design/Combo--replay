package com.replayx.app.ui;

import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.replayx.app.R;

/**
 * ViewBinding manual para MainActivity.
 * Original: C1150j (p000) - gerado automaticamente pelo build
 */
public final class MainActivityViewBinding implements ViewBinding {

    public final ParticleView particleView;
    public final TextView tvShizukuStatus;
    public final TextView tvKeyInfo;
    public final TextView tvTimer;
    public final SwitchMaterial switchHideStream;
    public final TextView tvHideStreamStatus;
    public final Button btnSensiConfig;
    public final Button btnOtimizador;
    public final Button btnBypassMaxToNormal;
    public final Button btnBypassNormalToMax;
    public final Button btnClearLog;
    public final ScrollView scrollLog;
    public final TextView tvLog;

    private MainActivityViewBinding(
            ParticleView particleView,
            TextView tvShizukuStatus,
            TextView tvKeyInfo,
            TextView tvTimer,
            SwitchMaterial switchHideStream,
            TextView tvHideStreamStatus,
            Button btnSensiConfig,
            Button btnOtimizador,
            Button btnBypassMaxToNormal,
            Button btnBypassNormalToMax,
            Button btnClearLog,
            ScrollView scrollLog,
            TextView tvLog) {
        this.particleView = particleView;
        this.tvShizukuStatus = tvShizukuStatus;
        this.tvKeyInfo = tvKeyInfo;
        this.tvTimer = tvTimer;
        this.switchHideStream = switchHideStream;
        this.tvHideStreamStatus = tvHideStreamStatus;
        this.btnSensiConfig = btnSensiConfig;
        this.btnOtimizador = btnOtimizador;
        this.btnBypassMaxToNormal = btnBypassMaxToNormal;
        this.btnBypassNormalToMax = btnBypassNormalToMax;
        this.btnClearLog = btnClearLog;
        this.scrollLog = scrollLog;
        this.tvLog = tvLog;
    }

    public static MainActivityViewBinding bind(View rootView) {
        return new MainActivityViewBinding(
            rootView.findViewById(R.id.particleView),
            rootView.findViewById(R.id.tvShizukuStatus),
            rootView.findViewById(R.id.tvKeyInfo),
            rootView.findViewById(R.id.tvTimer),
            rootView.findViewById(R.id.switchHideStream),
            rootView.findViewById(R.id.tvHideStreamStatus),
            rootView.findViewById(R.id.btnSensiConfig),
            rootView.findViewById(R.id.btnOtimizador),
            rootView.findViewById(R.id.btnBypassMaxToNormal),
            rootView.findViewById(R.id.btnBypassNormalToMax),
            rootView.findViewById(R.id.btnClearLog),
            rootView.findViewById(R.id.scrollLog),
            rootView.findViewById(R.id.tvLog)
        );
    }

    @Override
    public View getRoot() {
        return particleView;
    }
}
