package br.com.ledstock.led_stock.led_stock.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.services.LedService;

public class Dialog_Itens_Of_Orcamento extends DialogFragment {

    private static long ID_ORCAMENTO;
    private static Activity activity;

    private static long LEDIndex;
    private String DescricaoLED;
    private long HandsOnIndex;
    private String DescricaoHandsOn;
    private View view_frag;

    AutoCompleteTextView AutoCompletetextView;

    public Dialog_Itens_Of_Orcamento() {
        // Required empty public constructor
    }

    public static Dialog_Itens_Of_Orcamento newInstance() {
        Dialog_Itens_Of_Orcamento fragment = new Dialog_Itens_Of_Orcamento();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static void show(FragmentManager fm, long id, Activity context) {

        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("add_itens_of_orcamento");

        activity = context;

        ID_ORCAMENTO = id;

        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        Dialog_Itens_Of_Orcamento frag = new Dialog_Itens_Of_Orcamento();
        //frag.callback = callback;
        frag.show(ft, "add_itens_of_orcamento");
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

        final View view = inflater.inflate(R.layout.fragment_add_itens_of_orcamento, null);
        view_frag = view;
        addListenerOnRadio();
        AutoCompletetextView = (AutoCompleteTextView) view.findViewById(R.id.autocompleteled);

        setAdapter(0);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.salvar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {


                        RadioButton led = (RadioButton) view_frag.findViewById(R.id.radio_solucao_led);

                        if (led.isChecked()) {

                            EditText quantidade = (EditText) view_frag.findViewById(R.id.quantidade);
                            EditText desconto =   (EditText) view_frag.findViewById(R.id.descount_orcamento);


                            AutoCompleteTextView AutoCompletetextView = (AutoCompleteTextView) view_frag.findViewById(R.id.autocompleteled);

                            String descricao = AutoCompletetextView.getText().toString();

                            if (descricao.equals(DescricaoLED)
                                    && (quantidade.getText().toString().trim().length() != 0)) {

                                String quant = quantidade.getText().toString();
                                String desc = desconto.getText().toString();


                                LedStockDB insert_itens = new LedStockDB(activity);
                                Cursor c_value = insert_itens.SelectLEDByID(java.lang.String.valueOf(LEDIndex));
                                Double valor = c_value.getDouble(c_value.getColumnIndex("valor"));

                                long ID_ITENS = insert_itens.Insert_Itens_Of_Orcamento(null,
                                        java.lang.String.valueOf(ID_ORCAMENTO), null,
                                        java.lang.String.valueOf(LEDIndex), null,
                                        1,
                                        null, null,
                                        quant,
                                        java.lang.String.valueOf(valor),
                                        desc,
                                        "1");

                                LedService insertremote = new LedService();
                                insertremote.InsertItensOfOrcamentoRemote(ID_ITENS, ID_ORCAMENTO);

                                Intent intent = new Intent();
                                intent.setAction("REFRESH_ITENS_ORCAMENTO");
                                getActivity().sendBroadcast(intent);

                                Toast.makeText(getActivity(), "LED Inserido com Sucesso !", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getActivity(), "Os campos não podem ser vazios !", Toast.LENGTH_SHORT).show();
                            }


                        } else {

                            EditText quantidade = (EditText) view_frag.findViewById(R.id.quantidade);
                            EditText desconto =   (EditText) view_frag.findViewById(R.id.descount_orcamento);

                            AutoCompleteTextView AutoCompletetextView = (AutoCompleteTextView) view_frag.findViewById(R.id.autocompleteled);

                            String descricao = AutoCompletetextView.getText().toString();

                            if (descricao.equals(DescricaoHandsOn)
                                    && (quantidade.getText().toString().trim().length() != 0)) {

                                String quant = quantidade.getText().toString();
                                String desc = desconto.getText().toString();

                                LedStockDB insert_itens = new LedStockDB(activity);
                                Cursor c_value = insert_itens.SelectHandsOnByID(java.lang.String.valueOf(HandsOnIndex));
                                Double valor = c_value.getDouble(c_value.getColumnIndex("valor"));

                                long ID_ITENS = insert_itens.Insert_Itens_Of_Orcamento(null,
                                        java.lang.String.valueOf(ID_ORCAMENTO), null,
                                        null, null,
                                        2,
                                        java.lang.String.valueOf(HandsOnIndex), null,
                                        quant,
                                        java.lang.String.valueOf(valor),
                                        desc,
                                        "1");

                                LedService insertremote = new LedService();
                                insertremote.InsertItensOfOrcamentoRemote(ID_ITENS, ID_ORCAMENTO);

                                Intent intent = new Intent();
                                intent.setAction("REFRESH_ITENS_ORCAMENTO");
                                getActivity().sendBroadcast(intent);

                                Toast.makeText(getActivity(), "Mão de Obra Inserida com Sucesso !", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getActivity(), "Os campos não podem ser vazios !", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog_Itens_Of_Orcamento.this.getDialog().cancel();
                    }
                });

        builder.setTitle("Inserir itens");

        return builder.create();
    }

    private void setAdapter(int radio) {
        if (radio == 0) {

            LedStockDB db = new LedStockDB(getActivity());
            Cursor lampadas = db.Select_ListLEDSAutoComplete();

            final SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    lampadas,
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
                    DescricaoLED = descricao;
                    AutoCompletetextView.setSelection(AutoCompletetextView.getText().length());
                    LEDIndex = id;
                }
            });

            // Set the CursorToStringConverter, to provide the labels for the
            // choices to be displayed in the AutoCompleteTextView.
            mAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
                public String convertToString(Cursor cursor) {
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
                    Cursor cursor = db.getMatchingLED((constraint != null ? constraint.toString() : null));
                    return cursor;
                }
            });

        } else {

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

        }
    }

    public void addListenerOnRadio() {

        RadioGroup radioGroup = (RadioGroup) view_frag.findViewById(R.id.radio_orcamento);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

                TextView textView = (TextView) view_frag.findViewById(R.id.choose_radio_orcamento);
                EditText textViewQuantidade = (EditText) view_frag.findViewById(R.id.quantidade);
                EditText textViewDescount = (EditText) view_frag.findViewById(R.id.descount_orcamento);

                LEDIndex = 0;
                DescricaoLED = null;
                HandsOnIndex = 0;
                DescricaoHandsOn = null;

                textViewQuantidade.setText("");
                textViewDescount.setText("");

                RadioButton led = (RadioButton) view_frag.findViewById(R.id.radio_solucao_led);
                RadioButton maodeobra = (RadioButton) view_frag.findViewById(R.id.radio_mao_de_obra);

                if (led.isChecked()) {
                    textView.setText(R.string.led);
                    setAdapter(0);
                } else if (maodeobra.isChecked()) {
                    textView.setText(R.string.maodeobra);
                    setAdapter(1);
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}

