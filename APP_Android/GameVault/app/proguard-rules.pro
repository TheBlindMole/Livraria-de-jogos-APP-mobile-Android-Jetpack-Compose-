-keepattributes Signature, InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations, AnnotationDefault

-keep,includedescriptorclasses class com.theblindmole.gamevault.**$$serializer { *; }
-keepclassmembers class com.theblindmole.gamevault.** {
    *** Companion;
}
-keepclasseswithmembers class com.theblindmole.gamevault.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep class com.theblindmole.gamevault.data.remote.dto.** { *; }
