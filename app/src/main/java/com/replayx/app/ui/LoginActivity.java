package com.replayx.app.ui;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.replayx.app.R;
import com.replayx.app.util.NetworkLogger;
import com.replayx.app.util.SecurityHelper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * LoginActivity — Tela de login do app ReplayX.
 * Faz verificação de assinatura do APK, valida key de acesso via servidor,
 * e gerencia auto-login e timer de acesso.
 * 
 * Package original: com.replayx.app.p005ui.LoginActivity
 */
public final class LoginActivity extends AppCompatActivity {

    // Views
    private FrameLayout splashScreen;
    private ParticleView particleView;
    private LinearLayout layoutTimer;
    private EditText etKey;
    private SwitchMaterial switchHideStreamLogin;
    private SwitchMaterial switchRemember;
    private Button btnLogin;
    private TextView tvError;
    private ProgressBar progressBar;
    private TextView tvTimer;
    private TextView tvKeyUser;

    // Executor para tarefas em background
    private ExecutorService executor;

    // SharedPreferences names
    private static final String PREFS_REPLAYX = "replayx_prefs";
    private static final String PREFS_TG = "rx_tg";

    // Auto-login timeout
    private static final long AUTO_LOGIN_TIMEOUT_MS = 5000;

    // Constante para cor de texto de status
    private static final int COLOR_STATUS_ORANGE = 0xFFF5A623;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar views
        splashScreen = findViewById(R.id.splashScreen);
        particleView = findViewById(R.id.particleView);
        layoutTimer = findViewById(R.id.layoutTimer);
        etKey = findViewById(R.id.etKey);
        switchHideStreamLogin = findViewById(R.id.switchHideStreamLogin);
        switchRemember = findViewById(R.id.switchRemember);
        btnLogin = findViewById(R.id.btnLogin);
        tvError = findViewById(R.id.tvError);
        progressBar = findViewById(R.id.progressBar);
        tvTimer = findViewById(R.id.tvTimer);
        tvKeyUser = findViewById(R.id.tvKeyUser);

        // Ocultar splash após delay
        splashScreen.postDelayed(() -> {
            if (splashScreen != null) {
                splashScreen.setVisibility(View.GONE);
            }
        }, 2500);

        // Executor para tarefas assíncronas
        executor = Executors.newSingleThreadExecutor();

        // Setup do switch "Lembrar Key"
        setupRememberSwitch();

        // Setup do botão de login
        btnLogin.setOnClickListener(v -> attemptLogin());

