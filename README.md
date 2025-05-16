Mendaftar Lowongan
##  Modul **Manajemen Mata Kuliah** – Penjelasan kalo misalnya gw sulit di kontak

> Versi API : **v1**  
> Base‑path  : `/api/v1/matakuliah`  
> Auth       : require **JWT / Session** with role `ADMIN`

---
###  *  Kesimpulan 
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

