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
# proguard 官网
#https://www.guardsquare.com/manual/configuration/examples
#https://www.guardsquare.com/proguard
#-keepclassmembers 作用只是保证类成员 ( 成员变量 , 成员方法 ) 不被混淆 , 类名还是会被混淆的
#-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
#    public static ** bind(***);
#    public static ** inflate(***);
#    public static ** inflate(**,**);
#}
#-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
#    *;
#}
#-keep class * implements androidx.viewbinding.ViewBinding {
#    *;
#}
