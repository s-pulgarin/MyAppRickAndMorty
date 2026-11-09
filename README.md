# Dimensionados · App de Rick and Morty

App Android hecha en **Kotlin + Jetpack Compose** que consume la [Rick and Morty API](https://rickandmortyapi.com/). Incluye inicio de sesión con **Firebase**, favoritos y récords guardados en **Firestore**, y foto de perfil subida a **Cloudinary**, que se puede tomar con la **cámara** del celular.

---

## Funcionalidades

| Sección | Qué hace |
|---|---|
| **Login / Registro** | Cuenta con correo y contraseña (Firebase Authentication). La sesión queda guardada. |
| **Personajes** | Lista con scroll infinito, búsqueda por nombre y filtros por **estado**, **género** y **especie**. Botón **Al azar** que abre un personaje aleatorio. |
| **Ficha del personaje** | Estado, especie, tipo, género, origen, última ubicación y apariciones (primer y último episodio). Botón ♥ para agregar a favoritos. |
| **Episodios** | Los 51 episodios agrupados por temporada. Cada episodio muestra su fecha y el **reparto** completo. |
| **Ubicaciones** | Desde la ficha, al tocar *Origen* o *Última ubicación* se abre el lugar con su tipo, dimensión y residentes. |
| **Favoritos** | Lista de favoritos de cada usuario, guardada en Firestore y sincronizada en tiempo real. |
| **Perfil** | Foto de perfil (**cámara** o galería), estadísticas de los favoritos y acceso al quiz. |
| **Quiz ¿Vivo o muerto?** | Mini juego en modo racha: adivina si un personaje al azar está vivo o muerto. El récord se guarda en Firestore. |

## Tecnologías

- Kotlin, Jetpack Compose, Material 3, Navigation Compose
- ViewModel + estado de Compose (`mutableStateOf`)
- Retrofit + Gson para la API REST
- Coil para las imágenes
- Firebase Authentication y Cloud Firestore
- Cloudinary (subida de imágenes sin firma)
- Cámara del dispositivo: `ActivityResultContracts.TakePicture` + `FileProvider` + permiso en tiempo de ejecución

---

## Requisitos

- **Android Studio** reciente, compatible con Android Gradle Plugin **9.3**, que es la versión que usa el proyecto. El wrapper descarga Gradle 9.5 automáticamente.
- **JDK 17 o superior**. El que trae Android Studio sirve.
- Un emulador o celular con **Android 7.0 (API 24)** o superior.
- Una cuenta de **Firebase** (plan gratuito Spark) y otra de **Cloudinary** (plan gratuito).

> Hay dos archivos con datos privados que **no están en el repositorio** (están en `.gitignore`) y cada persona debe crear: `app/google-services.json` y las claves de Cloudinary en `local.properties`. Los pasos para crearlos están abajo.

---

## Instalación paso a paso

### 1. Clonar el proyecto

```bash
git clone https://github.com/s-pulgarin/MyAppRickAndMorty.git
```

Ábrelo en Android Studio con **File → Open** y espera a que termine el primer *Gradle Sync*.

### 2. Configurar Firebase

1. Entra a la [consola de Firebase](https://console.firebase.google.com/) y crea un proyecto.
2. **Agrega una app Android** con este nombre de paquete exacto:
   ```
   com.example.myapprickandmorty
   ```
3. Descarga el archivo **`google-services.json`** y cópialo en la carpeta **`app/`** del proyecto, de modo que quede en `app/google-services.json`.
4. **Authentication** → *Comenzar* → en *Método de acceso* activa **Correo electrónico/contraseña**.
5. **Firestore Database** → *Crear base de datos* → elige una ubicación y empieza en **modo de producción**.
6. En **Firestore → Reglas**, reemplaza todo por las reglas de la sección siguiente y dale **Publicar**.

#### Reglas de Firestore

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{uid} {
      // Perfil del usuario (nombre, correo, foto, récord del quiz)
      allow read, write: if request.auth != null && request.auth.uid == uid;

      // Favoritos del usuario
      match /favorites/{characterId} {
        allow read, write: if request.auth != null && request.auth.uid == uid;
      }
    }
  }
}
```

Cada usuario solo puede leer y escribir **sus propios** datos. El bloque de `favorites` es necesario porque la regla de `users/{uid}` **no aplica a las subcolecciones**. Sin él, los favoritos fallan con *"No se pudo actualizar el favorito"*.

#### Estructura de datos en Firestore

```
users/{uid}
 ├─ uid: string
 ├─ name: string
 ├─ email: string
 ├─ photoUrl: string          ← URL de Cloudinary
 ├─ quizBest: number          ← récord del quiz
 └─ favorites/{characterId}
     ├─ id: number
     ├─ name: string
     ├─ image: string
     ├─ status: string
     ├─ species: string
     └─ addedAt: number       ← para ordenar del más reciente al más antiguo
