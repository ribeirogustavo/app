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

public class Info_users extends Fragment {

    private String ID_USER;
    static Context context;

    public Info_users() {
        // Required empty public constructor
    }

    public static Info_users newInstance() {
        Info_users fragment = new Info_users();
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
        View view = inflater.inflate(R.layout.fragment_info_users, container, false);

        context = getActivity();

        Activity activity = getActivity();

        //Informar que este fragment contem menu na ToolBar
        setHasOptionsMenu(true);

        if (activity.getIntent().getExtras() != null) {
            if (activity.getIntent().getIntExtra("usuario", 0) != 0) {

                TextView nome = (TextView) view.findViewById(R.id.nome);
                TextView usuario = (TextView) view.findViewById(R.id.usuario);
                TextView acesso = (TextView) view.findViewById(R.id.acesso);

                int id = activity.getIntent().getExtras().getInt("usuario");
                LedStockDB search_user = new LedStockDB(activity);

                Cursor c = search_user.SelectUserByID(String.valueOf(id));

                CollapsingToolbarLayout ToolBarCollapse = (CollapsingToolbarLayout) activity.findViewById(R.id.CollapsingToolBar);
                ToolBarCollapse.setTitle(c.getString(c.getColumnIndex("nome")));

                ID_USER = c.getString(c.getColumnIndex("_id_usuario"));

                String access = null;

                if (c.getString(c.getColumnIndex("acesso")).equals("1")){
                    access = "Acesso Completo";
                }else{
                    access = "Acesso Restrito";
                }

                nome.setText(c.getString(c.getColumnIndex("nome")));
                usuario.setText(c.getString(c.getColumnIndex("usuario")));
                acesso.setText(access);

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

            Dialog_User_fragment.show(getChildFragmentManager(), Integer.parseInt(ID_USER), getActivity());

            return true;
        } else if (item.getItemId() == R.id.excluir) {

            DeletarUserDialog.show(getFragmentManager(), new DeletarUserDialog.Callback() {
                public void onClickYes() {
                    //Instancia o Banco de Dados
                    LedStockDB delete_user = new LedStockDB(getActivity());
                    delete_user.DeleteUser(ID_USER);

                    //Instancia o Serviço para Deletar Remotamente
                    LedService service = new LedService();
                    service.DeleteUserRemote(ID_USER);

                    Intent intent = new Intent();
                    intent.setAction("REFRESH_USERS");
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
