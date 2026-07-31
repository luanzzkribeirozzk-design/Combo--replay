package com.replayx.app.security

import android.content.Context
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Guarda o HTML de sensibilidade cifrado com AES-128-CBC.
 *
 * O arquivo em assets/sensi.enc é puro texto cifrado — mesmo descompactando
 * o APK (unzip/apktool) ninguém consegue ler o HTML sem a chave, que fica
 * fragmentada em partes (mesmo padrão usado em security/C.java) para
 * dificultar extração estática por decompiladores.
 *
 * O conteúdo só existe em texto puro dentro da memória do processo, pelo
 * tempo necessário para ser entregue à WebView.
 */
object HtmlVault {

    private val K1 = byteArrayOf(19, -115, -11, -17)
    private val K2 = byteArrayOf(-78, -38, 117, -21)
    private val K3 = byteArrayOf(-75, 91, 17, 89)
    private val K4 = byteArrayOf(-7, -85, 112, -38)

    private val IV = byteArrayOf(
        -40, -73, 35, 110, 119, 48, 106, -5,
        -49, 93, -22, -39, 118, -31, 90, -98
    )

    private fun aesKey(): ByteArray {
        val k = ByteArray(16)
        System.arraycopy(K1, 0, k, 0, 4)
        System.arraycopy(K2, 0, k, 4, 4)
        System.arraycopy(K3, 0, k, 8, 4)
        System.arraycopy(K4, 0, k, 12, 4)
        return k
    }

    /** Lê o asset cifrado e devolve o HTML em texto puro, só em memória. */
    fun loadHtml(context: Context, asset: String = "sensi.enc"): String {
        val enc = context.assets.open(asset).use { it.readBytes() }
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(aesKey(), "AES"), IvParameterSpec(IV))
        val dec = cipher.doFinal(enc)
        return String(dec, Charsets.UTF_8)
    }
}
