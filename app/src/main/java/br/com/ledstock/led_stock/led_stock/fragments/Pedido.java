package br.com.ledstock.led_stock.led_stock.fragments;

import android.content.Intent;
import android.database.Cursor;
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
import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.activity.ActivityEmpty;
import br.com.ledstock.led_stock.led_stock.adapter.PedidosAdapter;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;

/**
 * Created by Gustavo on 25/11/2016.
 */

public class Pedido extends android.support.v4.app.Fragment  {

    private View view_frag;
    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listapedidos, container, false);

        getActivity().findViewById(R.id.fab).setVisibility(View.INVISIBLE);

        //Informar que este fragment contem menu na ToolBar
        setHasOptionsMenu(true);

        view_frag = view;

        LoadRecyclerView();

        getActivity().findViewById(R.id.fab).setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        //Intent intent = new Intent(getActivity(), Activity_add.class);
                        //  intent.putExtra("add_fragment","fragment_add_estudo");
                        // startActivity(intent);
                        Dialog_Estudo_fragment.show(getChildFragmentManager(), 0, 0, getActivity());
                    }
                }
        );

        return view;
    }

    private PedidosAdapter.PedidosOnClickListener onClickPedido() {
        return new PedidosAdapter.PedidosOnClickListener() {
            @Override
            public void onClickPedido(View view, int idx) {
                Intent intent = new Intent(getContext(), ActivityEmpty.class);
                intent.putExtra("action", "content_estudo");
                intent.putExtra("id_estudo", idx);
                startActivity(intent);
            }
        };
    }

    private void LoadRecyclerView() {

        LedStockDB db = new LedStockDB(getActivity());
        Cursor c = db.Select_PedidosOfEstudo();

        if (c != null) {
            RecyclerView recyclerView = (RecyclerView) view_frag.findViewById(R.id.recyclerviewPedido);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManger);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setAdapter(new PedidosAdapter(getActivity(), c, onClickPedido()));
        }

        db.close();
    }

    private void LoadRecyclerViewQuery(String Query) {
        LedStockDB db = new LedStockDB(getActivity());
        Cursor c = db.getPesquisarClienteActivityPedidos(Query);

        if (c != null) {
            RecyclerView recyclerView = (RecyclerView) view_frag.findViewById(R.id.recyclerviewPedido);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManger);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setAdapter(new PedidosAdapter(getActivity(), c, onClickPedido()));
        }

        db.close();
    }

    @Override
    public void onPause(){
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.options_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setQueryHint("Pesquisar por Cliente");

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
        //   Toast.makeText(getActivity(), "Abrir campo pesquisa !", Toast.LENGTH_SHORT).show();
        //finish();
        //  }
        return super.onOptionsItemSelected(item);
    }


}

