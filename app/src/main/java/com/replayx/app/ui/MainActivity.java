package com.replayx.app.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.replayx.app.R;
import com.replayx.app.service.ReplayTransferService;
import com.replayx.app.util.TransferResult;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import rikka.shizuku.Shizuku;

/**
 * MainActivity — Tela principal do app ReplayX.
 * Gerencia:
 * - Status do Shizuku
 * - Hide Stream (muda ponteiro do mouse/cursor)
 * - Bypass Free Fire MAX <-> Free Fire Normal
 * - Timer de acesso
 * - TextToSpeech para feedback
 *
 * Package original: com.replayx.app.p005ui.MainActivity
 */
public final class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    // Views
    private ParticleView particleView;
    private TextView tvShizukuStatus;
    private TextView tvKeyInfo;
    private TextView tvTimer;
    private SwitchMaterial switchHideStream;
    private TextView tvHideStreamStatus;
    private Button btnSensiConfig;
    private Button btnOtimizador;
    private Button btnBypassMaxToNormal;
    private Button btnBypassNormalToMax;
    private Button btnClearLog;
    private TextView tvLog;
    private ScrollView scrollLog;

    // Serviços e estado
    private ReplayTransferService transferService;
    private int bypassCount;
    private SharedPreferences prefs;
    private CountDownTimer countDownTimer;
    private TextToSpeech textToSpeech;
    private boolean ttsReady;

    // Shizuku listeners
    private Shizuku.OnRequestPermissionResultListener permissionResultListener;
    private Shizuku.OnBinderReceivedListener binderReceivedListener;
    private Shizuku.OnBinderDeadListener binderDeadListener;

    // Permission request code
    private static final int PERMISSION_CODE = 2001;

    // Prefs keys
    private static final String PREFS_REPLAYX = "replayx_prefs";
    private static final String PREF_HIDE_STREAM = "hide_stream";
    private static final String PREF_BYPASS_COUNT = "bypass_count";
    private static final String PREF_EXPIRY_TIME = "expiry_time";

    // Cores
    private static final int COLOR_GREEN = 0xFF006600;
    private static final int COLOR_RED = 0xFFFF0000;
    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_ORANGE = 0xFFF5A623;
    private static final int COLOR_GREY = 0xFF444444;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        // Inicializar views
        particleView = findViewById(R.id.particleView);
        tvShizukuStatus = findViewById(R.id.tvShizukuStatus);
        tvKeyInfo = findViewById(R.id.tvKeyInfo);
        tvTimer = findViewById(R.id.tvTimer);
        switchHideStream = findViewById(R.id.switchHideStream);
        tvHideStreamStatus = findViewById(R.id.tvHideStreamStatus);
        btnSensiConfig = findViewById(R.id.btnSensiConfig);
        btnOtimizador = findViewById(R.id.btnOtimizador);
        btnBypassMaxToNormal = findViewById(R.id.btnBypassMaxToNormal);
        btnBypassNormalToMax = findViewById(R.id.btnBypassNormalToMax);
        btnClearLog = findViewById(R.id.btnClearLog);
        tvLog = findViewById(R.id.tvLog);
        scrollLog = findViewById(R.id.scrollLog);

        // Inicializar serviços
        transferService = new ReplayTransferService();
        prefs = getSharedPreferences(PREFS_REPLAYX, MODE_PRIVATE);
        bypassCount = prefs.getInt(PREF_BYPASS_COUNT, 0);

        // Configurar Hide Stream switch
        setupHideStream();

        // Configurar botões de navegação
        btnSensiConfig.setOnClickListener(v -> {
            startActivity(new Intent(this, SensiActivity.class));
        });

        btnOtimizador.setOnClickListener(v -> {
            startActivity(new Intent(this, OtimizacaoActivity.class));
        });

        // Configurar botões de bypass
        btnBypassMaxToNormal.setOnClickListener(v -> {
            if (checkShizukuPermission()) {
                logMessage("[SYS] Iniciando bypass FFM -> FFN...");
                transferService.transferMaxToNormal(bypassCount, this::logMessage);
                incrementBypassCount();
            }
        });

        btnBypassNormalToMax.setOnClickListener(v -> {
            if (checkShizukuPermission()) {
                logMessage("[SYS] Iniciando bypass FFN -> FFM...");
                transferService.transferNormalToMax(bypassCount, this::logMessage);
                incrementBypassCount();
            }
        });

        // Configurar botão de limpar logs
        btnClearLog.setOnClickListener(v -> {
            tvLog.setText("");
        });

        // Inicializar TextToSpeech
        textToSpeech = new TextToSpeech(this, this);

        // Configurar Shizuku listeners
        setupShizukuListeners();

        // Verificar timer de acesso
        checkExpiryTimer();

        // Verificar status do Shizuku
        checkShizukuStatus();
    }

    /**
     * Configura o switch Hide Stream.
     * Original: lógica com Shizuku para mudar pointer icon
     */
    private void setupHideStream() {
        boolean currentHide = prefs.getBoolean(PREF_HIDE_STREAM, false);
        switchHideStream.setChecked(currentHide);
        updateHideStreamStatus(currentHide);

        switchHideStream.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(PREF_HIDE_STREAM, isChecked).apply();
            updateHideStreamStatus(isChecked);

            if (isChecked) {
                logMessage("[SYS] HIDE STREAM ativado");
                if (ttsReady) {
                    textToSpeech.speak("Dev Will bypass ativado", TextToSpeech.QUEUE_FLUSH, null, null);
                }
            } else {
                logMessage("[SYS] HIDE STREAM desativado");
            }
        });
    }

    /**
     * Atualiza a exibição do status Hide Stream.
     */
    private void updateHideStreamStatus(boolean enabled) {
        tvHideStreamStatus.setText(enabled ? "HIDE STREAM: ON" : "HIDE STREAM: OFF");
        tvHideStreamStatus.setTextColor(enabled ? COLOR_GREEN : COLOR_GREY);
    }

    /**
     * Verifica se o Shizuku está disponível e com permissão.
     */
    private boolean checkShizukuPermission() {
        try {
            if (!Shizuku.pingBinder()) {
                logMessage("[ERR] SHIZUKU_NAO_ATIVO");
                new AlertDialog.Builder(this)
                    .setTitle("Shizuku necessário")
                    .setMessage("O Shizuku nao esta ativo.\n\n1. Abra o app Shizuku\n2. Ative o servico\n3. Volte e tente novamente")
                    .setPositiveButton("Abrir Shizuku", (dialog, which) -> {
                        try {
                            Intent intent = getPackageManager()
                                .getLaunchIntentForPackage("moe.shizuku.privileged.api");
                            if (intent != null) startActivity(intent);
                        } catch (Exception ignored) {
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
                return false;
            } else if (Shizuku.checkSelfPermission() != 0) {
                logMessage("[SYS] SHIZUKU_SOLICITANDO_PERMISSAO");
                Shizuku.requestPermission(PERMISSION_CODE);
                return false;
            }
            return true;
        } catch (Exception e) {
            logMessage("[ERR] SHIZUKU: " + (e.getMessage() != null ? e.getMessage() : ""));
            return false;
        }
    }

    /**
     * Configura os listeners do Shizuku para binder received/dead.
     */
    private void setupShizukuListeners() {
        binderReceivedListener = () -> {
            runOnUiThread(() -> checkShizukuStatus());
        };

        binderDeadListener = () -> {
            runOnUiThread(() -> {
                tvShizukuStatus.setText("● OFFLINE");
                tvShizukuStatus.setTextColor(COLOR_RED);
            });
        };

        permissionResultListener = (requestCode, grantResult) -> {
            if (requestCode == PERMISSION_CODE) {
                if (grantResult == 0) {
                    logMessage("[SYS] SHIZUKU_PERMISSAO_CONCEDIDA");
                } else {
                    logMessage("[ERR] SHIZUKU_PERMISSAO_NEGADA");
                }
            }
        };

        try {
            Shizuku.addBinderReceivedListener(binderReceivedListener);
            Shizuku.addBinderDeadListener(binderDeadListener);
            Shizuku.addRequestPermissionResultListener(permissionResultListener);
        } catch (Exception ignored) {
        }
    }

    /**
     * Verifica e exibe o status do Shizuku.
     */
    private void checkShizukuStatus() {
        try {
            if (Shizuku.pingBinder()) {
                tvShizukuStatus.setText("● ATIVO");
                tvShizukuStatus.setTextColor(COLOR_GREEN);
            } else {
                tvShizukuStatus.setText("● OFFLINE");
                tvShizukuStatus.setTextColor(COLOR_RED);
            }
        } catch (Exception e) {
            tvShizukuStatus.setText("● ERRO");
            tvShizukuStatus.setTextColor(COLOR_RED);
        }
    }

    /**
     * Verifica se o timer de acesso expirou.
     */
    private void checkExpiryTimer() {
        long expiryTime = prefs.getLong(PREF_EXPIRY_TIME, 0);
        if (expiryTime > 0 && expiryTime > System.currentTimeMillis()) {
            long remaining = expiryTime - System.currentTimeMillis();
            startCountDown(remaining);
        } else {
            tvTimer.setText("00d 00h 00m 00s");
        }
    }

    /**
     * Inicia o countdown timer.
     */
    private void startCountDown(long millis) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText(formatTime(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                tvTimer.setText("00d 00h 00m 00s");
                // Expirou - voltar para login
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        }.start();
    }

    /**
     * Formata tempo em milissegundos para string legível.
     * Original: m3645e(long)
     */
    public static String formatTime(long millis) {
        long totalSeconds = millis / 1000;
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(),
            "%02dd %02dh %02dm %02ds", days, hours, minutes, seconds);
    }

    /**
     * Incrementa o contador de bypass e salva.
     */
    private void incrementBypassCount() {
        bypassCount++;
        prefs.edit().putInt(PREF_BYPASS_COUNT, bypassCount).apply();
    }

    /**
     * Adiciona mensagem ao log com timestamp.
     */
    public void logMessage(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
            .format(new Date());
        String currentText = tvLog.getText().toString();
        String newLine = "[" + timestamp + "] " + message;
        String newText = currentText.isEmpty() ? newLine : currentText + "\n" + newLine;
        tvLog.setText(newText);

        // Auto-scroll para o final
        scrollLog.post(() -> scrollLog.fullScroll(View.FOCUS_DOWN));
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale.US);
            textToSpeech.setSpeechRate(0.9f);
            ttsReady = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        try {
            Shizuku.removeBinderReceivedListener(binderReceivedListener);
            Shizuku.removeBinderDeadListener(binderDeadListener);
        } catch (Exception ignored) {
        }
    }
}
