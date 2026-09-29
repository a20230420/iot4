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
