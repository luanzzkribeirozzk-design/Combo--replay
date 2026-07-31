package com.replayx.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.replayx.app.databinding.ActivityExpiredBinding
import com.replayx.app.util.KeyGuard

/**
 * Tela cheia, intransponível, mostrada assim que o tempo da key acaba.
 * Não tem como voltar (onBackPressed não faz nada) nem tocar em qualquer
 * outra tela por trás, porque essa Activity é aberta com FLAG_ACTIVITY_CLEAR_TASK,
 * o que mata todas as outras Activities da pilha (Main, Otimização, WebView).
 */
class ExpiredActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpiredBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        super.onCreate(savedInstanceState)
        binding = ActivityExpiredBinding.inflate(layoutInflater)
        setContentView(binding.root)

        KeyGuard.clearSavedKey(this)

        binding.btnLoginExpired.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Bloqueado de propósito: não deixa sair dessa tela sem logar de novo
    }
}
