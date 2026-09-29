# Prompts para Gemini – Laboratorio 1TEL05

> **Antes de usarlo**
> 1. Confirma con el JP que se permite Gemini y **qué** se permite (¿pegar archivos? ¿usar el chat web?). El sílabo exige **declarar y citar** el uso de IA generativa cuando el docente lo indique y puede pedir el **reporte de prompts**: guarda tu conversación (ver plantilla de declaración al final).
> 2. Prepara este archivo **antes** del laboratorio (impreso, en el celular, en tu correo o en la nube), por si dentro del lab no tienes acceso a tu PC.
> 3. Abre el enunciado del lab, la API en el navegador y **copia un ejemplo del JSON** (es lo que más evita errores).

## Cómo usarlo (2 minutos)
1. Abre un chat **nuevo** en Gemini.
2. Copia **todo** lo que está entre `INICIO DEL PROMPT A` y `FIN DEL PROMPT A` y pégalo.
3. Baja al final del prompt y reemplaza `[PEGA AQUÍ ...]` por el enunciado y por el JSON de ejemplo de la API (y, si el enunciado da nombre/paquete/campos, déjalos dentro del enunciado).
4. Envía. Si Gemini corta la respuesta, escribe `sigue`.
5. Copia los archivos a Android Studio en el orden en que Gemini los entrega (Gradle → manifest → res → java) y sigue sus "Pasos manuales".
6. Si algo falla usa los **prompts de seguimiento** (S1–S6) en la **misma conversación**.

Si el chat no acepta tanto texto: borra la sección `PROYECTO MODELO` del prompt (queda solo la parte de reglas) y pega los archivos del modelo por partes o solo los que vayas a cambiar.

---

## ===== INICIO DEL PROMPT A =====

Actúa como **ingeniero Android senior y tutor**. Estoy rindiendo un laboratorio práctico **individual y con tiempo limitado** del curso "Servicios y Aplicaciones para IoT" (1TEL05, PUCP). Necesito un proyecto de **Android Studio en Java** que cumpla el ENUNCIADO del final al nivel **Avanzado** de la rúbrica, usando como base el PROYECTO MODELO que te pego más abajo (ya compila y funciona; sigue **su mismo estilo, nombres y convenciones**).

### 1. Rúbrica (20 puntos) – todo debe cumplirse al nivel Avanzado
| Criterio | Nivel Avanzado |
|---|---|
| 1. Implementación de interfaces (3) | Presenta **todas** las pantallas solicitadas, integrando correctamente los componentes visuales y la información requerida en cada una. |
| 2. Fragments y navegación (4) | Implementa **todos** los fragments solicitados con navegación fluida y coherente, respetando el flujo definido. |
| 3. Sensores (3) | Implementa correctamente el sensor solicitado, **obtiene y procesa** sus datos y los **integra** en la app según lo requerido. |
| 4. RecyclerView con información externa (5) | RecyclerView que obtiene y presenta **dinámicamente** una lista de objetos desde un **servicio externo**, aplicando **buenas prácticas** de Android. |
| 5. Funcionalidad (5) | App **completamente funcional** que cumple todas las funcionalidades del enunciado. |

### 2. Reglas técnicas (no las rompas)
- **Java 11**. Nada de Kotlin, Compose, DataBinding ni `findViewById`: usa **ViewBinding** en todo (fragments y ViewHolder).
- **Una sola Activity** con `NavHostFragment`. Navegar **solo** con `NavController` + **Safe Args** (clases `...Directions` / `...Args`); prohibido `startActivity` entre pantallas o `FragmentTransaction` manual para navegar. Objetos entre pantallas: argumento `Serializable` en el `nav_graph`. Usa animaciones `enter/exit/popEnter/popExit` en las acciones (como el modelo).
- **Versiones EXACTAS** (no las cambies ni inventes otras): AGP `9.2.0`, Gradle `9.4.1`, plugin `androidx.navigation.safeargs` `2.9.6`, `navigation-fragment` y `navigation-ui` `2.9.6`, Retrofit `2.9.0`, converter-gson `2.9.0`, Gson `2.10`, appcompat `1.7.1`, material `1.13.0`, constraintlayout `2.2.1`, activity `1.8.0`, compileSdk `36`, minSdk `34`, targetSdk `36`. **No agregues otras librerías.** Si el enunciado exige algo que las necesite (p. ej. cargar imágenes por URL), dilo **antes** del código y explica por qué.
- **Paquete base** `com.example.labmodelo` con subpaquetes `fragmentos/`, `entity/`, `retrofitHelpers/`, `adapter/`, `viewModels/`. Si el enunciado pide otro nombre de app/paquete, úsalo y ajusta `namespace`, `applicationId` y `package`.
- **Retrofit**: interfaz en `retrofitHelpers/`; servicio creado en un método `createRetrofitService()`; llamadas con `enqueue` + `Callback` (`onResponse`/`onFailure`); `baseUrl` termina en `/` y el `@GET` va sin `/` inicial; **HTTPS** sin cleartext (solo si el enunciado da una URL `http://` agrega `usesCleartextTraffic` y avísame). Permiso `INTERNET` en el manifest. Si el JSON es un arreglo → `Call<List<Entidad>>`; si es un objeto contenedor → crea un Dto.
- **RecyclerView (buenas prácticas OBLIGATORIAS)**: el adapter **no guarda Context** (usa `parent.getContext()`); el click se comunica con una **interfaz listener** hacia el fragment (el adapter no navega); adapter y `LayoutManager` se crean **una sola vez** y los datos se actualizan con `setLista()` + `notifyDataSetChanged()` (o DiffUtil); estados visibles: **ProgressBar** cargando, **mensaje de error** (falla de red y respuesta no exitosa) y **lista vacía**.
- **Fragments**: `binding = null` en `onDestroyView`; usar `getViewLifecycleOwner()` al observar LiveData y `requireContext()` / `requireArguments()`.
- **Sensor**: verificar que existan `SensorManager` y el `Sensor` (si no, mostrar un mensaje en pantalla y **no crashear**); `registerListener` en `onResume` y `unregisterListener` en `onPause`; implementar `SensorEventListener`; **mostrar los valores en TextViews** (no solo Logcat); calcular un **valor derivado** y cambiar mensaje/color según un **umbral**.
- **Logs** con tag `"msg-test-<NombreDeLaClase>"`. **Comentarios en español, cortos**, explicando el "por qué".
- **No agregues funcionalidades que el enunciado no pida.**

