# Guía de adaptación – LabModelo (1TEL05)

Proyecto base: **una Activity + NavHostFragment + 4 fragments**, lista con Retrofit y sensor con datos en pantalla.
Paquete base: `com.example.labmodelo`. Todo lo que debes tocar según el enunciado está marcado con `// >>> CAMBIAR AQUÍ <<<`
(en Android Studio: *Edit → Find → Find in Files* → `CAMBIAR AQUÍ`).

## Versiones usadas (y por qué)

| Componente | Versión | Origen |
|---|---|---|
| AGP | 9.2.0 | Clase6 |
| Gradle wrapper | 9.4.1 | Clase6 |
| Plugin Safe Args | 2.9.6 | Clase6 |
| navigation-fragment / navigation-ui | **2.9.6** | Clase6 usaba 2.5.3 con el plugin 2.9.6; aquí se alinearon al plugin |
| Retrofit / converter-gson | 2.9.0 | Clase5 |
| Gson | 2.10 | Clase5 |
| appcompat / material / constraintlayout / activity | 1.7.1 / 1.13.0 / 2.2.1 / 1.8.0 | Clase5 |
| compileSdk / minSdk / targetSdk | 36 / 34 / 36 | Clase5 (compileSdk 36 porque navigation 2.9.x lo exige; Clase6 usaba 34) |
| Java | 11 | Clase5 |

`gradle.properties` es idéntico al de Clase6 (las opciones `android.builtInKotlin=false` y `android.newDsl=false` son necesarias para que el plugin de Safe Args funcione con AGP 9). Los avisos de "deprecated" al compilar son de esas opciones y son normales.

---

## (a) Rúbrica → dónde se cumple

| Criterio | Dónde se cumple |
|---|---|
| **Implementación de interfaces** | • `adapter/UsuarioAdapter.java`: define y usa la interfaz `OnUsuarioClickListener` (el adapter avisa el click, no navega). `ListaFragment` la implementa con `this::navegarAlDetalle`.<br>• `fragmentos/SensorFragment.java`: `implements SensorEventListener` (`onSensorChanged`, `onAccuracyChanged`).<br>• `retrofitHelpers/UsuarioService.java`: interfaz Retrofit; `viewModels/ListaViewModel.java`: `new Callback<...>() { onResponse / onFailure }`. |
| **Fragments y navegación** | • `MainActivity.java` + `res/layout/activity_main.xml`: única Activity con `FragmentContainerView` → `NavHostFragment`.<br>• `res/navigation/nav_graph.xml`: destinos, acciones con animaciones `enter/exit/popEnter/popExit` y el `<argument>` Serializable.<br>• `HomeFragment` → `HomeFragmentDirections...`; `ListaFragment` → `ListaFragmentDirections.actionListaFragmentToDetalleFragment(usuario)`; `DetalleFragment` → `DetalleFragmentArgs.fromBundle(...)`.<br>• `binding = null` en `onDestroyView` en los 4 fragments; `getViewLifecycleOwner()` en `ListaFragment.onViewCreated`; `requireContext()` / `requireArguments()` en Lista, Sensor y Detalle. |
| **Sensores** | `fragmentos/SensorFragment.java`: valida `SensorManager` y `Sensor` (`onViewCreated`), `registerListener` en `onResume`, `unregisterListener` en `onPause`, `onSensorChanged` muestra x/y/z en TextViews, calcula la magnitud y cambia mensaje/color según `UMBRAL_MOVIMIENTO`. Layout: `res/layout/fragment_sensor.xml`. |
| **RecyclerView con servicio externo** | • `retrofitHelpers/UsuarioService.java` (endpoint), `viewModels/ListaViewModel.java` (`createRetrofitService()`, `cargarListaWebService()` con `enqueue`), `entity/Usuario.java` + `Company.java` (DTO).<br>• `adapter/UsuarioAdapter.java`: sin Context guardado (`parent.getContext()`), `setLista()` + `notifyDataSetChanged()`.<br>• `ListaFragment.java`: adapter y `LinearLayoutManager` creados una sola vez; estados cargando / lista / vacía / error en `mostrarEstado()`; layouts `fragment_lista.xml` (ProgressBar + TextView de mensaje) e `irv_usuario.xml`.<br>• `AndroidManifest.xml`: permiso `INTERNET`. |
| **Funcionalidad** | Flujo Home → Lista → Detalle y Home → Sensor, con back normal (nav_graph + `MainActivity`). API HTTPS sin cleartext. Mensajes de error visibles en pantalla (`strings.xml`: `lista_error_red`, `lista_error_servidor`, `lista_vacia`, `sensor_no_disponible`). |

