# Introduction

Floating mod menu for il2cpp base on [ByNameModding](https://github.com/ByNameModding/BNM-Android)

![preview.gif](images/preview.gif)

# Getting Start

## Decoding `game.apk` with [apktool](https://apktool.org/)

```zsh
apktool d -f game.apk
```

## Build Android-Mod-Menu-BNM with [Android Studio](https://developer.android.com/studio)

```
Build > Generate App Bundles or APKs > Generate APKs
```

## Decoding `app-debug.apk` with [apktool](https://apktool.org/)

```zsh
apktool d -f app-debug.apk
```

## Copy `app-debug` files to `game` fold

```zsh
cp app-debug/lib/${ARCH_ABI}/libModBNM.so game/lib/${ARCH_ABI}/libModBNM.so
cp -r app-debug/smali_classes{2,3,4,5} game/
```

## Update game's main activity

1. open `game/AndroidManifest.xml` and find main activity

    ```xml
    <activity
        android:configChanges="mcc|mnc|locale|touchscreen|keyboard|keyboardHidden|navigation|orientation|screenLayout|uiMode|screenSize|smallestScreenSize|density|layoutDirection|fontScale"
        android:exported="true" android:hardwareAccelerated="false" android:label="@string/app_name"
        android:launchMode="singleTask" android:maxAspectRatio="2.0"
        android:name="com.nutaku.unity.UnityPlayerActivity" android:resizeableActivity="false"
        android:screenOrientation="userLandscape">
        <meta-data android:name="unityplayer.UnityActivity" android:value="true" />
    </activity>
    ```

2. open `game/smali/com/nutaku/unity/UnityPlayerActivity.smali` and edit
   `onCreate` method

    ```smali
    .method protected onCreate(Landroid/os/Bundle;)V
        # invoke Main.start(context)
        invoke-static {p0}, Lcom/android/support/Main;->start(Landroid/content/Context;)V
        ...
   .end method
    ```

## Rebuilding `game` with [apktool](https://apktool.org/)

```zsh
apktool b -f game
```

## Sign `game/dist/game.apk` with [AndroidToolKit](https://github.com/LazyIonEs/AndroidToolKit/)

![sign.png](images/sign.png)

# Credits

- [Android-Mod-Menu](https://github.com/LGLTeam/Android-Mod-Menu) Floating mod menu for il2cpp and
  other native android games.
- [ByNameModding](https://github.com/ByNameModding/BNM-Android) Modding il2cpp games by classes,
  methods, fields names on Android.
