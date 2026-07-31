package com.replayx.app.util

/**
 * Comandos de otimização (via Shizuku/ADB shell) extraídos dos arquivos
 * "otimizaçãocomandos" (guia de 150 comandos) e "RESET_OTIMIZAÇÃO" enviados.
 *
 * Só entraram aqui os itens que realmente rodam via shell no Android.
 * Foram excluídos de propósito:
 *  - Itens de Windows/Regedit, NVIDIA/AMD e emulador (não fazem sentido em
 *    um app Android via Shizuku).
 *  - Ajustes de dalvik.vm.heap* /`ro.*` que são propriedades fixadas no boot
 *    (não mudam em runtime e podem deixar o sistema instável).
 *  - Troca manual de DPI (risco real de deixar a tela ilegível).
 * Cada comando de ATIVAR tem o par correspondente em DESATIVAR, na mesma
 * ordem, pra reverter exatamente o que foi mudado.
 */
object OptimizationCommands {

    /** label, comando */
    val APPLY: List<Pair<String, String>> = listOf(
        "Modo performance fixo" to "cmd power set-fixed-performance-mode-enabled true",
        "Taxa de atualização (peak) 120Hz" to "settings put system peak_refresh_rate 120.0",
        "Taxa de atualização (min) 120Hz" to "settings put system min_refresh_rate 120.0",
        "Thermal throttling OFF" to "settings put secure game_auto_temperature_control 0",
        "Compilação speed (FF Normal)" to "cmd package compile -m speed com.dts.freefireth",
        "Compilação speed (FF Max)" to "cmd package compile -m speed com.dts.freefiremax",

        "Animação de janela OFF" to "settings put global window_animation_scale 0.0",
        "Animação de transição OFF" to "settings put global transition_animation_scale 0.0",
        "Duração de animator OFF" to "settings put global animator_duration_scale 0.0",
        "Reduzir transparência" to "settings put global accessibility_reduce_transparency 1",
        "Desativar blur de janelas" to "settings put global disable_window_blurs 1",
        "Multicore packet scheduler ON" to "settings put system multicore_packet_scheduler 1",
        "Long press timeout curto" to "settings put secure long_press_timeout 250",
        "Multi press timeout curto" to "settings put secure multi_press_timeout 250",
        "Tap duration threshold 0" to "settings put secure tap_duration_threshold 0.0",
        "Touch blocking period 0" to "settings put secure touch_blocking_period 0.0",
        "debug.egl.hw" to "settings put global debug.egl.hw 1",
        "debug.egl.profiler" to "settings put global debug.egl.profiler 1",
        "debug.sf.hw" to "settings put global debug.sf.hw 1",
        "debug.sf.latch_unsignaled" to "settings put global debug.sf.latch_unsignaled 1",
        "debug.composition.type gpu" to "settings put global debug.composition.type gpu",
        "framebuffer surface buffers 3" to "settings put global debug.gr.num_framebuffer_surface_buffers 3",
        "dithering OFF" to "settings put global persist.sys.use_dithering 0",
        "purgeable assets ON" to "settings put global persist.sys.purgeable_assets 1",
        "ui.hw ON" to "settings put global persist.sys.ui.hw 1",
        "windowsmgr max events/sec 150" to "settings put global windowsmgr.max_events_per_sec 150",
        "scroll friction reduzida" to "settings put global view.scroll_friction 0.005",
        "touch pressure scale reduzida" to "settings put global touch.pressure.scale 0.001",
        "scrollingcache 3" to "settings put global persist.sys.scrollingcache 3",
        "performance profile ON" to "settings put global debug.performance.profile 1",
        "pcsync OFF" to "settings put global persist.service.pcsync.enable 0",
        "lgospd OFF" to "settings put global persist.service.lgospd.enable 0",
        "activity start logging OFF" to "settings put global activity_starts_logging_enabled 0",
        "security reports OFF" to "settings put global send_security_reports 0",
        "gamesdk_version OFF (GOS)" to "settings put secure gamesdk_version 0",
        "game_home OFF" to "settings put secure game_home_enable 0",
        "zram OFF" to "settings put global zram_enabled 0",

        "Limpeza profunda de cache" to "pm trim-caches 128G",
        "Max cached processes 10" to "settings put global activity_manager_constants max_cached_processes=10",
        "profilebootclasspath" to "device_config put runtime_native_boot profilebootclasspath true",
        "app_image_startup_cache" to "device_config put runtime_native_boot use_app_image_startup_cache true",
        "Matar processos em segundo plano" to "am kill-all",
        "Simular carregamento (bateria)" to "dumpsys battery set status 2",
        "Reset resolução" to "wm size reset",
        "Reset densidade" to "wm density reset",
        "Wi-Fi nunca dormir" to "settings put global wifi_sleep_policy 2",
        "Tela ligada ao carregar" to "settings put global stay_on_while_plugged_in 3",
        "Economia de energia OFF" to "settings put global low_power_mode 0",
        "Sincronização de hora OFF" to "settings put global auto_time 0",
        "Fuso automático OFF" to "settings put global auto_time_zone 0",
        "Bluetooth OFF" to "settings put global bluetooth_on 0",
        "Adaptive low power OFF" to "settings put global adaptive_low_power_setting 0",
        "Roaming OFF" to "settings put global data_roaming 0",
        "Dados móveis sempre ativos OFF" to "settings put global mobile_data_always_on 0",
        "Wi-Fi scan sempre ativo OFF" to "settings put global wifi_scan_always_enabled 0",
        "BLE scan sempre ativo OFF" to "settings put global ble_scan_always_enabled 0",
        "GPS OFF" to "settings put global location_mode 0",

        "DNS privado 1.1.1.1 (modo)" to "settings put global private_dns_mode hostname",
        "DNS privado 1.1.1.1 (host)" to "settings put global private_dns_specifier one.one.one.one"
    )

