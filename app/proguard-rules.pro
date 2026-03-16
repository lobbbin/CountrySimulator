# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the SDK tools.

# Keep data classes
-keepclassmembers class com.countrysimulator.game.domain.* { *; }

# Compose
-dontwarn androidx.compose.**