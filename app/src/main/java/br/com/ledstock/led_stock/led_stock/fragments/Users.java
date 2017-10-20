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
import br.com.ledstock.led_stock.led_stock.adapter.UsersAdapter;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;

public class Users extends Fragment {

    private View view_frag;

    public Users() {
        // Required empty public constructor
    }

    public static Users newInstance() {
        Users fragment = new Users();
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
        getActivity().registerReceiver(RefreshUsuarios, new IntentFilter("REFRESH_USERS"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        view_frag = view;

        LedStockDB db = new LedStockDB(getActivity());
        Cursor c = db.Select_ListUsers();

        c.moveToNext();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerviewUsers);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManger);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(new UsersAdapter(getActivity(), c, onClickUser()));

        getActivity().findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog_User_fragment.show(getChildFragmentManager(), 0, getActivity());
            }
        });

        return view;
    }

    private UsersAdapter.UsersOnClickListener onClickUser() {

        return new UsersAdapter.UsersOnClickListener() {
            @Override
            public void onClickUser(View view, int idx) {
                Intent intent = new Intent(getContext(), Activity_content.class);
                intent.putExtra("usuario", idx);
                startActivity(intent);
            }
        };
    }

    private BroadcastReceiver RefreshUsuarios = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {

            LedStockDB db = new LedStockDB(getActivity());
            Cursor c = db.Select_ListUsers();

            c.moveToNext();

            RecyclerView recyclerView = (RecyclerView) view_frag.findViewById(R.id.recyclerviewUsers);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManger);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setAdapter(new UsersAdapter(getActivity(), c, onClickUser()));
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
        //Cancela o Registro de Refresh Usuarios
        getActivity().unregisterReceiver(RefreshUsuarios);
    }
}
