# Hardened R8 / ProGuard Obfuscation & Protection Rules
# Protects app architecture, classes, and API logic from reverse engineering

-repackageclasses 'o'
-allowaccessmodification
-renamesourcefileattribute SourceFile
-keepattributes Signature,InnerClasses,EnclosingMethod

# Strip verbose debug logs in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
}

# Room & SQLite entities
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Moshi & Kotlin reflection
-keepclassmembers class * {
    @com.squareup.moshi.* <fields>;
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonClass class * { *; }

# Firebase & Google Credential Manager
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
    @com.google.firebase.firestore.PropertyName <methods>;
}
-dontwarn com.google.firebase.**
-dontwarn androidx.credentials.**

# Keep WebView JS/Android callbacks intact
-keepclassmembers class * extends android.webkit.WebViewClient {
    public *;
}
-keepclassmembers class * extends android.webkit.WebChromeClient {
    public *;
}
