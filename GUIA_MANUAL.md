# Guía manual – Laboratorio 1TEL05 (sin IA)

Para usar si el JP **no permite IA**. Está pensada para reconstruir la app completa siguiendo pasos, con el código real del proyecto `LabModelo` (que compila) y explicando el **porqué** de cada bloque, para que puedas adaptarlo a un enunciado distinto.

> Los comentarios del código están para aprender: **al copiar/escribir a mano puedes omitirlos**.

## 0. Estrategia según la rúbrica (20 pts)

Ve en este orden: así siempre tienes una app que corre y puntos asegurados aunque se acabe el tiempo.

| Etapa | Qué haces | Rúbrica | Puntos en juego | Tiempo sugerido* |
|---|---|---|---|---|
| A | Proyecto + Gradle + Home + nav_graph (pantallas vacías con botones) | Interfaces (3) + Fragments/navegación (4) | hasta 7 | 20 % |
| B | Lista con Retrofit + RecyclerView + estados | RecyclerView externo (5) | hasta 5 | 35 % |
| C | Detalle con Safe Args | Interfaces + Fragments + Funcionalidad | (suma a los anteriores) | 10 % |
| D | Sensor con TextViews y umbral | Sensores (3) | hasta 3 | 20 % |
| E | Pruebas, pulido, checklist final | Funcionalidad (5) | hasta 5 | 15 % |

\*Reparte según la duración real del laboratorio. Si vas justo de tiempo: **no** pierdas los puntos de navegación (A) por pulir la lista (B).

## 1. Antes del laboratorio (haz esto en casa, con internet)

- [ ] Abre `LabModelo` en Android Studio y compila una vez (`Build → Make Project`): así se descargan Gradle, el SDK Platform 36 y las dependencias. En el lab puede no haber internet rápido.
- [ ] Verifica que tienes un **emulador con API 34 o superior** (el proyecto usa `minSdk 34`) o un celular con Android 14+.
- [ ] Prueba una vez la app completa y mueve los sensores virtuales (*Extended controls (⋯) → Virtual sensors*).
- [ ] Lleva (si está permitido) esta guía y `GUIA_ADAPTACION.md` **y** una copia de la carpeta `LabModelo` (USB, nube).
- [ ] Repasa los archivos del profe de la tabla del punto 2.

## 2. Mapa: tema del sílabo → dónde lo viste

Unidad 2 del sílabo ("Diseño y construcción de aplicaciones móviles"): *Navegación, Activity Lifecycle y Elementos de UI*, *Worker Threads y Internet connection*, *RecyclerView y sensores*.

| Tema | Archivos de los repos del profe | Archivo equivalente en LabModelo | PPT de Paideia (anota tú el nombre) |
|---|---|---|---|
| Retrofit + servicio web | `Clase5`: `MainActivity.java` (`createRetrofitService`, `cargarListaWebService`), `retrofitHelpers/EmployeeService.java`, `entity/Employee*.java`, `AndroidManifest.xml` (permiso INTERNET) | `viewModels/ListaViewModel.java`, `retrofitHelpers/UsuarioService.java`, `entity/Usuario.java` | ______ |
| RecyclerView | `Clase5`: `EmployeeAdapter.java`, `res/layout/irv_employee.xml`, `activity_main.xml` | `adapter/UsuarioAdapter.java`, `irv_usuario.xml`, `fragment_lista.xml` | ______ |
| Sensores | `Clase5`: `SensorActivity.java` (`getSystemService`, `getDefaultSensor`, `registerListener`), `SensorAccListener.java` | `fragmentos/SensorFragment.java` (el profe desregistra en `onStop`; aquí se usa `onPause`, que es el par correcto de `onResume`) | ______ |
| Fragments, Navigation, Safe Args | `Clase6`: `NavigationActivity.java`, `activity_navigation.xml`, `res/navigation/nav_graph.xml`, `FragmentA/B/C.java`, `res/anim/*` | `MainActivity.java`, `activity_main.xml`, `nav_graph.xml`, `fragmentos/*`, `res/anim/*` | ______ |
| ViewModel / LiveData | `Clase6`: `viewModels/MainActivityViewModel.java`, `MainActivity.java` (`observe`) | `viewModels/ListaViewModel.java`, `ListaFragment.java` | ______ |

**Sobre las PPT:** busqué en tu PC (Descargas, Documentos, Escritorio) y **no hay PPT de 1TEL05 sobre Android**; las "Clase 00 / 01 / 1.x" que aparecen en Descargas son del curso GTICS (Spring). Descarga las de Paideia y completa la última columna para tener a mano qué diapositiva abrir por tema.

