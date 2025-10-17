# 🌱 VISOR – Plataforma de Diagnóstico y Conexión Agrícola Inteligente

**VISOR** es una aplicación móvil desarrollada en **Android Studio (Kotlin)** que permite a los productores agrícolas obtener un diagnóstico inteligente del estado de sus cultivos, acceder a información meteorológica y participar en una comunidad digital de colaboración.  
El proyecto integra inteligencia artificial, base de datos MySQL mediante API, autenticación con Firebase, y arquitectura MVVM para garantizar escalabilidad y mantenibilidad.

---

## 🚀 Características Principales

### 🔍 Diagnóstico Inteligente
- Clasificación de enfermedades en cultivos mediante un modelo **TensorFlow Lite (tflite)**.
- Captura de imágenes desde cámara o galería.
- Procesamiento local y visualización de resultados con porcentaje de confianza.
- Posibilidad de almacenar resultados en Firestore o MySQL (según configuración).

### 👥 Autenticación y Gestión de Usuarios
- **Firebase Authentication** (correo y contraseña).
- Validación de correo electrónico.
- Recuperación de contraseña.
- Perfil de usuario con datos cargados desde la base de datos.

### 🌐 Backend Integrado (API + MySQL)
- Servidor Node.js / Express optimizado con conexión **pool** a MySQL.
- Endpoints RESTful para CRUD completo:
   - `/usuarios`
   - `/cultivos`
   - `/diagnosticos`
- Validación, sanitización y manejo de errores.
- Integración en la app mediante **Retrofit** y `ApiClient.kt`.

### 💬 Foro Comunitario
- Espacio de comunicación entre usuarios (similar a un chat global).
- Envío y recepción de mensajes en tiempo real (Firebase Realtime Database o API).
- Interfaz limpia e intuitiva en `ForoActivity.kt` con Material Design.

### 🛒 Marketplace Agrícola
- Sección para publicar y explorar productos agrícolas.
- Visualización de nombre, descripción, precio e imagen.
- Función de refresco mediante `SwipeRefreshLayout`.

### 🧠 Arquitectura de Software
- Implementación basada en el modelo **MVVM (Model–View–ViewModel)**:
   - **Model:** Gestión de datos y conexión con API / Firebase.
   - **ViewModel:** Controla la lógica, estados y actualización de vistas.
   - **View:** Actividades y fragmentos observando `LiveData`.
- Facilita la escalabilidad, depuración y mantenimiento.

### 🧩 Seguridad y Usabilidad
- Validación de inputs en formularios (correo, contraseñas, campos vacíos).
- Sanitización de datos antes del envío a la API.
- Comunicación segura (HTTPS / configuración `network_security_config.xml`).
- Alertas y mensajes amigables para el usuario (Toasts, Snackbars, Dialogs).
- Prevención de acciones accidentales (confirmaciones antes de eliminar).

### 🖥️ Interfaz (UI/UX)
- Diseño responsivo optimizado para móviles y tabletas.
- Paleta de colores y elementos inspirados en el logo de VISOR.
- Implementación de Material 3 y ConstraintLayout adaptativo.
- Formularios, spinners y toolbars personalizadas.
- Animaciones sutiles y feedback visual en botones.

---

## ⚙️ Tecnologías Utilizadas

| Categoría | Tecnología |
|------------|------------|
| Lenguaje | Kotlin |
| Arquitectura | MVVM |
| UI Framework | Android XML + Material Design 3 |
| Inteligencia Artificial | TensorFlow Lite |
| Autenticación | Firebase Auth |
| Base de Datos | MySQL (API REST) |
| Backend | Node.js + Express |
| API Client | Retrofit |
| Almacenamiento | Firebase Firestore / Storage |
| Chat / Foro | Firebase Realtime Database |
| Control de versiones | Git + GitHub |
| Diseño | Figma (Dev Mode) |

---

## 🧠 Estructura del Proyecto (App Android)

