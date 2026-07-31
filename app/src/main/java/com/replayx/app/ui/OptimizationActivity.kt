package com.replayx.app.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.replayx.app.databinding.ActivityOptimizationBinding
import com.replayx.app.util.OptimizationCommands
import com.replayx.app.util.KeyGuard
import com.replayx.app.util.ShizukuHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OptimizationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOptimizationBinding
    private val SHIZUKU_CODE = 2001
    private val binderReceived = Shizuku.OnBinderReceivedListener { updateStatus(true) }
    private val binderDead = Shizuku.OnBinderDeadListener { updateStatus(false) }
    private var running = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOptimizationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        KeyGuard.attach(this)

        Shizuku.addBinderReceivedListenerSticky(binderReceived)
        Shizuku.addBinderDeadListener(binderDead)
        updateStatus(try { Shizuku.pingBinder() } catch (e: Exception) { false })

        binding.btnAtivarOtimizacao.setOnClickListener {
            if (checkShizuku()) runCommands("ATIVAR OTIMIZAÇÃO", OptimizationCommands.APPLY)
        }
        binding.btnDesativarOtimizacao.setOnClickListener {
            if (checkShizuku()) runCommands("DESATIVAR OTIMIZAÇÃO", OptimizationCommands.RESET)
        }
        binding.btnClearLogOpt.setOnClickListener { binding.tvLogOpt.text = "" }
        binding.btnVoltarOpt.setOnClickListener { finish() }
    }

    private fun runCommands(title: String, cmds: List<Pair<String, String>>) {
        if (running) return
        running = true
        binding.btnAtivarOtimizacao.isEnabled = false
        binding.btnDesativarOtimizacao.isEnabled = false
        lifecycleScope.launch {
            log("--------------------------------")
            log("[SYS] >> $title (${cmds.size} comandos)")
            var ok = 0
            var fail = 0
            for ((label, cmd) in cmds) {
                val result = withContext(Dispatchers.IO) { ShizukuHelper.run(cmd) }
                val failed = result.startsWith("ERR") || result.contains("Exception")
                if (failed) fail++ else ok++
                log("[${if (failed) "ERR" else "OK "}] $label")
                delay(30L)
            }
            log("[SYS] >> concluído: $ok ok, $fail falharam")
            log("--------------------------------")
            running = false
            binding.btnAtivarOtimizacao.isEnabled = true
            binding.btnDesativarOtimizacao.isEnabled = true
        }
    }

    private fun checkShizuku(): Boolean {
        return try {
            if (!Shizuku.pingBinder()) {
                log("[ERR] SHIZUKU_NAO_ATIVO")
                AlertDialog.Builder(this)
                    .setTitle("Shizuku necessario")
                    .setMessage("O Shizuku nao esta ativo.\n\n1. Abra o app Shizuku\n2. Ative o servico\n3. Volte e tente novamente")
                    .setPositiveButton("Abrir Shizuku") { _, _ ->
                        try {
                            val intent = packageManager.getLaunchIntentForPackage("moe.shizuku.privileged.api")
                            if (intent != null) startActivity(intent)
                            else log("[ERR] SHIZUKU_NAO_INSTALADO")
                        } catch (e: Exception) {
                            log("[ERR] Instale o Shizuku primeiro")
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
                false
            } else if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                log("[SYS] SHIZUKU_SOLICITANDO_PERMISSAO")
                Shizuku.requestPermission(SHIZUKU_CODE)
                false
            } else true
        } catch (ex: Exception) {
            log("[ERR] SHIZUKU: " + ex.message.orEmpty())
            false
        }
    }

    private fun log(msg: String) {
        val t = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        val cur = binding.tvLogOpt.text.toString()
        val sep = System.lineSeparator()
        val next = if (cur.isEmpty()) "[$t] $msg" else "$cur$sep[$t] $msg"
        binding.tvLogOpt.text = next
        binding.scrollLogOpt.post { binding.scrollLogOpt.fullScroll(View.FOCUS_DOWN) }
    }

    private fun updateStatus(active: Boolean) {
        runOnUiThread {
            if (active) {
                binding.tvShizukuStatusOpt.text = "● SHIZUKU ATIVO"
                binding.tvShizukuStatusOpt.setTextColor(getColor(android.R.color.holo_green_light))
            } else {
                binding.tvShizukuStatusOpt.text = "● SHIZUKU INATIVO"
                binding.tvShizukuStatusOpt.setTextColor(getColor(android.R.color.holo_red_light))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeBinderReceivedListener(binderReceived)
        Shizuku.removeBinderDeadListener(binderDead)
    }
}
