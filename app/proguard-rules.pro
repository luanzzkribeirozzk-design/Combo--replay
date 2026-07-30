# ─── Ofuscação máxima ───────────────────────────────────────────────────────
# NÃO mantemos mais "com.devwilltech.otimizacao.** { *; }" de propósito:
# aquilo desativava a ofuscação do app inteiro, deixando nomes de classes,
# métodos e a lógica de validação de key totalmente legíveis num APK
# descompilado. Agora só é mantido o estritamente necessário para rodar.

-optimizationpasses 5
-allowaccessmodification
-repackageclasses ''
-overloadaggressively

# Remove logs e prints em release (evita vazar dados de debug/keys em logcat)
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}
-assumenosideeffects class java.io.PrintStream {
    public void println(...);
    public void print(...);
}

# ─── Compose / Coroutines (necessário para não quebrar em runtime) ─────────
-keep class androidx.compose.runtime.** { *; }
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }
-keepclassmembers class kotlin.coroutines.Continuation

# ─── Entry points que o Android precisa localizar pelo nome ────────────────
-keep public class com.devwilltech.otimizacao.MainActivity { public <init>(); }
-keep class rikka.shizuku.ShizukuProvider

# ─── Modelos serializados via JSONObject (reflexão manual em runtime) ──────
-keepclassmembers class com.devwilltech.otimizacao.KeyData { *; }
-keepclassmembers class com.devwilltech.otimizacao.MainUiState { *; }

# ─── Shizuku (usa AIDL/reflection internamente) ────────────────────────────
-keep class rikka.shizuku.** { *; }
-keep interface rikka.shizuku.** { *; }
-dontwarn rikka.shizuku.**

# Remove metadados de Kotlin (nomes de parâmetros, anotações de debug)
-keepattributes !SourceFile,!LineNumberTable
-renamesourcefileattribute ''
