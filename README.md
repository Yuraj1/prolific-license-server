# 📜 Prolific Licence Server

Licence server for **Prolific** application. Provides generation, validation and management of user licence keys.

---

## 🚀 How to run

### 🧾 Requirements

- Java 21 (installed and available in `PATH`)
- Gradle 8.12+ (the project already has Gradle Wrapper, you don't need to install it separately)
  - Default port: `8081`
    spring.application.name = `prolific-license-server`
---

### 💻 Cloning the project

``bash
git clone https://github.com/Yuraj1/prolific-license-server.git
cd prolific-license-server


./gradlew bootRun

http://localhost:8081


licenses.dat file stores all created licences 

activations.dat file stores all activated licences