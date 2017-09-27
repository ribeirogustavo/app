package br.com.ledstock.led_stock.led_stock.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.services.LedService;

public class Info_place extends Fragment {

    private String ID_PLACE;
    static Context context;

    public Info_place() {
        // Required empty public constructor
    }

    public static Info_place newInstance() {
        Info_place fragment = new Info_place();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

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
        View view = inflater.inflate(R.layout.fragment_info_place, container, false);

        context = getActivity();

        Activity activity = getActivity();

        //Informar que este fragment contem menu na ToolBar
        setHasOptionsMenu(true);

        if (activity.getIntent().getExtras() != null) {
            if (activity.getIntent().getIntExtra("ambiente", 0) != 0) {

                TextView descricao = (TextView) view.findViewById(R.id.ambiente);

                int id = activity.getIntent().getExtras().getInt("ambiente");
                LedStockDB search_ambiente = new LedStockDB(activity);

                Cursor c = search_ambiente.SelectAmbienteByID(String.valueOf(id));

                CollapsingToolbarLayout ToolBarCollapse = (CollapsingToolbarLayout) activity.findViewById(R.id.CollapsingToolBar);
                ToolBarCollapse.setTitle(c.getString(c.getColumnIndex("descricao")));

                ID_PLACE = c.getString(c.getColumnIndex("_id_ambiente"));

                descricao.setText(c.getString(c.getColumnIndex("descricao")));
            }
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editar) {

            Dialog_Place_fragment.show(getChildFragmentManager(), Integer.parseInt(ID_PLACE), getActivity());

            return true;
        } else if (item.getItemId() == R.id.excluir) {

            DeletarAmbienteDialog.show(getFragmentManager(), new DeletarAmbienteDialog.Callback() {
                public void onClickYes() {
                    //Instancia o Banco de Dados
                    LedStockDB delete_ambiente = new LedStockDB(getActivity());
                    delete_ambiente.DeleteAmbiente(ID_PLACE);

                    //Instancia o Serviço para Deletar Remotamente
                    LedService service = new LedService();
                    service.DeleteAmbienteRemote(ID_PLACE);

                    Intent intent = new Intent();
                    intent.setAction("REFRESH_AMBIENTES");
                    getActivity().sendBroadcast(intent);

                    //Fecha a Activity
                    getActivity().finish();
                }
            });

            return true;
        } else if (item.getItemId() == android.R.id.home) {

        }
        return super.onOptionsItemSelected(item);
    }

}
