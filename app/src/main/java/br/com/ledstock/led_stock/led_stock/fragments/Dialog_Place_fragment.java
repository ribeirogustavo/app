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

public class Dialog_Place_fragment extends DialogFragment {

    private static String action;
    private static long ID_PLACE;
    private static Activity activity;

    public Dialog_Place_fragment() {
        // Required empty public constructor
    }

    public static Dialog_Place_fragment newInstance() {
        Dialog_Place_fragment fragment = new Dialog_Place_fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static void show(FragmentManager fm, long act, Activity context) {

        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("add_place");

        activity = context;

        if (act == 0) {
            action = "add";
        } else {
            ID_PLACE = act;
            action = "edit";
        }

        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        Dialog_Place_fragment frag = new Dialog_Place_fragment();
        //frag.callback = callback;
        frag.show(ft, "add_place");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dialog_place, container, false);

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.fragment_dialog_place, null);

        if (action == "add") {
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(R.string.salvar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            EditText ambiente = (EditText) view.findViewById(R.id.ambiente);

                            if ((ambiente.getText().toString().trim().length() != 0)) {

                                LedStockDB insert_place = new LedStockDB(activity);
                                long ID_PLACE = insert_place.Insert_Place(ambiente.getText().toString(), null, null);
                                LedService insertremote = new LedService();
                                insertremote.InsertAmbienteRemote(ID_PLACE);

                                Intent intent = new Intent();
                                intent.setAction("REFRESH_AMBIENTES");
                                getActivity().sendBroadcast(intent);

                                Toast.makeText(getActivity(), "Ambiente Criado com Sucesso !", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "O campo não pode ser vazio !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Dialog_Place_fragment.this.getDialog().cancel();
                        }
                    });

            builder.setTitle("Inserir Ambiente");

        } else if (action == "edit") {

            EditText ambiente = (EditText) view.findViewById(R.id.ambiente);

            LedStockDB search_ambiente = new LedStockDB(activity);
            Cursor c = search_ambiente.SelectAmbienteByID(String.valueOf(ID_PLACE));
            ambiente.setText(c.getString(c.getColumnIndex("descricao")));
            c.close();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(R.string.salvar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            EditText ambiente = (EditText) view.findViewById(R.id.ambiente);


                            if ((ambiente.getText().toString().trim().length() != 0)) {

                                LedStockDB update_ambiente = new LedStockDB(activity);
                                update_ambiente.Update_Ambiente(String.valueOf(ID_PLACE), ambiente.getText().toString(), "1");
                                LedService updateremote = new LedService();
                                updateremote.UpdateAmbienteRemote(ID_PLACE);

                                Intent intent_broadcast = new Intent();
                                intent_broadcast.setAction("REFRESH_AMBIENTES");
                                //intent_broadcast.putExtra("ACTION", "EDIT");
                                getActivity().sendBroadcast(intent_broadcast);

                                activity.finish();
                                Intent intent = new Intent(activity, Activity_content.class);
                                intent.putExtra("ambiente", Integer.parseInt(String.valueOf(ID_PLACE)));
                                startActivity(intent);

                                Toast.makeText(getActivity(), "Ambiente Editado com Sucesso !", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "O campo não pode ser vazio !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Dialog_Place_fragment.this.getDialog().cancel();
                        }
                    });

            builder.setTitle("Editar Ambiente");
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
