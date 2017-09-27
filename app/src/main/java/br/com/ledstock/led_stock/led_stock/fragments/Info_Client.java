package br.com.ledstock.led_stock.led_stock.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
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
import br.com.ledstock.led_stock.led_stock.activity.Container_Main;
import br.com.ledstock.led_stock.led_stock.domain.GoogleContact;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.services.LedService;

public class Info_Client extends Fragment {

    private String ID_CLIENT;
    static Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // mParam1 = getArguments().getString(ARG_PARAM1);
            //  mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_client, container, false);

        context = getActivity();

        //Informar que este fragment contem menu na ToolBar
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String address = "";
        String cidade_concat = "";

        Activity activity = getActivity();

        if (activity.getIntent().getExtras() != null) {
            if (activity.getIntent().getIntExtra("cliente", 0) != 0) {

                String sendereco, snum, scomp, scidade, suf;

                TextView nome = (TextView) view.findViewById(R.id.tNome);
                TextView cnpj_cpf = (TextView) view.findViewById(R.id.tCnpj_cpf);
                TextView endereco = (TextView) view.findViewById(R.id.tEndereco);
                TextView cep = (TextView) view.findViewById(R.id.tCep);
                TextView bairro = (TextView) view.findViewById(R.id.tBairro);
                TextView cidade = (TextView) view.findViewById(R.id.tCidade);
                TextView contato = (TextView) view.findViewById(R.id.tContato);
                final TextView email1 = (TextView) view.findViewById(R.id.tEmail);
                final TextView email2 = (TextView) view.findViewById(R.id.tEmail2);
                final TextView tel1 = (TextView) view.findViewById(R.id.tTel1);
                final TextView tel2 = (TextView) view.findViewById(R.id.tTel2);

                int id = activity.getIntent().getExtras().getInt("cliente");
                LedStockDB search_cliente = new LedStockDB(activity);

                Cursor c = search_cliente.SelectClientById(String.valueOf(id));

                CollapsingToolbarLayout ToolBarCollapse = (CollapsingToolbarLayout) activity.findViewById(R.id.CollapsingToolBar);
                ToolBarCollapse.setTitle(c.getString(c.getColumnIndex("nome")));

                ID_CLIENT = c.getString(c.getColumnIndex("_id_cliente"));

                sendereco = c.getString(c.getColumnIndex("endereco"));
                snum = c.getString(c.getColumnIndex("numero"));
                scomp = c.getString(c.getColumnIndex("comp"));
                scidade = c.getString(c.getColumnIndex("cidade"));
                suf = c.getString(c.getColumnIndex("uf"));

                if (sendereco.trim().length() != 0) {
                    address += sendereco;
                }
                if (snum.trim().length() != 0) {
                    address += ", " + snum;
                }
                if (scomp.trim().length() != 0) {
                    address += " - " + scomp;
                }

                nome.setText(c.getString(c.getColumnIndex("nome")));
                cnpj_cpf.setText(c.getString(c.getColumnIndex("cnpj_cpf")));

                endereco.setText(address);

                cep.setText(c.getString(c.getColumnIndex("cep")));
                bairro.setText(c.getString(c.getColumnIndex("bairro")));

                if (scidade.trim().length() != 0) {
                    cidade_concat += scidade;
                }
                if (suf.trim().length() != 0) {
                    cidade_concat += " - " + suf;
                }

                cidade.setText(cidade_concat);
                contato.setText(c.getString(c.getColumnIndex("contato")));
                email1.setText(c.getString(c.getColumnIndex("email")));
                email2.setText(c.getString(c.getColumnIndex("email2")));
                tel1.setText(c.getString(c.getColumnIndex("tel")));
                tel2.setText(c.getString(c.getColumnIndex("tel2")));

                LinearLayout linearEmail = (LinearLayout) view.findViewById(R.id.linear_email);
                LinearLayout linearEmail2 = (LinearLayout) view.findViewById(R.id.linear_email2);
                LinearLayout lineartel = (LinearLayout) view.findViewById(R.id.linear_tel);
                LinearLayout lineartel2 = (LinearLayout) view.findViewById(R.id.linear_tel2);

                linearEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (email1.getText().toString().trim().length() != 0){
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto:" + email1.getText().toString())); // only email apps should handle this
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(intent);
                            }
                        }
                    }
                });

                linearEmail2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (email2.getText().toString().trim().length() != 0){
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto:" + email2.getText().toString())); // only email apps should handle this
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(intent);
                            }
                        }
                    }
                });

                lineartel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tel1.getText().toString().trim().length() != 0){
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + tel1.getText().toString()));
                            startActivity(intent);
                        }
                    }
                });

                lineartel2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tel2.getText().toString().trim().length() != 0){
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + tel2.getText().toString()));
                            startActivity(intent);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_info_client, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editar) {

            EditarClienteDialog edit = new EditarClienteDialog();
            edit.show(getFragmentManager(), ID_CLIENT);

            return true;

        } else if (item.getItemId() == R.id.criar_estudo) {

            Dialog_Estudo_fragment.show(getChildFragmentManager(), 2, Long.parseLong(ID_CLIENT), getActivity());

        } else if (item.getItemId() == R.id.excluir) {

            //DeletarClienteDialog delete = new DeletarClienteDialog();
            //delete.show(getFragmentManager(), ID_CLIENT);

            DeletarClienteDialog.show(getFragmentManager(), new DeletarClienteDialog.Callback() {
                public void onClickYes() {

                    try {
                        ConnectivityManager connMgr = (ConnectivityManager)
                                context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                        if (networkInfo != null && networkInfo.isConnected()) {
                            //Instancia o Banco de Dados
                            LedStockDB delete_cliente = new LedStockDB(getActivity());
                            //Instancia o Serviço
                            LedService service = new LedService();
                            //Exclui o Cliente Remotamente
                            service.RemoveClientRemote(delete_cliente.SelectRemoteIDById(ID_CLIENT));
                            //Pega o RAWCONTACTID do Contato através do Banco de Dados
                            String RAWCONTACTID = delete_cliente.SelectRawContactIDById(ID_CLIENT);
                            //Deleta o Cliente do Banco de Dados
                            delete_cliente.DeleteClientById(ID_CLIENT);
                            //Instancia a variável da classe Google Contact
                            GoogleContact delete_contact = new GoogleContact();
                            //Deleta o cliente da Conta Google
                            delete_contact.DeleteContact(getActivity(), RAWCONTACTID);
                            //Chama uma Intent para abrir a tela principal
                            Intent intent = new Intent(getContext(), Container_Main.class);
                            intent.putExtra("action", "del_client");
                            startActivity(intent);
                            //Fecha a Activity
                            getActivity().finish();
                        } else {
                            Toast.makeText(context, "Você deve estar conectado a internet para deletar o cliente !", Toast.LENGTH_SHORT).show();
                        }
                    } catch (SecurityException e2) {
                        //Log.e(TAG, "Sem conexão com a internet !");
                    }
                }
            });
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(getContext(), Container_Main.class);
            // intent.putExtra("action", "del_client");
            startActivity(intent);
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