### 3. Cómo quiero que trabajes
1. Empieza con una sección **SUPUESTOS** (máx. 8 líneas): endpoint, campos de la entidad que usarás (**solo campos presentes en el JSON de ejemplo**; no inventes campos), sensor elegido, umbral, nombres de pantallas. **No me hagas preguntas** salvo que falte el enunciado o el JSON de ejemplo; en ese caso pídeme solo eso.
2. Reutiliza el modelo: cambia solo lo que el enunciado obliga (entidad, servicio, baseUrl, layouts del ítem/detalle, sensor, textos, pantallas nuevas). Si el enunciado pide **más pantallas** de las del modelo, agrégalas como fragments nuevos (layout + clase + destino y acciones en `nav_graph` + Directions).
3. Entrega **cada archivo COMPLETO** (nunca "..." ni "el resto igual"), cada uno con su **ruta relativa a la raíz del proyecto** como título, en este orden: Gradle → `AndroidManifest.xml` → `res/values` → `res/navigation` → `res/layout` → Java. Solo los archivos que cambian **más** los nuevos; los que no cambian, dime "sin cambios: ...".
4. Al final: **PASOS MANUALES** (sync de Gradle, rebuild, emulador con API 34 o superior, cómo probar cada pantalla) y una **AUTOVERIFICACIÓN vs RÚBRICA**: tabla con cada criterio, ✔/✘ y el archivo donde se cumple.
5. Antes de responder, revisa que: los ids de layout coincidan con lo usado en `binding.*`; los nombres de acciones/argumentos del `nav_graph` coincidan con las `Directions`/`Args`; todo import exista; ninguna clase usada esté sin crear.
6. Si la respuesta es muy larga, córtala en partes, termina cada una con `CONTINÚA` y espera mi `sigue`.

### 4. PROYECTO MODELO (referencia que compila)

Estructura del modelo: una Activity (MainActivity) con NavHostFragment; fragments Home, Lista, Detalle, Sensor; API de ejemplo https://jsonplaceholder.typicode.com/users; sensor: acelerómetro. Los archivos con `>>> CAMBIAR AQUÍ <<<` son los que normalmente se adaptan.

#### ARCHIVO: build.gradle

```groovy
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id 'com.android.application' version '9.2.0' apply false
    // El plugin de Safe Args debe tener la MISMA versión que las librerías navigation-* del app/build.gradle
    id 'androidx.navigation.safeargs' version '2.9.6' apply false
}
```

#### ARCHIVO: settings.gradle

```groovy
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "LabModelo"
include ':app'
```

#### ARCHIVO: gradle.properties

```properties
# Project-wide Gradle settings.
# IDE (e.g. Android Studio) users:
# Gradle settings configured through the IDE *will override*
# any settings specified in this file.
# For more details on how to configure your build environment visit
# http://www.gradle.org/docs/current/userguide/build_environment.html
# Specifies the JVM arguments used for the daemon process.
# The setting is particularly useful for tweaking memory settings.
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
# When configured, Gradle will run in incubating parallel mode.
# This option should only be used with decoupled projects. More details, visit
# http://www.gradle.org/docs/current/userguide/multi_project_builds.html#sec:decoupled_projects
# org.gradle.parallel=true
# AndroidX package structure to make it clearer which packages are bundled with the
# Android operating system, and which are packaged with your app's APK
# https://developer.android.com/topic/libraries/support-library/androidx-rn
android.useAndroidX=true
# Enables namespacing of each library's R class so that its R class includes only the
# resources declared in the library itself and none from the library's dependencies,
# thereby reducing the size of the R class for that library
android.nonTransitiveRClass=true
android.defaults.buildfeatures.resvalues=true
android.sdk.defaultTargetSdkToCompileSdkIfUnset=false
android.enableAppCompileTimeRClass=false
android.usesSdkInManifest.disallowed=false
android.uniquePackageNames=false
android.dependency.useConstraints=true
android.r8.strictFullModeForKeepRules=false
android.r8.optimizedResourceShrinking=false
android.builtInKotlin=false
android.newDsl=false
```

#### ARCHIVO: app/build.gradle

```groovy
plugins {
    id 'com.android.application'
    id 'androidx.navigation.safeargs'
}

android {
    namespace 'com.example.labmodelo'
    compileSdk 36

    defaultConfig {
        applicationId "com.example.labmodelo"
        minSdk 34
        targetSdk 36
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding true
    }
}

dependencies {
    // Base (versiones de Clase5)
    implementation 'androidx.appcompat:appcompat:1.7.1'
    implementation 'com.google.android.material:material:1.13.0'
    implementation 'androidx.activity:activity:1.8.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.2.1'
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.3.0'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.7.0'

    // Retrofit + Gson (Clase5)
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.google.code.gson:gson:2.10'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

    // Navigation Component (Clase6). Clase6 usaba 2.5.3 con el plugin 2.9.6;
    // aquí se alinean ambos a 2.9.6 para evitar desajustes con el código que genera Safe Args.
    implementation 'androidx.navigation:navigation-fragment:2.9.6'
    implementation 'androidx.navigation:navigation-ui:2.9.6'
}
```

#### ARCHIVO: app/src/main/AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <!-- Sin este permiso Retrofit falla con "Unable to resolve host" / SecurityException -->
    <uses-permission android:name="android.permission.INTERNET" />

    <!-- El acelerómetro es opcional: la app no crashea si falta (SensorFragment lo valida) -->
    <!-- >>> CAMBIAR AQUÍ <<< si cambias de sensor: android.hardware.sensor.light / android.hardware.sensor.proximity -->
    <uses-feature
        android:name="android.hardware.sensor.accelerometer"
        android:required="false" />

    <!-- Sin usesCleartextTraffic: la API de ejemplo es HTTPS. Si tu enunciado usa http://, ver GUIA_ADAPTACION.md -->
    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.LabModelo"
        tools:targetApi="34">
        <!-- Una sola Activity: aloja el NavHostFragment -->
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

