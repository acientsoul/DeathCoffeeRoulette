@echo off
setlocal enabledelayedexpansion

set ANDROID_HOME=D:\Android\Sdk
set BUILD_TOOLS=%ANDROID_HOME%\build-tools\34.0.0
set PLATFORM=%ANDROID_HOME%\platforms\android-34
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
set PATH=%JAVA_HOME%\bin;%BUILD_TOOLS%;%PATH%

set PROJECT=D:\이동근\5. Coding Source\DeathCoffeeRoulette\manual-apk
set BUILD=%PROJECT%\build

echo ======================================
echo  죽음의 커피 룰렛 APK Builder
echo ======================================

echo.
echo [1/6] Cleaning build directory...
if exist "%BUILD%" rmdir /s /q "%BUILD%"
mkdir "%BUILD%"
mkdir "%BUILD%\gen"
mkdir "%BUILD%\obj"
mkdir "%BUILD%\apk"

echo.
echo [2/6] Compiling resources with aapt2...
"%BUILD_TOOLS%\aapt2.exe" compile --dir "%PROJECT%\res" -o "%BUILD%\compiled_res.zip"
if errorlevel 1 (echo FAILED at aapt2 compile & exit /b 1)

echo.
echo [3/6] Linking resources...
"%BUILD_TOOLS%\aapt2.exe" link ^
    -o "%BUILD%\apk\app.unsigned.apk" ^
    -I "%PLATFORM%\android.jar" ^
    --manifest "%PROJECT%\AndroidManifest.xml" ^
    -R "%BUILD%\compiled_res.zip" ^
    --java "%BUILD%\gen" ^
    -A "%PROJECT%\assets" ^
    --auto-add-overlay
if errorlevel 1 (echo FAILED at aapt2 link & exit /b 1)

echo.
echo [4/6] Compiling Java source...
dir /s /b "%PROJECT%\src\*.java" > "%BUILD%\sources.txt"
"%JAVA_HOME%\bin\javac.exe" ^
    -source 1.8 -target 1.8 ^
    -bootclasspath "%PLATFORM%\android.jar" ^
    -classpath "%PLATFORM%\android.jar" ^
    -d "%BUILD%\obj" ^
    @"%BUILD%\sources.txt"
if errorlevel 1 (echo FAILED at javac & exit /b 1)

echo.
echo [5/6] Converting to DEX...
call "%BUILD_TOOLS%\d8.bat" ^
    --output "%BUILD%\apk" ^
    --lib "%PLATFORM%\android.jar" ^
    "%BUILD%\obj\com\deathcoffee\roulette\MainActivity.class"
if errorlevel 1 (echo FAILED at d8 & exit /b 1)

echo.
echo [6/6] Signing APK...
rem Create a debug keystore if needed
if not exist "%PROJECT%\debug.keystore" (
    "%JAVA_HOME%\bin\keytool.exe" -genkeypair ^
        -v ^
        -keystore "%PROJECT%\debug.keystore" ^
        -alias androiddebugkey ^
        -keyalg RSA ^
        -keysize 2048 ^
        -validity 10000 ^
        -storepass android ^
        -keypass android ^
        -dname "CN=Debug,O=Debug,L=Debug,ST=Debug,C=US"
)

rem Zipalign first
"%BUILD_TOOLS%\zipalign.exe" -f 4 ^
    "%BUILD%\apk\app.unsigned.apk" ^
    "%BUILD%\apk\app.aligned.apk"
if errorlevel 1 (echo FAILED at zipalign & exit /b 1)

rem Sign with apksigner
call "%BUILD_TOOLS%\apksigner.bat" sign ^
    --ks "%PROJECT%\debug.keystore" ^
    --ks-key-alias androiddebugkey ^
    --ks-pass pass:android ^
    --key-pass pass:android ^
    --out "%BUILD%\DeathCoffeeRoulette.apk" ^
    "%BUILD%\apk\app.aligned.apk"
if errorlevel 1 (echo FAILED at apksigner & exit /b 1)

echo.
echo ======================================
echo  BUILD SUCCESSFUL!
echo  APK: %BUILD%\DeathCoffeeRoulette.apk
echo ======================================

endlocal
