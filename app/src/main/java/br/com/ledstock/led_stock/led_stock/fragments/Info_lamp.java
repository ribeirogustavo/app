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

public class Info_lamp extends Fragment {

    private String ID_LAMP;
    static Context context;

    public Info_lamp() {
        // Required empty public constructor
    }

    public static Info_lamp newInstance(String param1, String param2) {
        Info_lamp fragment = new Info_lamp();
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
        View view = inflater.inflate(R.layout.fragment_info_lamp, container, false);

        context = getActivity();

        Activity activity = getActivity();

        //Informar que este fragment contem menu na ToolBar
        setHasOptionsMenu(true);

        if (activity.getIntent().getExtras() != null) {
            if (activity.getIntent().getIntExtra("lamp", 0) != 0) {


                TextView descricao = (TextView) view.findViewById(R.id.descricao);
                TextView potencia = (TextView) view.findViewById(R.id.potencia);

                int id = activity.getIntent().getExtras().getInt("lamp");
                LedStockDB search_lamp = new LedStockDB(activity);

                Cursor c = search_lamp.SelectLampByID(String.valueOf(id));

                CollapsingToolbarLayout ToolBarCollapse = (CollapsingToolbarLayout) activity.findViewById(R.id.CollapsingToolBar);
                ToolBarCollapse.setTitle(c.getString(c.getColumnIndex("descricao")));

                ID_LAMP = c.getString(c.getColumnIndex("_id_lamp"));

                String pot = c.getString(c.getColumnIndex("potencia")) + "W";

                descricao.setText(c.getString(c.getColumnIndex("descricao")));
                potencia.setText(pot);

            }
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editar) {

            Dialog_Lamp_fragment.show(getChildFragmentManager(), Integer.parseInt(ID_LAMP), getActivity());

            //EditarClienteDialog edit = new EditarClienteDialog();
            //edit.show(getFragmentManager(), ID_LAMP);

            return true;
        } else if (item.getItemId() == R.id.excluir) {

            DeletarLampDialog.show(getFragmentManager(), new DeletarLampDialog.Callback() {
                public void onClickYes() {
                    //Instancia o Banco de Dados
                    LedStockDB delete_lamp = new LedStockDB(getActivity());
                    delete_lamp.DeleteLamp(ID_LAMP);

                    //Instancia o Serviço para Deletar Remotamente
                    LedService service = new LedService();
                    service.DeleteLampRemote(ID_LAMP);

                    Intent intent = new Intent();
                    intent.setAction("REFRESH_LAMPS");
                    getActivity().sendBroadcast(intent);

                    //Fecha a Activity
                    getActivity().finish();
                }
            });

            return true;
        } else if (item.getItemId() == android.R.id.home) {

            //Intent intent = new Intent(getContext(), Container_Main.class);
            // intent.putExtra("action", "del_client");
            //startActivity(intent);
            //getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