#### ARCHIVO: app/src/main/res/values/strings.xml

```xml
<resources>
    <string name="app_name">LabModelo</string>

    <!-- >>> CAMBIAR AQUÍ <<< textos según el enunciado -->
    <!-- Home -->
    <string name="home_titulo">Laboratorio modelo IoT</string>
    <string name="home_subtitulo">RecyclerView + Retrofit + Sensores</string>
    <string name="home_boton_lista">Ver lista</string>
    <string name="home_boton_sensor">Ver sensor</string>

    <!-- Lista -->
    <string name="lista_titulo">Lista de usuarios</string>
    <string name="lista_vacia">No hay datos para mostrar</string>
    <string name="lista_error_red">No se pudo conectar. Revisa tu conexión a internet.</string>
    <string name="lista_error_servidor">El servidor respondió con un error. Intenta más tarde.</string>
    <string name="item_boton_detalle">Ver detalle</string>

    <!-- Detalle -->
    <string name="detalle_titulo">Detalle del usuario</string>
    <string name="detalle_label_id">ID</string>
    <string name="detalle_label_nombre">Nombre</string>
    <string name="detalle_label_usuario">Usuario</string>
    <string name="detalle_label_email">Email</string>
    <string name="detalle_label_telefono">Teléfono</string>
    <string name="detalle_label_web">Sitio web</string>
    <string name="detalle_label_empresa">Empresa</string>
    <string name="detalle_sin_dato">-</string>

    <!-- Sensor -->
    <string name="sensor_titulo">Acelerómetro</string>
    <string name="sensor_sin_manager">Este dispositivo no posee sensores</string>
    <string name="sensor_no_disponible">Este dispositivo no dispone de acelerómetro</string>
    <string name="sensor_disponible">Acelerómetro detectado</string>
    <string name="sensor_valor_x">x: %1$.2f m/s²</string>
    <string name="sensor_valor_y">y: %1$.2f m/s²</string>
    <string name="sensor_valor_z">z: %1$.2f m/s²</string>
    <string name="sensor_valor_magnitud">Magnitud: %1$.2f m/s²</string>
    <string name="sensor_umbral">Umbral de movimiento: ±%1$.1f m/s² sobre la gravedad</string>
    <string name="sensor_mensaje_estable">Dispositivo estable</string>
    <string name="sensor_mensaje_movimiento">¡Movimiento brusco!</string>
</resources>
```

#### ARCHIVO: app/src/main/res/values/colors.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
    <!-- Colores del mensaje del sensor (verde = estable, rojo = supera el umbral) -->
    <color name="sensor_estable">#FF2E7D32</color>
    <color name="sensor_alerta">#FFC62828</color>
</resources>
```

#### ARCHIVO: app/src/main/res/values/themes.xml

```xml
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme. DayNight: sigue el modo claro/oscuro del sistema -->
    <style name="Base.Theme.LabModelo" parent="Theme.Material3.DayNight.NoActionBar">
        <!-- Customize your theme here. -->
        <!-- <item name="colorPrimary">@color/my_light_primary</item> -->
    </style>

    <style name="Theme.LabModelo" parent="Base.Theme.LabModelo" />

    <!-- Estilos de la pantalla de detalle: etiqueta (arriba) y valor (abajo) -->
    <style name="EtiquetaDetalle">
        <item name="android:layout_width">wrap_content</item>
        <item name="android:layout_height">wrap_content</item>
        <item name="android:layout_marginTop">16dp</item>
        <item name="android:textSize">14sp</item>
        <item name="android:textStyle">bold</item>
    </style>

    <style name="ValorDetalle">
        <item name="android:layout_width">wrap_content</item>
        <item name="android:layout_height">wrap_content</item>
        <item name="android:textSize">20sp</item>
    </style>
</resources>
```

#### ARCHIVO: app/src/main/res/anim/enter_anim.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android">
    <translate android:duration="200"
        android:fromXDelta="100%"
        android:toXDelta="0%"
        />
</set>
```

#### ARCHIVO: app/src/main/res/anim/exit_anim.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android">
    <translate
        android:fromXDelta="0%"
        android:toXDelta="-100%"
        android:duration="200" />
</set>
```

#### ARCHIVO: app/src/main/res/anim/pop_enter_anim.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android">
    <translate
        android:fromXDelta="-100%"
        android:toXDelta="0%"
        android:duration="200" />
</set>
```

#### ARCHIVO: app/src/main/res/anim/pop_exit_anim.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android">
    <translate
        android:fromXDelta="0%"
        android:toXDelta="100%"
        android:duration="200" />
</set>
```

#### ARCHIVO: app/src/main/res/navigation/nav_graph.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/nav_graph"
    app:startDestination="@id/homeFragment">

    <!-- Home: punto de entrada. Safe Args genera HomeFragmentDirections a partir de estas acciones -->
    <fragment
        android:id="@+id/homeFragment"
        android:name="com.example.labmodelo.fragmentos.HomeFragment"
        android:label="fragment_home"
        tools:layout="@layout/fragment_home">
        <action
            android:id="@+id/action_homeFragment_to_listaFragment"
            app:destination="@id/listaFragment"
            app:enterAnim="@anim/enter_anim"
            app:exitAnim="@anim/exit_anim"
            app:popEnterAnim="@anim/pop_enter_anim"
            app:popExitAnim="@anim/pop_exit_anim" />
        <action
            android:id="@+id/action_homeFragment_to_sensorFragment"
            app:destination="@id/sensorFragment"
            app:enterAnim="@anim/enter_anim"
            app:exitAnim="@anim/exit_anim"
            app:popEnterAnim="@anim/pop_enter_anim"
            app:popExitAnim="@anim/pop_exit_anim" />
    </fragment>

    <fragment
        android:id="@+id/listaFragment"
        android:name="com.example.labmodelo.fragmentos.ListaFragment"
        android:label="fragment_lista"
        tools:layout="@layout/fragment_lista">
        <action
            android:id="@+id/action_listaFragment_to_detalleFragment"
            app:destination="@id/detalleFragment"
            app:enterAnim="@anim/enter_anim"
            app:exitAnim="@anim/exit_anim"
            app:popEnterAnim="@anim/pop_enter_anim"
            app:popExitAnim="@anim/pop_exit_anim" />
    </fragment>

    <fragment
        android:id="@+id/detalleFragment"
        android:name="com.example.labmodelo.fragmentos.DetalleFragment"
        android:label="fragment_detalle"
        tools:layout="@layout/fragment_detalle">
        <!-- >>> CAMBIAR AQUÍ <<< nombre y tipo del argumento (tu entidad Serializable) -->
        <argument
            android:name="usuario"
            app:argType="com.example.labmodelo.entity.Usuario" />
    </fragment>

    <fragment
        android:id="@+id/sensorFragment"
        android:name="com.example.labmodelo.fragmentos.SensorFragment"
        android:label="fragment_sensor"
        tools:layout="@layout/fragment_sensor" />
</navigation>
```

