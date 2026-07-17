# Add project specific ProGuard rules here.

# --- General: keep line numbers for stack traces ---
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations

# --- Retrofit ---
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# --- Gson (serialization/deserialization of API models by reflection) ---
-keep class com.example.weatherapp_ramide.api.** { *; }
-keep class com.google.gson.** { *; }
-keepattributes EnclosingMethod

# --- Firebase / Firestore (models deserialized by reflection) ---
-keep class com.example.weatherapp_ramide.db.fb.** { *; }
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# --- Coil ---
-dontwarn coil.**
-keep class coil.** { *; }