    /** Reverso exato do APPLY, na mesma ordem, pra usar no botão Desativar. */
    val RESET: List<Pair<String, String>> = listOf(
        "Modo performance fixo OFF" to "cmd power set-fixed-performance-mode-enabled false",
        "Taxa de atualização (peak) 60Hz" to "settings put system peak_refresh_rate 60.0",
        "Taxa de atualização (min) 60Hz" to "settings put system min_refresh_rate 60.0",
        "Thermal throttling padrão" to "settings put secure game_auto_temperature_control 1",
        "Reset compilação (FF Normal)" to "cmd package compile --reset com.dts.freefireth",
        "Reset compilação (FF Max)" to "cmd package compile --reset com.dts.freefiremax",

        "Animação de janela padrão" to "settings put global window_animation_scale 1.0",
        "Animação de transição padrão" to "settings put global transition_animation_scale 1.0",
        "Duração de animator padrão" to "settings put global animator_duration_scale 1.0",
        "Reduzir transparência OFF" to "settings put global accessibility_reduce_transparency 0",
        "Blur de janelas padrão" to "settings put global disable_window_blurs 0",
        "Multicore packet scheduler OFF" to "settings put system multicore_packet_scheduler 0",
        "Long press timeout padrão" to "settings put secure long_press_timeout 500",
        "Multi press timeout padrão" to "settings put secure multi_press_timeout 500",
        "Tap duration threshold padrão" to "settings put secure tap_duration_threshold 150",
        "Touch blocking period padrão" to "settings put secure touch_blocking_period 100",
        "debug.egl.hw OFF" to "settings put global debug.egl.hw 0",
        "debug.egl.profiler OFF" to "settings put global debug.egl.profiler 0",
        "debug.sf.hw OFF" to "settings put global debug.sf.hw 0",
        "debug.sf.latch_unsignaled OFF" to "settings put global debug.sf.latch_unsignaled 0",
        "debug.composition.type auto" to "settings put global debug.composition.type auto",
        "framebuffer surface buffers 2" to "settings put global debug.gr.num_framebuffer_surface_buffers 2",
        "dithering padrão" to "settings put global persist.sys.use_dithering 1",
        "purgeable assets OFF" to "settings put global persist.sys.purgeable_assets 0",
        "ui.hw OFF" to "settings put global persist.sys.ui.hw 0",
        "windowsmgr max events/sec 90" to "settings put global windowsmgr.max_events_per_sec 90",
        "scroll friction padrão" to "settings put global view.scroll_friction 0.01",
        "touch pressure scale padrão" to "settings put global touch.pressure.scale 0.003",
        "scrollingcache padrão" to "settings put global persist.sys.scrollingcache 1",
        "performance profile OFF" to "settings put global debug.performance.profile 0",
        "pcsync ON" to "settings put global persist.service.pcsync.enable 1",
        "lgospd ON" to "settings put global persist.service.lgospd.enable 1",
        "activity start logging ON" to "settings put global activity_starts_logging_enabled 1",
        "security reports ON" to "settings put global send_security_reports 1",
        "gamesdk_version ON (GOS)" to "settings put secure gamesdk_version 1",
        "game_home ON" to "settings put secure game_home_enable 1",
        "zram ON" to "settings put global zram_enabled 1",

        "Max cached processes padrão" to "settings put global activity_manager_constants max_cached_processes=32",
        "Reset resolução" to "wm size reset",
        "Reset densidade" to "wm density reset",
        "Wi-Fi sleep policy padrão" to "settings put global wifi_sleep_policy 0",
        "Tela ligada ao carregar OFF" to "settings put global stay_on_while_plugged_in 0",
        "Economia de energia ON" to "settings put global low_power_mode 1",
        "Sincronização de hora ON" to "settings put global auto_time 1",
        "Fuso automático ON" to "settings put global auto_time_zone 1",
        "Bluetooth ON" to "settings put global bluetooth_on 1",
        "Adaptive low power ON" to "settings put global adaptive_low_power_setting 1",
        "Roaming ON" to "settings put global data_roaming 1",
        "Dados móveis sempre ativos ON" to "settings put global mobile_data_always_on 1",
        "Wi-Fi scan sempre ativo ON" to "settings put global wifi_scan_always_enabled 1",
        "BLE scan sempre ativo ON" to "settings put global ble_scan_always_enabled 1",
        "GPS padrão (alta precisão)" to "settings put global location_mode 3",
        "Bateria: reset simulação" to "dumpsys battery reset",

        "DNS privado padrão (off)" to "settings put global private_dns_mode off"
    )
}
