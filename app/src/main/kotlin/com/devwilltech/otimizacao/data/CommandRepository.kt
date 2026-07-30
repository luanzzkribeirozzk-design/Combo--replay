package com.devwilltech.otimizacao.data

// ─── Domain model ──────────────────────────────────────────────────────────────
data class AdbCommand(
    val id: Int,
    val category: String,
    val name: String,
    val description: String,
    val command: String
)

// ─── Result per command after execution ───────────────────────────────────────
data class CommandResult(
    val command: AdbCommand,
    val success: Boolean,
    val output: String = ""
)

// ─── All 100 optimisation commands + reset + monitors ─────────────────────────
object CommandRepository {

    val resetCommand = AdbCommand(
        id = 0,
        category = "Sistema",
        name = "Restaurar Padrões",
        description = "Restaurar Padrões de Fábrica e Limpar Otimizações",
        command = """
            settings put system peak_refresh_rate 60.0;
            settings put system min_refresh_rate 60.0;
            settings put global window_animation_scale 1.0;
            settings put global transition_animation_scale 1.0;
            settings put global animator_duration_scale 1.0;
            settings put global thermal_limit_refresh_rate 1;
            setprop debug.hwui.renderer "";
            setprop debug.cpurend.vsync true;
            settings put global max_cached_processes 32;
            settings put global low_power 0;
            settings put global app_standby_enabled 1;
            cmd game mode reset com.dts.freefireth;
            cmd game mode reset com.dts.freefiremax;
            cmd appops reset com.dts.freefireth;
            cmd appops reset com.dts.freefiremax;
            cmd deviceidle whitelist -com.dts.freefireth;
            cmd deviceidle whitelist -com.dts.freefiremax;
            settings put global wifi_scan_throttle_enabled 1;
            settings put global wifi_data_performance_mode 0;
            settings put system screen_brightness_mode 1;
            settings put global smart_battery_management_enabled 1;
            settings put global adaptive_battery_management_enabled 1;
            setprop persist.sys.performance.level 0;
            setprop persist.sys.extreme_mode 0;
            am force-stop com.dts.freefireth;
            am force-stop com.dts.freefiremax
        """.trimIndent()
    )

    val bypassCommands = listOf(
        AdbCommand(201, "Bypass", "Bypass Normal -> Max", "Migrar Replays do FF Normal para o Max", "rm -rf /data/data/com.dts.freefiremax/files/replays/* && cp -r /data/data/com.dts.freefireth/files/replays/* /data/data/com.dts.freefiremax/files/replays/"),
        AdbCommand(202, "Bypass", "Bypass Max -> Normal", "Migrar Replays do FF Max para o Normal", "rm -rf /data/data/com.dts.freefireth/files/replays/* && cp -r /data/data/com.dts.freefiremax/files/replays/* /data/data/com.dts.freefireth/files/replays/")
    )

