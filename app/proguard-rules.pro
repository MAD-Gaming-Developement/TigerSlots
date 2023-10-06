# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class io.console5g.tigerslots.ui.MainActivity {
   public *;
}

# Keep the AppsFlyer classes
-keep class com.appsflyer.*{ *; }
-keep class com.android.installreferrer.*{ *; }

# Keep the Google Play Install Referrer classes
-keep class com.google.android.gms.common.ConnectionResult {
   int SUCCESS;
}

-keep class com.google.android.gms.measurement.*{ *; }
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {
   com.google.android.gms.ads.identifier.AdvertisingIdClient$Info getAdvertisingIdInfo(android.content.Context);
}

-keep public class * extends android.app.Application

-keep class com.appsflyer.**$InstallReceiver { *; }
-keep class com.appsflyer.**$referrer { *; }
-keep class com.appsflyer.AppsFlyerLib

# Keep any methods that are accessed via reflection
-keepclassmembers class * {
    @com.appsflyer.*
    <methods>;
}