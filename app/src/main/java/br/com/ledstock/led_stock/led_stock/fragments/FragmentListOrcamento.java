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
import android.util.Log;
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

import br.com.ledstock.led_stock.led_stock.adapter.ListItensOfOrcamento;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.services.LedService;

public class FragmentListOrcamento extends android.support.v4.app.Fragment {

    private View view_frag;
    private static Long ID_ORCAMENTO;
    public static android.support.v7.view.ActionMode actionMode;
    public static List<String> Itens;
    protected RecyclerView recyclerView;

    public FragmentListOrcamento() {
        // Required empty public constructor
    }

    public static FragmentListOrcamento newInstance() {
        FragmentListOrcamento fragment = new FragmentListOrcamento();
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
        getActivity().registerReceiver(RefreshListOfOrcamento, new IntentFilter("REFRESH_ITENS_ORCAMENTO"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_orcamento, container, false);

        view_frag = view;

        ID_ORCAMENTO = getParentFragment().getArguments().getLong("id_orcamento", 0);


        LedStockDB db = new LedStockDB(getActivity());
        Long id_orcamento_remote = db.SelectOrcamentoRemoteIDById(String.valueOf(ID_ORCAMENTO));

       /* Cursor c_content = db.Select_InfoOfClientinOrcamento(String.valueOf(ID_ORCAMENTO), String.valueOf(id_orcamento_remote));

        TextView textView_nome = (TextView) view_frag.findViewById(R.id.orcamento_client_name);
        textView_nome.setText(c_content.getString(c_content.getColumnIndex("cliente")));
        TextView textView_cnpj_cpf = (TextView) view_frag.findViewById(R.id.orcamento_cnpj_cpf);
        textView_cnpj_cpf.setText(c_content.getString(c_content.getColumnIndex("cnpj_cpf")));

        String concat_tels;
        String tel1 = c_content.getString(c_content.getColumnIndex("tel"));
        String tel2 = c_content.getString(c_content.getColumnIndex("tel2"));

        if (!tel2.equals("")){
            concat_tels = tel1 + "/" + tel2;
        }else{
            concat_tels =tel1;
        }

        TextView textView_tel = (TextView) view_frag.findViewById(R.id.orcamento_tel);
        textView_tel.setText(concat_tels);*/

        Cursor c = db.Select_ListOfOrcamento(String.valueOf(ID_ORCAMENTO), String.valueOf(id_orcamento_remote));

        recyclerView = (RecyclerView) view_frag.findViewById(R.id.recyclerviewListItensOfOrcamento);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManger);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(new ListItensOfOrcamento(getActivity(), c, onClickLongListItensOfOrcamento()));

        return view;
    }

    private ListItensOfOrcamento.ListItensOfOrcamentoOnClickListener onClickLongListItensOfOrcamento() {

        return new ListItensOfOrcamento.ListItensOfOrcamentoOnClickListener() {
            @Override
            public void onClickListItensOfOrcamento(View view, int idx) {

                if (actionMode != null) {

                    int index = Itens.indexOf(String.valueOf(idx));
                    if (index == -1) {
                        Itens.add(String.valueOf(idx));
                    } else {
                        Itens.remove(index);
                    }
                    //Atualiza o titulo com a quantidade de carros selecionados
                    updateActionModetitle();
                    //Redesenha a lista
                    recyclerView.getAdapter().notifyDataSetChanged();
                }

            }

            @Override
            public void onClickLongListItensOfOrcamento(View view, int idx) {
                if (actionMode != null) {
                    return;
                }

                if (Itens == null) {
                    Itens = new ArrayList<>();
                }

                int index = Itens.indexOf(String.valueOf(idx));
                if (index == -1) {
                    Itens.add(String.valueOf(idx));
                } else {
                    Itens.remove(index);
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
            actionMode.setTitle("Selecione os ítens !");
            actionMode.setSubtitle(null);

            int lenght = Itens.size();

            if (lenght == 0) {
                actionMode.finish();
                Itens.clear();
            } else if (lenght == 1) {
                actionMode.setSubtitle("1 ítem selecionado !");
            } else if (lenght > 1) {
                actionMode.setSubtitle(lenght + " ítens selecionados !");
            }
            //updateShareIntent(selectedCarros);
        }
    }

    //BroadCast Receiver para Editar as Lamps Remotamente
    private BroadcastReceiver RefreshListOfOrcamento = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {

            LedStockDB db = new LedStockDB(getActivity());
            Long id_orcamento_remote = db.SelectOrcamentoRemoteIDById(String.valueOf(ID_ORCAMENTO));
            Cursor c = db.Select_ListOfOrcamento(String.valueOf(ID_ORCAMENTO), String.valueOf(id_orcamento_remote));

            recyclerView = (RecyclerView) view_frag.findViewById(R.id.recyclerviewListItensOfOrcamento);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManger);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setAdapter(new ListItensOfOrcamento(getActivity(), c, onClickLongListItensOfOrcamento()));

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
        getActivity().unregisterReceiver(RefreshListOfOrcamento);
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
                    int size = Itens.size();
                    try {
                        for (int i = 0; i < Itens.size(); i++) {
                            db.DeleteItensOfOrcamento(Itens.get(i));
                            LedService remote = new LedService();
                            remote.DeleteItensOfOrcamentoRemote(Itens.get(i));
                        }
                    } finally {
                        db.close();
                        Itens.clear();
                        Intent intent = new Intent();
                        intent.setAction("REFRESH_ITENS_ORCAMENTO");
                        getActivity().sendBroadcast(intent);
                        if (size == 1) {
                            Toast.makeText(getActivity(), "ítem Excluido com Sucesso !", Toast.LENGTH_SHORT).show();
                        } else if (size > 1) {
                            Toast.makeText(getActivity(), "ítens Excluidos com Sucesso !", Toast.LENGTH_SHORT).show();
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
                if (Itens != null) {
                    Itens.clear();
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
