package br.com.ledstock.led_stock.led_stock.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import br.com.ledstock.led_stock.R;

public class Luximetro extends Fragment implements SensorEventListener {

    private static final int TIPO_SENSOR = Sensor.TYPE_LIGHT;
    private SensorManager sensorManager;
    private Sensor sensor;
    private View view_frag;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(TIPO_SENSOR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_luximetro, container, false);
        view_frag = view;

        getActivity().findViewById(R.id.fab).setVisibility(View.INVISIBLE);

        if (sensor == null){
            Toast.makeText(getActivity(), "Sensor não disponível !", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    @Override
    public void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        if (sensor != null){
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Lê o valor do Sensor
        float value = event.values[0];
        TextView valuesensor = (TextView) view_frag.findViewById(R.id.valuesensor);
        valuesensor.setText(String.valueOf(value));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
    }
}