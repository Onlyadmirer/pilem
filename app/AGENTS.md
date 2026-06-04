# PROFIL PROYEK
- Nama Proyek: Aplikasi Film (Tugas Final Lab Mobile)
- Bahasa: Java (Bukan Kotlin)
- Build Config: Kotlin DSL (build.gradle.kts)
- Minimum SDK: 29

# ATURAN ARSITEKTUR & SPESIFIKASI TEKNIS
1. Komunikasi antar Activity WAJIB menggunakan Intent eksplisit.
2. Harus menggunakan Navigation Component untuk berpindah antar Fragment.
3. Seluruh daftar data WAJIB ditampilkan menggunakan RecyclerView.
4. Networking menggunakan Retrofit. Jika API gagal (tidak ada koneksi), sediakan tombol Refresh di UI.
5. Penyimpanan lokal (Fitur Favorit) menggunakan Room Database.
6. Operasi database (Insert/Delete/Get) WAJIB menggunakan `ExecutorService` murni di Java, dilarang berjalan di Main Thread.
7. Aplikasi harus mendukung Dark Theme dan Light Theme.

# KONFIGURASI KEAMANAN
- API Key TMDB disimpan di `local.properties` dan diakses melalui `BuildConfig.TMDB_API_KEY`.
- Retrofit Client harus memiliki OkHttp Interceptor untuk menyematkan `api_key` tersebut di setiap request secara otomatis.