#### ARCHIVO: app/src/main/res/layout/activity_main.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <!-- Único contenedor de la app: los 4 fragments se muestran aquí según nav_graph -->
    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/fragmentContainerView"
        android:name="androidx.navigation.fragment.NavHostFragment"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:defaultNavHost="true"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:navGraph="@navigation/nav_graph" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

#### ARCHIVO: app/src/main/res/layout/fragment_home.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".fragmentos.HomeFragment">

    <TextView
        android:id="@+id/textViewTitulo"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/home_titulo"
        android:textSize="28sp"
        android:textStyle="bold"
        app:layout_constraintBottom_toTopOf="@+id/textViewSubtitulo"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_chainStyle="packed" />

    <TextView
        android:id="@+id/textViewSubtitulo"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        android:text="@string/home_subtitulo"
        android:textSize="16sp"
        app:layout_constraintBottom_toTopOf="@+id/buttonVerLista"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/textViewTitulo" />

    <Button
        android:id="@+id/buttonVerLista"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="32dp"
        android:text="@string/home_boton_lista"
        app:layout_constraintBottom_toTopOf="@+id/buttonVerSensor"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/textViewSubtitulo" />

    <Button
        android:id="@+id/buttonVerSensor"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="@string/home_boton_sensor"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/buttonVerLista" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

#### ARCHIVO: app/src/main/res/layout/fragment_lista.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".fragmentos.ListaFragment">

    <TextView
        android:id="@+id/textViewTituloLista"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="@string/lista_titulo"
        android:textSize="24sp"
        android:textStyle="bold"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerViewUsuarios"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_marginTop="16dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/textViewTituloLista" />

    <!-- Estado "cargando": visible solo mientras se espera la respuesta de la API -->
    <ProgressBar
        android:id="@+id/progressBarCarga"
        style="?android:attr/progressBarStyle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:visibility="gone"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <!-- Estados "error" y "lista vacía": el mismo TextView muestra el mensaje correspondiente -->
    <TextView
        android:id="@+id/textViewMensaje"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="24dp"
        android:layout_marginEnd="24dp"
        android:gravity="center"
        android:textSize="18sp"
        android:visibility="gone"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

#### ARCHIVO: app/src/main/res/layout/irv_usuario.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- >>> CAMBIAR AQUÍ <<< layout del ítem: ajusta los TextView a los campos de tu entidad.
     Si cambias un id, cambia también su uso en UsuarioAdapter (ViewBinding: textViewNombre -> binding.textViewNombre) -->
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <TextView
        android:id="@+id/textViewNombre"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="16dp"
        android:layout_marginEnd="8dp"
        android:textSize="18sp"
        android:textStyle="bold"
        app:layout_constraintEnd_toStartOf="@+id/buttonDetalle"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        tools:text="Nombre" />

    <TextView
        android:id="@+id/textViewEmail"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="4dp"
        android:layout_marginEnd="8dp"
        app:layout_constraintEnd_toStartOf="@+id/buttonDetalle"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/textViewNombre"
        tools:text="correo@ejemplo.com" />

    <TextView
        android:id="@+id/textViewEmpresa"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="4dp"
        android:layout_marginEnd="8dp"
        app:layout_constraintEnd_toStartOf="@+id/buttonDetalle"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/textViewEmail"
        tools:text="Empresa" />

    <Button
        android:id="@+id/buttonDetalle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginEnd="16dp"
        android:text="@string/item_boton_detalle"
        app:layout_constraintBottom_toBottomOf="@+id/textViewEmpresa"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="@+id/textViewNombre" />

    <View
        android:id="@+id/divider"
        android:layout_width="0dp"
        android:layout_height="1dp"
        android:layout_marginTop="16dp"
        android:background="?android:attr/listDivider"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/textViewEmpresa" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

#### ARCHIVO: app/src/main/res/layout/fragment_detalle.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- >>> CAMBIAR AQUÍ <<< un par etiqueta/valor por cada campo de tu entidad que quieras mostrar -->
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".fragmentos.DetalleFragment">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="24dp">

        <TextView
            android:id="@+id/textViewTituloDetalle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:text="@string/detalle_titulo"
            android:textSize="24sp"
            android:textStyle="bold" />

        <TextView
            style="@style/EtiquetaDetalle"
            android:text="@string/detalle_label_id" />

        <TextView
            android:id="@+id/textViewDetalleId"
            style="@style/ValorDetalle"
            tools:text="1" />

        <TextView
            style="@style/EtiquetaDetalle"
            android:text="@string/detalle_label_nombre" />

        <TextView
            android:id="@+id/textViewDetalleNombre"
            style="@style/ValorDetalle"
            tools:text="Nombre completo" />

        <TextView
            style="@style/EtiquetaDetalle"
            android:text="@string/detalle_label_usuario" />

        <TextView
            android:id="@+id/textViewDetalleUsername"
            style="@style/ValorDetalle"
            tools:text="username" />

        <TextView
            style="@style/EtiquetaDetalle"
            android:text="@string/detalle_label_email" />

        <TextView
            android:id="@+id/textViewDetalleEmail"
            style="@style/ValorDetalle"
            tools:text="correo@ejemplo.com" />

        <TextView
            style="@style/EtiquetaDetalle"
            android:text="@string/detalle_label_telefono" />

        <TextView
            android:id="@+id/textViewDetalleTelefono"
            style="@style/ValorDetalle"
            tools:text="123-456" />

        <TextView
            style="@style/EtiquetaDetalle"
            android:text="@string/detalle_label_web" />

        <TextView
            android:id="@+id/textViewDetalleWeb"
            style="@style/ValorDetalle"
            tools:text="ejemplo.org" />

        <TextView
            style="@style/EtiquetaDetalle"
            android:text="@string/detalle_label_empresa" />

        <TextView
            android:id="@+id/textViewDetalleEmpresa"
            style="@style/ValorDetalle"
            tools:text="Empresa" />
    </LinearLayout>
