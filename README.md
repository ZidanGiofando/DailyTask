# DailyTask 📋

Aplikasi Android **Pengelola & Pengingat Tugas Harian** — dibuat untuk memenuhi Ujian Akhir Semester Mobile Programming (PG119), Kelompok AI, Universitas Budi Luhur.

**Disusun oleh:**
- Zidan Giofando — 2411500834
- Fayruz Azzuhri — 2411500024

---

## Daftar Isi

1. [Deskripsi Aplikasi](#1-deskripsi-aplikasi)
2. [Daftar Fitur](#2-daftar-fitur)
3. [Daftar Activity](#3-daftar-activity)
4. [Daftar Intent](#4-daftar-intent)
5. [Daftar Widget](#5-daftar-widget)
6. [Daftar Library](#6-daftar-library)
7. [Manajemen Database & ERD](#7-manajemen-database--erd)
8. [Daftar REST API](#8-daftar-rest-api)

---

## 1. Deskripsi Aplikasi

**Nama Aplikasi:** DailyTask
**Kategori:** Produktivitas

**Latar Belakang:**
Banyak mahasiswa dan pekerja kesulitan mengelola tugas harian karena jumlah aktivitas yang menumpuk, deadline yang mudah terlupakan, dan tidak adanya sistem prioritas yang jelas. Pencatatan tugas secara manual (kertas/catatan singkat) rentan hilang dan tidak dapat diakses dari mana saja.

**Tujuan Solusi:**
- Menyediakan wadah pencatatan tugas harian yang terstruktur (judul, deskripsi, deadline, jam, prioritas, status).
- Membantu pengguna memantau progres penyelesaian tugas melalui dashboard statistik.
- Menyediakan pencarian dan filter agar pengguna cepat menemukan tugas yang relevan.
- Menjamin keamanan akun melalui autentikasi (register/login) dengan password yang di-hash di sisi server.
- Menyimpan seluruh data pada database MySQL/MariaDB melalui REST API PHP — bukan penyimpanan lokal — sehingga data tetap ada meski aplikasi di-uninstall atau berpindah perangkat.

---

## 2. Daftar Fitur

| Fitur | Penjelasan |
|---|---|
| Splash Screen | Mengecek sesi login lalu mengarahkan otomatis ke Login atau Dashboard. |
| Register | Pendaftaran akun baru dengan validasi nama, format email, dan panjang password. |
| Login | Autentikasi pengguna terdaftar dengan validasi input & pesan error dari server. |
| Dashboard | Salam dinamis, ringkasan total/aktif/selesai tugas, progress bar, poin XP, 5 tugas terbaru. |
| Daftar Tugas | RecyclerView dinamis, pencarian judul/deskripsi, filter status (All/Pending/Done), pull-to-refresh. |
| Tambah Tugas | Form dengan DatePicker & TimePicker, pemilihan prioritas (High/Medium/Low), validasi input. |
| Edit Tugas | Form yang sama dengan Tambah Tugas namun ter-prefill data lama. |
| Detail Tugas | Rincian lengkap satu tugas dengan indikator warna sesuai prioritas & status. |
| Tandai Selesai | Mengubah status tugas dari pending menjadi done tanpa membuka form edit. |
| Hapus Tugas | Menghapus tugas dengan dialog konfirmasi. |
| Profile | Data akun, statistik ringkas, info aplikasi & anggota kelompok, logout. |

---

## 3. Daftar Activity

Aplikasi memiliki **6 Activity** (memenuhi ketentuan minimal 3 Activity) dan **3 Fragment** yang berjalan di dalam `MainActivity`.

| Activity | Fungsi |
|---|---|
| `SplashActivity` | Halaman pembuka (2 detik) yang mengecek status login (`SessionManager`) lalu mengarahkan ke `MainActivity` (sudah login) atau `LoginActivity` (belum login). |
| `LoginActivity` | Form login (email & password) dengan validasi input, memanggil REST API `auth/login.php`, menyimpan sesi jika berhasil. |
| `RegisterActivity` | Form pendaftaran akun baru (nama, email, password, konfirmasi password), memanggil REST API `auth/register.php`. |
| `MainActivity` | Container yang menampung 3 Fragment (`DashboardFragment`, `TaskFragment`, `ProfileFragment`) via `BottomNavigationView`. |
| `AddTaskActivity` | Form tambah **maupun** edit tugas (dual mode berdasarkan extra `EXTRA_TASK_ID`), memanggil `tasks/create.php` atau `tasks/update.php`. |
| `DetailTaskActivity` | Detail lengkap satu tugas (dikirim via Intent Serializable), menyediakan aksi Edit, Tandai Selesai, dan Hapus. |

**Fragment** (berjalan di dalam `MainActivity`):
- `DashboardFragment` — ringkasan statistik & tugas terbaru
- `TaskFragment` — daftar seluruh tugas, pencarian, filter
- `ProfileFragment` — data akun, statistik, logout

---

## 4. Daftar Intent

| Jenis Intent | Perpindahan (Dari → Ke) | Tujuan |
|---|---|---|
| Explicit Intent | `SplashActivity` → `LoginActivity` | Arahkan ke login bila belum ada sesi aktif. |
| Explicit Intent | `SplashActivity` → `MainActivity` | Arahkan langsung ke dashboard bila sesi masih tersimpan. |
| Explicit Intent | `LoginActivity` → `RegisterActivity` | Navigasi ke halaman pendaftaran akun baru. |
| Explicit Intent | `LoginActivity` → `MainActivity` | Masuk ke halaman utama setelah login berhasil. |
| Explicit Intent | `RegisterActivity` → `LoginActivity` | Kembali ke login setelah registrasi berhasil. |
| Explicit Intent + extra data | `TaskFragment`/`DashboardFragment` → `AddTaskActivity` | Buka form tambah tugas, atau form edit membawa data tugas (id, title, description, deadline, time, priority) via `putExtra()`. |
| Explicit Intent + Serializable extra | `TaskFragment`/`DashboardFragment` → `DetailTaskActivity` | Kirim objek `Task` utuh via `putExtra()` (`Task implements Serializable`). |
| Explicit Intent + extra data | `DetailTaskActivity` → `AddTaskActivity` | Buka form edit dari halaman detail. |
| Explicit Intent + flags | `ProfileFragment` → `LoginActivity` | Logout: bersihkan session & back stack via `FLAG_ACTIVITY_NEW_TASK` + `FLAG_ACTIVITY_CLEAR_TASK`. |
| Intent + `ActivityResultLauncher` | `TaskFragment` → `AddTaskActivity` / `DetailTaskActivity` | Terima hasil (`RESULT_OK`) agar daftar tugas otomatis dimuat ulang (`loadTasks()`). |

---

## 5. Daftar Widget

| Widget | Digunakan pada | Fungsi |
|---|---|---|
| `TextInputLayout` & `TextInputEditText` | Login, Register, Add/Edit Task | Input teks + pesan error validasi. |
| `MaterialButton` | Semua form & aksi | Tombol Login, Register, Simpan/Update, Edit, Selesai, Hapus, Logout. |
| `RecyclerView` | Dashboard & Task List | Menampilkan daftar tugas dinamis dari REST API. |
| `SwipeRefreshLayout` | Task List | Pull-to-refresh daftar tugas. |
| `ChipGroup` & `Chip` | Task List (filter), Add Task (prioritas) | Filter status (All/Pending/Done) & pilih prioritas (High/Medium/Low). |
| `BottomNavigationView` | MainActivity | Navigasi 3 tab (Dashboard/Task/Profile) tanpa berpindah Activity. |
| `FloatingActionButton` | Task List | Tombol pintas tambah tugas baru. |
| `ProgressBar` | Login, Register, Add Task, Dashboard | Loading indicator & progres penyelesaian tugas. |
| `MaterialCardView` | Profile | Kartu menu "Tentang Aplikasi". |
| `MaterialToolbar` | Add Task, Detail Task | Judul halaman + tombol kembali. |
| `ImageButton` | Item Task (RecyclerView) | Aksi cepat Edit / Selesai / Hapus per baris tugas. |
| `TextView`, `ImageView` | Seluruh layout | Teks informasi & ikon/gambar. |
| `ConstraintLayout`, `LinearLayout`, `FrameLayout`, `NestedScrollView` | Seluruh layout | Struktur dasar penyusunan tampilan. |

---

## 6. Daftar Library

| Library | Kategori | Alasan Penggunaan |
|---|---|---|
| Retrofit2 (2.11.0) | HTTP Client / REST API | Pemanggilan REST API berbasis interface (`ApiService`), request-response lebih terstruktur, minim boilerplate. |
| OkHttp (4.12.0) | HTTP Client dasar | Koneksi jaringan tingkat rendah + timeout agar aplikasi tidak hang saat koneksi lambat. |
| Gson & converter-gson (2.11.0) | JSON Parsing | Konversi otomatis JSON ↔ objek Java (`Task`, `User`, `ApiResponse<T>`). |
| Material Components for Android | UI/UX | Komponen desain modern (Chip, MaterialButton, BottomNavigationView, TextInputLayout, dll). |
| AndroidX AppCompat, ConstraintLayout, Activity-KTX | UI dasar & kompatibilitas | Komponen fundamental AndroidX lintas versi Android. |
| AndroidX SwipeRefreshLayout | UX Task List | Fitur pull-to-refresh saat memuat ulang daftar tugas. |

---

## 7. Manajemen Database & ERD

Aplikasi menggunakan **MySQL/MariaDB** sebagai basis data utama yang diakses sepenuhnya melalui **REST API berbasis PHP** (native `mysqli` + prepared statement). Tidak ada penggunaan SQLite/Room sebagai penyimpanan utama; `SharedPreferences` (`SessionManager`) hanya menyimpan status sesi login, bukan data tugas.

**Struktur Tabel:**
- `users (id, name, email, password, created_at)` — akun pengguna, password disimpan dalam bentuk hash bcrypt.
- `tasks (id, user_id, title, description, deadline, time, priority, status, created_at)` — data tugas, berelasi *one-to-many* terhadap `users` melalui foreign key `user_id` dengan `ON DELETE CASCADE`.

**Entity Relationship Diagram (ERD):**

![ERD DailyTask](docs/erd_dailytask.png)

<img width="906" height="511" alt="image" src="https://github.com/user-attachments/assets/0cace59b-daf4-4e07-9726-dd1e7e5a8e46" />


---

## 8. Daftar REST API

**Base URL** (sesuaikan dengan environment): `http://<host>/dailytask_api/`

| Method | Endpoint | Deskripsi |
|---|---|---|
| POST | `auth/register.php` | Mendaftarkan akun pengguna baru. |
| POST | `auth/login.php` | Autentikasi pengguna (login). |
| GET | `tasks/read.php` | Mengambil seluruh tugas milik seorang user. |
| GET | `tasks/read_single.php` | Mengambil detail satu tugas berdasarkan id. |
| POST | `tasks/create.php` | Menambahkan tugas baru (Create). |
| POST | `tasks/update.php` | Memperbarui data tugas (Update). |
| POST | `tasks/update_status.php` | Memperbarui status tugas menjadi pending/done. |
| POST | `tasks/delete.php` | Menghapus tugas (Delete). |

Seluruh response mengikuti format standar:

```json
{
  "status": "success" | "error",
  "message": "keterangan proses",
  "data": { ... } | [ ... ] | null
}
```

### 1. POST `auth/register.php`
Mendaftarkan akun pengguna baru. Validasi: nama/email/password wajib diisi, format email valid, password minimal 6 karakter, email belum terdaftar.

**Request**
```
POST /dailytask_api/auth/register.php
Content-Type: application/x-www-form-urlencoded

name=Zidan Giofando&email=zidan@gmail.com&password=rahasia123
```

**Response (sukses)**
```json
{
  "status": "success",
  "message": "Registrasi berhasil",
  "data": {
    "id": 1,
    "name": "Zidan Giofando",
    "email": "zidan@gmail.com"
  }
}
```

### 2. POST `auth/login.php`
Autentikasi pengguna berdasarkan email dan password (dicocokkan dengan hash bcrypt).

**Request**
```
POST /dailytask_api/auth/login.php
Content-Type: application/x-www-form-urlencoded

email=zidan@gmail.com&password=rahasia123
```

**Response (sukses)**
```json
{
  "status": "success",
  "message": "Login berhasil",
  "data": {
    "id": 1,
    "name": "Zidan Giofando",
    "email": "zidan@gmail.com"
  }
}
```

### 3. GET `tasks/read.php`
Mengambil seluruh data tugas milik user tertentu, diurutkan berdasarkan deadline dan jam terdekat.

**Request**
```
GET /dailytask_api/tasks/read.php?user_id=1
```

**Response (sukses)**
```json
{
  "status": "success",
  "message": "Data tugas berhasil diambil",
  "data": [
    {
      "id": 3,
      "user_id": 1,
      "title": "Mengerjakan UAS Mobile Programming",
      "description": "Membuat aplikasi DailyTask & dokumentasi",
      "deadline": "2026-07-10",
      "time": "23:59:00",
      "priority": "High",
      "status": "pending",
      "created_at": "2026-07-05 10:20:00"
    }
  ]
}
```

### 4. GET `tasks/read_single.php`
Mengambil detail satu tugas berdasarkan id.

**Request**
```
GET /dailytask_api/tasks/read_single.php?id=3
```

**Response (sukses)**
```json
{
  "status": "success",
  "message": "Data tugas berhasil diambil",
  "data": {
    "id": 3,
    "user_id": 1,
    "title": "Mengerjakan UAS Mobile Programming",
    "description": "Membuat aplikasi DailyTask & dokumentasi",
    "deadline": "2026-07-10",
    "time": "23:59:00",
    "priority": "High",
    "status": "pending",
    "created_at": "2026-07-05 10:20:00"
  }
}
```

### 5. POST `tasks/create.php`
Menambahkan tugas baru. Validasi: `user_id` valid, judul & deskripsi tidak kosong, format deadline (`YYYY-MM-DD`), format jam (`HH:mm`), prioritas salah satu dari High/Medium/Low.

**Request**
```
POST /dailytask_api/tasks/create.php
Content-Type: application/x-www-form-urlencoded

user_id=1&title=Belajar Android&description=Belajar RecyclerView & Retrofit&deadline=2026-07-08&time=19:30&priority=High
```

**Response (sukses)**
```json
{
  "status": "success",
  "message": "Tugas berhasil ditambahkan",
  "data": {
    "id": 4,
    "user_id": 1,
    "title": "Belajar Android",
    "description": "Belajar RecyclerView & Retrofit",
    "deadline": "2026-07-08",
    "time": "19:30",
    "priority": "High",
    "status": "pending"
  }
}
```

### 6. POST `tasks/update.php`
Memperbarui data tugas yang sudah ada berdasarkan id.

**Request**
```
POST /dailytask_api/tasks/update.php
Content-Type: application/x-www-form-urlencoded

id=4&user_id=1&title=Belajar Android Lanjutan&description=Menambahkan fitur searching&deadline=2026-07-09&time=20:00&priority=Medium
```

**Response (sukses)**
```json
{
  "status": "success",
  "message": "Tugas berhasil diperbarui",
  "data": {
    "id": 4,
    "title": "Belajar Android Lanjutan",
    "description": "Menambahkan fitur searching",
    "deadline": "2026-07-09",
    "time": "20:00:00",
    "priority": "Medium",
    "status": "pending"
  }
}
```

### 7. POST `tasks/update_status.php`
Memperbarui status tugas menjadi `pending` atau `done` (dipakai saat pengguna menekan tombol "Tandai Selesai").

**Request**
```
POST /dailytask_api/tasks/update_status.php
Content-Type: application/x-www-form-urlencoded

id=4&status=done
```

**Response (sukses)**
```json
{
  "status": "success",
  "message": "Status tugas berhasil diperbarui",
  "data": {
    "id": 4,
    "status": "done"
  }
}
```

### 8. POST `tasks/delete.php`
Menghapus tugas berdasarkan id.

**Request**
```
POST /dailytask_api/tasks/delete.php
Content-Type: application/x-www-form-urlencoded

id=4
```

**Response (sukses)**
```json
{
  "status": "success",
  "message": "Tugas berhasil dihapus",
  "data": {
    "id": 4
  }
}
```

---
