package br.com.ledstock.led_stock.led_stock.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.services.LedService;

public class Watts_hora extends Fragment {

    private View view_frag;

    private static Double valor_anterior;

    public Watts_hora() {
        // Required empty public constructor
    }

    public static Watts_hora newInstance() {
        Watts_hora fragment = new Watts_hora();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmentview;
        View view = inflater.inflate(R.layout.fragment_watts_hora, container, false);

        view_frag = view;

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        LedStockDB db = new LedStockDB(getActivity());
        String value_KWh;

        valor_anterior = db.Select_KWh();

        if (valor_anterior == null) {
            valor_anterior = 0.0;
        }

        value_KWh = String.valueOf(valor_anterior);

        TextView valor = (TextView) view_frag.findViewById(R.id.preco);

        valor.setText(value_KWh);
        getActivity().findViewById(R.id.fab).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        Double valor_atual;
        String val_ant;
        String val_after;
        TextView valor = (TextView) view_frag.findViewById(R.id.preco);
        valor_atual = Double.parseDouble(valor.getText().toString());

        val_ant = String.valueOf(valor_anterior);
        val_after = String.valueOf(valor_atual);

        if (!val_ant.equals(val_after)) {
            LedStockDB db = new LedStockDB(getActivity());
            db.Update_KWh(valor_atual);
            LedService update_remote = new LedService();
            update_remote.UpdateKWhRemote(valor_atual);
        }
        getActivity().findViewById(R.id.fab).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
