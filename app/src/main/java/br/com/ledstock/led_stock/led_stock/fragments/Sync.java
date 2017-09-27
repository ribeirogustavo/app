package br.com.ledstock.led_stock.led_stock.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;


/**
 * Created by Gustavo on 25/11/2016.
 */

public class Sync extends Fragment {

    private View view_frag;

    public Sync() {
        // Required empty public constructor
    }

    public static Sync newInstance() {
        Sync fragment = new Sync();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            // mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sync, container, false);

        view_frag = view;

        getActivity().findViewById(R.id.fab).setVisibility(View.INVISIBLE);

        TextView cliente = (TextView) view.findViewById(R.id.sync_cliente);
        TextView estudo = (TextView) view.findViewById(R.id.sync_estudo);
        TextView lampada = (TextView) view.findViewById(R.id.sync_lampada);
        TextView led = (TextView) view.findViewById(R.id.sync_led);
        TextView mdo = (TextView) view.findViewById(R.id.sync_mdo);
        TextView ambiente = (TextView) view.findViewById(R.id.sync_ambiente);
        TextView usuario = (TextView) view.findViewById(R.id.sync_usuario);

        LedStockDB db = new LedStockDB(getActivity());

        Cursor c_cliente = db.SelectClientsPending();
        if (c_cliente != null) {
            cliente.setText(String.valueOf(c_cliente.getCount()));
            c_cliente.close();
        } else {
            cliente.setText("0");
        }
        Cursor c_estudo = db.SelectEstudoInsertPending();
        if (c_estudo != null) {
            estudo.setText(String.valueOf(c_estudo.getCount()));
            c_estudo.close();
        } else {
            estudo.setText("0");
        }
        Cursor c_lamp = db.SelectLampsInsertPending();
        if (c_lamp != null) {
            lampada.setText(String.valueOf(c_lamp.getCount()));
            c_lamp.close();
        } else {
            lampada.setText("0");
        }
        Cursor c_led = db.SelectLEDsInsertPending();
        if (c_led != null) {
            led.setText(String.valueOf(c_led.getCount()));
            c_led.close();
        } else {
            led.setText("0");
        }
        Cursor c_mdo = db.SelectHandsOnInsertPending();
        if (c_mdo != null) {
            mdo.setText(String.valueOf(c_mdo.getCount()));
            c_mdo.close();
        } else {
            mdo.setText("0");
        }
        Cursor c_ambi = db.SelectAmbientesInsertPending();
        if (c_ambi != null) {
            ambiente.setText(String.valueOf(c_ambi.getCount()));
            c_ambi.close();
        } else {
            ambiente.setText("0");
        }
        Cursor c_usuario = db.SelectUsersInsertPending();
        if (c_usuario != null) {
            usuario.setText(String.valueOf(c_usuario.getCount()));
            c_usuario.close();
        } else {
            usuario.setText("0");
        }

        db.close();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
