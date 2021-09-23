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
-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
    #只保留指定的几个方法，挺高代码混淆/压缩率
    public static * bind(android.view.View); #目前反射未采用bind的方式，这个可以不保留
    public static * inflate(android.view.LayoutInflater);
    public static * inflate(android.view.LayoutInflater, android.view.ViewGroup);
    public static * inflate(android.view.LayoutInflater,  android.view.ViewGroup,boolean);
}

#keep 所有类成员，类名会被混淆
#-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
#    *;
#}

#keep 类名和所有类成员不会被移除和混淆
#-keepclasseswithmembers class * implements androidx.viewbinding.ViewBinding {
#    *;
#}

#keep 类名和类成员都不会被移除和混淆
#-keep class * implements androidx.viewbinding.ViewBinding {
#    *;
#}

#keep 类名不会被移除和混淆，类成员都被混淆
#-keep class * implements androidx.viewbinding.ViewBinding