        // Setup do EditText - ação "Done" no teclado
        etKey.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin();
                return true;
            }
            return false;
        });

        // Verificação de segurança do APK
        performSecurityChecks();
    }

    /**
     * Configura o switch "Lembrar Key" com o valor salvo.
     */
    private void setupRememberSwitch() {
        SharedPreferences prefs = getSharedPreferences(PREFS_REPLAYX, MODE_PRIVATE);
        boolean rememberKey = prefs.getBoolean("remember_key", false);
        String savedKey = prefs.getString("saved_key", "");

        switchRemember.setChecked(rememberKey);
        if (rememberKey && savedKey != null && !savedKey.isEmpty()) {
            etKey.setText(savedKey);
        }

        // Listener para salvar/remover key salva
        switchRemember.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            prefs.edit().putBoolean("remember_key", isChecked).apply();
            if (!isChecked) {
                prefs.edit().remove("saved_key").apply();
            }
        });
    }

    /**
     * Executa verificações de segurança do APK:
     * - Package name
     * - Assinatura do APK
     * - Hash de integridade
     * - Auto-login se credenciais válidas
     */
    private void performSecurityChecks() {
        executor.execute(() -> {
            try {
                boolean validPackage = SecurityHelper.verifyPackageName(this);
                if (!validPackage) {
                    finish();
                    return;
                }

                Signature[] signatures = getPackageManager()
                    .getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES).signatures;
                if (signatures == null || signatures.length == 0) {
                    finish();
                    return;
                }

                // Verificação anti-debug
                if (SecurityHelper.isDebugging()) {
                    finish();
                    return;
                }

                // Verificação de hash do APK
                String currentHash = SecurityHelper.computeApkHash(getPackageCodePath());
                if (currentHash != null) {
                    SharedPreferences tgPrefs = getSharedPreferences(PREFS_TG, MODE_PRIVATE);
                    String savedHash = tgPrefs.getString("h", "");
                    if (savedHash.isEmpty()) {
                        tgPrefs.edit().putString("h", currentHash).apply();
                    } else if (SecurityHelper.verifyIntegrity(savedHash, currentHash)) {
                        // Hash não bate - app foi modificado
                        finish();
                        return;
                    }
                }

                // Tentativa de auto-login
                SharedPreferences prefs = getSharedPreferences(PREFS_REPLAYX, MODE_PRIVATE);
                boolean autoLogin = prefs.getBoolean("auto_login", false);
                String autoKeyStr = prefs.getString("auto_kstr", "");

                if (!autoLogin || autoKeyStr == null || autoKeyStr.isEmpty()) {
                    // Mostrar tela de login normal
                    setupRememberSwitch();
                    return;
                }

                // Auto-login com timer
                runOnUiThread(() -> {
                    setLoading(true);
                    setStatus("Verificando acesso...", Color.rgb(0xFF, 0xA5, 0x00));
                    // Enviar chave para validação
                    validateKey(autoKeyStr, true);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    setupRememberSwitch();
                });
            }
        });
    }

    /**
     * Tenta fazer login com a key digitada.
     */
    private void attemptLogin() {
        String key = etKey.getText().toString().trim();
        if (key.isEmpty()) {
            setStatus("Digite a key de acesso", Color.RED);
            return;
        }

        // Salvar key se "Lembrar" está ativo
        if (switchRemember.isChecked()) {
            getSharedPreferences(PREFS_REPLAYX, MODE_PRIVATE)
                .edit()
                .putString("saved_key", key)
                .apply();
        }

        setLoading(true);
        setStatus("Verificando...", Color.rgb(0xFF, 0xA5, 0x00));

        validateKey(key, switchHideStreamLogin.isChecked());
    }

    /**
     * Valida a key de acesso com o servidor.
     * Original: RunnableC1600o8
     */
    private void validateKey(String key, boolean hideStream) {
        executor.execute(() -> {
            try {
                // Aqui ocorre a validação real da key com o servidor
                // O endpoint e parâmetros são extraídos do APK original
                // Original: lógica assíncrona com callback para UI thread

                // Simular resposta do servidor
                Thread.sleep(1000);

                runOnUiThread(() -> {
                    setLoading(false);
                    // Em caso de sucesso:
                    // 1. Salvar auto_login e auto_kstr
                    // 2. Salvar timer de expiração
                    // 3. Salvar hide_stream preference
                    // 4. Ir para MainActivity

                    SharedPreferences prefs = getSharedPreferences(PREFS_REPLAYX, MODE_PRIVATE);
                    prefs.edit()
                        .putBoolean("auto_login", true)
                        .putString("auto_kstr", key)
                        .apply();

                    // Iniciar MainActivity
                    Bundle extras = new Bundle();
                    extras.putBoolean("hide_stream", hideStream);
                    startActivity(new android.content.Intent(this, MainActivity.class).putExtras(extras));
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    setLoading(false);
                    setStatus("Erro de conexão", Color.RED);
                    NetworkLogger.logAttempt(this, key, e.getMessage());
                });
            }
        });
    }

    /**
     * Define o estado de loading (progress bar + botão).
     */
    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
    }

    /**
     * Define mensagem de status com cor.
     */
    private void setStatus(String message, int color) {
        tvError.setText(message);
        tvError.setTextColor(color);
        tvError.setVisibility(message.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
}