**Documentación y libros (para consultar rápido):**
- Libro del sílabo: *Android Apps for Absolute Beginners* (Jackson, Apress, 2017) – Android básico.
- Navegación y Safe Args: `developer.android.com/guide/navigation` (secciones "Pass data between destinations" y "Safe Args").
- RecyclerView: `developer.android.com/develop/ui/views/layout/recyclerview`.
- Sensores: `developer.android.com/develop/sensors-and-location/sensors/sensors_overview` y `.../sensors_motion`.
- Retrofit: `square.github.io/retrofit`.

## 3. Partir de LabModelo (opción recomendada) o crear desde cero

**Opción A – copiar `LabModelo` (si puedes llevarlo).** Copia la carpeta, ábrela en Android Studio (*File → Open*), deja que sincronice y compila. Luego salta a la sección **11 (Adaptar)**. Las secciones 4–9 explican cómo se construye cada pieza por si necesitas modificarla.

**Opción B – desde cero** (secciones 4–9 en orden):
1. *File → New → New Project → Empty Views Activity*. Name: `LabModelo`, Package: `com.example.labmodelo`, Language: **Java**, Minimum SDK: **API 34**, build configuration language: **Groovy DSL**.
2. Sigue los pasos siguientes. Si el asistente de tu Android Studio genera archivos distintos (por ejemplo `libs.versions.toml`), puedes dejarlos y **solo agregar** lo que indica cada paso.

## 4. Etapa A – Gradle, manifest y Safe Args

### 4.1 `build.gradle` (raíz): plugin de Safe Args
Agrega el plugin **con `apply false`** (lo activa el módulo `app`). Su versión debe coincidir con la de `navigation-*`.

**`build.gradle`**

```groovy
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id 'com.android.application' version '9.2.0' apply false
    // El plugin de Safe Args debe tener la MISMA versión que las librerías navigation-* del app/build.gradle
    id 'androidx.navigation.safeargs' version '2.9.6' apply false
}
```

### 4.2 `app/build.gradle`: plugin, ViewBinding y dependencias
Puntos clave: `id 'androidx.navigation.safeargs'` (genera `Directions`/`Args`), `viewBinding true` y las dependencias de Retrofit/Navigation.

**`app/build.gradle`**

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

### 4.3 `gradle.properties`
Es el del profe (Clase6). Las líneas `android.builtInKotlin=false` y `android.newDsl=false` están en Clase6 y en este modelo, y con ellas el plugin de Safe Args funcionó. Si tu `gradle.properties` es distinto y *Sync* falla con Safe Args, compara con este.

**`gradle.properties`**

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

Después de editar Gradle: **File → Sync Project with Gradle Files**. Si Android Studio pide instalar el SDK Platform 36, acéptalo.

### 4.4 `AndroidManifest.xml`
Lo esencial: permiso `INTERNET`, una sola Activity, `uses-feature` opcional del sensor. Sin `usesCleartextTraffic` porque la API es HTTPS.

**`app/src/main/AndroidManifest.xml`**

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

### 4.5 Animaciones (copiar de Clase6)
Crea `res/anim/` con `enter_anim.xml`, `exit_anim.xml`, `pop_enter_anim.xml`, `pop_exit_anim.xml` (cada una es un `<translate>` de 200 ms; entra desde la derecha, sale a la izquierda, y al volver al revés).

**`app/src/main/res/anim/enter_anim.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android">
    <translate android:duration="200"
        android:fromXDelta="100%"
        android:toXDelta="0%"
        />
</set>
```

Las otras tres solo cambian `fromXDelta`/`toXDelta`: `exit` = `0%`→`-100%`, `pop_enter` = `-100%`→`0%`, `pop_exit` = `0%`→`100%`.

## 5. Etapa A – Una Activity + NavHost + nav_graph + Home

**Idea:** la Activity solo es un contenedor. Cada pantalla es un fragment y el `nav_graph` define destinos, acciones y argumentos. Safe Args genera de ahí las clases de navegación.

### 5.1 Layout de la Activity y `MainActivity`
**`app/src/main/res/layout/activity_main.xml`**

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

**`app/src/main/java/com/example/labmodelo/MainActivity.java`**

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

### 5.2 `nav_graph.xml`
Crea `res/navigation/nav_graph.xml` (*New → Android Resource File → Resource type: Navigation*). Cada `<action id="action_A_to_B">` genera el método `AFragmentDirections.actionAToB()`. El `<argument>` genera `DetalleFragmentArgs`.

**`app/src/main/res/navigation/nav_graph.xml`**

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

> Si cambias un `id` de acción o el `android:name` del argumento, cambian los nombres de los métodos generados. Después de tocar el grafo: **Build → Rebuild Project**.

### 5.3 `HomeFragment`
Layout (`fragment_home.xml`, ver Apéndice A) y clase. Fíjate en `binding = null` en `onDestroyView` y en cómo se navega con `Directions`.

