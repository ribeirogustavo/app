package br.com.ledstock.led_stock.led_stock.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.activity.Activity_content;
import br.com.ledstock.led_stock.led_stock.domain.Array_Clients;
import br.com.ledstock.led_stock.led_stock.domain.GoogleContact;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.services.LedService;
import br.com.ledstock.led_stock.led_stock.utils.Mask;


public class EditarClienteDialog extends android.support.v4.app.DialogFragment {

    private String ID_CLIENTE;
    private View view_frag;
    private final String TAG = "EDIT_CLIENT";
    private TextWatcher cnpj_cpfMask;
    private TextWatcher TelMask;
    private TextWatcher TelMask2;
    private TextWatcher CepMask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            // mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_cliente_dialog, container, false);

        view.findViewById(R.id.Salvar).setOnClickListener(onClickSalvar());
        view.findViewById(R.id.Cancelar).setOnClickListener(onClickCancelar());

        view_frag = view;

        Spinner spinner = (Spinner) view.findViewById(R.id.SpinnerUF);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.UF_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        this.ID_CLIENTE = getArguments().getString("cliente");

        if (ID_CLIENTE != null) {

            LedStockDB search_db = new LedStockDB(getActivity());

            Cursor c = search_db.SelectClientById(ID_CLIENTE);

            EditText nome = (EditText) view.findViewById(R.id.tNome);
            final EditText cnpj_cpf = (EditText) view.findViewById(R.id.tcnpj_cpf);
            EditText endereco = (EditText) view.findViewById(R.id.tEndereco);
            EditText numero = (EditText) view.findViewById(R.id.tNum);
            EditText comp = (EditText) view.findViewById(R.id.tComp);
            final EditText cep = (EditText) view.findViewById(R.id.tCep);
            EditText bairro = (EditText) view.findViewById(R.id.tBairro);
            EditText cidade = (EditText) view.findViewById(R.id.tCidade);
            Spinner uf = (Spinner) view.findViewById(R.id.SpinnerUF);
            EditText contato = (EditText) view.findViewById(R.id.tContato);
            EditText email1 = (EditText) view.findViewById(R.id.tEmail);
            EditText email2 = (EditText) view.findViewById(R.id.tEmail2);
            final EditText tel1 = (EditText) view.findViewById(R.id.tTel1);
            final EditText tel2 = (EditText) view.findViewById(R.id.tTel2);

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


            nome.setText(c.getString(c.getColumnIndex("nome")));
            cnpj_cpf.setText(c.getString(c.getColumnIndex("cnpj_cpf")));
            endereco.setText(c.getString(c.getColumnIndex("endereco")));
            numero.setText(c.getString(c.getColumnIndex("numero")));
            comp.setText(c.getString(c.getColumnIndex("comp")));
            cep.setText(c.getString(c.getColumnIndex("cep")));
            bairro.setText(c.getString(c.getColumnIndex("bairro")));
            cidade.setText(c.getString(c.getColumnIndex("cidade")));

            ArrayAdapter<String> array_spinner = (ArrayAdapter<String>) uf.getAdapter();
            uf.setSelection(array_spinner.getPosition(c.getString(c.getColumnIndex("uf"))));

            contato.setText(c.getString(c.getColumnIndex("contato")));
            email1.setText(c.getString(c.getColumnIndex("email")));
            email2.setText(c.getString(c.getColumnIndex("email2")));
            tel1.setText(c.getString(c.getColumnIndex("tel")));
            tel2.setText(c.getString(c.getColumnIndex("tel2")));
        }
        return view;
    }

    private View.OnClickListener onClickCancelar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Fecha o DialogFragment
                dismiss();
            }
        };
    }

    private View.OnClickListener onClickSalvar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UpDate_Client_DB()) {
                    getActivity().finish();
                    Intent intent = new Intent(getContext(), Activity_content.class);
                    intent.putExtra("cliente", Integer.parseInt(ID_CLIENTE));
                    startActivity(intent);
                    //Fecha o Fragment Dialog
                    dismiss();
                    Toast.makeText(getActivity(), "Cliente Editado com Sucesso !", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    //Metodo utilitario para criar o dialog
    public void show(FragmentManager fm, String id_cliente) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("editar_cliente");
        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);
        EditarClienteDialog frag = new EditarClienteDialog();
        Bundle args = new Bundle();
        //Passa o id do cliente como parâmetro
        args.putString("cliente", id_cliente);
        frag.setArguments(args);
        frag.show(ft, "editar_cliente");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) {
            return;
        }

        /*
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.
        display.getSize(size);
        int largura = size.x;
        int altura = size.y;
        */

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height_atual = displaymetrics.heightPixels;
        int width_atual = displaymetrics.widthPixels;

        //Atualiza o tamanho do dialog
        //int with = getResources().getDimensionPixelSize();
        //int height = getResources().getDimensionPixelSize();
        getDialog().getWindow().setLayout((int) Math.floor(width_atual * 0.95), (int) Math.floor(height_atual * 0.95));
    }

    public EditarClienteDialog() {
        // Required empty public constructor
    }

    private boolean UpDate_Client_DB() {

        String address = "";
        String RAW_CONTACT_ID = null;
        String old_name = null;
        String old_tel = null;
        String old_email = null;
        String old_tel2 = null;
        String old_email2 = null;
        String old_address = null;
        String old_num = null;
        String old_comp = null;
        String conj_address = "";

        EditText tNome = (EditText) view_frag.findViewById(R.id.tNome);
        EditText tcnpj_cpf = (EditText) view_frag.findViewById(R.id.tcnpj_cpf);
        EditText tendereco = (EditText) view_frag.findViewById(R.id.tEndereco);
        EditText tnum = (EditText) view_frag.findViewById(R.id.tNum);
        EditText tcomp = (EditText) view_frag.findViewById(R.id.tComp);
        EditText tcep = (EditText) view_frag.findViewById(R.id.tCep);
        EditText tbairro = (EditText) view_frag.findViewById(R.id.tBairro);
        EditText tcidade = (EditText) view_frag.findViewById(R.id.tCidade);
        Spinner tuf = (Spinner) view_frag.findViewById(R.id.SpinnerUF);
        EditText tcontato = (EditText) view_frag.findViewById(R.id.tContato);
        EditText temail = (EditText) view_frag.findViewById(R.id.tEmail);
        EditText temail2 = (EditText) view_frag.findViewById(R.id.tEmail2);
        EditText ttel = (EditText) view_frag.findViewById(R.id.tTel1);
        EditText ttel2 = (EditText) view_frag.findViewById(R.id.tTel2);

        if ((tNome.getText().toString().trim().length() != 0)
                && (tcontato.getText().toString().trim().length() != 0)
                && (temail.getText().toString().trim().length() != 0)
                && (ttel.getText().toString().trim().length() != 0)) {

            Array_Clients clients = new Array_Clients();
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

            if (tendereco.getText().toString().trim().length() != 0) {
                address += clients.endereco;
            }
            if (tnum.getText().toString().trim().length() != 0) {
                address += ", " + clients.numero;
            }
            if (tcomp.getText().toString().trim().length() != 0) {
                address += " - " + clients.comp;
            }

            LedStockDB db = new LedStockDB(getContext());

            Cursor old_c = db.SelectClientById(ID_CLIENTE);

            old_name = old_c.getString(old_c.getColumnIndex("nome"));
            old_tel = old_c.getString(old_c.getColumnIndex("tel"));
            old_email = old_c.getString(old_c.getColumnIndex("email"));
            old_tel2 = old_c.getString(old_c.getColumnIndex("tel2"));
            old_email2 = old_c.getString(old_c.getColumnIndex("email2"));
            old_address = old_c.getString(old_c.getColumnIndex("endereco"));
            old_num = old_c.getString(old_c.getColumnIndex("numero"));
            old_comp = old_c.getString(old_c.getColumnIndex("comp"));
            RAW_CONTACT_ID = old_c.getString(old_c.getColumnIndex("raw_contact_id"));

            if (old_address.trim().length() != 0) {
                conj_address += old_address;
            }
            if (old_num.trim().length() != 0) {
                conj_address += ", " + old_num;
            }
            if (old_comp.trim().length() != 0) {
                conj_address += " - " + old_comp;
            }

            String[] Old_Contact = new String[]{old_name, old_tel, old_email, old_tel2, old_email2, conj_address};
            String[] New_Contact = new String[]{clients.nome, clients.tel1, clients.email, clients.tel2, clients.email2, address};

            if (RAW_CONTACT_ID != null) {
                GoogleContact contact = new GoogleContact();
                contact.UpdateContactByID(getActivity(), RAW_CONTACT_ID, Old_Contact, New_Contact);
                Log.d(TAG, "Contato: " + RAW_CONTACT_ID + "-> Editado com Sucesso !");
            }

            db.UpDate_ClientById(ID_CLIENTE, clients);
            //Instancia a Classe de Serviço
            LedService service = new LedService();
            //Edita no Banco de Dados remoto o Cliente
            service.EditClientRemote(Integer.parseInt(ID_CLIENTE));
            Log.d(TAG, "Contato Editado no DB interno com Sucesso !");


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
            return false;
        }
    }

}
