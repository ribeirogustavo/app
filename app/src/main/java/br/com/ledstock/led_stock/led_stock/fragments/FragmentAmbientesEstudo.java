package br.com.ledstock.led_stock.led_stock.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.activity.Activity_more;
import br.com.ledstock.led_stock.led_stock.adapter.AmbientesOfEstudoAdapter;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;

public class FragmentAmbientesEstudo extends android.support.v4.app.Fragment {

    private View view_frag;
    private static int ID_ESTUDO;

    public FragmentAmbientesEstudo() {
        // Required empty public constructor
    }

    public static FragmentAmbientesEstudo newInstance() {
        FragmentAmbientesEstudo fragment = new FragmentAmbientesEstudo();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // mParam1 = getArguments().getString(ARG_PARAM1);
            // mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //Registra o Receiver para Refresh em Lamps
        getActivity().registerReceiver(RefreshAmbientesofEstudo, new IntentFilter("REFRESH_AMBIENTESOFESTUDOS"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ambientes_estudo, container, false);

        view_frag = view;

        ID_ESTUDO = getParentFragment().getArguments().getInt("id_estudo", 0);


        LedStockDB db = new LedStockDB(getActivity());
        Long id_estudo_remote = db.SelectEstudoRemoteIDById(String.valueOf(ID_ESTUDO));
        Cursor c = db.Select_ListAmbientesOfEstudo(String.valueOf(ID_ESTUDO), String.valueOf(id_estudo_remote));

        RecyclerView recyclerView = (RecyclerView) view_frag.findViewById(R.id.recyclerviewAmbienteOfEstudo);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManger);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(new AmbientesOfEstudoAdapter(getActivity(), c, onClickAmbienteOfEstudo()));

        return view;
    }

    private AmbientesOfEstudoAdapter.AmbientesOfEstudoOnClickListener onClickAmbienteOfEstudo() {

        return new AmbientesOfEstudoAdapter.AmbientesOfEstudoOnClickListener() {
            @Override
            public void onClickAmbienteOfEstudo(View view, int idx) {
                //Toast.makeText(getActivity(), "Clicou no Ambiente !", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), Activity_more.class);
                intent.putExtra("add_fragment", "fragment_add_itens_estudo");
                intent.putExtra("estudo", ID_ESTUDO);
                intent.putExtra("ambiente_estudo", idx);
                startActivity(intent);
            }
        };
    }

    //BroadCast Receiver para Editar as Lamps Remotamente
    private BroadcastReceiver RefreshAmbientesofEstudo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {

            LedStockDB db = new LedStockDB(getActivity());
            Long id_estudo_remote = db.SelectEstudoRemoteIDById(String.valueOf(ID_ESTUDO));
            Cursor c = db.Select_ListAmbientesOfEstudo(String.valueOf(ID_ESTUDO), String.valueOf(id_estudo_remote));

            RecyclerView recyclerView = (RecyclerView) view_frag.findViewById(R.id.recyclerviewAmbienteOfEstudo);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManger);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setAdapter(new AmbientesOfEstudoAdapter(getActivity(), c, onClickAmbienteOfEstudo()));
            /*
            LedStockDB db = new LedStockDB(getContext());
            Cursor c = db.Select_ListLamps();

            RecyclerView recyclerView = (RecyclerView) view_frag.findViewById(R.id.recyclerviewLamp);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManger);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setAdapter(new LampsAdapter(getActivity(), c, onClickLamp()));

            /*
            if (intent.getExtras() != null) {
                if (intent.getStringExtra("ACTION").equals("ADD")) {

                    if (coor != null) {
                        Log.d("TAG", "CoordinatorLayout is null !");
                    } else {
                        Log.d("TAG", "CoordinatorLayout is not null !");
                    }

                    context.getApplicationContext();

                    Snackbar mySnack = Snackbar.make(coor, "Lampada Inserida com Sucesso !", 1);
                    //mySnack.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                    mySnack.show();

                } else if (intent.getStringExtra("ACTION").equals("EDIT")) {
                    //Snackbar mySnack = Snackbar.make(getActivity().findViewById(R.id.CoordinatorContent), "Lampada Editada com Sucesso !", Snackbar.LENGTH_LONG);
                    //mySnack.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                    //mySnack.show();

                } else {
                    Toast.makeText(getActivity(), "Extras is not null but, non ADD !", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Extras is null !", Toast.LENGTH_SHORT).show();
            }
            */
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
        getActivity().unregisterReceiver(RefreshAmbientesofEstudo);
    }
}
