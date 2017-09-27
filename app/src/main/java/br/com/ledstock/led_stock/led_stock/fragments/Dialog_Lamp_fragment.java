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
import android.support.v7.app.AlertDialog;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.activity.Activity_content;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.services.LedService;

public class Dialog_Lamp_fragment extends DialogFragment {

    private static String action;
    private static long ID_LAMP;
    private static Activity activity;
    private TextWatcher potMask;

    public Dialog_Lamp_fragment() {
        // Required empty public constructor
    }

    public static Dialog_Lamp_fragment newInstance() {
        Dialog_Lamp_fragment fragment = new Dialog_Lamp_fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static void show(FragmentManager fm, long act, Activity context) {

        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("add_lamp_actual");

        activity = context;

        if (act == 0) {
            action = "add";
        } else {
            ID_LAMP = act;
            action = "edit";
        }

        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        Dialog_Lamp_fragment frag = new Dialog_Lamp_fragment();
        //frag.callback = callback;
        frag.show(ft, "add_lamp_actual");
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

        final View view = inflater.inflate(R.layout.fragment_add_lamp, null);

        if (action == "add") {
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(R.string.salvar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            EditText descricao = (EditText) view.findViewById(R.id.descricao);
                            EditText potencia = (EditText) view.findViewById(R.id.potencia);

                            if ((descricao.getText().toString().trim().length() != 0)
                                    && (potencia.getText().toString().trim().length() != 0)) {

                                LedStockDB insert_lamp = new LedStockDB(activity);
                                long ID_LAMP = insert_lamp.Insert_Lamp(descricao.getText().toString(), potencia.getText().toString(), null, null);
                                LedService insertremote = new LedService();
                                insertremote.InsertLampRemote(ID_LAMP);

                                Intent intent = new Intent();
                                intent.setAction("REFRESH_LAMPS");
                                //intent.putExtra("ACTION", "ADD");
                                getActivity().sendBroadcast(intent);

                                Toast.makeText(getActivity(), "Lampada Criada com Sucesso !", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Os campos não podem ser vazios !", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Dialog_Lamp_fragment.this.getDialog().cancel();
                        }
                    });

            builder.setTitle("Inserir Lampada");

        } else if (action == "edit") {

            EditText desc = (EditText) view.findViewById(R.id.descricao);
            EditText pot = (EditText) view.findViewById(R.id.potencia);

            LedStockDB search_lamp = new LedStockDB(activity);
            Cursor c = search_lamp.SelectLampByID(String.valueOf(ID_LAMP));

            desc.setText(c.getString(c.getColumnIndex("descricao")));
            pot.setText(c.getString(c.getColumnIndex("potencia")));

            c.close();


            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(R.string.salvar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            EditText descricao = (EditText) view.findViewById(R.id.descricao);
                            EditText potencia = (EditText) view.findViewById(R.id.potencia);

                            if ((descricao.getText().toString().trim().length() != 0)
                                    && (potencia.getText().toString().trim().length() != 0)) {

                                LedStockDB update_lamp = new LedStockDB(activity);
                                update_lamp.Update_Lamp(String.valueOf(ID_LAMP), descricao.getText().toString(), potencia.getText().toString(),"1");
                                LedService updateremote = new LedService();
                                updateremote.UpdateLampRemote(ID_LAMP);

                                Intent intent_broadcast = new Intent();
                                intent_broadcast.setAction("REFRESH_LAMPS");
                                //intent_broadcast.putExtra("ACTION", "EDIT");
                                getActivity().sendBroadcast(intent_broadcast);

                                activity.finish();
                                Intent intent = new Intent(activity, Activity_content.class);
                                intent.putExtra("lamp", Integer.parseInt(String.valueOf(ID_LAMP)));
                                startActivity(intent);

                                Toast.makeText(getActivity(), "Lampada Editada com Sucesso !", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Os campos não podem ser vazios !", Toast.LENGTH_SHORT).show();
                            }


                        }
                    })
                    .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Dialog_Lamp_fragment.this.getDialog().cancel();
                        }
                    });

            builder.setTitle("Editar Lampada");
        }

        return builder.create();
    }

    /*

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_lamp, container, false);
    }*/

}