**`app/src/main/java/com/example/labmodelo/fragmentos/HomeFragment.java`**

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

**Crea también** clases vacías (solo `onCreateView` con su binding) para `ListaFragment`, `DetalleFragment` y `SensorFragment` con sus layouts básicos, para poder compilar y **probar la navegación** antes de seguir. Punto de control A: Home → Sensor y Home → Lista abren pantallas; back regresa.

## 6. Etapa B – Lista con Retrofit y RecyclerView

**Idea del flujo:** `ListaFragment` observa un `ListaViewModel`; el ViewModel hace la llamada Retrofit con `enqueue` y publica un **estado** (cargando / ok / vacío / error) y la **lista**; el fragment dibuja según el estado y actualiza el adapter.

### 6.1 Probar la API y crear la entidad
Abre `https://jsonplaceholder.typicode.com/users` en el navegador. La entidad tiene **un atributo por cada clave que uses, con el mismo nombre**. Objeto anidado (`company`) = otra clase. Ambas `implements Serializable` (para Safe Args).

**`app/src/main/java/com/example/labmodelo/entity/Usuario.java`**

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

**`app/src/main/java/com/example/labmodelo/entity/Company.java`**

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

### 6.2 Interfaz Retrofit
Arreglo JSON directo ⇒ `Call<List<Usuario>>`. Si tu API devuelve `{"lista": [...]}` crea un Dto (como `EmployeeDto` de Clase5).

**`app/src/main/java/com/example/labmodelo/retrofitHelpers/UsuarioService.java`**

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

### 6.3 ViewModel (red + estados)
`createRetrofitService()` arma Retrofit; `cargarListaWebService()` hace `enqueue`. `baseUrl` termina en `/`. Se cancela la llamada en `onCleared`.

**`app/src/main/java/com/example/labmodelo/viewModels/ListaViewModel.java`**

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

### 6.4 Adapter con interfaz de click (sin Context)
El adapter **no guarda Context** (usa `parent.getContext()`) y **no navega**: avisa por la interfaz `OnUsuarioClickListener`. Se crea una vez y se actualiza con `setLista()`.

**`app/src/main/java/com/example/labmodelo/adapter/UsuarioAdapter.java`**

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

### 6.5 `ListaFragment`
Crea adapter y `LayoutManager` **una vez** en `onViewCreated`; observa con `getViewLifecycleOwner()`; navega con `Directions` en el listener.

**`app/src/main/java/com/example/labmodelo/fragmentos/ListaFragment.java`**

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

Layouts `fragment_lista.xml` (RecyclerView + ProgressBar + TextView de mensaje) e `irv_usuario.xml` (ítem con botón): ver Apéndice A.

**Punto de control B:** la lista aparece; con el modo avión activado sale el mensaje de error (`adb shell cmd connectivity airplane-mode enable` o desde los ajustes del emulador); girar el equipo no recarga la lista.

## 7. Etapa C – Detalle con Safe Args

`DetalleFragment` recibe el objeto con `DetalleFragmentArgs.fromBundle(requireArguments())`. El método `getUsuario()` existe porque el `<argument android:name="usuario">` está en el grafo.

**`app/src/main/java/com/example/labmodelo/fragmentos/DetalleFragment.java`**

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

Layout `fragment_detalle.xml` en el Apéndice A.

## 8. Etapa D – Sensor

**Reglas:** (1) validar `SensorManager` y `Sensor` antes de usarlos y avisar en pantalla si faltan; (2) `registerListener` en `onResume`, `unregisterListener` en `onPause`; (3) mostrar en TextViews; (4) valor derivado y umbral. La magnitud √(x²+y²+z²) en reposo vale ≈ 9.81 m/s².

**`app/src/main/java/com/example/labmodelo/fragmentos/SensorFragment.java`**

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

Layout `fragment_sensor.xml` en el Apéndice A. Para probar: *Extended controls → Virtual sensors → Device pose / Additional sensors* (acelerómetro, luz, proximidad).

**Cambiar de sensor:** ver los comentarios `>>> CAMBIAR AQUÍ <<<` del fragment. `TYPE_LIGHT` y `TYPE_PROXIMITY` solo tienen `values[0]`.

## 9. Etapa E – Pruebas finales (checklist antes de entregar)

