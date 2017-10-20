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
import br.com.ledstock.led_stock.led_stock.adapter.LedAdapter;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;

public class LED_Solution extends Fragment {

    private View view_frag;
    private RecyclerView recyclerView;

    public LED_Solution() {
        // Required empty public constructor
    }

    public static LED_Solution newInstance() {
        LED_Solution fragment = new LED_Solution();
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
        getActivity().registerReceiver(RefreshLed, new IntentFilter("REFRESH_LEDS"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_led_solution, container, false);

        view_frag = view;

        LedStockDB db = new LedStockDB(getContext());
        Cursor c = db.Select_ListLEDS();

        if (c != null) {
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerviewLed);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManger);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setAdapter(new LedAdapter(getActivity(), c, onClickLed()));
        }

        return view;
    }

    private LedAdapter.LedOnClickListener onClickLed() {

        return new LedAdapter.LedOnClickListener() {
            @Override
            public void onClickLed(View view, int idx) {
                Intent intent = new Intent(getContext(), Activity_content.class);
                intent.putExtra("led", idx);
                startActivity(intent);
            }
        };
    }

    //BroadCast Receiver para Editar as Lamps Remotamente
    private BroadcastReceiver RefreshLed = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {

            LedStockDB db = new LedStockDB(getActivity());
            Cursor c = db.Select_ListLEDS();

            if (c != null) {
                recyclerView = (RecyclerView) view_frag.findViewById(R.id.recyclerviewLed);
                recyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(mLayoutManger);
                recyclerView.setItemAnimator(new DefaultItemAnimator());

                recyclerView.setAdapter(new LedAdapter(getActivity(), c, onClickLed()));
            }

        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Cancela o Registro de Refresh Lamps
        getActivity().unregisterReceiver(RefreshLed);
    }

}
