package br.com.ledstock.led_stock.led_stock.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.activity.Container_Main;
import br.com.ledstock.led_stock.led_stock.domain.Array_Clients;
import br.com.ledstock.led_stock.led_stock.domain.GoogleContact;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.services.LedService;
import br.com.ledstock.led_stock.led_stock.utils.Mask;


public class Add_clientes extends android.support.v4.app.Fragment {

    String TAG = "INSERT_CONTACT";
    private TextWatcher cnpj_cpfMask;
    private TextWatcher TelMask;
    private TextWatcher TelMask2;
    private TextWatcher CepMask;

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
        View view = inflater.inflate(R.layout.fragment_add_clientes, container, false);

        Spinner spinner = (Spinner) view.findViewById(R.id.SpinnerUF);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.UF_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        final EditText cnpj_cpf = (EditText) view.findViewById(R.id.tcnpj_cpf);
        final EditText tel1 = (EditText) view.findViewById(R.id.tTel1);
        final EditText tel2 = (EditText) view.findViewById(R.id.tTel2);
        final EditText cep = (EditText) view.findViewById(R.id.tCep);

        CepMask = Mask.insert("#####-###", cep);
        cep.addTextChangedListener(CepMask);

        TelMask = Mask.insert("(##) ####-####", tel1);
        tel1.addTextChangedListener(TelMask);