> **Nota de diseño:** el `Callback` de Retrofit vive en un `ViewModel` (como Clase6) y no en el fragment. Así el callback nunca toca vistas destruidas, la lista sobrevive a rotaciones/volver del detalle y hay un uso real de `getViewLifecycleOwner()` (observers de LiveData).

---

## (b) Checklist para adaptar a un enunciado nuevo (< 30 min)

Sigue este orden; compila (`Build → Make Project`) después de los pasos 3, 5 y 8.

1. **(2 min) Identidad.** Si te piden otro nombre: cambia `app_name` en `strings.xml`. (Renombrar el paquete con *Refactor → Rename* es opcional y no hace falta para el laboratorio.)
2. **(2 min) Prueba la API en el navegador** o Postman y mira el JSON: ¿es un arreglo `[...]` o un objeto `{ "lista": [...] }`? ¿qué nombres tienen las claves?
3. **(5 min) Entidad.** `entity/Usuario.java`: renómbrala si quieres (*Refactor → Rename*) y deja **solo** los campos del JSON con el **mismo nombre** que la clave (o `@SerializedName`). Objetos anidados → otra clase `implements Serializable` (como `Company`). Si el JSON es un objeto contenedor, crea un `Dto` con `List<Entidad>` como `EmployeeDto` de Clase5.
4. **(3 min) Servicio Retrofit.** `retrofitHelpers/UsuarioService.java`: cambia `@GET("users")` y el tipo de retorno (`Call<List<Usuario>>` o `Call<TuDto>`). Si hay parámetros: `@Path`, `@Query`.
5. **(2 min) baseUrl.** `viewModels/ListaViewModel.java` → `BASE_URL` (**debe terminar en `/`**). Si tu Dto es contenedor, ajusta `Callback<...>` y saca la lista con `body.getLista()`.
6. **(5 min) Ítem de la lista.** `res/layout/irv_usuario.xml` (TextViews) y `UsuarioAdapter.onBindViewHolder` (los `setText`). Cada id `textViewNombre` se convierte en `binding.textViewNombre`.
7. **(4 min) Detalle.** `res/layout/fragment_detalle.xml` (un par etiqueta/valor por campo), `DetalleFragment.java` (los `setText`) y `strings.xml` (etiquetas). Si cambiaste el nombre de la entidad: en `nav_graph.xml` cambia `app:argType` y, si cambias `android:name="usuario"`, cambia también `args.getUsuario()`.
8. **(3 min) Sensor.** `SensorFragment.java`: cambia `TIPO_SENSOR`, `UMBRAL_MOVIMIENTO` y el bloque de procesamiento en `onSensorChanged`:
   ```java
   // Luz:        private static final int TIPO_SENSOR = Sensor.TYPE_LIGHT;      // values[0] = lux
   // Proximidad: private static final int TIPO_SENSOR = Sensor.TYPE_PROXIMITY;  // values[0] = cm
   ```
   Para luz/proximidad solo hay `values[0]`: usa `sensorEvent.values[0]`, oculta `textViewY`/`textViewZ` en el layout y ajusta la comparación con el umbral. Cambia también el `uses-feature` del manifest (`android.hardware.sensor.light` / `.proximity`) y los textos `sensor_*` en `strings.xml`.
9. **(2 min) Textos.** `strings.xml`: títulos, botones, mensajes de estado.
10. **(2 min) Prueba final** en el emulador: lista carga; apaga el WiFi (o pon la URL mal) para ver el mensaje de error; entra al detalle y regresa; gira el equipo; en el sensor usa *Extended controls → Virtual sensors* para mover el dispositivo y ver el cambio de mensaje.

**¿Tu API usa `http://` (sin HTTPS)?** Añade en `<application>` del manifest `android:usesCleartextTraffic="true"` (así lo hace Clase5 para `http://10.0.2.2:8080`). Recuerda que en el emulador `localhost` de tu PC es `10.0.2.2`.

---

## (c) Errores típicos

