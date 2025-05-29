# 🔐 Spring Boot Authentication API

Bu proje, Spring Boot kullanılarak geliştirilen güvenli bir kullanıcı kimlik doğrulama sistemidir. JWT, Refresh Token, Blacklist, E-posta doğrulama ve şifre sıfırlama gibi modern güvenlik özelliklerini içerir.

## 🚀 Özellikler

- ✅ Kullanıcı Kayıt (Register)
- 🔐 JWT ile Kimlik Doğrulama (Login)
- 🔁 Refresh Token ile Token Yenileme
- 🚪 Güvenli Çıkış (Logout + Blacklist)
- ✅ E-posta ile doğrulama (Email Verification)
- 🔑 Şifre Sıfırlama (Forgot Password)
- 📩 JavaMailSender ile E-posta Gönderimi
- 🛡️ Brute Force Saldırılarına Karşı Koruma

---

## 🛠️ Kullanılan Teknolojiler

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- H2 / PostgreSQL / MySQL (tercihe bağlı)
- JWT (JSON Web Token)
- JavaMailSender
- Thymeleaf (HTML Mail Template için)
- Lombok
- Postman (API Testleri için)

---

## 🧪 API Uç Noktaları (Endpoints)

### 🔐 Kimlik Doğrulama

| Method | Endpoint               | Açıklama                      |
|--------|------------------------|-------------------------------|
| POST   | `/api/auth/register`   | Yeni kullanıcı kaydı          |
| POST   | `/api/auth/login`      | Giriş ve access/refresh token |
| POST   | `/api/auth/refresh`    | Yeni access token alma        |
| POST   | `/api/auth/logout`     | Çıkış ve token blacklist      |

### 📩 E-posta İşlemleri

| Method | Endpoint                          | Açıklama                          |
|--------|-----------------------------------|-----------------------------------|
| POST   | `/api/auth/verify`                | E-posta doğrulama kodu gönderimi  |
| POST   | `/api/auth/verify-password-code`  | Kodu doğrulama işlemi             |
| POST   | `/api/auth/forgot-password`       | Şifre sıfırlama kodu gönderimi    |
| POST   | `/api/auth/reset-password`        | Yeni şifre ile sıfırlama          |

---

## 🔐 Güvenlik Yaklaşımları

- **JWT Access Token**: Kısa süreli, sadece doğrulama için kullanılır.
- **Refresh Token**: Uzun ömürlü, veritabanında saklanır.
- **Blacklist**: Logout olan kullanıcıların token’ları geçersiz sayılır.
- **Şifre Sıfırlama**: Kullanıcının e-postasına tek kullanımlık kod gönderilir.
- **Brute Force Koruması**: Çok sayıda yanlış denemede kullanıcı geçici olarak kilitlenebilir.
- **BCrypt ile güçlü şifreleme**: Her şifre için rastgele salt üretilir ve brute force saldırılarına karşı yüksek güvenlik sağlar.

---

## ⚙️ Projeyi Çalıştırma

### 1. Gerekli bağımlılıkları indir


./mvnw clean install

### 2. Uygulamayı başlat
./mvnw spring-boot:run
