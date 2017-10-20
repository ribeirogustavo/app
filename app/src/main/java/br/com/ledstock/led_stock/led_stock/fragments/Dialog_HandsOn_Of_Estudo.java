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
import android.widget.TextView;
import android.widget.Toast;

import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.services.LedService;

/**
 * Created by Gustavo on 07/11/2016.
 */

public class Dialog_HandsOn_Of_Estudo extends DialogFragment {

    private static long ID_AMBIENTE_ESTUDO;
    private static long ID_ESTUDO;
    private static Activity activity;

    private long HandsOnIndex;
    private String DescricaoHandsOn;

    public Dialog_HandsOn_Of_Estudo() {
        // Required empty public constructor
    }

    public static Dialog_HandsOn_Of_Estudo newInstance() {
        Dialog_HandsOn_Of_Estudo fragment = new Dialog_HandsOn_Of_Estudo();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static void show(FragmentManager fm, long id, long id_estudo, Activity context) {

        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("add_handson_of_estudo");

        activity = context;

        ID_AMBIENTE_ESTUDO = id;
        ID_ESTUDO = id_estudo;

        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        Dialog_HandsOn_Of_Estudo frag = new Dialog_HandsOn_Of_Estudo();
        //frag.callback = callback;
        frag.show(ft, "add_handson_of_estudo");
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

        final View view = inflater.inflate(R.layout.fragment_add_handson_estudo, null);

        final AutoCompleteTextView AutoCompletetextView = (AutoCompleteTextView) view.findViewById(R.id.autocompletehandson);

        LedStockDB db = new LedStockDB(getActivity());
        Cursor HandsOn = db.Select_ListHandsOnAutoComplete();

        final SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_list_item_1,
                HandsOn,
                new String[]{"descricao"},
                new int[]{android.R.id.text1},
                0
        );

        AutoCompletetextView.setAdapter(mAdapter);

        // Set an OnItemClickListener, to update dependent fields when
        // a choice is made in the AutoCompleteTextView.
        AutoCompletetextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                // Get the cursor, positioned to the corresponding row in the
                // result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                // Get the state's capital from this row in the database.
                String descricao = cursor.getString(cursor.getColumnIndexOrThrow("descricao"));
                // Update the parent class's TextView
                AutoCompletetextView.setText(descricao);
                DescricaoHandsOn = descricao;
                AutoCompletetextView.setSelection(AutoCompletetextView.getText().length());
                HandsOnIndex = id;
            }
        });

        // Set the CursorToStringConverter, to provide the labels for the
        // choices to be displayed in the AutoCompleteTextView.
        mAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            public String convertToString(android.database.Cursor cursor) {
                // Get the label for this row out of the "state" column
                final int columnIndex = cursor.getColumnIndexOrThrow("descricao");
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
                Cursor cursor = db.getMatchingHandsOn((constraint != null ? constraint.toString() : null));
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

                        AutoCompleteTextView AutoCompletetextView = (AutoCompleteTextView) view.findViewById(R.id.autocompletehandson);

                        TextView quant = (TextView) view.findViewById(R.id.quantidade);

                        String descricao = AutoCompletetextView.getText().toString();

                        if (descricao.equals(DescricaoHandsOn)
                                && (quant.getText().toString().trim().length() != 0)) {

                            LedStockDB insert_itens = new LedStockDB(activity);
                            long ID_ITENS = insert_itens.Insert_Itens_Of_Estudo(null, String.valueOf(ID_ESTUDO), null,
                                    String.valueOf(ID_AMBIENTE_ESTUDO), null,
                                    2, String.valueOf(HandsOnIndex), null, null, null, null, null,
                                    quant.getText().toString(), "0", "1");

                            LedService insertremote = new LedService();
                            insertremote.InsertItensOfEstudoRemote(ID_ITENS, ID_AMBIENTE_ESTUDO, ID_ESTUDO);

                            Intent intent = new Intent();
                            intent.setAction("REFRESH_ITENS_HANDSON");
                            getActivity().sendBroadcast(intent);

                            Toast.makeText(getActivity(), "Mão de Obra Inserida com Sucesso !", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "O campo não pode ser vazio !", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog_HandsOn_Of_Estudo.this.getDialog().cancel();
                    }
                });

        builder.setTitle("Inserir Mão de Obra");

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}