</ScrollView>
```

#### ARCHIVO: app/src/main/res/layout/fragment_sensor.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".fragmentos.SensorFragment">

    <TextView
        android:id="@+id/textViewTituloSensor"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="@string/sensor_titulo"
        android:textSize="24sp"
        android:textStyle="bold"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <!-- Aquí se informa si el sensor existe (o el motivo por el que no se puede usar) -->
    <TextView
        android:id="@+id/textViewEstadoSensor"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="24dp"
        android:layout_marginTop="16dp"
        android:layout_marginEnd="24dp"
        android:gravity="center"
        android:textSize="16sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/textViewTituloSensor"
        tools:text="@string/sensor_disponible" />

    <!-- >>> CAMBIAR AQUÍ <<< TYPE_LIGHT y TYPE_PROXIMITY solo tienen values[0]: puedes ocultar y/z -->
    <LinearLayout
        android:id="@+id/layoutDatosSensor"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent">

        <TextView
            android:id="@+id/textViewX"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:textSize="22sp"
            tools:text="x: 0.00 m/s²" />

        <TextView
            android:id="@+id/textViewY"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:gravity="center"
            android:textSize="22sp"
            tools:text="y: 0.00 m/s²" />

        <TextView
            android:id="@+id/textViewZ"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:gravity="center"
            android:textSize="22sp"
            tools:text="z: 9.81 m/s²" />

        <TextView
            android:id="@+id/textViewMagnitud"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="24dp"
            android:gravity="center"
            android:textSize="20sp"
            android:textStyle="bold"
            tools:text="Magnitud: 9.81 m/s²" />

        <!-- Mensaje derivado: su texto y color cambian según el umbral (ver SensorFragment) -->
        <TextView
            android:id="@+id/textViewAlerta"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="24dp"
            android:gravity="center"
            android:textSize="26sp"
            android:textStyle="bold"
            tools:text="@string/sensor_mensaje_estable"
            tools:textColor="@color/sensor_estable" />

        <TextView
            android:id="@+id/textViewUmbral"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            android:gravity="center"
            android:textSize="14sp"
            tools:text="Umbral de movimiento: ±3.0 m/s² sobre la gravedad" />
    </LinearLayout>
</androidx.constraintlayout.widget.ConstraintLayout>
```

#### ARCHIVO: app/src/main/java/com/example/labmodelo/MainActivity.java

```java
package com.example.labmodelo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;

import com.example.labmodelo.databinding.ActivityMainBinding;

// Única Activity de la app: solo aloja el NavHostFragment (ver activity_main.xml y nav_graph.xml)
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Con targetSdk 36 la app se dibuja bajo la barra de estado/navegación; se agrega padding
        // para que los fragments no queden tapados por ellas
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (view, windowInsets) -> {
            Insets barras = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return windowInsets;
        });
    }
}
```

#### ARCHIVO: app/src/main/java/com/example/labmodelo/fragmentos/HomeFragment.java

```java
package com.example.labmodelo.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.labmodelo.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(HomeFragment.this);

        // Se navega con las Directions que Safe Args genera desde nav_graph.xml
        binding.buttonVerLista.setOnClickListener(v ->
                navController.navigate(HomeFragmentDirections.actionHomeFragmentToListaFragment()));

        binding.buttonVerSensor.setOnClickListener(v ->
                navController.navigate(HomeFragmentDirections.actionHomeFragmentToSensorFragment()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // La vista se destruye pero el fragment puede seguir vivo (back stack): se suelta el binding
        binding = null;
    }
}
```

#### ARCHIVO: app/src/main/java/com/example/labmodelo/fragmentos/ListaFragment.java

