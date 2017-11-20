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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.services.LedService;

public class Dialog_Pedido_Orcamento_fragment extends DialogFragment {

    private static Activity activity;
    private static String ID_ORCAMENTO;

    public Dialog_Pedido_Orcamento_fragment() {
        // Required empty public constructor
    }

    public static Dialog_Pedido_Orcamento_fragment newInstance() {
        Dialog_Pedido_Orcamento_fragment fragment = new Dialog_Pedido_Orcamento_fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static void show(FragmentManager fm, String id_orcamento ,Activity context) {

        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("add_pedido");

        activity = context;

        ID_ORCAMENTO = id_orcamento;

        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        Dialog_Pedido_Orcamento_fragment frag = new Dialog_Pedido_Orcamento_fragment();
        //frag.callback = callback;
        frag.show(ft, "add_pedido");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.fragment_pedido, null);

        final RadioButton radio_psm = (RadioButton) view.findViewById(R.id.radio_psm);
        final RadioButton radio_pcm = (RadioButton) view.findViewById(R.id.radio_pcm);
        final RadioButton radio_cancel = (RadioButton) view.findViewById(R.id.radio_cancel);

        LedStockDB db = new LedStockDB(getActivity());
        Cursor pedido = db.SelectOrcamentoByID(ID_ORCAMENTO);

        do{
            String getpedido_psm = pedido.getString(pedido.getColumnIndex("psm"));
            String getpedido_pcm = pedido.getString(pedido.getColumnIndex("pcm"));

            if ((getpedido_pcm.equals("1")) && ((getpedido_psm.equals("0")))){
                radio_pcm.setChecked(true);
            }else if((getpedido_pcm.equals("0")) && ((getpedido_psm.equals("1")))){
                radio_psm.setChecked(true);
            }else{
                radio_psm.setChecked(false);
                radio_pcm.setChecked(false);
            }

        }while (pedido.moveToNext());

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.salvar, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        String formattedDate = df.format(c.getTime());
                        LedStockDB db = new LedStockDB(getActivity());

                        if (radio_pcm.isChecked()) {
                            db.Update_Orcamento(ID_ORCAMENTO, null, "1", null, null, formattedDate, "0", "1", null);
                        } else if (radio_psm.isChecked()) {
                            db.Update_Orcamento(ID_ORCAMENTO, null, "1", null, null, formattedDate, "1", "0", null);
                        } else if (radio_cancel.isChecked()) {
                            db.Update_Orcamento(ID_ORCAMENTO, null, "1", null, null, "", "0", "0", null);
                        }
                        LedService updateremote = new LedService();
                        updateremote.UpdateOrcamentoRemote(Long.parseLong(ID_ORCAMENTO));
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog_Pedido_Orcamento_fragment.this.getDialog().cancel();
                    }
                });

        builder.setTitle("Pedido");

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
