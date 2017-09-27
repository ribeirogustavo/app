package br.com.ledstock.led_stock.led_stock.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.activity.Activity_content;
import br.com.ledstock.led_stock.led_stock.adapter.AmbientesAdapter;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;

public class Places extends Fragment {

    private View view_frag;

    public Places() {
        // Required empty public constructor
    }

    public static Places newInstance() {
        Places fragment = new Places();
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
        //Registra o Receiver para Refresh em Lamps
        getActivity().registerReceiver(RefreshAmbientes, new IntentFilter("REFRESH_AMBIENTES"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_places, container, false);

        view_frag = view;

        LedStockDB db = new LedStockDB(getActivity());
        Cursor c = db.Select_ListAmbientes();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerviewAmbientes);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManger);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(new AmbientesAdapter(getActivity(), c, onClickAmbiente()));

        getActivity().findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog_Place_fragment.show(getChildFragmentManager(), 0, getActivity());
            }
        });

        return view;
    }

    private AmbientesAdapter.AmbientesOnClickListener onClickAmbiente() {

        return new AmbientesAdapter.AmbientesOnClickListener() {
            @Override
            public void onClickAmbiente(View view, int idx) {
                Intent intent = new Intent(getContext(), Activity_content.class);
                intent.putExtra("ambiente", idx);
                startActivity(intent);
            }
        };
    }

    private BroadcastReceiver RefreshAmbientes = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {

            LedStockDB db = new LedStockDB(getActivity());
            Cursor c = db.Select_ListAmbientes();

            RecyclerView recyclerView = (RecyclerView) view_frag.findViewById(R.id.recyclerviewAmbientes);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManger);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setAdapter(new AmbientesAdapter(getActivity(), c, onClickAmbiente()));

        }
    };

    @Override
    public void onResume(){
        super.onResume();
    }

    public void onPause(){
        super.onPause();
        getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Cancela o Registro de Refresh Ambientes
        getActivity().unregisterReceiver(RefreshAmbientes);
    }
}
