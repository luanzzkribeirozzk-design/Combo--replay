package com.replayx.app.security;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import java.security.MessageDigest;

/**
 * Verificação de integridade real: confere se o APK instalado foi assinado
 * com a MESMA chave de release que gerou a versão oficial. Se alguém pegar
 * o código, modificar (remover checagens, mudar a key de admin, etc.) e
 * remontar o APK, ele NECESSARIAMENTE precisa reassinar com outra chave
 * (não tem como usar a chave original sem o arquivo .jks + senha) — e essa
 * nova assinatura não bate com o hash abaixo, então o app se recusa a abrir.
 *
 * minSdk 29 (Android 10+) já garante PackageManager.GET_SIGNING_CERTIFICATES,
 * que é a API correta e não é vulnerável ao bug de assinatura múltipla
 * (Janus/APK signature v1) que o antigo GET_SIGNATURES tinha.
 */
public final class IntegrityCheck {
    private IntegrityCheck() {}

    private static final String PACKAGE = "com.replayx.app";

    // SHA-256 do certificado de assinatura de release (não é segredo —
    // é só o "hash público" da chave legítima; sem o .jks + senha originais
    // ninguém consegue gerar uma assinatura diferente que bata com esse hash).
    private static final String EXPECTED_CERT_SHA256 =
        "42a7f540792ae2b361cf9bebaf1cc25ee8fa9afeb0494fa8677b80d773a1e3ab";

    public static boolean isValid(Context ctx) {
        try {
            if (!ctx.getPackageName().equals(PACKAGE)) return false;
            if (isEmulator()) return false;
            if (!isDebuggerSafe(ctx)) return false;
            return hasExpectedSignature(ctx);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isDebuggerSafe(Context ctx) {
        // Bloqueia se o próprio APK instalado estiver marcado como debuggable
        // (não deveria nunca acontecer num build de release, mas garante mesmo
        // que alguém tente forçar debuggable=true numa remontagem).
        int flags = ctx.getApplicationInfo().flags;
        boolean debuggable = (flags & android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        return !debuggable;
    }

    private static boolean hasExpectedSignature(Context ctx) throws Exception {
        Signature[] sigs;
        PackageInfo pi = ctx.getPackageManager().getPackageInfo(
            ctx.getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES);
        android.content.pm.SigningInfo si = pi.signingInfo;
        if (si == null) return false;
        sigs = si.hasMultipleSigners()
            ? si.getApkContentsSigners()
            : si.getSigningCertificateHistory();
        if (sigs == null || sigs.length == 0) return false;

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        for (Signature sig : sigs) {
            byte[] digest = md.digest(sig.toByteArray());
            String hex = toHex(digest);
            if (constantTimeEquals(hex, EXPECTED_CERT_SHA256)) return true;
        }
        return false;
    }

    private static String toHex(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte x : b) sb.append(String.format("%02x", x & 0xFF));
        return sb.toString();
    }

    /** Comparação em tempo constante, evita timing attack na checagem do hash. */
    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;
        int diff = 0;
        for (int i = 0; i < a.length(); i++) diff |= (a.charAt(i) ^ b.charAt(i));
        return diff == 0;
    }

    private static boolean isEmulator() {
        String fp = Build.FINGERPRINT.toLowerCase();
        String model = Build.MODEL.toLowerCase();
        String product = Build.PRODUCT.toLowerCase();
        String hardware = Build.HARDWARE.toLowerCase();
        String manufacturer = Build.MANUFACTURER.toLowerCase();

        if (fp.contains("generic") || fp.contains("unknown")) return true;
        if (model.contains("google_sdk") || model.contains("emulator")) return true;
        if (manufacturer.contains("genymotion")) return true;
        if (hardware.equals("goldfish") || hardware.equals("ranchu")) return true;
        if (product.contains("sdk_gphone") || product.contains("vbox86p")) return true;
        if (product.contains("nox") || product.contains("bluestacks")) return true;
        return false;
    }
}
