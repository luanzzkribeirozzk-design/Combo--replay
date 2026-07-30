package com.devwilltech.otimizacao.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Debug
import java.security.MessageDigest

/**
 * Verificação de integridade simples para dificultar clonagem/repack do APK.
 *
 * COMO CONFIGURAR (obrigatório antes de publicar a versão release):
 * 1. Gere o APK/AAB assinado com sua keystore de release normalmente.
 * 2. Rode: `keytool -printcert -jarfile app-release.apk`
 *    (ou `apksigner verify --print-certs app-release.apk`)
 * 3. Copie o hash SHA-256 do certificado (sem os ":") e cole em
 *    [EXPECTED_SIGNATURE_SHA256] abaixo.
 *
 * Enquanto [EXPECTED_SIGNATURE_SHA256] estiver vazio, a checagem de
 * assinatura fica DESATIVADA (não quebra builds de debug/dev). Preencha-o
 * apenas no momento de gerar o build de release final.
 *
 * IMPORTANTE — limite real dessa proteção:
 * isso dificulta repack casual (repackers automatizados, clones em lojas
 * alternativas), mas não é indestrutível: quem tem conhecimento avançado de
 * engenharia reversa pode remover essa checagem do bytecode. A defesa que
 * realmente importa para a sua lógica de negócio (validação de key) é do
 * lado do servidor — no seu caso, as Regras de Segurança do Firebase
 * Realtime Database. Trate este guard como uma camada a mais, não como a
 * única linha de defesa.
 */
object IntegrityGuard {

    private const val EXPECTED_SIGNATURE_SHA256 = "" // preencher antes do build de release

    /** true se o app foi instalado a partir de um pacote com assinatura diferente da esperada. */
    @SuppressLint("PackageManagerGetSignatures")
    fun isSignatureTampered(context: Context): Boolean {
        if (EXPECTED_SIGNATURE_SHA256.isBlank()) return false // checagem não configurada ainda

        return try {
            val currentHash = currentSignatureSha256(context)
            !currentHash.equals(EXPECTED_SIGNATURE_SHA256, ignoreCase = true)
        } catch (_: Exception) {
            // Se não conseguimos ler a assinatura, tratamos como suspeito.
            true
        }
    }

    /** true se um debugger estiver anexado ao processo (ex.: engenharia reversa dinâmica). */
    fun isDebuggerAttached(): Boolean = Debug.isDebuggerConnected() || Debug.waitingForDebugger()

    @Suppress("DEPRECATION")
    @SuppressLint("PackageManagerGetSignatures")
    private fun currentSignatureSha256(context: Context): String {
        val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val info = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            )
            val signingInfo = info.signingInfo
            if (signingInfo?.hasMultipleSigners() == true) {
                signingInfo.apkContentsSigners
            } else {
                signingInfo?.signingCertificateHistory ?: emptyArray()
            }
        } else {
            val info = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
            info.signatures ?: emptyArray()
        }

        val cert = signatures.firstOrNull() ?: throw IllegalStateException("Sem assinatura encontrada")
        val digest = MessageDigest.getInstance("SHA-256").digest(cert.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
