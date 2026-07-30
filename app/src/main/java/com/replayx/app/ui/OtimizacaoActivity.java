package com.replayx.app.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.replayx.app.R;
import com.replayx.app.util.ShellExecutor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import rikka.shizuku.Shizuku;

/**
 * OtimizacaoActivity — Tela do otimizador de Free Fire.
 * Executa 54 comandos shell via Shizuku para otimizar o desempenho do FF.
 * 
 * Comandos otimizam:
 * - Animações de janela (window_animation_scale, etc.)
 * - Refresh rate (peak/min_refresh_rate)
 * - Performance GPU/egl (debug.sf.hw, debug.egl.hw, etc.)
 * - Processos em cache (max_cached_processes)
 * - Compile apps (cmd package compile -m speed)
 * - E muito mais
 *
 * Package original: com.replayx.app.p005ui.OtimizacaoActivity
 */
public final class OtimizacaoActivity extends AppCompatActivity {

    // Views
    private TextView tvLog;
    private ScrollView scrollLog;
    private Button btnVoltar;
    private Button btnOtimizar;
    private Button btnReset;
    private TextView tvStatus;
    private ProgressBar progressBar;
    private TextView tvContador;

    // Shizuku permission code
    private final int PERMISSION_CODE = 2001;

    // Listener de resultado de permissão
    private Shizuku.OnRequestPermissionResultListener permissionResultListener;

    // Comandos de otimização (54 comandos para otimizar)
    private final List<String> optimizeCommands = new ArrayList<>(Arrays.asList(
        "settings put global window_animation_scale 0.0",
        "settings put global transition_animation_scale 0.0",
        "settings put global animator_duration_scale 0.5",
        "settings put system peak_refresh_rate 120.0",
        "settings put system min_refresh_rate 120.0",
        "settings put global disable_window_blurs 1",
        "settings put global accessibility_reduce_transparency 1",
        "settings put secure long_press_timeout 250",
        "settings put secure multi_press_timeout 250",
        "settings put secure tap_duration_threshold 0",
        "settings put secure touch_blocking_period 0",
        "settings put secure pointer_speed 7",
        "settings put global windowsmgr.max_events_per_sec 150",
        "settings put global view.scroll_friction 0.005",
        "settings put global touch.pressure.scale 0.001",
        "settings put global debug.egl.hw 1",
        "settings put global debug.egl.profiler 1",
        "settings put global debug.sf.hw 1",
        "settings put global debug.sf.latch_unsignaled 1",
        "settings put global debug.composition.type gpu",
        "settings put global debug.gr.num_framebuffer_surface_buffers 3",
        "settings put global debug.performance.profile 1",
        "settings put global debug.sf.showfps 0",
        "settings put global persist.sys.ui.hw 1",
        "settings put global persist.sys.use_dithering 0",
        "settings put global persist.sys.purgeable_assets 1",
        "settings put global persist.sys.scrollingcache 3",
        "settings put global ro.config.low_ram false",
        "settings put global wifi_sleep_policy 2",
        "settings put global low_power_mode 0",
        "settings put global auto_time 0",
        "settings put global auto_time_zone 0",
        "settings put global bluetooth_on 0",
        "settings put global adaptive_low_power_setting 0",
        "settings put global wifi_scan_always_enabled 0",
        "settings put global ble_scan_always_enabled 0",
        "settings put global location_mode 0",
        "settings put global persist.service.pcsync.enable 0",
        "settings put global persist.service.lgospd.enable 0",
        "settings put global activity_starts_logging_enabled 0",
        "settings put global send_security_reports 0",
        "settings put secure gamesdk_version 0",
        "settings put secure game_home_enable 0",
        "settings put global zram_enabled 0",
        "settings put global activity_manager_constants max_cached_processes=10",
        "settings put system multicore_packet_scheduler 1",
        "device_config put runtime_native_boot profilebootclasspath true",
        "device_config put runtime_native_boot use_app_image_startup_cache true",
        "am kill-all",
        "pm trim-caches 128G",
        "cmd package compile -m speed com.dts.freefireth",
        "cmd package compile -m speed com.dts.freefiremax"
    ));

