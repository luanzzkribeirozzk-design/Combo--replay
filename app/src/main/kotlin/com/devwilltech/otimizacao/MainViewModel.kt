package com.devwilltech.otimizacao

import android.app.Application
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devwilltech.otimizacao.data.AdbCommand
import com.devwilltech.otimizacao.data.CommandRepository
import com.devwilltech.otimizacao.data.CommandResult
import com.devwilltech.otimizacao.utils.ShizukuManager
import com.devwilltech.otimizacao.utils.PrefsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

enum class ShizukuStatus {
    CHECKING, NOT_INSTALLED, NOT_RUNNING, PERMISSION_NEEDED, READY
}

data class KeyData(
    val id: String,
    val firstUsed: String? = null,
    val validity: Int = 0,
    val deviceId: String? = null
)

data class MainUiState(
    val showWelcomePopup: Boolean      = false,
    val shizukuStatus: ShizukuStatus   = ShizukuStatus.CHECKING,
    val isRunning: Boolean             = false,
    val isResetting: Boolean           = false,
    val currentCommandIndex: Int       = 0,
    val totalCommands: Int             = 0,
    val results: List<CommandResult>   = emptyList(),
    val showReport: Boolean            = false,
    val selectedCategory: String       = "Todos",
    val isHideStreamEnabled: Boolean   = false,
    val savedKey: String               = "",
    val bypassLogs: List<String>       = emptyList(),
    val isBypassing: Boolean           = false,
    val isLoggedIn: Boolean            = false,
    val keyData: KeyData?              = null,
    val loginError: String?            = null,
    val isLoggingIn: Boolean           = false,
    val processingMessage: String      = "",
    val processingProgress: Float      = 0f
) {
    val canAct: Boolean
        get() = shizukuStatus == ShizukuStatus.READY && !isRunning && !isResetting
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PrefsManager(application)
    
    private val _uiState = MutableStateFlow(MainUiState(
        showWelcomePopup = prefs.showWelcomePopup,
        isHideStreamEnabled = prefs.isHideStreamEnabled(),
        savedKey = prefs.savedKey,
        isLoggedIn = prefs.savedKey.isNotEmpty() // Auto-login se houver key salva
    ))
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        refreshShizukuStatus()
    }

    fun login(key: String) {
        prefs.savedKey = key
        _uiState.update { it.copy(isLoggedIn = true, savedKey = key) }
    }

    fun logout() {
        prefs.savedKey = ""
        _uiState.update { it.copy(isLoggedIn = false, savedKey = "") }
    }

    fun dismissWelcomePopup(dontShowAgain: Boolean) {
        if (dontShowAgain) prefs.showWelcomePopup = false
        _uiState.update { it.copy(showWelcomePopup = false) }
    }

    fun toggleHideStream(enabled: Boolean) {
        prefs.setHideStreamEnabled(enabled)
        _uiState.update { it.copy(isHideStreamEnabled = enabled) }
    }

    fun refreshShizukuStatus() {
        val status = when {
            !ShizukuManager.isShizukuAvailable()  -> ShizukuStatus.NOT_RUNNING
            !ShizukuManager.isPermissionGranted() -> ShizukuStatus.PERMISSION_NEEDED
            else                                   -> ShizukuStatus.READY
        }
        _uiState.update { it.copy(shizukuStatus = status) }
        
        if (status == ShizukuStatus.PERMISSION_NEEDED) {
            requestShizukuPermission()
        }
    }

    fun requestShizukuPermission() = ShizukuManager.requestPermission()

    fun activateAll() {
        if (_uiState.value.shizukuStatus != ShizukuStatus.READY) {
            refreshShizukuStatus()
            return
        }
        val commands = CommandRepository.optimisationCommands
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isRunning           = true,
                    currentCommandIndex = 0,
                    totalCommands       = commands.size,
                    results             = emptyList(),
                    showReport          = false,
                    processingMessage   = "ATIVANDO OTIMIZAÇÕES..."
                )
            }
            commands.forEachIndexed { index, cmd ->
                _uiState.update { 
                    it.copy(
                        currentCommandIndex = index + 1,
                        processingProgress = (index + 1).toFloat() / commands.size
                    ) 
                }
                ShizukuManager.execute(cmd.command)
                delay(10)
            }
            _uiState.update { it.copy(isRunning = false, showReport = true) }
        }
    }

    fun deactivateAll() {
        if (_uiState.value.shizukuStatus != ShizukuStatus.READY) {
            refreshShizukuStatus()
            return
        }
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isResetting = true, 
                    currentCommandIndex = 0, 
                    totalCommands = 1,
                    showReport = false,
                    processingMessage = "RESTAURANDO PADRÕES..."
                ) 
            }
            _uiState.update { it.copy(currentCommandIndex = 1, processingProgress = 1f) }
            ShizukuManager.execute(CommandRepository.resetCommand.command)
            delay(500)
            _uiState.update { it.copy(isResetting = false) }
        }
    }

    fun startBypass(type: String) {
        if (_uiState.value.shizukuStatus != ShizukuStatus.READY) {
            refreshShizukuStatus()
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isBypassing = true, bypassLogs = it.bypassLogs + "Iniciando Bypass...") }
            
            val commands = if (type == "MAX_TO_NORMAL") {
                listOf(
                    "rm -rf /data/data/com.dts.freefiremax/files/replays",
                    "mkdir -p /data/data/com.dts.freefiremax/files",
                    "cp -r /data/data/com.dts.freefireth/files/replays /data/data/com.dts.freefiremax/files/"
                )
            } else {
                listOf(
                    "rm -rf /data/data/com.dts.freefireth/files/replays",
                    "mkdir -p /data/data/com.dts.freefireth/files",
                    "cp -r /data/data/com.dts.freefiremax/files/replays /data/data/com.dts.freefireth/files/"
                )
            }

            commands.forEach { cmd ->
                ShizukuManager.execute(cmd)
                _uiState.update { it.copy(bypassLogs = it.bypassLogs + cmd) }
                delay(200)
            }
            
            _uiState.update { it.copy(isBypassing = false, bypassLogs = it.bypassLogs + "Bypass Concluído!") }
        }
    }

    fun clearBypassLogs() {
        _uiState.update { it.copy(bypassLogs = emptyList()) }
    }

    fun dismissReport() = _uiState.update { it.copy(showReport = false) }
}
