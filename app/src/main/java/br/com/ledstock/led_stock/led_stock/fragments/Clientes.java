package br.com.ledstock.led_stock.led_stock.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.activity.Activity_add;
import br.com.ledstock.led_stock.led_stock.activity.Activity_content;
import br.com.ledstock.led_stock.led_stock.adapter.ClientsAdapter;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;

public class Clientes extends android.support.v4.app.Fragment {

    private View view_frag;
    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

        }
        getActivity().registerReceiver(RefreshClients, new IntentFilter("REFRESH_CLIENTS"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clientes, container, false);

        //Informar que este fragment contem menu na ToolBar
        setHasOptionsMenu(true);

        view_frag = view;

        getActivity().findViewById(R.id.fab).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), Activity_add.class);
                        intent.putExtra("add_fragment", "fragment_add_client");
                        startActivity(intent);
                    }
                }
        );

        LoadRecyclerView();
        /*
        LedStockDB db = new LedStockDB(getActivity());
        Cursor c = db.Select_ListClients();
        //ListView listclients = (ListView) view.findViewById(R.id.listview_client);
        //listclients.setAdapter(new ClientsAdapter(getContext(), c));
        //listclients.setOnClickListener(onListItemClick(););

        //ProgressBar progress = (ProgressBar) view.findViewById(R.id.progress);

        //progress.setVisibility(View.VISIBLE);

        RecyclerView recyclerView = (RecyclerView) view_frag.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManger);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        */

        //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        //listclients.setAdapter(new ClientsAdapter(getContext(), c));

        //recyclerView.setAdapter(new ClientsAdapter(getActivity(), c, onClickCliente()));

        // progress.setVisibility(View.GONE);

        return view;
    }

    private ClientsAdapter.ClienteOnClickListener onClickCliente() {
        return new ClientsAdapter.ClienteOnClickListener() {
            @Override
            public void onClickCliente(View view, int idx) {
                Intent intent = new Intent(getContext(), Activity_content.class);
                intent.putExtra("cliente", idx);
                startActivity(intent);
            }

            /*
            @Override
            public void onClickCarro(View view, int idx) {
                Carro c = carros.get(idx);
                if (actionMode == null) {
                    Intent intent = new Intent(getContext(), CarroActivity.class);
                    intent.putExtra("carro", c);
                    startActivity(intent);
                }else{
                    //Seleciona o carro
                    c.selected = !c.selected;
                    //Atualiza o titulo com a quantidade de carros selecionados
                    updateActionModetitle();
                    //Redesenha a lista
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }*/


        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(RefreshClients);
    }

    private BroadcastReceiver RefreshClients = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            LoadRecyclerView();
        }
    };

    private void LoadRecyclerView() {
        LedStockDB db = new LedStockDB(getActivity());
        Cursor c = db.Select_ListClients();

        if (c != null) {
            recyclerView = (RecyclerView) view_frag.findViewById(R.id.recyclerview);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManger);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setAdapter(new ClientsAdapter(getActivity(), c, onClickCliente()));
        }

        db.close();
    }

    private void LoadRecyclerViewQuery(String Query) {
        LedStockDB db = new LedStockDB(getActivity());
        Cursor c = db.getPesquisarClienteActivityClientes(Query);

        if (c != null) {
            recyclerView = (RecyclerView) view_frag.findViewById(R.id.recyclerview);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManger);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setAdapter(new ClientsAdapter(getActivity(), c, onClickCliente()));
        }

        db.close();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.options_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setQueryHint("Pesquisar Cliente");

        // traverse the view to the widget containing the hint text
        LinearLayout ll = (LinearLayout) searchView.getChildAt(0);
        LinearLayout ll2 = (LinearLayout) ll.getChildAt(2);
        LinearLayout ll3 = (LinearLayout) ll2.getChildAt(1);
        SearchView.SearchAutoComplete autoComplete = (SearchView.SearchAutoComplete) ll3.getChildAt(0);
        // set the hint text color
        autoComplete.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        // set the text color
        autoComplete.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                LoadRecyclerView();
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().length() != 0) {
                    LoadRecyclerViewQuery(newText);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // if (item.getItemId() == R.id.pesquisar) {
        //Intent intent = new Intent(getActivity(), ActivitySearch.class);
        //startActivity(intent);
        // }
        return super.onOptionsItemSelected(item);
    }
}

