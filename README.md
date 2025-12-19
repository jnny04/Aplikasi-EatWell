# EatWell - Smart Food Diary & Recipe Finder

**EatWell** adalah aplikasi Android cerdas yang membantu pengguna menjaga pola makan sehat melalui pelacakan nutrisi otomatis berbasis AI, rekomendasi resep personal, dan fitur gamifikasi.

Aplikasi ini dibangun menggunakan **Kotlin** dan **Jetpack Compose** dengan arsitektur **MVVM Clean Architecture**, mengutamakan performa, efisiensi baterai, dan keamanan data pengguna.

---

## Fitur Utama

### 1. AI Food Detection (Smart Scan)
Deteksi makanan secara instan menggunakan kamera.
- Menggunakan **Gradio API** (Hugging Face) untuk klasifikasi gambar dan estimasi nutrisi.
- Sistem **Polling Mechanism** yang efisien untuk menangani *inference time* server AI.
- Hasil deteksi otomatis dikonversi menjadi log nutrisi harian (Kalori, Protein, Lemak, Karbo).

### 2. Advanced Recipe Discovery
- **Spoonacular API Integration:** Mencari ribuan resep dengan detail nutrisi lengkap.
- **Smart Filtering:** Filter canggih untuk preferensi diet (Vegan, Keto, Gluten-Free) dan **Halal Filter** (secara otomatis mengecualikan bahan non-halal).
- **Shake to Search:** Fitur unik menggunakan sensor **Accelerometer**; goyangkan HP untuk mendapatkan rekomendasi resep acak!

### 3. Nutritional Tracking & Reporting
- **Daily Dashboard:** Visualisasi sisa kalori dan *macro-nutrients* dengan progress bar animasi.
- **Weekly & Monthly Recap:** Laporan visual asupan makanan pengguna.
- **PDF Export:** Generate laporan nutrisi bulanan ke dalam format PDF secara native (tanpa library berat).

### 4. Gamification (Streak System)
- Sistem **Daily Streak** untuk memotivasi pengguna tetap konsisten.
- Data streak disimpan dengan aman menggunakan **EncryptedSharedPreferences**.

### 5. Security & Privacy
- **Biometric Login:** Mendukung otentikasi sidik jari dan pemindai wajah.
- **Security Logging:** Mencatat aktivitas login dan *suspicious actions* ke Firestore untuk audit keamanan.

---

## Tech Stack & Architecture

Aplikasi ini dikembangkan dengan standar industri modern:

* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose (Material3 Design)
* **Architecture:** MVVM (Model-View-ViewModel)
* **Dependency Injection:** Dagger Hilt
* **Network:** Retrofit2 + OkHttp3
* **Local Database:** Room Database (Offline Caching)
* **Cloud Backend:** Firebase (Authentication, Firestore, Storage)
* **Image Loading:** Coil (Memory & Disk Cache Optimized)
* **Hardware Sensors:** CameraX, Accelerometer, Proximity Sensor

---

## Technical Highlights (Performance & Efficiency)

Proyek ini tidak hanya sekadar berjalan, tetapi dioptimalkan untuk efisiensi:

1.  **Battery Efficient Networking:**
    * Mengimplementasikan strategi **HTTP Caching** agresif (Header `Cache-Control`).
    * Aplikasi memprioritaskan data dari *Disk Cache* sebelum melakukan *Network Call* untuk menghemat radio baterai.
    * *Interceptor* khusus mencatat log "HEMAT BATERAI" vs "BOROS BATERAI" untuk monitoring.

2.  **Resource Optimization:**
    * **Image Payload Optimizer:** Secara otomatis me-resize URL gambar dari API sebelum diunduh untuk menghemat kuota data pengguna.
    * **Memory Management:** Konfigurasi Coil membatasi penggunaan cache memori maksimal 25% dan disk 2% untuk mencegah *Out of Memory*.

3.  **Sensor Lifecycle Awareness:**
    * Sensor (Accelerometer/Proximity) secara otomatis dinonaktifkan (`unregisterListener`) saat aplikasi masuk ke *background* (`onPause`) untuk mencegah *battery drain*.

---

## Setup & Installation

Untuk menjalankan proyek ini di lokal:

1.  **Clone Repository:**
    ```bash
    git clone [https://github.com/username/EatWell.git](https://github.com/username/EatWell.git)
    ```
2.  **Firebase Setup:**
    * Buat project di Firebase Console.
    * Unduh `google-services.json` dan letakkan di folder `app/`.
    * Aktifkan *Authentication (Email/Password)* dan *Firestore Database*.

3.  **API Keys Configuration:**
    * Dapatkan API Key gratis dari [Spoonacular](https://spoonacular.com/food-api).
    * Buka `com.example.recappage.util.ApiConfig` (atau `local.properties` jika sudah dikonfigurasi) dan masukkan API Key Anda.

4.  **Build & Run:**
    * Buka di Android Studio (Koala/Jellyfish atau terbaru).
    * Sync Gradle dan Run di Emulator/Device fisik.

---


<p align="center">
  Made with ❤️ by Ratu Rinjanhei, Jason Nathan, dan Karina Amalia
</p>