```java
package com.example.labmodelo.fragmentos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.labmodelo.R;
import com.example.labmodelo.adapter.UsuarioAdapter;
import com.example.labmodelo.databinding.FragmentListaBinding;
import com.example.labmodelo.entity.Usuario;
import com.example.labmodelo.viewModels.ListaViewModel;

public class ListaFragment extends Fragment {

    private static final String TAG = "msg-test-ListaFragment";

    FragmentListaBinding binding;
    ListaViewModel listaViewModel;
    UsuarioAdapter usuarioAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentListaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listaViewModel = new ViewModelProvider(ListaFragment.this).get(ListaViewModel.class);

        // Adapter y LayoutManager se crean UNA vez; luego solo se le cambian los datos con setLista()
        usuarioAdapter = new UsuarioAdapter(this::navegarAlDetalle);
        binding.recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewUsuarios.setAdapter(usuarioAdapter);

        // getViewLifecycleOwner(): el observer se desactiva cuando la vista muere (no cuando muere el fragment)
        listaViewModel.getLista().observe(getViewLifecycleOwner(), lista -> {
            usuarioAdapter.setLista(lista);
        });
        listaViewModel.getEstado().observe(getViewLifecycleOwner(), this::mostrarEstado);

        listaViewModel.cargarSiHaceFalta();
    }

    // Un único lugar decide qué se ve en cada estado (cargando / lista / vacía / error)
    private void mostrarEstado(ListaViewModel.Estado estado) {
        binding.progressBarCarga.setVisibility(View.GONE);
        binding.textViewMensaje.setVisibility(View.GONE);
        binding.recyclerViewUsuarios.setVisibility(View.GONE);

        switch (estado) {
            case CARGANDO:
                binding.progressBarCarga.setVisibility(View.VISIBLE);
                break;
            case OK:
                binding.recyclerViewUsuarios.setVisibility(View.VISIBLE);
                break;
            case VACIO:
                mostrarMensaje(R.string.lista_vacia);
                break;
            case ERROR_RED:
                mostrarMensaje(R.string.lista_error_red);
                break;
            case ERROR_SERVIDOR:
                mostrarMensaje(R.string.lista_error_servidor);
                break;
        }
    }

    private void mostrarMensaje(int mensajeResId) {
        binding.textViewMensaje.setText(mensajeResId);
        binding.textViewMensaje.setVisibility(View.VISIBLE);
    }

    private void navegarAlDetalle(Usuario usuario) {
        Log.d(TAG, "Navegando al detalle del usuario: " + usuario.getId());

        NavController navController = NavHostFragment.findNavController(ListaFragment.this);
        // >>> CAMBIAR AQUÍ <<< nombre de la acción y tipo del argumento (según nav_graph.xml)
        navController.navigate(ListaFragmentDirections.actionListaFragmentToDetalleFragment(usuario));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // El RecyclerView y el adapter pertenecían a esta vista; se sueltan junto con el binding
        binding = null;
        usuarioAdapter = null;
    }
}
```

#### ARCHIVO: app/src/main/java/com/example/labmodelo/fragmentos/DetalleFragment.java

```java
package com.example.labmodelo.fragmentos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.labmodelo.R;
import com.example.labmodelo.databinding.FragmentDetalleBinding;
import com.example.labmodelo.entity.Usuario;

public class DetalleFragment extends Fragment {

    private static final String TAG = "msg-test-DetalleFragment";

    FragmentDetalleBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDetalleBinding.inflate(inflater, container, false);

        // >>> CAMBIAR AQUÍ <<< DetalleFragmentArgs y getUsuario() se generan desde el <argument> del nav_graph
        DetalleFragmentArgs args = DetalleFragmentArgs.fromBundle(requireArguments());
        Usuario usuario = args.getUsuario();
        Log.d(TAG, "Usuario recibido por Safe Args: " + usuario.getId());

        // >>> CAMBIAR AQUÍ <<< campos que se muestran (ver fragment_detalle.xml)
        binding.textViewDetalleId.setText(String.valueOf(usuario.getId()));
        binding.textViewDetalleNombre.setText(usuario.getName());
        binding.textViewDetalleUsername.setText(usuario.getUsername());
        binding.textViewDetalleEmail.setText(usuario.getEmail());
        binding.textViewDetalleTelefono.setText(usuario.getPhone());
        binding.textViewDetalleWeb.setText(usuario.getWebsite());
        if (usuario.getCompany() != null) {
            binding.textViewDetalleEmpresa.setText(usuario.getCompany().getName());
        } else {
            binding.textViewDetalleEmpresa.setText(R.string.detalle_sin_dato);
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
```

#### ARCHIVO: app/src/main/java/com/example/labmodelo/fragmentos/SensorFragment.java

```java
package com.example.labmodelo.fragmentos;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.labmodelo.R;
import com.example.labmodelo.databinding.FragmentSensorBinding;

// El fragment implementa SensorEventListener para poder escribir el dato directo en sus TextViews
public class SensorFragment extends Fragment implements SensorEventListener {

    private static final String TAG = "msg-test-SensorFragment";

    // >>> CAMBIAR AQUÍ <<< tipo de sensor. Alternativas:
    //   Sensor.TYPE_LIGHT      -> luz ambiental, values[0] en lux
    //   Sensor.TYPE_PROXIMITY  -> proximidad, values[0] en cm (muchos equipos solo dan "cerca"/"lejos")
    private static final int TIPO_SENSOR = Sensor.TYPE_ACCELEROMETER;

    // >>> CAMBIAR AQUÍ <<< umbral: cuánto puede desviarse la magnitud de la gravedad (9.81) antes de avisar.
    // Para TYPE_LIGHT sería un valor en lux (ej. 50f); para TYPE_PROXIMITY, comparar con getMaximumRange().
    private static final float UMBRAL_MOVIMIENTO = 3.0f;

    FragmentSensorBinding binding;
    SensorManager sensorManager;
    Sensor sensor;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSensorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);

        // Validaciones en cadena: primero el SensorManager, luego el sensor en particular.
        // Si falta algo se avisa en pantalla y sensor queda en null (nunca se registra el listener).
        if (sensorManager == null) {
            mostrarSensorNoDisponible(R.string.sensor_sin_manager);
            return;
        }

        sensor = sensorManager.getDefaultSensor(TIPO_SENSOR);
        if (sensor == null) {
            mostrarSensorNoDisponible(R.string.sensor_no_disponible);
            return;
        }

        binding.textViewEstadoSensor.setText(R.string.sensor_disponible);
        binding.textViewUmbral.setText(getString(R.string.sensor_umbral, UMBRAL_MOVIMIENTO));
    }

    private void mostrarSensorNoDisponible(int mensajeResId) {
        Log.d(TAG, "sensor no disponible");
        binding.textViewEstadoSensor.setText(mensajeResId);
        binding.layoutDatosSensor.setVisibility(View.GONE);
    }

    // Registrar en onResume y desregistrar en onPause: el sensor consume batería,
    // solo debe estar activo mientras el fragment es visible
    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null && sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (binding == null || sensorEvent.sensor.getType() != TIPO_SENSOR) {
            return;
        }

        // >>> CAMBIAR AQUÍ <<< procesamiento del dato según el sensor.
        // Para TYPE_LIGHT / TYPE_PROXIMITY solo existe values[0]; ejemplos:
        //   float lux = sensorEvent.values[0];
        //   boolean oscuro = lux < UMBRAL;                                    // TYPE_LIGHT
        //   boolean cerca = sensorEvent.values[0] < sensorEvent.sensor.getMaximumRange(); // TYPE_PROXIMITY
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        binding.textViewX.setText(getString(R.string.sensor_valor_x, x));
        binding.textViewY.setText(getString(R.string.sensor_valor_y, y));
        binding.textViewZ.setText(getString(R.string.sensor_valor_z, z));

        // Valor derivado: magnitud del vector. En reposo vale ~9.81 (la gravedad), sin importar la orientación
        float magnitud = (float) Math.sqrt(x * x + y * y + z * z);
        binding.textViewMagnitud.setText(getString(R.string.sensor_valor_magnitud, magnitud));

        // Lógica del umbral: mensaje y color cambian si el equipo se mueve más de lo permitido
        float desviacion = Math.abs(magnitud - SensorManager.GRAVITY_EARTH);
        if (desviacion > UMBRAL_MOVIMIENTO) {
            binding.textViewAlerta.setText(R.string.sensor_mensaje_movimiento);
            binding.textViewAlerta.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.sensor_alerta));
        } else {
            binding.textViewAlerta.setText(R.string.sensor_mensaje_estable);
            binding.textViewAlerta.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.sensor_estable));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No se necesita para este laboratorio
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
```

