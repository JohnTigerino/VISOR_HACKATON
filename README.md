# VISOR 🌱

*Cuidamos del campo contigo.*

---

## 📖 Propósito del proyecto

**VISOR** es una aplicación móvil Android para productores agrícolas.
Permite **analizar cultivos**, recibir **alertas tempranas**, y consultar **recomendaciones personalizadas** con apoyo de IA e imágenes. Además, incluye monitoreo de humedad, plagas y módulos multigrano (frijol, soja, maíz).

En pocas palabras: **una herramienta digital que acerca la tecnología al campo, ayudando al productor a tomar mejores decisiones.**

---

## ⚙️ Instalación y ejecución

### Requisitos

* Android Studio **Koala/Giraffe o superior**
* **JDK 17** (incluido en Android Studio)
* Min SDK **26** (Android 8)

### Pasos

1. Clonar el repositorio:

   ```bash
   git clone https://github.com/JohnTigerino/VISOR_HACKATON.git
   cd VISOR_HACKATON
   ```
2. Abrir el proyecto en **Android Studio**.
3. Esperar a que sincronice las dependencias de **Gradle**.
4. Conectar un emulador o dispositivo físico.
5. Ejecutar con el botón ▶️ (Run).

---

## ✨ Funcionalidades iniciales

* Autenticación (login/registro).
* Pantalla principal con módulos de cultivos.
* Interfaz con **ConstraintLayout** optimizada para móviles.
* Estructura lista para integrar análisis con IA y alertas.

---

## 📦 Stack técnico

* **Android**: Kotlin, AndroidX, Material 3
* **Arquitectura**: MVVM (sugerida), ViewModel, LiveData/Flow
* **Firebase**: Auth, Firestore, Storage, FCM

---

## 📁 Estructura del proyecto (resumen)

```text
app/
├─ manifests/
│  └─ AndroidManifest.xml        👉 Declaración de Activities, permisos y tema
├─ kotlin+java/com/bionica/visor_prueba3/
│  ├─ AuthActivity.kt            👉 Pantalla de login
│  ├─ RegisterActivity.kt        👉 Pantalla de registro
│  └─ HomeActivity.kt            👉 Pantalla principal
├─ res/
│  ├─ layout/                    👉 XML de interfaces
│  ├─ drawable/                  👉 Fondos, botones, formas
│  ├─ values/                    👉 Colores, strings, temas
│  ├─ mipmap/                    👉 Íconos de la app
│  └─ xml/                       👉 Reglas de backup y restore
└─ build.gradle.kts              👉 Configuración del módulo app
```

---

## 👥 Contactos

**Equipo VISOR**

* Massiel Torrez – 📧 [massielt054@gmail.com](mailto:massielt054@gmail.com) – 📱 +505 7823-1150
* Gonzalo Centeno – 📧 [gonzalocenteno343@gmail.com] 📱 +505 7782-4923
* Shari Ramirez – 📧 [shariramirez239@gmail.com](mailto:shariramirez239@gmail.com) – 📱 +505 7519-2157
* John Tigerino – 📧 [johntigerino2103@gmail.com](mailto:johntigerino2103@gmail.com) – 📱 +505 8523-5193
* Oscar Mojica – 📧 [o.gabrielbarrera@gmail.com](mailto:o.gabrielbarrera@gmail) – 📱 +505 8129 0891

