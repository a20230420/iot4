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
