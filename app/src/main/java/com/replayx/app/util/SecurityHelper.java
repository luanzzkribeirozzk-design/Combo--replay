package com.replayx.app.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Classe utilitária que encapsula verificações de segurança do app.
 * Original: AbstractC1173k4 (p000)
 * Contém verificações de assinatura do pacote, hash de integridade
 * e validação de package name.
 */
public final class SecurityHelper {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Verifica se o app está rodando no package correto.
     */
    public static boolean verifyPackageName(Context context) {
        return context.getPackageName().equals("com.replayx.app");
    }

    /**
     * Obtém as assinaturas do pacote.
     */
    public static Signature[] getPackageSignatures(Context context) {
        try {
            PackageInfo info = context.getPackageManager()
                .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            return info.signatures;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Verifica a integridade do APK calculando hash.
     * Original: m3890s(getPackageCodePath())
     */
    public static String computeApkHash(String apkPath) {
        try {
            // Hash computation based on APK file content
            // Original implementation is obfuscated
            return computeFileHash(apkPath);
        } catch (Exception e) {
            return null;
        }
    }

    private static String computeFileHash(String path) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            java.io.FileInputStream fis = new java.io.FileInputStream(path);
            byte[] buffer = new byte[8192];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                md.update(buffer, 0, read);
            }
            fis.close();
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Verifica a integridade da assinatura via hash XOR.
     * Original: comparação XOR entre hash salvo no SharedPreferences e hash atual
     */
    public static boolean verifyIntegrity(String savedHash, String currentHash) {
        if (savedHash == null || currentHash == null) return false;
        if (savedHash.length() != currentHash.length()) return false;
        int xorResult = 0;
        for (int i = 0; i < savedHash.length(); i++) {
            xorResult |= savedHash.charAt(i) ^ currentHash.charAt(i);
        }
        return xorResult == 0;
    }

    /**
     * Verificação anti-debug / anti-tamper.
     * Original: AbstractC1173k4.m3881j()
     */
    public static boolean isDebugging() {
        return false; // Simplificado - a verificação real é ofuscada
    }
}
