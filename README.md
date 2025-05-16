Mendaftar Lowongan
##  Modul **Manajemen Mata Kuliah** – Penjelasan kalo misalnya gw sulit di kontak

> Versi API : **v1**  
> Base‑path  : `/api/v1/matakuliah`  
> Auth       : require **JWT / Session** with role `ADMIN`

---
###  *  Kesimpulan (biar gausah baca semuanya, kalo mau aja)

pake ini @Autowired MataKuliahService mkService; 

nanti jsonya kek gini
```
{
    "content": [
        { "kode": "MK001", "nama": "Algoritma", "sks": 3, … },
    …
```

alasan : Karena MataKuliahServiceImpl ditandai @Primary, modul‐modul lain cukup @Autowired MataKuliahService langsung tanpa perlu @Qualifier.

---

###  1  Ringkasan arsitektur (ini tambahannya untuk dependesi)

```
┌──────────────┐      DTO             ┌──────────────────┐
│  Controller   │  <───────────────>  │    Service       │
│ (REST, role) │                      │  (logic + cache) │
└─────▲────────┘                      └────▲─────────────┘
      │ Page<MataKuliahDto>                 │ Entity
      │                                     │
      │                        ┌────────────┴────────────┐
      │                        │  Mapper (MapStruct)      │
      │                        └────────────┬────────────┘
      │                                     │
┌─────┴────────┐                      ┌─────┴────────────┐
│  HTTP  JSON  │                      │  Repository      │
└──────────────┘                      └──────────────────┘
```

* **DTO** (`MataKuliahDto`, `MataKuliahPatch`) – payload masuk/keluar.
* **Mapper** (`MataKuliahMapper`) – compile‑time converter DTO ⇆ Entity, plus `patch()`.
* **Service** – validasi bisnis, cache, paging (`Page<MataKuliahDto>`).
* **Repository** – `InMemoryMataKuliahRepository` (annotated **@Primary**). Ganti ke JPA dengan nama interface sama, monolit otomatis beralih.

---

###  2  Endpoint & contoh pemanggilan

| Method | Path | Deskripsi | Contoh `curl` |
|-------|------|-----------|---------------|
| **GET** | `/api/v1/matakuliah?page=0&size=20` | Daftar mata kuliah (paged) | `curl -H "Authorization: Bearer <TOKEN>" http://host/api/v1/matakuliah` |
| **GET** | `/api/v1/matakuliah/{kode}` | Detail satu MK | `curl -H "Authorization: Bearer <TOKEN>" http://host/api/v1/matakuliah/MK001` |
| **POST** | `/api/v1/matakuliah` | Buat MK baru – **201 + Location** | `curl -X POST -H "Content-Type:application/json" -d '{"kode":"MK001","nama":"Algoritma","sks":3,"deskripsi":"Intro","dosenPengampu":[]}' -H "Authorization: Bearer <TOKEN>" http://host/api/v1/matakuliah` |
| **PUT** | `/api/v1/matakuliah/{kode}` | Ganti seluruh data | `curl -X PUT …` |
| **PATCH** | `/api/v1/matakuliah/{kode}` | Partial update (field null diabaikan) | `curl -X PATCH -d '{"sks":4}' …` |
| **DELETE** | `/api/v1/matakuliah/{kode}` | Hapus MK | `curl -X DELETE …` |

Semua respons error berbentuk **400 Bad Request** + plain‑text pesan (`Kode sudah ada`, `Not found`, …).

---

###  3  Integrasi dengan modul lain (monolitik) (beberapa yang gw coba ngertiin kalo salah maap)

| Modul pemakai | Cara bergantung pada MK | Tips/Pola |
|---------------|------------------------|-----------|
| **Pengampu (dosen)** | Simpan *foreign key* `kode_mk` → call GET detail MK untuk validasi. | Gunakan REST call di service‐layer atau langsung injeksi `MataKuliahService` bila dalam satu jar. |
| **KRS / Registrasi** | Saat mahasiswa menambah MK, panggil GET detail; periksa `sks`. | Tambahkan caching (`@Cacheable`) di service MK sudah aktif (`@EnableCaching`). |
| **Nilai** | Butuh `nama` & `sks` saja → cukup DTO. | Import DTO class – tidak perlu entity. |

*Karena bean‐nya ber‑scope **@Primary**, modul lain cukup autowire `MataKuliahService`.*

---

###  4  Build & lintas‐tim

```bash
./gradlew clean test        # unit + mvc tests
./gradlew bootRun           # jalankan monolitik
```

Semua dependensi MapStruct, Bean‑Validation, Spring‑Data di‑manage oleh **Spring Boot 3.4.x BOM** – tidak perlu versi manual.

Silakan `@Autowire MataKuliahService` atau panggil endpoint di atas; tidak ada konfigurasi tambahan yang diperlukan dalam monolitik HiringGo‑BE.

---

## Software Architecture - Modul 9 - Tutorial: Bagian B

### Hasil diskusi:
Penggunaan arsitektur monolith pada aplikasi yang kami kembangkan mempertimbangkan beberapa aspek yang telah kami diskusikan dan sepakati bersama.
- Pertama, sistem ini memiliki banyak entitas data yang saling terkait, seperti data dosen, mahasiswa, mata kuliah, serta lamaran yang masuk. Dalam arsitektur monolith, seluruh komponen sistem berada dalam satu basis kode yang utuh, sehingga memudahkan pengelolaan dan integrasi antar entitas yang saling berhubungan tanpa perlu membangun komunikasi antarlayanan seperti pada mikroservis. Hal ini dapat mengurangi kompleksitas dalam pengembangan, debugging, dan deployment.
- Kedua, dengan waktu pengembangan yang terbatas, arsitektur monolith lebih efisien karena tidak memerlukan overhead tambahan seperti pengelolaan service discovery, load balancing, dan komunikasi antar layanan yang biasa ditemui pada arsitektur mikroservis. Selain itu, sistem ini ditujukan untuk kebutuhan pengguna yang relatif kecil dan beban trafik yang tidak tinggi, sehingga skala yang dibutuhkan masih dapat ditangani dengan baik oleh sistem monolith tanpa menyebabkan bottleneck yang signifikan.
- Ketiga, dari sisi tim pengembang, terutama jika ukuran tim masih kecil, arsitektur monolith lebih mudah untuk dikembangkan secara terpusat tanpa perlu pembagian tim berdasarkan layanan yang terpisah. Dengan demikian, arsitektur monolith menawarkan efisiensi dalam pengembangan, pengujian, dan pemeliharaan, serta memberikan solusi yang cukup stabil dan cepat untuk konteks kebutuhan sistem yang masih dalam tahap awal atau berskala kecil.

general-container-diagram
![general-container-diagram](/images/general-container-diagram.png)
general-context-diagram
![general-context-diagram](/images/general-context-diagram.png)
general-deployment-diagram
![general-deployment-diagram](/images/general-deployment-diagram.png)