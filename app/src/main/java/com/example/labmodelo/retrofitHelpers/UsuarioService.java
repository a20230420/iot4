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
