# NutriMeal

Aplikasi Android resep makanan modern yang dibangun dengan Java. Menggabungkan fitur pencarian resep, perencanaan makan mingguan, catatan pribadi, dan sistem autentikasi pengguna.

---

## Fitur Utama

- **Jelajah Resep** — Telusuri resep berdasarkan kategori dengan data real dari TheMealDB API
- **Pencarian Cerdas** — Cari resep berdasarkan nama atau gunakan fitur "What's in my Fridge?" untuk filter berdasarkan bahan yang kamu punya
- **Detail Resep** — Lihat bahan-bahan, langkah memasak, beri rating bintang, dan tonton tutorial YouTube
- **Favorit** — Simpan resep ke daftar favorit pribadimu
- **Catatan Pribadi** — Tulis catatan dan beri rating 1-5 bintang untuk setiap resep
- **Perencana Makan Mingguan** — Rencanakan menu makan untuk 7 hari (Senin-Minggu)
- **Autentikasi Pengguna** — Daftar dan login menggunakan username & password
- **Profil & Foto** — Atur foto profil dan lihat statistik koleksi pribadimu
- **Mode Gelap** — Beralih antara tema terang dan gelap sesuai preferensi
- **Dukungan Offline** — Data tersimpan ditampilkan saat tidak ada koneksi internet

---

## Teknologi yang Digunakan

| Lapisan | Teknologi |
|---------|-----------|
| Bahasa | Java |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 36 |
| Arsitektur | Fragment + Navigation Component |
| Jaringan | Retrofit2 + Gson |
| Pemuatan Gambar | Glide |
| Penyimpanan Lokal | SQLite (SQLiteOpenHelper) |
| Komponen UI | Material Design 3 |
| Navigasi | Navigation Component + BottomNavigationView |

---

## API yang Digunakan

### TheMealDB
Base URL: `https://www.themealdb.com/api/json/v1/1/`

| Endpoint | Kegunaan |
|----------|----------|
| `categories.php` | Ambil semua kategori resep |
| `search.php?s={nama}` | Cari resep berdasarkan nama |
| `filter.php?c={kategori}` | Filter resep berdasarkan kategori |
| `filter.php?i={bahan}` | Filter resep berdasarkan bahan (fitur kulkas) |
| `lookup.php?i={id}` | Ambil detail lengkap resep |

---

## Skema Database

Nama database: `nutrimeal.db`

### users
| Kolom | Tipe |
|-------|------|
| id | INTEGER PK |
| name | TEXT |
| username | TEXT UNIQUE |
| password | TEXT |
| photo_path | TEXT |

### favorites
| Kolom | Tipe |
|-------|------|
| id | INTEGER PK |
| user_id | INTEGER |
| meal_id | TEXT |
| meal_name | TEXT |
| meal_thumb | TEXT |
| category | TEXT |
| area | TEXT |
| instructions | TEXT |
| date_added | TEXT |

### personal_notes
| Kolom | Tipe |
|-------|------|
| id | INTEGER PK |
| user_id | INTEGER |
| meal_id | TEXT |
| meal_name | TEXT |
| note_text | TEXT |
| star_rating | INTEGER |
| date_modified | TEXT |

### meal_planner
| Kolom | Tipe |
|-------|------|
| id | INTEGER PK |
| user_id | INTEGER |
| day_of_week | TEXT |
| meal_id | TEXT |
| meal_name | TEXT |
| meal_thumb | TEXT |

### fridge_ingredients
| Kolom | Tipe |
|-------|------|
| id | INTEGER PK |
| user_id | INTEGER |
| ingredient_name | TEXT |
| date_added | TEXT |

---

## Struktur Proyek
com.example.nutrimeal/

├── activity/

│   ├── SplashActivity.java       # Layar pembuka dengan animasi

│   ├── LoginActivity.java        # Halaman masuk

│   ├── RegisterActivity.java     # Halaman daftar akun

│   └── HomeActivity.java         # Aktivitas utama + BottomNav

├── fragment/

│   ├── HomeFragment.java         # Beranda + kategori + daftar resep

│   ├── SearchFragment.java       # Pencarian + mode kulkas

│   ├── DetailFragment.java       # Detail resep + bahan + instruksi

│   ├── PlannerFragment.java      # Perencana makan mingguan

│   ├── ProfileFragment.java      # Profil + pengaturan

│   ├── FavoritesFragment.java    # Daftar resep favorit

│   └── NotesFragment.java        # Daftar catatan pribadi

├── adapter/

│   ├── MealAdapter.java          # Adapter daftar resep

│   ├── CategoryAdapter.java      # Adapter kategori horizontal

│   ├── IngredientAdapter.java    # Adapter daftar bahan

│   ├── PlannerAdapter.java       # Adapter perencana mingguan

│   ├── FavoritesAdapter.java     # Adapter favorit + picker

│   └── NotesAdapter.java         # Adapter catatan

├── api/

│   ├── ApiClient.java            # Konfigurasi Retrofit

│   └── MealApiService.java       # Definisi endpoint API

├── model/

│   ├── Meal.java                 # Model resep ringkas

│   ├── MealDetail.java           # Model detail resep + bahan

│   ├── MealResponse.java         # Wrapper response API resep

│   ├── MealDetailResponse.java   # Wrapper response detail

│   ├── Category.java             # Model kategori

│   ├── CategoryResponse.java     # Wrapper response kategori

│   ├── Ingredient.java           # Model bahan masakan

│   ├── FavoriteEntity.java       # Model data favorit (DB)

│   ├── NoteEntity.java           # Model data catatan (DB)

│   ├── PlannerEntity.java        # Model data planner (DB)

│   └── User.java                 # Model data pengguna (DB)

├── database/

│   ├── NutriMealDatabase.java    # SQLiteOpenHelper + definisi tabel

│   ├── MealDao.java              # Operasi DB resep, favorit, catatan

│   └── UserDao.java              # Operasi DB pengguna

└── utils/

├── NetworkUtils.java         # Cek ketersediaan internet

├── ThemeUtils.java           # Manajemen tema gelap/terang

└── SessionManager.java       # Manajemen sesi login pengguna

---

## Cara Menjalankan

1. Clone repositori
```bash
git clone https://github.com/naylazaky/NutriMeal.git
```

2. Buka di Android Studio

3. Sinkronisasi dependensi Gradle

4. Jalankan di emulator atau perangkat fisik (API 24+)

> Tidak perlu API key — menggunakan TheMealDB tier gratis.

---

## Dependensi

```gradle
// Navigation Component
implementation 'androidx.navigation:navigation-fragment:2.7.7'
implementation 'androidx.navigation:navigation-ui:2.7.7'

// Retrofit + Gson
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

// Glide
implementation 'com.github.bumptech.glide:glide:4.16.0'

// Material Design
implementation 'com.google.android.material:material:1.14.0'

// SwipeRefreshLayout
implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
```

---

## Lisensi

Proyek ini dibuat untuk Memenuhi tugas akhir Lab Mobile