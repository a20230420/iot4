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
