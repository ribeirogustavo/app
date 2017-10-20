package br.com.ledstock.led_stock.led_stock.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.services.LedService;

public class Dialog_Info_Client_fragment extends DialogFragment {

    private static Activity activity;
    private static String ID_ORCAMENTO;
    private static long item;

    public Dialog_Info_Client_fragment() {
        // Required empty public constructor
    }

    public static Dialog_Info_Client_fragment newInstance() {
        Dialog_Info_Client_fragment fragment = new Dialog_Info_Client_fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static void show(FragmentManager fm, String id_orcamento, Activity context) {

        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("add_info_cliente");

        activity = context;

        ID_ORCAMENTO = id_orcamento;

        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        Dialog_Info_Client_fragment frag = new Dialog_Info_Client_fragment();
        //frag.callback = callback;
        frag.show(ft, "add_info_cliente");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();

        item = 0;

        final View view = inflater.inflate(R.layout.fragment_info_cliente, null);

        LedStockDB db = new LedStockDB(getActivity());
        final Long id_orcamento_remote = db.SelectOrcamentoRemoteIDById(ID_ORCAMENTO);
        Cursor c = db.Select_InfoOfClientinOrcamento(String.valueOf(ID_ORCAMENTO), String.valueOf(id_orcamento_remote));

        final TextView cliente = (TextView) view.findViewById(R.id.cliente);
        final TextView cnpj_cpf = (TextView) view.findViewById(R.id.cpf_cnpj);
        final TextView tel = (TextView) view.findViewById(R.id.tel);
        final TextView email = (TextView) view.findViewById(R.id.email);
        final TextView contato = (TextView) view.findViewById(R.id.contato);


        if (c != null) {
            if (c.getCount() > 0) {

                if (c.getString(c.getColumnIndex("cliente")) != null){
                    cliente.setText(c.getString(c.getColumnIndex("cliente")) );
                }else{
                    cliente.setText("");
                }

                if (c.getString(c.getColumnIndex("cnpj_cpf")) != null){
                    cnpj_cpf.setText(c.getString(c.getColumnIndex("cnpj_cpf")) );
                }else{
                    cnpj_cpf.setText("");
                }

                if (c.getString(c.getColumnIndex("tel")) != null){
                    tel.setText(c.getString(c.getColumnIndex("tel")) );
                }else{
                    tel.setText("");
                }

                if (c.getString(c.getColumnIndex("email")) != null){
                    email.setText(c.getString(c.getColumnIndex("email")) );
                }else{
                    email.setText("");
                }

                if (c.getString(c.getColumnIndex("contato")) != null){
                    contato.setText(c.getString(c.getColumnIndex("contato")) );
                }else{
                    contato.setText("");
                }


                tel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (tel.getText().toString().trim().length() != 0){
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + tel.getText().toString()));
                            startActivity(intent);
                        }

                    }
                });

                email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (email.getText().toString().trim().length() != 0){
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto:" + email.getText().toString())); // only email apps should handle this
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(intent);
                            }
                        }

                    }
                });

            }
        }

        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        builder.setTitle("Informações do Cliente");

        return builder.create();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
