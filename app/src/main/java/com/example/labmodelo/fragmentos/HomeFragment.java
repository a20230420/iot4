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
