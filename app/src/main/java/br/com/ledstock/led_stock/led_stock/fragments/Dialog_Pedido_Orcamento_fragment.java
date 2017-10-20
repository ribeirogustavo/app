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

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dialog_led, container, false);
    }*/

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

        //builder.setView(view);

        builder.setTitle("Pedido");


        /*
        if (action == "add") {
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)

                    // Add action buttons
                    .setPositiveButton(R.string.salvar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
/*
                            EditText descricao = (EditText) view.findViewById(R.id.led);
                            EditText potencia = (EditText) view.findViewById(R.id.potencia);
                            EditText valor = (EditText) view.findViewById(R.id.valor);
                            EditText valor_revenda = (EditText) view.findViewById(R.id.valor_revenda);

                            if ((descricao.getText().toString().trim().length() != 0)
                                    && (potencia.getText().toString().trim().length() != 0)
                                    && (valor.getText().toString().trim().length() != 0)
                                    && (valor_revenda.getText().toString().trim().length() != 0)) {

                                String desc = descricao.getText().toString();
                                String pot = potencia.getText().toString();
                                double val = Double.parseDouble(valor.getText().toString());
                                double val_rev = Double.parseDouble(valor_revenda.getText().toString());

                                LedStockDB insert_led = new LedStockDB(activity);
                                long ID_LED = insert_led.Insert_LED(desc, pot, val, val_rev, null, null);

                                LedService insertremote = new LedService();
                                insertremote.InsertLEDRemote(ID_LED);

                                Intent intent = new Intent();
                                intent.setAction("REFRESH_LEDS");
                                //intent.putExtra("ACTION", "ADD");
                                getActivity().sendBroadcast(intent);

                                Toast.makeText(getActivity(), "LED Criado com Sucesso !", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Os campos não podem ser vazios !", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Dialog_Pedido_fragment.this.getDialog().cancel();
                        }
                    });

            builder.setTitle("Inserir Solução LED");

        } else if (action == "edit") {

            /*
            EditText desc = (EditText) view.findViewById(R.id.led);
            EditText pot = (EditText) view.findViewById(R.id.potencia);
            EditText val = (EditText) view.findViewById(R.id.valor);
            EditText val_revenda = (EditText) view.findViewById(R.id.valor_revenda);

            LedStockDB search_led = new LedStockDB(activity);
            Cursor c = search_led.SelectLEDByID(String.valueOf(ID_LED));

            desc.setText(c.getString(c.getColumnIndex("descricao")));
            pot.setText(c.getString(c.getColumnIndex("potencia")));
            val.setText(c.getString(c.getColumnIndex("valor")));
            val_revenda.setText(c.getString(c.getColumnIndex("valor_revenda")));

            c.close();


            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(R.string.salvar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {


                            EditText descricao = (EditText) view.findViewById(R.id.led);
                            EditText potencia = (EditText) view.findViewById(R.id.potencia);
                            EditText valor = (EditText) view.findViewById(R.id.valor);
                            EditText valor_revenda = (EditText) view.findViewById(R.id.valor_revenda);

                            if ((descricao.getText().toString().trim().length() != 0)
                                    && (potencia.getText().toString().trim().length() != 0)
                                    && (valor.getText().toString().trim().length() != 0)
                                    && (valor_revenda.getText().toString().trim().length() != 0)) {

                                String desc = descricao.getText().toString();
                                String pot = potencia.getText().toString();
                                double val = Double.parseDouble(valor.getText().toString());
                                double val_rev = Double.parseDouble(valor_revenda.getText().toString());

                                LedStockDB update_led = new LedStockDB(activity);
                                update_led.Update_Led(String.valueOf(ID_LED), desc, pot, val, val_rev, "1");

                                LedService updateremote = new LedService();
                                updateremote.UpdateLEDRemote(ID_LED);

                                Intent intent_broadcast = new Intent();
                                intent_broadcast.setAction("REFRESH_LEDS");
                                getActivity().sendBroadcast(intent_broadcast);

                                activity.finish();
                                Intent intent = new Intent(activity, Activity_content.class);
                                intent.putExtra("led", Integer.parseInt(String.valueOf(ID_LED)));
                                startActivity(intent);

                                Toast.makeText(getActivity(), "LED Editado com Sucesso !", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Os campos não podem ser vazios !", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Dialog_Pedido_fragment.this.getDialog().cancel();
                        }
                    });

            builder.setTitle("Editar Solução LED");
        }
        */

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
