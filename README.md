![Now in Android](docs/images/visara-splash.png "Now in Android")
<!-- <a href="https://play.google.com/store/apps/details?id=com.google.samples.apps.nowinandroid"><img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" height="70"></a> -->

Visara App
==================
Visara is a Kotlin-based Android native app built with Jetpack Compose for sharing and watching videos, featuring adaptive streaming, push notifications, and social interactions such as like, comment, and follow.

## Features
- Adaptive video streaming using MPEG-DASH and ExoPlayer
- Background audio playback and media session support
- Video upload using WorkManager (handles large files & background)
- Real-time push notifications with Firebase Cloud Messaging
- Multilingual support (English, Vietnamese)
- User profiles, feeds, likes, comments, and follows
- Dynamic theme switching (Light / Dark / System)
- QR code scanning for quick profile access

## Tech Stack
- **Core:** Android, Kotlin, Jetpack Compose, Jetpack Media3, Dagger/Hilt
- **Networking:** OkHttp
- **Storage:** Room Database, DataStore, EncryptedSharedPreferences
- **Notifications:** Firebase Cloud Messaging
- **Others:** WorkManager, Google ML Kit (Barcode Scanner)

## Architecture
<img
src="docs/images/mobile_architecture.png"
alt="Architecture Diagram"
title="Architecture Diagram"
width="100%"
style="border:1px solid #ccc; border-radius:8px;"
/>


## Screenshots

**1. Home Screen:** Supports persistent video playback with a floating mini player, allowing users to browse other content without interrupting playback — just like YouTube’s experience.

<img
src="docs/images/home_screen.png"
alt="Home screen"
title="Home screen"
width="50%"
style="border:1px solid #ccc; border-radius:8px;"
/>

**2. Detail video screen:** The video detail screen supports smooth automatic fullscreen transition on device rotation, and delivers quick playback using adaptive streaming — without downloading the entire file.

<img
src="docs/images/video_detail_1.png"
alt="Video detail screen"
title="Video detail screen"
width="50%"
style="border:1px solid #ccc; border-radius:8px;"
/>

**3. Play background:** Supports background audio playback with media controls in the notification shade and lock screen, even when the app is no longer in the foreground.

<img
src="docs/images/video_detail_2.png"
alt="Play background"
title="Play background"
width="50%"
style="border:1px solid #ccc; border-radius:8px;"
/>

**4. Settings screen:** Supports language switching, theme customization (Light, Dark, or System), and account management directly from the settings screen.

<img
src="docs/images/settings_screen.png"
alt="Settings screen"
title="Settings screen"
width="50%"
style="border:1px solid #ccc; border-radius:8px;"
/>


## Setup
**1. Clone project**

```powershell
git clone https://github.com/lmkha/visara-android-app.git
```

**2. [Add local.properties(env)](docs/setup_local_properties.md)**


**3. Setup FCM:**
- Create new firebase project
- Go to Project Overview -> Project settings
- Download **google-services.json** file in **Android apps** section
- Put **google-services.json** in **project/app** folder
     ```
     visara-android-app/
     │
     ├── app/
     │   ├── google-services.json 
     │   └...
     ...
     ```