- [ ] Home → Lista → Detalle y Home → Sensor funcionan; **back** regresa correctamente.
- [ ] La lista muestra datos reales; hay **ProgressBar** al cargar; con modo avión sale un **mensaje**; con lista vacía sale un mensaje.
- [ ] El detalle muestra los campos del objeto elegido.
- [ ] El sensor muestra x/y/z (o el valor pedido) **en pantalla**, el valor derivado y el mensaje/color cambian al pasar el umbral.
- [ ] Sensor inexistente: la pantalla muestra el aviso y **no crashea**.
- [ ] Manifest: permiso `INTERNET`. API en HTTPS (o `usesCleartextTraffic` si el enunciado da `http://`).
- [ ] `binding = null` en `onDestroyView` en todos los fragments; adapter sin Context.
- [ ] Textos legibles, sin datos de prueba visibles ni pantallas vacías.
- [ ] Lee otra vez el enunciado y marca **cada** requisito. Entrega lo que pida el enunciado (revisa formato y qué carpeta/archivo subir; no incluyas la carpeta `build/`).

## 10. Añadir un fragment extra (si el enunciado pide más pantallas)

1. **Layout:** clic derecho en `res/layout` → *New → Layout Resource File* → `fragment_xxx.xml` (root ConstraintLayout).
2. **Clase:** en `fragmentos/`, `XxxFragment extends Fragment` con `onCreateView` que infla `FragmentXxxBinding`, retorna `binding.getRoot()` y `onDestroyView` que hace `binding = null`.
3. **nav_graph.xml:** agrega `<fragment android:id="@+id/xxxFragment" android:name="com.example.labmodelo.fragmentos.XxxFragment" tools:layout="@layout/fragment_xxx" />`.
4. **Acción:** dentro del fragment de origen agrega `<action android:id="@+id/action_origen_to_xxxFragment" app:destination="@id/xxxFragment" app:enterAnim="@anim/enter_anim" app:exitAnim="@anim/exit_anim" app:popEnterAnim="@anim/pop_enter_anim" app:popExitAnim="@anim/pop_exit_anim" />`.
5. **Argumento (opcional):** dentro del destino, `<argument android:name="dato" app:argType="string" />` (tipos: `string`, `integer`, `boolean`, o una clase `Serializable` con su ruta completa).
6. **Rebuild** y navega desde el origen: `navController.navigate(OrigenFragmentDirections.actionOrigenToXxxFragment(dato));` (sin parámetros si no hay argumento).
7. Recibir: `XxxFragmentArgs.fromBundle(requireArguments()).getDato()`.

## 11. Adaptar el modelo a un enunciado nuevo

Sigue el **checklist de 10 pasos de `GUIA_ADAPTACION.md`** (sección b). Resumen del orden: JSON → entidad → `UsuarioService` → `BASE_URL` → ítem (`irv_*.xml` + adapter) → detalle → sensor → textos → pruebas. Busca `CAMBIAR AQUÍ` en el proyecto para llegar directo a cada lugar.

## 12. Errores típicos y qué hacer

Tabla completa en `GUIA_ADAPTACION.md`, sección (c). Los más frecuentes:
- **`Cannot resolve ...Directions/...Args`** → *Sync*, luego *Rebuild*; revisa el plugin Safe Args en **los dos** `build.gradle` y que no haya errores en `nav_graph.xml`.
- **`binding.xxx` no existe** → el id del XML no coincide (camelCase) o cambiaste el nombre del XML.
- **Lista vacía o `Unable to resolve host`** → falta permiso `INTERNET` o el emulador no tiene red.
- **Campos `null`** → los nombres de los atributos no coinciden con las claves del JSON.
- **`Expected BEGIN_OBJECT but was BEGIN_ARRAY`** → el tipo de retorno de Retrofit no coincide con el JSON (arreglo ⇒ `List<...>`).
- **Crash al navegar con doble clic** → deshabilita el botón tras el clic o comprueba el destino actual.
- **El sensor "no responde" o gasta batería** → falta `unregisterListener` en `onPause`.

---

## Apéndice A – Layouts y recursos

Crea cada layout con el editor visual o pega el XML en la pestaña **Code**. Los `id` deben coincidir con los que usa el Java (`binding.<id>`).

### A.1 `fragment_home.xml`
**`app/src/main/res/layout/fragment_home.xml`**

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

### A.2 `fragment_lista.xml`
**`app/src/main/res/layout/fragment_lista.xml`**

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

### A.3 `irv_usuario.xml` (ítem del RecyclerView)
**`app/src/main/res/layout/irv_usuario.xml`**

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

### A.4 `fragment_detalle.xml`
**`app/src/main/res/layout/fragment_detalle.xml`**

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

### A.5 `fragment_sensor.xml`
**`app/src/main/res/layout/fragment_sensor.xml`**

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

### A.6 `values/strings.xml`
**`app/src/main/res/values/strings.xml`**

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

### A.7 `values/colors.xml`
**`app/src/main/res/values/colors.xml`**

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

### A.8 `values/themes.xml`
**`app/src/main/res/values/themes.xml`**

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