#### ARCHIVO: app/src/main/java/com/example/labmodelo/entity/Usuario.java

```java
package com.example.labmodelo.entity;

import java.io.Serializable;

// >>> CAMBIAR AQUÍ <<< entidad/DTO: los nombres de los atributos deben coincidir con las claves del JSON
// (o usar @SerializedName("clave_json") de Gson si quieres otro nombre en Java).
// Serializable es lo que permite pasarla como argumento por Safe Args (Clase6: Persona).
public class Usuario implements Serializable {

    private int id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String website;
    private Company company;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
```

#### ARCHIVO: app/src/main/java/com/example/labmodelo/entity/Company.java

```java
package com.example.labmodelo.entity;

import java.io.Serializable;

// Objeto anidado del JSON ("company": {...}). Debe ser Serializable porque viaja dentro de Usuario por Safe Args.
public class Company implements Serializable {

    private String name;
    private String catchPhrase;
    private String bs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatchPhrase() {
        return catchPhrase;
    }

    public void setCatchPhrase(String catchPhrase) {
        this.catchPhrase = catchPhrase;
    }

    public String getBs() {
        return bs;
    }

    public void setBs(String bs) {
        this.bs = bs;
    }
}
```

#### ARCHIVO: app/src/main/java/com/example/labmodelo/retrofitHelpers/UsuarioService.java

```java
package com.example.labmodelo.retrofitHelpers;

import com.example.labmodelo.entity.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UsuarioService {

    // >>> CAMBIAR AQUÍ <<< endpoint y tipo de retorno.
    // Esta API devuelve un arreglo JSON directo -> Call<List<Usuario>>.
    // Si tu API devuelve un objeto contenedor ({"lista": [...], "estado": "ok"}), crea un Dto como
    // EmployeeDto de Clase5 y devuelve Call<TuDto>.
    @GET("users")
    Call<List<Usuario>> obtenerLista();
}
```

#### ARCHIVO: app/src/main/java/com/example/labmodelo/adapter/UsuarioAdapter.java

```java
package com.example.labmodelo.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labmodelo.R;
import com.example.labmodelo.databinding.IrvUsuarioBinding;
import com.example.labmodelo.entity.Usuario;

import java.util.ArrayList;
import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private static final String TAG = "msg-test-UsuarioAdapter";

    // Interfaz de click: el adapter avisa "me presionaron este usuario" y el fragment decide qué hacer.
    // Así el adapter no necesita Context ni conocer la navegación.
    public interface OnUsuarioClickListener {
        void onUsuarioClick(Usuario usuario);
    }

    private List<Usuario> lista = new ArrayList<>();
    private final OnUsuarioClickListener listener;

    public UsuarioAdapter(OnUsuarioClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // El Context sale de parent, no se guarda en un atributo (evita fugas de memoria)
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        IrvUsuarioBinding binding = IrvUsuarioBinding.inflate(inflater, parent, false);
        return new UsuarioViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = lista.get(position);
        holder.usuario = usuario;

        // >>> CAMBIAR AQUÍ <<< campos del ítem (ver irv_usuario.xml)
        holder.binding.textViewNombre.setText(usuario.getName());
        holder.binding.textViewEmail.setText(usuario.getEmail());
        if (usuario.getCompany() != null) {
            holder.binding.textViewEmpresa.setText(usuario.getCompany().getName());
        } else {
            holder.binding.textViewEmpresa.setText(R.string.detalle_sin_dato);
        }

        holder.binding.buttonDetalle.setOnClickListener(view -> {
            Log.d(TAG, "Presionando el usuario con id: " + usuario.getId());
            listener.onUsuarioClick(usuario);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    // Se reutiliza el mismo adapter: solo se cambian los datos y se le avisa al RecyclerView
    public void setLista(List<Usuario> nuevaLista) {
        this.lista = (nuevaLista != null) ? new ArrayList<>(nuevaLista) : new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {

        IrvUsuarioBinding binding;
        Usuario usuario;

        public UsuarioViewHolder(IrvUsuarioBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
```

#### ARCHIVO: app/src/main/java/com/example/labmodelo/viewModels/ListaViewModel.java

