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
import br.com.ledstock.led_stock.led_stock.adapter.HandsOnAdapter;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;

public class HandsOn extends Fragment {

    private View view_frag;

    public HandsOn() {
        // Required empty public constructor
    }

    public static HandsOn newInstance() {
        HandsOn fragment = new HandsOn();
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
        //Registra o Receiver para Refresh em Usuarios
        getActivity().registerReceiver(RefreshHandsOn, new IntentFilter("REFRESH_HANDSON"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_handson, container, false);

        view_frag = view;

        LedStockDB db = new LedStockDB(getActivity());
        Cursor c = db.Select_ListHandsOn();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerviewHandsOn);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManger);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(new HandsOnAdapter(getActivity(), c, onClickHandsOn()));

        return view;
    }

    private HandsOnAdapter.HandsOnOnClickListener onClickHandsOn() {
        return new HandsOnAdapter.HandsOnOnClickListener() {
            @Override
            public void onClickHandsOn(View view, int idx) {
                Intent intent = new Intent(getContext(), Activity_content.class);
                intent.putExtra("handson", idx);
                startActivity(intent);
            }

            @Override
            public  void onLongClickHandsOn(View view, int idx){

            }
        };
    }

    private BroadcastReceiver RefreshHandsOn = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            LedStockDB db = new LedStockDB(getActivity());
            Cursor c = db.Select_ListHandsOn();

            RecyclerView recyclerView = (RecyclerView) view_frag.findViewById(R.id.recyclerviewHandsOn);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManger);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setAdapter(new HandsOnAdapter(getActivity(), c, onClickHandsOn()));
        }
    };

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Cancela o Registro de Refresh HandsOn
        getActivity().unregisterReceiver(RefreshHandsOn);
    }
}
