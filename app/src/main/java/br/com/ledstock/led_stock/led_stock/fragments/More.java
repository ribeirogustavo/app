package br.com.ledstock.led_stock.led_stock.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.activity.Activity_more;

/**
 * Created by Gustavo on 30/09/2016.
 */

public class More extends Fragment {

    private View view_frag;

    public More() {
        // Required empty public constructor
    }

    public static More newInstance() {
        More fragment = new More();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.more, container, false);

        TextView ambiente = (TextView) view.findViewById(R.id.more_ambiente);
        ambiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Activity_more.class);
                intent.putExtra("add_fragment","fragment_add_ambientes");
                startActivity(intent);
            }
        });

        TextView preco_KWh = (TextView) view.findViewById(R.id.more_kwh);
        preco_KWh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Activity_more.class);
                intent.putExtra("add_fragment","fragment_add_KWh");
                startActivity(intent);
            }
        });

        TextView usuarios = (TextView) view.findViewById(R.id.more_usuarios);
        usuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Activity_more.class);
                intent.putExtra("add_fragment","fragment_add_usuarios");
                startActivity(intent);
            }
        });

        TextView sync = (TextView) view.findViewById(R.id.more_sync);
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Activity_more.class);
                intent.putExtra("add_fragment","fragment_sync");
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onPause(){
        super.onPause();
    }
}
