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
