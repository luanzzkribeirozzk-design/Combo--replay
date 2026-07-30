package com.replayx.app.ui;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.viewbinding.ViewBinding;
import com.replayx.app.R;

/**
 * ViewBinding manual para OtimizacaoActivity.
 * Original: gerado automaticamente pelo build com viewBinding true
 */
public final class OtimizacaoActivityViewBinding implements ViewBinding {

    public final Button btnOtimVoltar;
    public final TextView tvOtimStatus;
    public final Button btnAplicarOtimizacao;
    public final Button btnResetOtimizacao;
    public final TextView tvContador;
    public final ProgressBar progressOtimizacao;
    public final TextView tvOtimLog;
    public final ScrollView scrollOtimLog;

    private OtimizacaoActivityViewBinding(
            Button btnOtimVoltar,
            TextView tvOtimStatus,
            Button btnAplicarOtimizacao,
            Button btnResetOtimizacao,
            TextView tvContador,
            ProgressBar progressOtimizacao,
            TextView tvOtimLog,
            ScrollView scrollOtimLog) {
        this.btnOtimVoltar = btnOtimVoltar;
        this.tvOtimStatus = tvOtimStatus;
        this.btnAplicarOtimizacao = btnAplicarOtimizacao;
        this.btnResetOtimizacao = btnResetOtimizacao;
        this.tvContador = tvContador;
        this.progressOtimizacao = progressOtimizacao;
        this.tvOtimLog = tvOtimLog;
        this.scrollOtimLog = scrollOtimLog;
    }

    public static OtimizacaoActivityViewBinding bind(View rootView) {
        return new OtimizacaoActivityViewBinding(
            rootView.findViewById(R.id.btnOtimVoltar),
            rootView.findViewById(R.id.tvOtimStatus),
            rootView.findViewById(R.id.btnAplicarOtimizacao),
            rootView.findViewById(R.id.btnResetOtimizacao),
            rootView.findViewById(R.id.tvContador),
            rootView.findViewById(R.id.progressOtimizacao),
            rootView.findViewById(R.id.tvOtimLog),
            rootView.findViewById(R.id.scrollOtimLog)
        );
    }

    @Override
    public View getRoot() {
        return btnOtimVoltar;
    }
}
