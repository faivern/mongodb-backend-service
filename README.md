# 🎬 MongoDB Movie Service

A lightweight REST API built in **Java** using **Spark** and **MongoDB**.  
It exposes movie data from the `sample_mflix` dataset through a set of RESTful endpoints.

---

## 🧩 Tech Stack
- **Java** (JDK 17)
- **Spark Framework**
- **MongoDB**
- **JSON / HTTP**
- **Eclipse**

---

## 💡 What it does
This API lets you:
- Fetch movie info by title, actor, or genre  
- Add, update, and delete movie entries  
- Query movies with limit filters  
- Return clean JSON responses with proper HTTP codes

Example:
```bash
GET /title/Inception  → returns movie details  
GET /genre/Action?limit=5  → returns top 5 action movies
