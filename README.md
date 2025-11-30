# 🛒 E-Commerce Platform

A modern full-stack e-commerce application built with **Spring Boot 3** and **React**.

![Homepage Screenshot](screenshots/homepage.png)

## ✨ Features

| User Features | Admin Features |
|--------------|----------------|
| 🔐 JWT Authentication | 📊 Dashboard & Analytics |
| 🛍️ Product Browsing & Search | 📦 Product Management |
| 🛒 Shopping Cart | 📂 Category Management |
| 📦 Order Tracking | 📋 Order Management |
| ⭐ Reviews & Ratings | 👥 User Management |

## 📸 Screenshots

<p align="center">
  <img src="screenshots/products.png" alt="Products Page" width="45%">
  <img src="screenshots/cart.png" alt="Cart Page" width="45%">
</p>

<p align="center">
  <img src="screenshots/admin-dashboard.png" alt="Admin Dashboard" width="45%">
  <img src="screenshots/orders.png" alt="Orders Page" width="45%">
</p>

## 🛠️ Tech Stack

| Backend | Frontend | Database |
|---------|----------|----------|
| Java 21 | React 18 | MongoDB |
| Spring Boot 3.2 | Vite 5 | |
| Spring Security (JWT) | Tailwind CSS | |
| Spring Data MongoDB | Zustand | |

## 🚀 Quick Start

### Prerequisites
- Java 21, Node.js 18+, MongoDB

### Backend
```bash
cd backend
# Set environment variables in .env file
mvn spring-boot:run
```
Server runs at `http://localhost:8080`

### Frontend
```bash
cd frontend
npm install
npm run dev
```
App runs at `http://localhost:5173`

## ⚙️ Environment Variables

**Backend** (`.env`)
```env
MONGODB_URI=mongodb://localhost:27017/ecommerce
JWT_SECRET=your-secret-key
```

**Frontend** (`.env`)
```env
VITE_API_URL=http://localhost:8080/api
```

## 📄 License


## 👥 Team

| Name                | Role                                                      |
|---------------------|------------------------------------------------------------
| Shriram Mange       | [GitHub](https://github.com/Shriram2005) · [Portfolio](https://shrirammange.tech) | · [Email](mailto:mange.shriram@gmail.com) |
| Chanchal Fegade     | [Email](mailto:fegadechanchal@gmail.com) |
| Sahil Gite          | [GitHub](https://github.com/sahilgite1023) · [LinkedIn](https://www.linkedin.com/in/sahilgite2003) |
| Anushka Shinde      | [GitHub](https://github.com/anushkashinde7188) · [Portfolio](https://anushkashinde.netlify.app/) · [Email](mailto:anushkashinde1504@gmail.com) |

---

MIT License

<p align="center">Built with ❤️ using Spring Boot & React</p>
