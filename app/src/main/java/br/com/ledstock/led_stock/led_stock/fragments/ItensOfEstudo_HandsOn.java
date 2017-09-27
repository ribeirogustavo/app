package br.com.ledstock.led_stock.led_stock.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.adapter.HandsOnAdapter;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.services.LedService;

public class ItensOfEstudo_HandsOn extends android.support.v4.app.Fragment {

    private View view_frag;
    private int ID_AMBIENTE_OF_ESTUDO;
    private int ID_ESTUDO;
    public static android.support.v7.view.ActionMode actionMode;
    protected RecyclerView recyclerView;
    public static List<String> HandsOn;

    public ItensOfEstudo_HandsOn() {
        // Required empty public constructor
    }

    public static ItensOfEstudo_HandsOn newInstance() {
        ItensOfEstudo_HandsOn fragment = new ItensOfEstudo_HandsOn();
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

        ID_ESTUDO = getParentFragment().getArguments().getInt("estudo", 0);
        ID_AMBIENTE_OF_ESTUDO = getParentFragment().getArguments().getInt("ambiente_estudo", 0);

        //Registra o Receiver para Refresh em Lamps
        getActivity().registerReceiver(RefreshItensOfEstudoHandsOn, new IntentFilter("REFRESH_ITENS_HANDSON"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_handson, container, false);

        view_frag = view;

        LedStockDB db = new LedStockDB(getActivity());
        String id_ambiente_estudo_remote = String.valueOf(db.SelectAmbienteOfEstudoRemoteIDById(String.valueOf(ID_AMBIENTE_OF_ESTUDO)));
        Cursor c;
        if (!id_ambiente_estudo_remote.equals("0")) {
            c = db.SelectHandsOnOfAmbienteOfEstudo(String.valueOf(ID_AMBIENTE_OF_ESTUDO), id_ambiente_estudo_remote);
        } else {
            c = db.SelectHandsOnOfAmbienteOfEstudo(String.valueOf(ID_AMBIENTE_OF_ESTUDO), "");
        }

        recyclerView = (RecyclerView) view_frag.findViewById(R.id.recyclerviewHandsOn);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManger);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(new HandsOnAdapter(getActivity(), c, onClickLong()));

        return view;
    }


    private HandsOnAdapter.HandsOnOnClickListener onClickLong() {

        return new HandsOnAdapter.HandsOnOnClickListener() {
            @Override
            public void onClickHandsOn(View view, int idx) {

                if (actionMode != null) {
                    int index = HandsOn.indexOf(String.valueOf(idx));
                    if (index == -1) {
                        HandsOn.add(String.valueOf(idx));
                    } else {
                        HandsOn.remove(index);
                    }
                    //Atualiza o titulo com a quantidade de carros selecionados
                    updateActionModetitle();
                    //Redesenha a lista
                    recyclerView.getAdapter().notifyDataSetChanged();
                }

            }

            @Override
            public void onLongClickHandsOn(View view, int idx) {
                if (actionMode != null) {
                    return;
                }

                if (HandsOn == null) {
                    HandsOn = new ArrayList<>();
                }

                int index = HandsOn.indexOf(String.valueOf(idx));
                if (index == -1) {
                    HandsOn.add(String.valueOf(idx));
                } else {
                    HandsOn.remove(index);
                }
                //Liga a actionBar de contexto
                actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(getActionModeCallback());
                //Solicita ao Android para desenhar a lista novamente
                recyclerView.getAdapter().notifyDataSetChanged();
                //Atualiza o titulo para mostrar a quantidade de carros selecionados
                updateActionModetitle();
                        }
        };
    }

    private void updateActionModetitle() {
        if (actionMode != null) {
            actionMode.setTitle("Selecione Mão de Obra !");
            actionMode.setSubtitle(null);

            int lenght = HandsOn.size();

            if (lenght == 0) {
                actionMode.finish();
                HandsOn.clear();
            } else if (lenght == 1) {
                actionMode.setSubtitle("1 Mão de Obra selecionada !");
            } else if (lenght > 1) {
                actionMode.setSubtitle(lenght + " Mão de Obras selecionadas !");
            }
            //updateShareIntent(selectedCarros);
        }
    }

    //BroadCast Receiver para Editar as Lamps Remotamente
    private BroadcastReceiver RefreshItensOfEstudoHandsOn = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            LedStockDB db = new LedStockDB(getActivity());
            String id_ambiente_estudo_remote = String.valueOf(db.SelectAmbienteOfEstudoRemoteIDById(String.valueOf(ID_AMBIENTE_OF_ESTUDO)));
            Cursor c;
            if (!id_ambiente_estudo_remote.equals("0")) {
                c = db.SelectHandsOnOfAmbienteOfEstudo(String.valueOf(ID_AMBIENTE_OF_ESTUDO), id_ambiente_estudo_remote);
            } else {
                c = db.SelectHandsOnOfAmbienteOfEstudo(String.valueOf(ID_AMBIENTE_OF_ESTUDO), "");
            }

            recyclerView = (RecyclerView) view_frag.findViewById(R.id.recyclerviewHandsOn);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManger);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setAdapter(new HandsOnAdapter(getActivity(), c, onClickLong()));
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
    public void onPause() {
        super.onPause();
        if (actionMode != null){
            actionMode.finish();
        }
        //Limpa a Array List com os itens Selecionados
        if (HandsOn != null){
            HandsOn.clear();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Cancela o Registro de Refresh Lamps
        getActivity().unregisterReceiver(RefreshItensOfEstudoHandsOn);
    }

    private android.support.v7.view.ActionMode.Callback getActionModeCallback() {
        return new android.support.v7.view.ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
                //Infla o menu específico da actionBar de contexto (CAB)
                MenuInflater inflater = getActivity().getMenuInflater();
                inflater.inflate(R.menu.menu_frag_context, menu);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(android.support.v7.view.ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.action_delete) {

                    LedStockDB db = new LedStockDB(getActivity());
                    int size = HandsOn.size();
                    try {
                        for (int i = 0; i < HandsOn.size(); i++) {
                            db.DeleteItensOfEstudo(HandsOn.get(i));
                            LedService remote = new LedService();
                            remote.DeleteItensOfEstudoRemote(HandsOn.get(i));
                        }
                    }finally {
                        db.close();
                        HandsOn.clear();
                        Intent intent = new Intent();
                        intent.setAction("REFRESH_ITENS_HANDSON");
                        getActivity().sendBroadcast(intent);
                        if (size == 1){
                            Toast.makeText(getActivity(), "Mão de Obra excluida com Sucesso !", Toast.LENGTH_SHORT).show();
                        }else if (size > 1){
                            Toast.makeText(getActivity(), "Mão de Obras Excluidas com Sucesso !", Toast.LENGTH_SHORT).show();
                        }
                    }
                    //snack(recyclerView, "Carros excluidos com sucesso !");
                }
                //Encerra o actionMode mode
                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(android.support.v7.view.ActionMode mode) {
                //Limpa o estado da ActionMode
                actionMode = null;
                //Limpa a Array List com os itens Selecionados
                if (HandsOn != null){
                    HandsOn.clear();
                }
                //Notifica o RecyclerView
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return true;
            }
        };
    }
}