    // Comandos de reset (reverte otimizações)
    private final List<String> resetCommands = new ArrayList<>(Arrays.asList(
        "settings put global window_animation_scale 1.0",
        "settings put global transition_animation_scale 1.0",
        "settings put global animator_duration_scale 1.0",
        "settings put system peak_refresh_rate 60.0",
        "settings put system min_refresh_rate 60.0",
        "settings put global disable_window_blurs 0",
        "settings put global accessibility_reduce_transparency 0",
        "settings put secure long_press_timeout 500",
        "settings put secure multi_press_timeout 500",
        "settings put secure tap_duration_threshold 150",
        "settings put secure touch_blocking_period 100",
        "settings put secure pointer_speed 0",
        "settings put global windowsmgr.max_events_per_sec 90",
        "settings put global view.scroll_friction 0.01",
        "settings put global touch.pressure.scale 0.003",
        "settings put global debug.egl.hw 0",
        "settings put global debug.egl.profiler 0",
        "settings put global debug.sf.hw 0",
        "settings put global debug.sf.latch_unsignaled 0",
        "settings put global debug.composition.type auto",
        "settings put global debug.gr.num_framebuffer_surface_buffers 2",
        "settings put global debug.performance.profile 0",
        "settings put global persist.sys.ui.hw 0",
        "settings put global persist.sys.use_dithering 1",
        "settings put global persist.sys.purgeable_assets 0",
        "settings put global persist.sys.scrollingcache 1",
        "settings put global ro.config.low_ram true",
        "settings put global wifi_sleep_policy 0",
        "settings put global low_power_mode 1",
        "settings put global auto_time 1",
        "settings put global auto_time_zone 1",
        "settings put global bluetooth_on 1",
        "settings put global adaptive_low_power_setting 1",
        "settings put global wifi_scan_always_enabled 1",
        "settings put global ble_scan_always_enabled 1",
        "settings put global location_mode 3",
        "settings put global persist.service.pcsync.enable 1",
        "settings put global persist.service.lgospd.enable 1",
        "settings put global activity_starts_logging_enabled 1",
        "settings put global send_security_reports 1",
        "settings put secure gamesdk_version 1",
        "settings put secure game_home_enable 1",
        "settings put secure game_auto_temperature_control 1",
        "settings put global zram_enabled 1",
        "settings put global activity_manager_constants max_cached_processes=32",
        "settings put system multicore_packet_scheduler 0",
        "cmd power set-fixed-performance-mode-enabled false",
        "cmd package compile --reset com.dts.freefireth",
        "cmd package compile --reset com.dts.freefiremax",
        "pm trim-caches"
    ));

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otimizacao);

        // Inicializar views
        tvLog = findViewById(R.id.tvOtimLog);
        scrollLog = findViewById(R.id.scrollOtimLog);
        btnVoltar = findViewById(R.id.btnOtimVoltar);
        btnOtimizar = findViewById(R.id.btnAplicarOtimizacao);
        btnReset = findViewById(R.id.btnResetOtimizacao);
        tvStatus = findViewById(R.id.tvOtimStatus);
        progressBar = findViewById(R.id.progressOtimizacao);
        tvContador = findViewById(R.id.tvContador);

        // Mensagem inicial
        logMessage("root@devwill:~/ otimizador_ff", true);
        logMessage("[SYS] Pronto. Selecione uma ação abaixo.", true);

        // Botão voltar
        btnVoltar.setOnClickListener(v -> finish());

        // Botão otimizar
        btnOtimizar.setOnClickListener(v -> {
            if (checkShizukuPermission()) {
                setBusy(true);
                executeCommands(optimizeCommands, true);
            }
        });

        // Botão resetar
        btnReset.setOnClickListener(v -> {
            if (checkShizukuPermission()) {
                setBusy(true);
                executeCommands(resetCommands, false);
            }
        });

        // Configurar listener de permissão Shizuku
        permissionResultListener = (requestCode, grantResult) -> {
            if (requestCode == PERMISSION_CODE) {
                if (grantResult == 0) {
                    setStatus("[SYS] SHIZUKU_PERMISSAO_CONCEDIDA — toque no botão novamente", true);
                } else {
                    setStatus("[ERR] SHIZUKU_PERMISSAO_NEGADA", false);
                }
            }
        };

        try {
            Shizuku.addRequestPermissionResultListener(permissionResultListener);
        } catch (Exception ignored) {
        }
    }

    /**
     * Verifica permissão do Shizuku.
     */
    private boolean checkShizukuPermission() {
        try {
            if (!Shizuku.pingBinder()) {
                logMessage("[ERR] SHIZUKU_NAO_ATIVO", false);
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
                logMessage("[SYS] SHIZUKU_SOLICITANDO_PERMISSAO", false);
                Shizuku.requestPermission(PERMISSION_CODE);
                return false;
            }
            return true;
        } catch (Exception e) {
            logMessage("[ERR] SHIZUKU: " + (e.getMessage() != null ? e.getMessage() : ""), false);
            return false;
        }
    }

    /**
     * Executa lista de comandos via Shizuku/shell.
     */
    private void executeCommands(List<String> commands, boolean isOptimize) {
        String header = isOptimize ? "OTIMIZANDO..." : "RESETANDO...";
        logMessage(header, false);

        // Atualizar progress bar
        progressBar.setVisibility(View.VISIBLE);
        tvContador.setVisibility(View.VISIBLE);
        tvContador.setText("0/" + commands.size());

        AtomicBoolean running = new AtomicBoolean(true);
        AtomicInteger completed = new AtomicInteger(0);

        // Executar em thread separada
        new Thread(() -> {
            for (String cmd : commands) {
                if (!running.get()) break;

                try {
                    String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                        .format(new Date());
                    logMessage("[" + timestamp + "] " + cmd, false);

                    // Executar comando via Shizuku shell
                    ProcessBuilder pb = new ProcessBuilder("sh", "-c", cmd);
                    pb.redirectErrorStream(true);
                    Process process = pb.start();
                    java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logMessage(line, false);
                    }
                    int exitCode = process.waitFor();

                    int done = completed.incrementAndGet();
                    final int current = done;
                    runOnUiThread(() -> {
                        tvContador.setText(current + "/" + commands.size());
                        progressBar.setProgress((int) ((current * 100.0f) / commands.size()));
                    });

                    // Pequeno delay entre comandos
                    Thread.sleep(50);

                } catch (Exception e) {
                    logMessage("[ERR] " + e.getMessage(), false);
                }
            }

            running.set(false);

            String footer = isOptimize ? "OTIMIZACAO CONCLUIDA" : "RESET CONCLUIDO";
            logMessage(footer, false);

            runOnUiThread(() -> {
                setBusy(false);
                tvContador.setVisibility(View.GONE);
                progressBar.setVisibility(View.INVISIBLE);
                setStatus("PRONTO", true);
            });
        }).start();
    }

    /**
     * Define estado de ocupado (botões desabilitados, progress visível).
     */
    private void setBusy(boolean busy) {
        btnOtimizar.setEnabled(!busy);
        btnReset.setEnabled(!busy);
        progressBar.setVisibility(busy ? View.VISIBLE : View.INVISIBLE);
        if (busy) {
            tvStatus.setText("EXECUTANDO...");
        }
    }

    /**
     * Atualiza status text.
     */
    private void setStatus(String text, boolean isSystem) {
        tvStatus.setText(text);
        if (isSystem) {
            tvStatus.setTextColor(0xFFeaff00);
        }
    }

    /**
     * Adiciona mensagem ao log.
     */
    private void logMessage(String message, boolean isSystem) {
        String timestamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            .format(new Date());
        String formatted = isSystem ? message : "[" + timestamp + "] " + message;

        String currentText = tvLog.getText().toString();
        String newText = currentText.isEmpty() ? formatted : currentText + "\n" + formatted;
        tvLog.setText(newText);

        // Auto-scroll
        scrollLog.post(() -> scrollLog.fullScroll(View.FOCUS_DOWN));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Shizuku.removeRequestPermissionResultListener(permissionResultListener);
        } catch (Exception ignored) {
        }
    }
}
