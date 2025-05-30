# ⛅ Weather App

> A modern, customizable weather app built with Jetpack Compose, offering real-time forecasts, location support, and offline usage

---

## 🧠 Table of Contents

- [About](#about)
- [Features](#features)
- [Screenshots](#screenshots)
- [Screencast](#screencast)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Configuration](#configuration)
- [Tech Stack](#tech-stack)
- [Known Issues](#known-issues)
- [License](#license)
- [Contributing](#contributing)
- [Contact](#contact)

---

## 📖 About

Weather App is a Jetpack Compose-based weather application that offers a smooth, customizable, and visually pretty experience. Users can view weather forecasts based on their location or favorites, search globally, and customize settings like language, temperature units, and time format.

- Solves: Viewing weather in a quick, personalized way with offline and multi-location support.
- For: Users who want a clean, modern weather UI with useful statistics and customization and no paywalled features

---

## ✨ Features

- ✅ Inspect weather data for any location
- ✅ Current weather and detailed hourly/daily forecasts
- ✅ GPS location support
- ✅ Favorite multiple locations and swipe between them
- ✅ Fully localized UI available in Finnish and English
- ✅ Dynamic backgrounds based on weather or user-selected preset
- ✅ World map-based location search
- ✅ Sunrise and sunset progress indicators
- ✅ Line chart for daily temperature changes
- ✅ Weather statistics including wind, precipitation, humidity, and more
- ✅ Manually configurable options for temperature units, wind speed, language and more
- ✅ Offline support for previously loaded locations
- ✅ Persistent settings and favorites stored to local storage

---

## 📸 Screenshots

<div align="center">
  <img src="screenshots/Weather_Screen_1.png" width="200" alt="Weather Screen 1" style="margin: 10px;" />
  <img src="screenshots/Weather_Screen_2.png" width="200" alt="Weather Screen 2" style="margin: 10px;" />
  <img src="screenshots/Search_Screen.png" width="200" alt="Search Screen" style="margin: 10px;" />
  <img src="screenshots/Settings_Screen.png" width="200" alt="Settings Screen" style="margin: 10px;" />
</div>

---

## 🎦 Screencast

<div align="center">
  <a href="https://www.youtube.com/watch?v=RfQ35aQOApA" target="_blank">
    <img src="https://img.youtube.com/vi/RfQ35aQOApA/0.jpg" width="480" alt="Watch Screencast on YouTube" style="margin: 10px;" />
  </a>
</div>

---

## 🚀 Getting Started

### Prerequisites

- Android Studio

### Installation

```bash
git clone https://github.com/simoalanne/4A00EZ65-3005-mobile-development-alanne-simo
cd 4A00EZ65-3005-mobile-development-alanne-simo

# Open in Android Studio and sync Gradle
```

---

## ⚙️ Usage
To run the app:

- Open the project in Android Studio

- Launch the emulator or connect your own device that has at least SDK 33 / Android 13

---

## 🛠 Configuration

No API keys are required. All APIs (Open-Meteo, Nominatim) are public

---

## 🧰 Tech Stack

- **Language**: Kotlin  

- **UI Framework**: Jetpack Compose

- **Networking**: Retrofit 

- **Async**: Kotlin Coroutines

- **Local Storage**: Room + DataStore

- **Location**: Fused Location Provider

- **Permissions**: Accompanist Permissions

- **Charts**: Compose Charts

- **Maps**: MapLibre Compose

- **Images**: Coil

- **Animations**: Lottie Compose  

---

## 🐞 Known Issues

- No known issues so far. Feel free to report them in case you find them

---

## 📜 License

Standard MIT License.

---

## 🤝 Contributing

Pull requests are welcome.

---

## 📫 Contact

Project maintained by [Simo Alanne](mailto:simo.alanne@gmail.com).

---