    val optimisationCommands: List<AdbCommand> = listOf(
        AdbCommand(1,  "Sistema",    "Status Otimização", "Status de Otimização",                          "setprop devwilltech.opt.status \"optimized\""),
        AdbCommand(2,  "Sistema",    "Cache Performance", "Diretório de Cache de Performance",             "mkdir -p /sdcard/Android/data/com.performance.optimizer/cache"),
        AdbCommand(3,  "Tela",       "Taxa Máxima Hz", "Taxa Máxima de Hz",                             "settings put system peak_refresh_rate 144.0"),
        AdbCommand(4,  "Tela",       "Taxa Mínima Hz", "Taxa Mínima de Hz",                             "settings put system min_refresh_rate 144.0"),
        AdbCommand(5,  "Tela",       "Velocidade Animações", "Velocidade das Animações",                      "settings put global window_animation_scale 0.5; settings put global transition_animation_scale 0.5; settings put global animator_duration_scale 0.5"),
        AdbCommand(6,  "Desempenho", "Modo Performance", "Modo Performance Nativo",                       "cmd power set-mode 0"),
        AdbCommand(7,  "Desempenho", "Mitigação Térmica", "Mitigação Térmica",                             "settings put global thermal_limit_refresh_rate 0"),
        AdbCommand(8,  "Gráficos",   "Render GPU", "Renderização por GPU",                          "setprop debug.hwui.renderer opengl"),
        AdbCommand(9,  "Desempenho", "Ignorar Restrições", "Ignorar Perfis Restritos",                      "setprop power.those_layers 0"),
        AdbCommand(10, "Jogo",       "Compilar FF Speed", "Compilar Free Fire – Velocidade Máxima",        "cmd package compile -m speed -f com.dts.freefireth"),
        AdbCommand(11, "Jogo",       "Compilar FF Max Speed", "Compilar Free Fire Max – Velocidade Máxima",    "cmd package compile -m speed -f com.dts.freefiremax"),
        AdbCommand(12, "Jogo",       "Compilar Layout FF", "Forçar Compilação de Layout FF",                "cmd package compile -m speed-profile -f com.dts.freefireth"),
        AdbCommand(13, "Jogo",       "Compilar Layout FF Max", "Forçar Compilação de Layout FF Max",            "cmd package compile -m speed-profile -f com.dts.freefiremax"),
        AdbCommand(14, "Sistema",    "Otimizar Cache Dex", "Otimizar Cache Dex Geral",                      "cmd package bg-dexopt-job"),
        AdbCommand(15, "Jogo",       "Background FF", "Ignorar Restrições de Fundo – FF",              "cmd appops set com.dts.freefireth RUN_IN_BACKGROUND allow"),
        AdbCommand(16, "Jogo",       "Background FF Max", "Ignorar Restrições de Fundo – FF Max",          "cmd appops set com.dts.freefiremax RUN_IN_BACKGROUND allow"),
        AdbCommand(17, "Jogo",       "Whitelist Bateria FF", "Isentar FF de Economia de Bateria",             "cmd deviceidle whitelist +com.dts.freefireth"),
        AdbCommand(18, "Jogo",       "Whitelist Bateria FF Max", "Isentar FF Max de Economia de Bateria",         "cmd deviceidle whitelist +com.dts.freefiremax"),
        AdbCommand(19, "Jogo",       "Alta Performance FF", "Forçar Modo Alta Performance Appops – FF",      "cmd appops set com.dts.freefireth FORCE_DEVICE_IDLE_EXEMPTION allow"),
        AdbCommand(20, "Jogo",       "Alta Performance FF Max", "Forçar Modo Alta Performance Appops – FF Max",  "cmd appops set com.dts.freefiremax FORCE_DEVICE_IDLE_EXEMPTION allow"),
        AdbCommand(21, "Jogo",       "Prioridade CPU FF", "Prioridade Extrema de CPU – FF",                "renice -n -20 \$(pidof com.dts.freefireth) 2>/dev/null"),
        AdbCommand(22, "Jogo",       "Prioridade CPU FF Max", "Prioridade Extrema de CPU – FF Max",            "renice -n -20 \$(pidof com.dts.freefiremax) 2>/dev/null"),
        AdbCommand(23, "Jogo",       "Agendador FF", "Prioridade de Agendador de Tarefas – FF",       "chrt -p -f 99 \$(pidof com.dts.freefireth) 2>/dev/null"),
        AdbCommand(24, "Jogo",       "Agendador FF Max", "Prioridade de Agendador de Tarefas – FF Max",   "chrt -p -f 99 \$(pidof com.dts.freefiremax) 2>/dev/null"),
        AdbCommand(25, "Gráficos",   "Vulkan", "Habilitar Camada Vulkan",                       "setprop debug.hwui.renderer vulkan"),
        AdbCommand(26, "Gráficos",   "Skia GL", "Forçar Renderizador Skia GL",                   "setprop debug.hwui.renderer skiagl"),
        AdbCommand(27, "Gráficos",   "Composição HWC", "Acelerar Composição de Tela HWC",               "setprop debug.sf.hw 1"),
        AdbCommand(28, "Gráficos",   "Desativar VSync", "Desativar Alinhamento VSync",                   "setprop debug.cpurend.vsync false"),
        AdbCommand(29, "Desempenho", "GPU Skia Thread", "Forçar Thread de GPU Skia",                     "setprop renderthread.skia.reduceopstasksplitting true"),
        AdbCommand(30, "Gráficos",   "Cache Texturas", "Otimizar Cache de Texturas 2D",                 "setprop ro.hwui.texture_cache_size 72"),
        AdbCommand(31, "Gráficos",   "Cache Camadas", "Aumentar Cache de Camadas Gráficas",            "setprop ro.hwui.layer_cache_size 48"),
        AdbCommand(32, "Gráficos",   "Alocação Path", "Alocação de Renderização de Path",              "setprop ro.hwui.path_cache_size 32"),
        AdbCommand(33, "Gráficos",   "Debug Overdraw", "Habilitar Overdraw Debug",                      "setprop debug.hwui.show_overdraw false"),
        AdbCommand(34, "Gráficos",   "Threads GPU", "Forçar Linhas de Threads de GPU",               "setprop debug.gr.numthreads 4"),
        AdbCommand(35, "Touch",      "Latência Toque", "Reduzir Latência de Resposta",                  "setprop view.touch_slop 2"),
        AdbCommand(36, "Touch",      "Taxa Amostragem", "Aumentar Taxa de Amostragem de Toque",          "setprop debug.performance.tuning 1"),
        AdbCommand(37, "Touch",      "Fila Eventos", "Prioridade na Fila de Eventos de Input",        "setprop windowsmgr.max_events_per_sec 300"),
        AdbCommand(38, "Touch",      "Delay Scroll", "Desativar Delay de Toque em Filtros",           "setprop view.scroll_friction 0.005"),
        AdbCommand(39, "Touch",      "Cache Scroll", "Estabilizar Sensibilidade contra Drop de Frame","setprop persist.sys.scrolling.cache 3"),
        AdbCommand(40, "Touch",      "Filtro Toque", "Filtragem Estrita de Input de Toque",           "setprop touch.deviceType touchScreen"),
        AdbCommand(41, "Touch",      "Resposta Cursor", "Acelerar Tempo de Resposta do Cursor",          "setprop ro.input.noresample false"),
        AdbCommand(42, "Memória",    "Limpeza RAM", "Limpeza de RAM e SystemUI",                     "cmd activity trim-caches; pidof com.android.systemui | xargs kill -9 2>/dev/null"),
        AdbCommand(43, "Memória",    "Limite Background", "Desativar Limite de Apps em Segundo Plano",     "settings put global max_cached_processes 64"),
        AdbCommand(44, "Memória",    "Alocador RAM", "Otimização do Alocador de RAM",                 "setprop sys.sysctl.extra_free_kbytes 20480"),
        AdbCommand(45, "Desempenho", "Verificação Dex", "Desativar Verificação de Erros do Dex",         "setprop dalvik.vm.verify-bytecode false"),
        AdbCommand(46, "Desempenho", "Heap Size", "Aumentar Tamanho do Buffer de Execução",        "setprop dalvik.vm.heapsize 512m"),
        AdbCommand(47, "Desempenho", "Heap Start", "Forçar Alocação Inicial de Memória",            "setprop dalvik.vm.heapstartsize 16m"),
        AdbCommand(48, "Memória",    "Launcher Adj", "Impedir Encerramento Agressivo de Launcher",    "setprop ro.HOME_APP_ADJ -17"),
        AdbCommand(49, "Sistema",    "Limpar Dalvik", "Limpar Cache de Dalvik VM Completo",            "pm trim-caches 999G"),
        AdbCommand(50, "Memória",    "Heap Growth", "Forçar Crescimento Limpo de Heap",              "setprop dalvik.vm.heapgrowthlimit 256m"),
        AdbCommand(51, "Memória",    "Cache JIT", "Ajustar Alocação de Cache JIT",                 "setprop dalvik.vm.jitthreshold 32"),
        AdbCommand(52, "Rede",       "Buffer Wi-Fi", "Aumentar Buffer Wi-Fi",                         "setprop net.tcp.buffersize.wifi 262144,524288,1048576,262144,524288,1048576"),
        AdbCommand(53, "Rede",       "Wi-Fi Sleep", "Travar Wi-Fi Ativo",                            "settings put global wifi_sleep_policy 2"),
        AdbCommand(54, "Rede",       "Wi-Fi Throttle", "Desativar Throttle de Busca Wi-Fi",             "settings put global wifi_scan_throttle_enabled 0"),
        AdbCommand(55, "Rede",       "Wi-Fi Perf", "Forçar Uso Máximo do Chip Wi-Fi",               "settings put global wifi_data_performance_mode 1"),
        AdbCommand(56, "Rede",       "Buffer LTE/5G", "Aumentar Buffer de Rede Móvel LTE/5G",          "setprop net.tcp.buffersize.lte 262144,524288,1048576"),
        AdbCommand(57, "Rede",       "Latência DNS", "Otimizar Latência de Pacotes TCP",              "setprop net.rmnet0.dns1 8.8.8.8"),
        AdbCommand(58, "Rede",       "Handover Dados", "Desativar Handover Agressivo de Dados",         "settings put global mobile_data_always_on 1"),
        AdbCommand(59, "Áudio",      "Latência Áudio", "Reduzir Latência de Som em Jogos",              "setprop ro.audio.latency.bg 2"),
        AdbCommand(60, "Áudio",      "Deep Buffer", "Forçar Modo de Baixa Latência Nativo",          "setprop audio.deep_buffer.media false"),
        AdbCommand(61, "Sistema",    "Logcat Tags", "Desativar Captura de Logs Logcat",              "setprop log.tag.all 0"),
        AdbCommand(62, "Sistema",    "Error Reports", "Desativar Envio de Relatórios de Erro",         "settings put global send_action_mb_app 0"),
        AdbCommand(63, "Bateria",    "Ultra Power", "Forçar Desligamento de Economia Ultra",         "settings put secure ultra_powersaving_mode 0"),
        AdbCommand(64, "Bateria",    "Low Power", "Desativar Modo de Baixo Consumo Global",        "settings put global low_power 0"),
        AdbCommand(65, "Bateria",    "App Standby", "Desativar Hibernação de Apps Externa",          "settings put global app_standby_enabled 0"),
        AdbCommand(66, "Sistema",    "FHA Enable", "Travar Agressor de Desempenho do Kernel",       "setprop ro.config.fha_enable false"),
        AdbCommand(67, "Desempenho", "4x MSAA", "Habilitar Forçar 4x MSAA",                     "settings put global hardware_accelerated_rendering true"),
        AdbCommand(68, "Desempenho", "Engine Opt", "Finalizar Otimização Extrema do Sistema",       "setprop devwilltech.opt.engine \"EXTROME_FF_MAX_CLEAN\""),
        AdbCommand(69, "Gráficos",   "EGL Swap", "Swap Intermitente de Buffers EGL",              "setprop debug.egl.swapinterval 0"),
        AdbCommand(70, "Gráficos",   "EGL HW", "Ignorar Profiler de Frames Ocioso",             "setprop debug.egl.hw 1"),
        AdbCommand(71, "Touch",      "Input Filter", "Forçar Sincronização de Filtros de Input",      "setprop persist.input.filter.enabled 0"),
        AdbCommand(72, "Touch",      "Slop Precision", "Remover Limitador de Eventos de Movimento",     "setprop view.touch_slop_precision 1"),
        AdbCommand(73, "Desempenho", "Thermal Mitigation", "Desativar Controle de Frame Drop por Térmico",  "settings put global thermal_mitigation_ready 0"),
        AdbCommand(74, "Desempenho", "FIFO UI", "Priorizar Tarefas em Foco no Scheduler",        "setprop sys.use_fifo_ui 1"),
        AdbCommand(75, "Desempenho", "JIT Profiles", "Desativar Verificação de Profiling JIT",        "setprop dalvik.vm.usejitprofiles false"),
        AdbCommand(76, "Memória",    "Low RAM False", "Alocação Limpa de Buffer Gráfico no Heap",      "setprop ro.config.low_ram false"),
        AdbCommand(77, "Memória",    "GCType 2g", "Otimizar Garbage Collection para Telas",        "setprop dalvik.vm.gctype \"2g\""),
        AdbCommand(78, "Sistema",    "Disable Atlas", "Desativar Verificação de Logs de Atividades",   "setprop config.disable_atlas true"),
        AdbCommand(79, "Sistema",    "App Offset", "Prevenir Sleep de Thread de Render no Core",    "setprop debug.sf.early_app_phase_offset_ns 5000000"),
        AdbCommand(80, "Sistema",    "GL App Offset", "Prevenir Sleep de Thread do Hardware Composer", "setprop debug.sf.early_gl_app_phase_offset_ns 15000000"),
        AdbCommand(81, "Bateria",    "Smart Battery", "Desativar Throttle por Economia Inteligente",   "settings put global smart_battery_management_enabled 0"),
        AdbCommand(82, "Bateria",    "Adaptive Battery", "Forçar Desativação de Standby Adaptativo",      "settings put global adaptive_battery_management_enabled 0"),
        AdbCommand(83, "Rede",       "Bandwidth Polling", "Desativar Suspensão de Dados em Background",    "settings put global bandwidth_avail_polling_ms 0"),
        AdbCommand(84, "Rede",       "TCP Buffer Def", "Otimizar Janela de Recepção TCP",               "setprop net.tcp.buffersize.default 4096,87380,110208,4096,16384,110208"),
        AdbCommand(85, "Rede",       "TCP Fast Open", "Habilitar TCP Fast Open",                      "setprop net.ipv4.tcp_fastopen 3"),
        AdbCommand(86, "Touch",      "Touch Resample", "Desativar Reamostragem de Input",              "setprop debug.input.noresample 1"),
        AdbCommand(87, "Desempenho", "CPU Boost", "Forçar Boost de CPU em Toque",                  "setprop sys.perf.boost 1"),
        AdbCommand(88, "Sistema",    "Dexopt Speed", "Compilar Sistema para Velocidade",             "cmd package compile -m speed -a"),
        AdbCommand(89, "Jogo",       "Game Driver", "Habilitar Driver de Jogo Global",               "settings put global game_driver_all_apps 1"),
        AdbCommand(90, "Gráficos",   "GPU Layers", "Habilitar Camadas de Debug de GPU",              "settings put global enable_gpu_debug_layers 1"),
        AdbCommand(91, "Gráficos",   "Force GPU", "Forçar Renderização 2D por GPU",                 "settings put global hwui_renderer opengl"),
        AdbCommand(92, "Sistema",    "Kernel Samepage", "Habilitar KSM (Kernel Samepage Merging)",     "setprop ro.config.ksm.support true"),
        AdbCommand(93, "Memória",    "ZRAM Size", "Aumentar Cache de ZRAM",                         "setprop ro.config.zram_size 1073741824"),
        AdbCommand(94, "Desempenho", "IO Scheduler", "Mudar Agendador de Disco para Noop",            "echo noop > /sys/block/mmcblk0/queue/scheduler"),
        AdbCommand(95, "Rede",       "IPv6 Privacy", "Otimizar Privacidade IPv6",                    "setprop net.ipv6.conf.all.use_tempaddr 2"),
        AdbCommand(96, "Sistema",    "Panic Reboot", "Desativar Reboot em Kernel Panic",              "setprop ro.kernel.android.checkjni 0"),
        AdbCommand(97, "Gráficos",   "Dithering", "Desativar Dithering Gráfico",                    "setprop persist.sys.use_dithering 0"),
        AdbCommand(98, "Touch",      "Long Press", "Reduzir Tempo de Toque Longo",                  "settings put secure long_press_timeout 250"),
        AdbCommand(99, "Sistema",    "User 0 Opt", "Otimizar Usuário 0 para Jogos",                  "cmd user optimize 0"),
        AdbCommand(100, "Desempenho", "Final Step", "Ativar Modo Extremo Final",                    "setprop persist.sys.extreme_mode 1")
    )
}