```java
package com.example.labmodelo.viewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.labmodelo.entity.Usuario;
import com.example.labmodelo.retrofitHelpers.UsuarioService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Se usa un ViewModel (como en Clase6) para que la lista sobreviva a rotaciones y al volver desde el
// detalle, y para que el callback de Retrofit nunca toque vistas que ya fueron destruidas.
public class ListaViewModel extends ViewModel {

    private static final String TAG = "msg-test-ListaViewModel";

    // >>> CAMBIAR AQUÍ <<< baseUrl: debe terminar en "/" y el endpoint va en UsuarioService
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

    // Estados que la pantalla sabe dibujar
    public enum Estado {
        CARGANDO, OK, VACIO, ERROR_RED, ERROR_SERVIDOR
    }

    private final MutableLiveData<List<Usuario>> lista = new MutableLiveData<>();
    private final MutableLiveData<Estado> estado = new MutableLiveData<>();

    private UsuarioService usuarioService;
    private Call<List<Usuario>> llamadaActual;

    public LiveData<List<Usuario>> getLista() {
        return lista;
    }

    public LiveData<Estado> getEstado() {
        return estado;
    }

    // Se llama cada vez que se crea la vista. Solo consulta la red si aún no hay datos o si la
    // vez anterior falló (así, volver a la pantalla funciona como "reintentar").
    public void cargarSiHaceFalta() {
        Estado actual = estado.getValue();
        boolean hayQueCargar = actual == null
                || actual == Estado.ERROR_RED
                || actual == Estado.ERROR_SERVIDOR;
        if (hayQueCargar) {
            cargarListaWebService();
        }
    }

    public void createRetrofitService() {
        usuarioService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UsuarioService.class);
    }

    public void cargarListaWebService() {
        if (usuarioService == null) {
            createRetrofitService();
        }

        estado.setValue(Estado.CARGANDO);

        llamadaActual = usuarioService.obtenerLista();
        llamadaActual.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (!response.isSuccessful()) {
                    // 404, 500, etc.: la red funcionó pero el servidor respondió con error
                    Log.d(TAG, "response unsuccessful, código: " + response.code());
                    estado.setValue(Estado.ERROR_SERVIDOR);
                    return;
                }

                List<Usuario> body = response.body();
                if (body == null || body.isEmpty()) {
                    Log.d(TAG, "la lista llegó vacía");
                    estado.setValue(Estado.VACIO);
                    return;
                }

                //tengo la lista -> ready!
                Log.d(TAG, "usuarios recibidos: " + body.size());
                lista.setValue(body);
                estado.setValue(Estado.OK);
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                // Una llamada cancelada (onCleared) también cae aquí: no es un error para el usuario
                if (call.isCanceled()) {
                    return;
                }
                Log.d(TAG, "algo pasó!!! " + t.getMessage());
                t.printStackTrace();
                estado.setValue(Estado.ERROR_RED);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // El ViewModel muere con el fragment: se cancela lo que siga en vuelo
        if (llamadaActual != null) {
            llamadaActual.cancel();
        }
    }
}
```

### 5. ENUNCIADO Y DATOS DEL LABORATORIO

**ENUNCIADO (pégalo completo, tal cual):**
[PEGA AQUÍ EL ENUNCIADO]

**EJEMPLO DE JSON de la API (abre la URL en el navegador y copia 2–3 elementos):**
[PEGA AQUÍ EL JSON]

**Datos extra (opcional):** nombre de la app / paquete / sensor pedido / umbral / nombres de pantallas: [ESCRIBE AQUÍ O BORRA ESTA LÍNEA]

Recuerda: entrega archivos completos con su ruta, en el orden indicado, con SUPUESTOS al inicio y PASOS MANUALES + AUTOVERIFICACIÓN al final.

## ===== FIN DEL PROMPT A =====

---

## Prompts de seguimiento (misma conversación, después del A)

### S1 – Error de compilación / crash
```
Me sale este error. Dime la causa exacta y dame SOLO los archivos a corregir, completos y con su ruta. No cambies nada más.

[PEGA AQUÍ el mensaje de Build/Logcat completo, con las líneas "Caused by"]
```

### S2 – Agregar una pantalla (fragment) nuevo
```
Agrega un fragment nuevo llamado <NombreFragment> con estos elementos: <describe>. Se llega desde <origen> con el botón/ítem <X> y debe recibir/no recibir argumentos: <cuáles>. Dame: layout, clase Java, el nav_graph completo actualizado y los cambios en el fragment de origen. Navegación solo con Safe Args, binding = null en onDestroyView.
```

### S3 – Cambiar el sensor
```
Cambia el sensor a <TYPE_LIGHT | TYPE_PROXIMITY | otro>. Umbral y mensajes: <describe>. Dame SensorFragment.java, fragment_sensor.xml, strings.xml y el uses-feature del manifest, completos. Debe validar que el sensor exista, registrar en onResume y desregistrar en onPause.
```

### S4 – La lista no carga / se ve mal
```
La lista no carga (o los campos salen null). Esta es la respuesta real de la API, mi entidad, mi interfaz Retrofit y mi baseUrl. Encuentra la incongruencia y dame los archivos corregidos completos.

JSON: [PEGA]
Entidad: [PEGA]
Servicio: [PEGA]
baseUrl: [PEGA]
Logcat (filtra por msg-test): [PEGA]
```

### S5 – Safe Args / navegación falla
```
Safe Args no genera ...Directions/...Args o la navegación crashea. Revisa mi nav_graph, mis build.gradle (raíz y app) y el fragment de origen; dime qué está mal y dame los archivos corregidos completos.

[PEGA nav_graph.xml, build.gradle raíz, app/build.gradle, fragment de origen y el error]
```

### S6 – Auditoría final contra la rúbrica (antes de entregar)
```
Audita mi proyecto contra la rúbrica (interfaces, fragments y navegación, sensores, RecyclerView con servicio externo y buenas prácticas, funcionalidad). Revisa: adapter sin Context, listener de click, adapter/LayoutManager creados una vez, ProgressBar/error/lista vacía, binding = null, getViewLifecycleOwner, register/unregister del sensor en onResume/onPause, validación de sensor null, permiso INTERNET, sin cleartext. Devuélveme una tabla ✔/✘ con archivo y línea, y corrige solo lo que esté mal (archivos completos).

[PEGA tus archivos: manifest, nav_graph, fragments, adapter, viewmodel/servicio, layouts]
```

---

## Plantilla de declaración de uso de IA (para el informe, si el docente la pide)
> Declaro que utilicé la herramienta de IA generativa **Gemini (Google)** como asistente de programación durante el laboratorio 4 del curso 1TEL05 (fecha: ____). La usé para: generar una propuesta de código a partir de un proyecto modelo y el enunciado, y para depurar errores de compilación. Revisé, probé y comprendo el código entregado. Los prompts utilizados se adjuntan como anexo.
