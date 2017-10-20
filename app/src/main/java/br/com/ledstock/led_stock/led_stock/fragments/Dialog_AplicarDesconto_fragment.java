package br.com.ledstock.led_stock.led_stock.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.services.LedService;

public class Dialog_AplicarDesconto_fragment extends DialogFragment {

    private static Activity activity;
    private static String ID_ORCAMENTO;
    private static long item;

    public Dialog_AplicarDesconto_fragment() {
        // Required empty public constructor
    }

    public static Dialog_AplicarDesconto_fragment newInstance() {
        Dialog_AplicarDesconto_fragment fragment = new Dialog_AplicarDesconto_fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static void show(FragmentManager fm, String id_orcamento, Activity context) {

        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("add_desconto");

        activity = context;

        ID_ORCAMENTO = id_orcamento;

        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        Dialog_AplicarDesconto_fragment frag = new Dialog_AplicarDesconto_fragment();
        //frag.callback = callback;
        frag.show(ft, "add_desconto");
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

        final View view = inflater.inflate(R.layout.fragment_aplicar_desconto, null);

        final EditText desconto = (EditText) view.findViewById(R.id.desconto);

        LedStockDB db = new LedStockDB(getActivity());

        final Long id_orcamento_remote = db.SelectOrcamentoRemoteIDById(ID_ORCAMENTO);
        Cursor c = db.Select_ListOfOrcamentoDescount(String.valueOf(ID_ORCAMENTO), String.valueOf(id_orcamento_remote));

        if (c != null) {
            if (c.getCount() > 0) {
                String desc = c.getString(c.getColumnIndex("descount"));
                if (desc != null) {
                    desconto.setText(desc);
                    item = c.getLong(c.getColumnIndex("_id"));
                }
            }
        }

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.salvar, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        LedStockDB db = new LedStockDB(getActivity());
                        LedService updateremote = new LedService();

                        String descount = desconto.getText().toString();
                        String id_orc_rem = null;

                        if (!descount.equals("")) {
                            if (item != 0) {
                                db.DeleteItensOfOrcamento(String.valueOf(item));
                                updateremote.DeleteItensOfOrcamentoRemote(String.valueOf(item));
                            }

                            if (id_orcamento_remote != 0){
                                id_orc_rem = String.valueOf(id_orcamento_remote);
                            }
                            Long id_itens = db.Insert_Itens_Of_Orcamento(null,
                                    ID_ORCAMENTO, id_orc_rem,
                                    null, null,
                                    3,
                                    null, null,
                                    "0", "0",
                                    descount, "1");

                            updateremote.InsertItensOfOrcamentoRemote(id_itens, Long.parseLong(ID_ORCAMENTO));
                        }
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog_AplicarDesconto_fragment.this.getDialog().cancel();
                    }
                });

        builder.setTitle("Aplicar Desconto");

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
