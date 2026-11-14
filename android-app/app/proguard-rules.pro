# 小步 R8/ProGuard 规则（release 构建启用）
#
# 目标：保护知识产权，降低反编译可读性；同时避免 Room / Compose / kotlinx.serialization
# 在混淆后运行期崩溃。这里只保留「运行必需」的符号，不做过度保留。

# 保留行号与源文件信息，便于自己排查线上崩溃（mapping.txt 不公开分发）
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Kotlin 元数据与注解（反射/序列化/Compose 运行时需要）
-keepattributes Signature,InnerClasses,EnclosingMethod
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,AnnotationDefault

# ---------- Room ----------
# Room 生成的 *_Impl 通过反射查找，实体字段名会被 SQL 使用
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keep class * implements androidx.room.RoomDatabase_Impl { *; }
-dontwarn androidx.room.paging.**

# ---------- Kotlin / 协程 ----------
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }
-dontwarn kotlinx.coroutines.**

# ---------- kotlinx.serialization ----------
# 当前只用 buildJsonObject 等非反射 API，保留运行时即可
-keep class kotlinx.serialization.json.** { *; }
-dontwarn kotlinx.serialization.**

# ---------- OkHttp / Okio ----------
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# ---------- 应用入口与构建常量 ----------
# BuildConfig 字段会被内联，保留类便于问题定位
-keep class com.learningloop.app.BuildConfig { *; }
-keep class com.learningloop.app.MainActivity { *; }
