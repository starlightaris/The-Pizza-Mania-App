# 🍕 The Pizza Mania Mobile App

## 📱 Project Overview
Android food delivery app with real-time tracking. Allows users to order pizza, track deliveries, and manage orders.

## 🚀 Features
- User authentication with Firebase
- Menu browsing & cart management
- Order placement with multiple statuses
- Real-time delivery tracking with Google Maps
- Order history and address management
- Beautiful animations with Lottie

## 🛠️ Tech Stack
- **Frontend**: Android Studio, Java, Google Maps SDK
- **Backend**: Firebase Firestore & Authentication
- **Architecture**: MVVM Pattern
- **Animations**: Lottie

## 🗄️ Database Schema (Firestore)

### Collections:
1. **users** - User profiles with addresses
2. **branches** - Restaurant locations with GeoPoint
3. **orders** - Order details with status tracking

### Order Status Flow:
```
Pending → Preparing → Out for Delivery → Delivered
```

## 🔧 Setup Instructions

### 1. Prerequisites
- Android Studio
- Firebase Account
- Google Maps API Key

### 2. Configuration
1. **Firebase Setup**:
   - Create Firebase project
   - Add `google-services.json` to `app/` folder

2. **Google Maps API**:
   - Enable Maps SDK for Android
   - Add API key to `local.properties`:
   ```properties
   MAPS_API_KEY=your_api_key_here
   ```

3. **Required Permissions** (AndroidManifest.xml):
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
```

### 3. Build & Run
1. Open project in Android Studio
2. Sync Gradle dependencies
3. Run on emulator/device (API 24+)

## 📱 Key Screens
- Login/Signup Screen
- Home/Menu Screen
- Cart & Checkout
- **Delivery Tracking Screen** (Main feature)
- Profile & History

## 🎯 Core Feature: Delivery Tracking
- Real-time location updates
- Google Maps integration
- Order status animations
- Progress indicator
- Branch & user location markers


## 📄 License
For educational use

---

**Developed for educational purposes**  
