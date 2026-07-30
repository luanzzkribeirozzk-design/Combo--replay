package com.replayx.app.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.content.Context;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Logger de eventos de rede que envia dados para o Firestore via REST API.
 * Original: RunnableC1600o8 e lógica inline em LoginActivity
 * Registra tentativas de login, falhas de key, e eventos do app.
 */
public final class NetworkLogger {

    private static final HandlerThread thread = new HandlerThread("netlog");
    private static final Handler handler;

    static {
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    /**
     * Obtém o ID do dispositivo (Android ID).
     */
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(
            context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Obtém o modelo do dispositivo.
     */
    public static String getDeviceModel() {
        return android.os.Build.MODEL;
    }

    /**
     * Envia log de tentativa de login para o Firestore.
     * Original: lógica inline em LoginActivity.m3637g()
     */
    public static void logAttempt(Context context, String keyTried, String reason) {
        handler.post(() -> {
            try {
                String str9 = getFirestoreProjectId();
                String firestoreKey = getFirestoreApiKey();
                String instant = java.time.Instant.now().toString();
                String deviceId = getDeviceId(context);
                String model = getDeviceModel();

                String url = "https://firestore.googleapis.com/v1/projects/" +
                    str9 + "/databases/(default)/documents/login_attempts/" +
                    "attempt_" + System.currentTimeMillis() + "?key=" + firestoreKey;

                JSONObject fields = new JSONObject();
                JSONObject keyTriedObj = new JSONObject();
                keyTriedObj.put("stringValue", keyTried != null && keyTried.length() > 0 ? keyTried : "—");
                fields.put("keyTried", keyTriedObj);

                JSONObject reasonObj = new JSONObject();
                reasonObj.put("stringValue", reason);
                fields.put("reason", reasonObj);

                JSONObject ipObj = new JSONObject();
                ipObj.put("stringValue", deviceId.length() > 0 ? deviceId : "desconhecido");
                fields.put("ip", ipObj);

                JSONObject modelObj = new JSONObject();
                modelObj.put("stringValue", model);
                fields.put("model", modelObj);

                JSONObject timestampObj = new JSONObject();
                timestampObj.put("timestampValue", instant);
                fields.put("timestamp", timestampObj);

                JSONObject doc = new JSONObject();
                doc.put("fields", fields);

                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("PATCH");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(8000);
                OutputStream os = conn.getOutputStream();
                os.write(doc.toString().getBytes("UTF-8"));
                os.flush();
                os.close();
                conn.getResponseCode();
                conn.disconnect();
            } catch (Exception ignored) {
            }
        });
    }

    /**
     * Obtém o project ID do Firestore (valores extraídos do APK original).
     * Original: AbstractC1717u0.f8666f
     */
    private static String getFirestoreProjectId() {
        // Valor extraído do APK original
        return "replayx-app";
    }

    /**
     * Obtém a API key do Firestore.
     * Original: AbstractC1717u0.m5035j()
     */
    private static String getFirestoreApiKey() {
        // Valor extraído do APK original
        return "AIzaSyD-REDACTED";
    }
}
