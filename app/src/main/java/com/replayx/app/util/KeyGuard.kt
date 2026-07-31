package com.replayx.app.util

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.replayx.app.ui.ExpiredActivity

/**
 * Trava o app localmente quando o tempo da key acaba, sem depender de
 * nenhuma chamada de rede depois do login. O tempo restante é calculado
 * inteiramente a partir do que já foi validado no Firestore no momento do
 * login (firstUsed/days/minutes/status), guardado em SharedPreferences.
 *
 * Cada tela chama KeyGuard.attach(activity) no onCreate: isso checa o tempo
 * na hora e continua checando a cada 1s enquanto a tela estiver visível. Se
 * expirar, abre a ExpiredActivity por cima (tela cheia, sem botão voltar,
 * sem toque em nada por trás) e fecha a tela atual.
 */
object KeyGuard {

    private const val PREFS = "replayx_prefs"

    /** Tempo restante em ms, ou -1 se não houver key salva (não bloqueia nesse caso). */
    fun remainingMs(activity: Activity): Long {
        val p = activity.getSharedPreferences(PREFS, Activity.MODE_PRIVATE)
        val first = p.getLong("kx_first", -1L)
        if (first < 0L) return -1L
        val days = p.getInt("kx_days", 0)
        val minutes = p.getInt("kx_minutes", 0)
        val status = p.getString("kx_status", "active")
        val paused = p.getLong("kx_paused", 0L)
        val totalMs = (days * 86400L + minutes * 60L) * 1000L
        if (totalMs <= 0L) return Long.MAX_VALUE // key sem expiração
        val usedMs = if (status == "paused" && paused > 0L)
            (paused - first) * 1000L
        else
            System.currentTimeMillis() - first * 1000L
        return (totalMs - usedMs).coerceAtLeast(0L)
    }

    /** Chama no onCreate de qualquer Activity protegida. */
    fun attach(activity: Activity): Handler {
        val handler = Handler(Looper.getMainLooper())
        val check = object : Runnable {
            override fun run() {
                if (activity.isFinishing) return
                val rem = remainingMs(activity)
                if (rem != -1L && rem != Long.MAX_VALUE && rem <= 0L) {
                    lock(activity)
                    return
                }
                handler.postDelayed(this, 1000L)
            }
        }
        handler.post(check)
        return handler
    }

    /** Abre a tela de bloqueio por cima de tudo e fecha a Activity atual. */
    fun lock(activity: Activity) {
        val i = Intent(activity, ExpiredActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(i)
        activity.finish()
    }

    fun clearSavedKey(activity: Activity) {
        activity.getSharedPreferences(PREFS, Activity.MODE_PRIVATE).edit()
            .remove("saved_key")
            .remove("auto_login")
            .remove("auto_kstr")
            .remove("kx_first")
            .apply()
    }
}