```

No hace falta crear nada a mano: la app crea los documentos sola al registrarse, al agregar favoritos y al jugar el quiz.

### 3. Configurar Cloudinary (foto de perfil)

1. Crea una cuenta en [cloudinary.com](https://cloudinary.com/). En el **Dashboard** copia tu **Cloud name**.
2. Ve a **Settings → Upload → Upload presets → Add upload preset**:
   - **Signing mode:** `Unsigned`
   - Guarda el preset y copia su **nombre**.
3. Abre el archivo **`local.properties`** en la raíz del proyecto (Android Studio lo crea solo) y agrega al final:

   ```properties
   cloudinary.cloudName=TU_CLOUD_NAME
   cloudinary.uploadPreset=TU_UPLOAD_PRESET
   ```

4. Haz **File → Sync Project with Gradle Files** para que Gradle lea los valores.

> La app solo usa el *cloud name* y un preset sin firma. **Nunca pongas el `api_secret` de Cloudinary en la app.** Las fotos quedan en la carpeta `rickandmorty/profiles` de tu cuenta.

Si no configuras Cloudinary, la app funciona igual; lo único que falla es la subida de la foto de perfil.

### 4. Ejecutar

Elige un emulador o celular y dale **Run ▶** en Android Studio. También puedes compilar desde la terminal:

```bash
./gradlew assembleDebug
```

El APK queda en `app/build/outputs/apk/debug/app-debug.apk`.

---

## Permisos

| Permiso | Para qué |
|---|---|
| `INTERNET` | API de Rick and Morty, Firebase y Cloudinary. |
| `CAMERA` | Tomar la foto de perfil. Se pide **en tiempo de ejecución** la primera vez que tocas *Tomar foto*. |

La cámara está declarada como **opcional** (`uses-feature ... required="false"`), así que la app se puede instalar en equipos sin cámara. La foto se guarda primero en un archivo temporal (`cache/camera/`), que se comparte con la app de cámara mediante un `FileProvider`, y luego se sube a Cloudinary.

**En el emulador** la cámara muestra una escena virtual. Para probarla con una cámara real, usa un celular físico o configura la cámara del AVD como *Webcam0* en Device Manager.

---

## Estructura del proyecto

```
app/src/main/java/com/example/myapprickandmorty/
├─ MainActivity.kt          Navegación (NavHost) y rutas de todas las pantallas
├─ MyApp.kt                 Inicializa Cloudinary
├─ data/
│  ├─ auth/                 Login y registro (Firebase Auth)
│  ├─ favorites/            Favoritos en Firestore y cálculo de estadísticas
│  ├─ model/                Modelos de la API (personaje, episodio, ubicación) y filtros
│  ├─ profile/              Perfil en Firestore y subida a Cloudinary
│  └─ remote/               Retrofit: endpoints de la Rick and Morty API
└─ ui/
   ├─ auth/                 Pantallas de login y registro
   ├─ home/                 ViewModel de la lista (búsqueda, filtros, scroll infinito)
   ├─ detail/               ViewModel de la ficha del personaje
   ├─ episodes/             ViewModels de episodios
   ├─ locations/            ViewModel de ubicaciones
   ├─ favorites/            ViewModel de favoritos
   ├─ profile/              Perfil, estadísticas y foto (cámara / galería)
   ├─ quiz/                 Lógica del quiz
   ├─ screens/              Pantallas Compose y barra de navegación
   └─ theme/                Colores y tipografía
```

### Endpoints de la API que se usan

| Endpoint | Uso |
|---|---|
| `GET /character?page=&name=&status=&gender=&species=` | Lista, búsqueda y filtros |
| `GET /character/{id}` | Ficha del personaje |
| `GET /character/{id1,id2,...}` | Reparto de un episodio, residentes de un lugar y quiz |
| `GET /episode?page=` | Lista de episodios (3 páginas) |
| `GET /episode/{id}` y `/episode/{id1,id2}` | Detalle y primera/última aparición |
| `GET /location/{id}` | Detalle de una ubicación |

> Ojo: cuando se piden varios IDs, la API responde una **lista**, pero si se pide **uno solo** responde un **objeto**. La app maneja los dos casos.

---

## Problemas comunes

**Error con `aapt2-...-windows.jar` al compilar.** La caché de Gradle quedó incompleta. Fuerza la descarga de nuevo:

```bash
./gradlew assembleDebug --refresh-dependencies
```

**"File google-services.json is missing".** Falta el paso 2.3: el archivo debe estar en `app/google-services.json`.

**"No se pudo actualizar el favorito" o los favoritos no cargan.** Las reglas de Firestore no incluyen la subcolección `favorites`. Revisa la sección de reglas.

**No sube la foto de perfil.** Revisa que `local.properties` tenga `cloudinary.cloudName` y `cloudinary.uploadPreset`, que el preset sea **Unsigned** y que hayas hecho *Sync* después de agregarlos.

**"Necesitas dar permiso de cámara para tomar la foto".** El permiso fue rechazado. Actívalo en *Ajustes → Apps → MyAppRickAndMorty → Permisos → Cámara*.

**Aviso "This app isn't 16 KB compatible" en el emulador.** Viene de librerías nativas de terceros (manejo de imágenes) y no impide usar la app. Toca **OK**.

---

## Créditos

Datos e imágenes de [The Rick and Morty API](https://rickandmortyapi.com/). *Rick and Morty* es propiedad de sus respectivos dueños; este es un proyecto académico sin fines comerciales.
