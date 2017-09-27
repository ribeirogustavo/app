package br.com.ledstock.led_stock.led_stock.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FilterQueryProvider;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;

public class Dialog_Orcamento_fragment extends DialogFragment {

    private static String action;
    private static long ID_ESTUDO;
    private static Activity activity;
    private static long ID_CLIENTE;
    private long ClientIndex;
    private String NomeCliente;


    public Dialog_Orcamento_fragment() {
        // Required empty public constructor
    }

    public static Dialog_Orcamento_fragment newInstance() {
        Dialog_Orcamento_fragment fragment = new Dialog_Orcamento_fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static void show(FragmentManager fm, long act, long id, Activity context) {

        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("add_orcamento_actual");

        activity = context;

        if (act == 0) {
            action = "add";
        } else if (act == 1) {
            ID_ESTUDO = id;
            action = "edit";
        } else if (act == 2) {
            ID_CLIENTE = id;
            action = "add_with_id";
        }

        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        Dialog_Orcamento_fragment frag = new Dialog_Orcamento_fragment();
        //frag.callback = callback;
        frag.show(ft, "add_orcamento_actual");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.fragment_add_orcamento, null);

        if (action.equals("add")) {
            //Spinner spinner = (Spinner) view.findViewById(R.id.spinner_clientes);
            final AutoCompleteTextView AutoCompletetextView = (AutoCompleteTextView) view.findViewById(R.id.autocompletecliente);

            LedStockDB db = new LedStockDB(getActivity());
            Cursor clients = db.Select_ListClientsAutoComplete();

            final SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    clients,
                    new String[]{"nome"},
                    new int[]{android.R.id.text1},
                    0
            );

            AutoCompletetextView.setAdapter(mAdapter);

        /*
        // Create a SimpleCursorAdapter for the State Name field.
        SimpleCursorAdapter adapter =
                new SimpleCursorAdapter(this,
                        android.R.layout.simple_dropdown_item_1line, null,
                        from, to);
        mStateNameView.setAdapter(adapter);
        */
            // Set an OnItemClickListener, to update dependent fields when
            // a choice is made in the AutoCompleteTextView.
            AutoCompletetextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                    // Get the cursor, positioned to the corresponding row in the
                    // result set
                    Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                    // Get the state's capital from this row in the database.
                    String nome = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
                    // Update the parent class's TextView
                    AutoCompletetextView.setText(nome);
                    NomeCliente = nome;
                    AutoCompletetextView.setSelection(AutoCompletetextView.getText().length());
                    ClientIndex = id;
                }
            });

            // Set the CursorToStringConverter, to provide the labels for the
            // choices to be displayed in the AutoCompleteTextView.
            mAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
                public String convertToString(android.database.Cursor cursor) {
                    // Get the label for this row out of the "state" column
                    final int columnIndex = cursor.getColumnIndexOrThrow("nome");
                    final String str = cursor.getString(columnIndex);
                    return str;
                }
            });

            // Set the FilterQueryProvider, to run queries for choices
            // that match the specified input.
            mAdapter.setFilterQueryProvider(new FilterQueryProvider() {
                public Cursor runQuery(CharSequence constraint) {
                    LedStockDB db = new LedStockDB(getActivity());
                    // Search for states whose names begin with the specified letters.
                    Cursor cursor = db.getMatchingClient((constraint != null ? constraint.toString() : null));
                    return cursor;
                }
            });

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(R.string.salvar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            //Spinner spinner = (Spinner) view.findViewById(R.id.spinner_clientes);
                            //String item = ListIndexClients.get(spinner.getSelectedItemPosition());

                            AutoCompleteTextView AutoCompletetextView = (AutoCompleteTextView) view.findViewById(R.id.autocompletecliente);
                            //String item = ListIndexClients.get(AutoCompletetextView.getListSelection());// getSelectedItemPosition());

                            String nome = AutoCompletetextView.getText().toString();

                            if ((nome.equals(NomeCliente))) {

                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                String formattedDate = df.format(c.getTime());

                                /*
                                LedStockDB insert_estudo = new LedStockDB(activity);
                                long ID_ESTUDO = insert_estudo.Insert_Estudo(descricao.getText().toString(), String.valueOf(ClientIndex), null, null, formattedDate, "0", "0", null, "1");
                                LedService insertremote = new LedService();
                                insertremote.InsertEstudoRemote(ID_ESTUDO);
                                */

                                Intent intent = new Intent();
                                intent.setAction("REFRESH_ORCAMENTOS");
                                getActivity().sendBroadcast(intent);

                                Toast.makeText(getActivity(), "Orçamento criado com Sucesso !", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getActivity(), "Os campos não podem ser vazios !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Dialog_Orcamento_fragment.this.getDialog().cancel();
                        }
                    });

            builder.setTitle("Inserir Orçamento");


        }    else if (action.equals("add_with_id")) {
            /*

            TextView textview = (TextView) view.findViewById(R.id.campo_cliente);
            textview.setVisibility(View.GONE);

            AutoCompleteTextView AutoCompletetextView = (AutoCompleteTextView) view.findViewById(R.id.autocompletecliente);
            AutoCompletetextView.setVisibility(View.GONE);


            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(R.string.salvar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            EditText descricao = (EditText) view.findViewById(R.id.nomeestudo);
                            //Spinner spinner = (Spinner) view.findViewById(R.id.spinner_clientes);
                            //String item = ListIndexClients.get(spinner.getSelectedItemPosition());


                            //String item = ListIndexClients.get(AutoCompletetextView.getListSelection());// getSelectedItemPosition());

                            //String nome = AutoCompletetextView.getText().toString();

                            if (descricao.getText().toString().trim().length() != 0) {

                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                String formattedDate = df.format(c.getTime());

                                LedStockDB insert_estudo = new LedStockDB(activity);
                                long ID_ESTUDO = insert_estudo.Insert_Estudo(descricao.getText().toString(), String.valueOf(ID_CLIENTE), null, null, formattedDate, "0", "0", null, "1");
                                LedService insertremote = new LedService();
                                insertremote.InsertEstudoRemote(ID_ESTUDO);

                                Intent intent = new Intent();
                                intent.setAction("REFRESH_ORCAMENTOS");
                                //intent.putExtra("ACTION", "ADD");
                                getActivity().sendBroadcast(intent);

                                Toast.makeText(getActivity(), "Orçamento criado com Sucesso !", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getActivity(), "Os campos não podem ser vazios !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }                          )
                    .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Dialog_Orcamento_fragment.this.getDialog().cancel();
                        }
                    });

            builder.setTitle("Inserir Orçamento");
        */
        }


        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}

