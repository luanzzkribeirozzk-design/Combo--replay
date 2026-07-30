# Add project specific ProGuard rules here.
# By default, the flags in this file are applied to all build variants.
# See http://developer.android.com/guide/developing/tools/proguard.html

# Keep Compose runtime
-keep class androidx.compose.** { *; }

# Keep Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }

# Keep our own classes from obfuscation for easier debugging
-keep class com.devwilltech.otimizacao.** { *; }