| Síntoma | Causa | Solución |
|---|---|---|
| `Cannot resolve symbol 'FragmentListaBinding'` / `binding.textViewX` no existe | La clase de binding sale del **nombre del XML** (`fragment_lista.xml` → `FragmentListaBinding`; `irv_usuario.xml` → `IrvUsuarioBinding`) y el campo sale del **id** (`textViewNombre` → `binding.textViewNombre`). Si renombras un id o un XML, el Java viejo deja de compilar. | Usa ids en camelCase (sin `_`); revisa que el nombre del XML coincida con la clase; *Build → Rebuild Project*. Si cambiaste un id, cámbialo también en Java. |
| `NullPointerException` al usar `binding` en un callback | El callback llegó cuando la vista ya se destruyó (`binding = null`) | Nunca toques la vista desde un callback de red: usa LiveData + `getViewLifecycleOwner()` (como `ListaFragment`) o comprueba `binding != null`. |
| Lista vacía / error `Unable to resolve host` | Falta `<uses-permission android:name="android.permission.INTERNET" />` o el emulador no tiene internet | Permiso en `AndroidManifest.xml` (fuera de `<application>`); revisa la red del emulador. |
| `Cleartext HTTP traffic not permitted` | La API usa `http://` | Ver el párrafo de arriba (`usesCleartextTraffic`) o usa HTTPS. |
| `IllegalArgumentException: baseUrl must end in /` | Falta la `/` final | `"https://dominio.com/"`. El endpoint del `@GET` va **sin** `/` inicial. |
| `JsonSyntaxException: Expected BEGIN_OBJECT but was BEGIN_ARRAY` (o al revés) | El tipo de retorno no coincide con el JSON | Arreglo → `List<Entidad>`; objeto contenedor → un Dto con la lista. |
| Los campos de la entidad salen `null` | El atributo Java no se llama igual que la clave del JSON | Renombra el atributo o usa `@SerializedName("clave")`. |
| `Cannot resolve symbol 'ListaFragmentDirections'` / `DetalleFragmentArgs` | Safe Args no ha regenerado las clases: falta el plugin, hay un error en `nav_graph.xml`, o cambiaste una acción/argumento | Verifica `id 'androidx.navigation.safeargs'` en **ambos** `build.gradle`; *File → Sync Project with Gradle Files* y *Build → Rebuild Project*. El método se llama `action` + id de la acción en camelCase (`action_listaFragment_to_detalleFragment` → `actionListaFragmentToDetalleFragment`). |
| Safe Args con `Serializable`: `ClassCastException` o error de compilación | La clase no es `Serializable` (o un objeto anidado no lo es) | `implements Serializable` en la entidad **y** en todas las clases anidadas (`Company`). |
| `IllegalArgumentException: Navigation action/destination ... cannot be found from the current destination` | Doble click rápido en un botón: se navega dos veces desde un destino que ya cambió | Opcional: deshabilitar el botón tras el click, o comprobar `navController.getCurrentDestination().getId()` antes de `navigate`. |
| Los cambios del `nav_graph` no se reflejan en `Directions` | Build en caché | *Build → Clean Project* y luego *Rebuild Project*. |
| El acelerómetro deja de "moverse" al volver al fragment / batería se gasta | `registerListener` sin su `unregisterListener` (o registrado en `onCreate`) | Registrar en `onResume`, desregistrar en `onPause`, siempre en pareja. |
| `NullPointerException` en `getDefaultSensor`/`registerListener` | No se validó que el `SensorManager` o el sensor existan | Validar `!= null` como en `SensorFragment.onViewCreated` antes de registrar. |
| En el emulador el sensor de luz/proximidad no cambia | Los sensores virtuales no se mueven solos | *Extended controls (⋯) → Virtual sensors*. |
| La app se ve bajo la barra de estado | targetSdk 36 fuerza *edge-to-edge* | Ya resuelto en `MainActivity` con `setOnApplyWindowInsetsListener`; si agregas otra Activity, repítelo. |
| `Unsupported class file major version` / Gradle no sincroniza | JDK equivocado | *Settings → Build → Gradle → Gradle JDK* = el JDK embebido de Android Studio (17+). |

---

## Otros documentos de este proyecto
- `GUIA_MANUAL.md`: paso a paso completo **sin IA** (con el código real, mapa a los repos del profe y cómo añadir un fragment nuevo).
- `PROMPT_GEMINI.md`: prompt completo para Gemini, prompts de seguimiento y plantilla de declaración de uso de IA.