        tel1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (tel1.getText().toString().length() == 6) {
                    char digito3 = tel1.getText().toString().charAt(5);
                    if (digito3 == '9') {
                        tel1.removeTextChangedListener(TelMask);
                        TelMask = Mask.insert("(##) #####-####", tel1);
                        tel1.addTextChangedListener(TelMask);
                    }else{
                        tel1.removeTextChangedListener(TelMask);
                        TelMask = Mask.insert("(##) ####-####", tel1);
                        tel1.addTextChangedListener(TelMask);
                    }
                }
                return false;
            }
        });

        TelMask2 = Mask.insert("(##) ####-####", tel2);
        tel2.addTextChangedListener(TelMask2);

        tel2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (tel2.getText().toString().length() == 6) {
                    char digito3 = tel2.getText().toString().charAt(5);
                    if (digito3 == '9') {
                        tel2.removeTextChangedListener(TelMask2);
                        TelMask2 = Mask.insert("(##) #####-####", tel2);
                        tel2.addTextChangedListener(TelMask2);
                    }else{
                        tel2.removeTextChangedListener(TelMask2);
                        TelMask2 = Mask.insert("(##) ####-####", tel2);
                        tel2.addTextChangedListener(TelMask2);
                    }
                }
                return false;
            }
        });

        cnpj_cpfMask = Mask.insert("###.###.###-##", cnpj_cpf);
        cnpj_cpf.addTextChangedListener(cnpj_cpfMask);

        cnpj_cpf.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (cnpj_cpf.getText().toString().length() == 14) {
                    char digito12 = cnpj_cpf.getText().toString().charAt(12);
                    char digito13 = cnpj_cpf.getText().toString().charAt(13);
                    if ((digito12 == '0') && (digito13 == '0')) {
                        cnpj_cpf.removeTextChangedListener(cnpj_cpfMask);
                        cnpj_cpfMask = Mask.insert("##.###.###/####-##", cnpj_cpf);
                        cnpj_cpf.addTextChangedListener(cnpj_cpfMask);
                    }else{
                        cnpj_cpf.removeTextChangedListener(cnpj_cpfMask);
                        cnpj_cpfMask = Mask.insert("###.###.###-##", cnpj_cpf);
                        cnpj_cpf.addTextChangedListener(cnpj_cpfMask);
                    }
                }else if(cnpj_cpf.getText().toString().length() == 0){
                    cnpj_cpf.removeTextChangedListener(cnpj_cpfMask);
                    cnpj_cpfMask = Mask.insert("###.###.###-##", cnpj_cpf);
                    cnpj_cpf.addTextChangedListener(cnpj_cpfMask);
                }
                return false;
            }
        });


        view.findViewById(R.id.Insert_Client).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Insert_Client_DB()) {
                            Intent intent = new Intent(getContext(), Container_Main.class);
                            intent.putExtra("action", "add_client");
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                }
        );

        return view;
    }

    private boolean Insert_Client_DB() {

        String address = "";
        String RAW_CONTACT_ID;
        long ID_TABLE_CLIENT;

        EditText tNome = (EditText) getView().findViewById(R.id.tNome);
        EditText tendereco = (EditText) getView().findViewById(R.id.tEndereco);
        EditText tnum = (EditText) getView().findViewById(R.id.tNum);
        EditText tcomp = (EditText) getView().findViewById(R.id.tComp);
        EditText tcep = (EditText) getView().findViewById(R.id.tCep);
        EditText tbairro = (EditText) getView().findViewById(R.id.tBairro);
        EditText tcidade = (EditText) getView().findViewById(R.id.tCidade);
        Spinner tuf = (Spinner) getView().findViewById(R.id.SpinnerUF);
        EditText tcontato = (EditText) getView().findViewById(R.id.tContato);
        EditText temail = (EditText) getView().findViewById(R.id.tEmail);
        EditText ttel = (EditText) getView().findViewById(R.id.tTel1);
        EditText ttel2 = (EditText) getView().findViewById(R.id.tTel2);
        EditText temail2 = (EditText) getView().findViewById(R.id.tEmail2);
        EditText tcnpj_cpf = (EditText) getView().findViewById(R.id.tcnpj_cpf);


        if ((tNome.getText().toString().trim().length() != 0)
                && (tcontato.getText().toString().trim().length() != 0)
                && (temail.getText().toString().trim().length() != 0)
                && (ttel.getText().toString().trim().length() != 0)) {

            Array_Clients clients = new Array_Clients();
            clients.id_remoto = "";
            clients.nome = tNome.getText().toString();
            clients.endereco = tendereco.getText().toString();
            clients.numero = tnum.getText().toString();
            clients.comp = tcomp.getText().toString();
            clients.cep = tcep.getText().toString();
            clients.bairro = tbairro.getText().toString();
            clients.cidade = tcidade.getText().toString();
            clients.uf = tuf.getSelectedItem().toString();
            clients.contato = tcontato.getText().toString();
            clients.email = temail.getText().toString();
            clients.tel1 = ttel.getText().toString();
            clients.tel2 = ttel2.getText().toString();
            clients.email2 = temail2.getText().toString();
            clients.cnpj_cpf = tcnpj_cpf.getText().toString();


            LedStockDB db = new LedStockDB(getActivity());
            ID_TABLE_CLIENT = db.Insert_Client(clients);
            //Instancia a Classe de Serviço
            LedService service = new LedService();
            //Insere no Banco de Dados remoto o novo cliente
            service.InsertClientRemote(ID_TABLE_CLIENT);

            if (tendereco.getText().toString().trim().length() != 0) {
                address += clients.endereco;
            }
            if (tnum.getText().toString().trim().length() != 0) {
                address += ", " + clients.numero;
            }
            if (tcomp.getText().toString().trim().length() != 0) {
                address += " - " + clients.comp;
            }

            String[] New_Contact = new String[]{clients.nome, clients.tel1, clients.email, clients.tel2, clients.email2, address};

            GoogleContact contact = new GoogleContact();
            RAW_CONTACT_ID = contact.InsertContactAccountGmail(getActivity(), New_Contact);

            Log.e(TAG,"RAW CONTACT"+ RAW_CONTACT_ID);

            if (!RAW_CONTACT_ID.equals("NOT_INSERTED")) {
                db.InsertRaw_Contact_ID(ID_TABLE_CLIENT, RAW_CONTACT_ID);
                Log.d(TAG, "RAW_CONTACT_ID Inserido com Sucesso !");
            }

            return true;

        } else {
            if (tNome.getText().toString().trim().length() == 0) {
                tNome.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red));
                //tNome.setBackgroundResource(R.drawable.line_color_edittext);
            }
            if (tcontato.getText().toString().trim().length() == 0) {
                tcontato.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red));
                // tcontato.setBackgroundResource(R.drawable.line_color_edittext);
            }
            if (ttel.getText().toString().trim().length() == 0) {
                ttel.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red));
                // ttel.setBackgroundResource(R.drawable.line_color_edittext);
            }
            if (temail.getText().toString().trim().length() == 0) {
                temail.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red));
                // temail.setBackgroundResource(R.drawable.line_color_edittext);
            }

            Snackbar mysnack = Snackbar.make(getView().findViewById(R.id.layout_linear), "Preencha os campos Corretamente !", Snackbar.LENGTH_LONG);
            mysnack.show();
            return false;

        }


    }

}
