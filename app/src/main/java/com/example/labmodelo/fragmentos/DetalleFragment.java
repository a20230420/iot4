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
