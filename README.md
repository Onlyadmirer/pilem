# Pilem - Aplikasi Informasi Film

**Pilem** adalah aplikasi Android berbasis Java yang dikembangkan sebagai Tugas Final Laboratorium Pemrograman Mobile. Aplikasi ini memungkinkan pengguna untuk menjelajahi daftar film populer, mencari film, dan menyimpan film favorit ke dalam penyimpanan lokal.

## 📱 Fitur Utama
- **Daftar Film Populer:** Menampilkan daftar film terbaru menggunakan RecyclerView.
- **Pencarian Film:** Memudahkan pengguna mencari film melalui fitur Explore.
- **Detail Film:** Informasi lengkap mengenai film yang dipilih (menggunakan Explicit Intent).
- **Favorit (Lokal):** Menyimpan daftar film favorit secara permanen menggunakan Room Database.
- **Dark/Light Mode:** Mendukung tema gelap dan terang secara otomatis mengikuti sistem.
- **Offline Resilience:** Penanganan kegagalan jaringan dengan fitur *Refresh*.

## 🛠️ Tech Stack & Spesifikasi
- **Bahasa Pemrograman:** Java
- **Arsitektur & UI:**
  - Navigation Component (Fragment-based navigation).
  - Material Design 3 (M3).
  - RecyclerView dengan ListAdapter/Adapter.
- **Networking:** Retrofit dengan OkHttp Interceptor untuk manajemen API Key.
- **Local Database:** Room Database untuk penyimpanan data favorit.
- **Concurrency:** `ExecutorService` untuk operasi database di background thread.
- **Library Pihak Ketiga:**
  - Glide (Image Loading).
  - Gson (JSON Parsing).

## 🚀 Cara Instalasi & Penggunaan

### 1. Prasyarat
- **Android Studio** (Ladybug atau versi lebih baru direkomendasikan).
- **JDK 11** atau lebih tinggi.
- **API Key** dari [TMDB (The Movie Database)](https://www.themoviedb.org/documentation/api).

### 2. Clone Repositori
Langkah pertama adalah menyalin repositori ini ke komputer lokal Anda. Buka terminal atau command prompt, lalu jalankan perintah:
```bash
git clone https://github.com/username/pilem.git
```
*(Catatan: Ganti URL di atas dengan URL repositori Anda yang sebenarnya)*

### 3. Buka Proyek di Android Studio
1. Jalankan **Android Studio**.
2. Pilih menu **File > Open**.
3. Navigasikan ke folder hasil clone tersebut (pilih folder `FINAL`).
4. Tunggu hingga proses **Gradle Sync** selesai secara otomatis oleh IDE.

### 4. Konfigurasi API Key
Aplikasi ini menggunakan sistem keamanan untuk melindungi API Key agar tidak terekspos secara publik.
1. Cari file `local.properties` di root project.
2. Tambahkan baris berikut di bagian paling bawah:
   ```properties
   TMDB_API_KEY=isi_api_key_anda_disini
   ```
3. Klik **Sync Project with Gradle Files** di toolbar Android Studio. API Key kini siap digunakan melalui `BuildConfig.TMDB_API_KEY`.

### 5. Build & Run
- Sambungkan perangkat Android fisik atau gunakan Emulator (Min SDK 29).
- Klik tombol **Run** (ikon segitiga hijau) di toolbar atas Android Studio untuk menjalankan aplikasi.

## 📝 Implementasi Teknis Singkat

- **Manajemen Fragment:** Perpindahan antar layar utama (Home, Explore, Favorit) dikelola oleh `NavHostFragment` dan `BottomNavigationView` melalui Navigation Graph.
- **Keamanan API:** Setiap request ke TMDB disisipkan API Key secara otomatis menggunakan `Interceptor` pada OkHttp Client, sehingga tidak perlu menuliskan key secara manual di setiap endpoint.
- **Operasi Database:** Semua operasi `Insert`, `Delete`, dan `Get` pada Room Database dijalankan secara asinkron menggunakan `ExecutorService` untuk menjaga kelancaran UI dan mencegah Application Not Responding (ANR).
- **Interaksi Antar Layar:** Perpindahan dari daftar film ke halaman detail menggunakan **Intent Eksplisit** dengan pengiriman data objek film.

---
*Proyek ini dikembangkan untuk memenuhi tugas akhir praktikum Mobile Programming.*
