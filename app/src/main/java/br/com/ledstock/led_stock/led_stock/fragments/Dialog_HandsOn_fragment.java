package br.com.ledstock.led_stock.led_stock.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.activity.Activity_content;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.services.LedService;

public class Dialog_HandsOn_fragment extends DialogFragment {

    private static String action;
    private static long ID_HANDSON;
    private static Activity activity;

    public Dialog_HandsOn_fragment() {
        // Required empty public constructor
    }

    public static Dialog_HandsOn_fragment newInstance() {
        Dialog_HandsOn_fragment fragment = new Dialog_HandsOn_fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static void show(FragmentManager fm, long act, Activity context) {

        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("add_handson");

        activity = context;

        if (act == 0) {
            action = "add";
        } else {
            ID_HANDSON = act;
            action = "edit";
        }

        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        Dialog_HandsOn_fragment frag = new Dialog_HandsOn_fragment();
        //frag.callback = callback;
        frag.show(ft, "add_handson");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dialog_handson, container, false);

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.fragment_dialog_handson, null);

        if (action == "add") {
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(R.string.salvar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            EditText descricao = (EditText) view.findViewById(R.id.maodeobra);
                            EditText valormaodeobra = (EditText) view.findViewById(R.id.valormaodeobra);

                            if ((descricao.getText().toString().trim().length() != 0)
                                    && (valormaodeobra.getText().toString().trim().length() != 0)) {

                                LedStockDB insert_handson = new LedStockDB(activity);
                                long ID_HANDSON = insert_handson.Insert_HandsOn(descricao.getText().toString(), Double.parseDouble(valormaodeobra.getText().toString()), null, null);
                                LedService insertremote = new LedService();
                                insertremote.InsertHandsOnRemote(ID_HANDSON);

                                Intent intent = new Intent();
                                intent.setAction("REFRESH_HANDSON");
                                getActivity().sendBroadcast(intent);

                                Toast.makeText(getActivity(), "Mão de Obra Criada com Sucesso !", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Os campos não podem ser vazios !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Dialog_HandsOn_fragment.this.getDialog().cancel();
                        }
                    });

            builder.setTitle("Inserir Mão de Obra");

        } else if (action == "edit") {

            EditText descricao = (EditText) view.findViewById(R.id.maodeobra);
            EditText valormaodeobra = (EditText) view.findViewById(R.id.valormaodeobra);

            LedStockDB search_handson = new LedStockDB(activity);
            Cursor c = search_handson.SelectHandsOnByID(String.valueOf(ID_HANDSON));
            descricao.setText(c.getString(c.getColumnIndex("descricao")));
            valormaodeobra.setText(c.getString(c.getColumnIndex("valor")));
            c.close();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(R.string.salvar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            EditText descricao = (EditText) view.findViewById(R.id.maodeobra);
                            EditText valormaodeobra = (EditText) view.findViewById(R.id.valormaodeobra);

                            if ((descricao.getText().toString().trim().length() != 0)
                                    && (valormaodeobra.getText().toString().trim().length() != 0)) {

                                String desc = descricao.getText().toString();
                                double value = Double.parseDouble(valormaodeobra.getText().toString());

                                LedStockDB update_handson = new LedStockDB(activity);
                                update_handson.Update_HandsOn(String.valueOf(ID_HANDSON), desc,value, "1");
                                LedService updateremote = new LedService();
                                updateremote.UpdateHandsOnRemote(ID_HANDSON);

                                Intent intent_broadcast = new Intent();
                                intent_broadcast.setAction("REFRESH_HANDSON");
                                //intent_broadcast.putExtra("ACTION", "EDIT");
                                getActivity().sendBroadcast(intent_broadcast);

                                activity.finish();
                                Intent intent = new Intent(activity, Activity_content.class);
                                intent.putExtra("handson", Integer.parseInt(String.valueOf(ID_HANDSON)));
                                startActivity(intent);

                                Toast.makeText(getActivity(), "Mão de Obra Editada com Sucesso !", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Os campos não podem ser vazios !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Dialog_HandsOn_fragment.this.getDialog().cancel();
                        }
                    });

            builder.setTitle("Editar Mão de Obra");
        }

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
