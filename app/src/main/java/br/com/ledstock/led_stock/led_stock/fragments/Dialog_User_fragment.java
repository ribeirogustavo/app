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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.activity.Activity_content;
import br.com.ledstock.led_stock.led_stock.domain.Array_Users;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.services.LedService;

public class Dialog_User_fragment extends DialogFragment {

    private static String action;
    private static long ID_USER;
    private static Activity activity;

    public Dialog_User_fragment() {
        // Required empty public constructor
    }

    public static Dialog_User_fragment newInstance() {
        Dialog_User_fragment fragment = new Dialog_User_fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static void show(FragmentManager fm, long act, Activity context) {

        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("add_user");

        activity = context;

        if (act == 0) {
            action = "add";
        } else {
            ID_USER = act;
            action = "edit";
        }

        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        Dialog_User_fragment frag = new Dialog_User_fragment();
        //frag.callback = callback;
        frag.show(ft, "add_user");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dialog_user, container, false);

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.fragment_dialog_user, null);

        if (action == "add") {
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(R.string.salvar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            EditText nome = (EditText) view.findViewById(R.id.nome);
                            EditText usuario = (EditText) view.findViewById(R.id.usuario);
                            EditText senha = (EditText) view.findViewById(R.id.senha);
                            CheckBox acesso = (CheckBox) view.findViewById(R.id.acesso);

                            if ((nome.getText().toString().trim().length() != 0)
                                    && (usuario.getText().toString().trim().length() != 0)
                                    && (senha.getText().toString().trim().length() != 0)) {

                                Array_Users users = new Array_Users();
                                users.id = null;
                                users.nome = nome.getText().toString();
                                users.usuario = usuario.getText().toString();
                                users.senha = senha.getText().toString();
                                if (acesso.isChecked()) {
                                    users.acesso = "1";
                                } else {
                                    users.acesso = "0";
                                }
                                LedStockDB insert_user = new LedStockDB(activity);
                                long ID_USER = insert_user.Insert_User(users);
                                LedService insertremote = new LedService();
                                insertremote.InsertUserRemote(ID_USER);

                                Intent intent = new Intent();
                                intent.setAction("REFRESH_USERS");
                                getActivity().sendBroadcast(intent);

                                Toast.makeText(getActivity(), "Usuário Criado com Sucesso !", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Os campos não podem ser vazios !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Dialog_User_fragment.this.getDialog().cancel();
                        }
                    });

            builder.setTitle("Inserir um Usuário");

        } else if (action == "edit") {

            EditText nome = (EditText) view.findViewById(R.id.nome);
            EditText usuario = (EditText) view.findViewById(R.id.usuario);
            CheckBox acesso = (CheckBox) view.findViewById(R.id.acesso);

            LedStockDB search_user = new LedStockDB(activity);
            Cursor c = search_user.SelectUserByID(String.valueOf(ID_USER));

            nome.setText(c.getString(c.getColumnIndex("nome")));
            usuario.setText(c.getString(c.getColumnIndex("usuario")));
            if (c.getString(c.getColumnIndex("acesso")).equals("1")) {
                acesso.setChecked(true);
            } else {
                acesso.setChecked(false);
            }
            c.close();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(R.string.salvar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            EditText nome = (EditText) view.findViewById(R.id.nome);
                            EditText usuario = (EditText) view.findViewById(R.id.usuario);
                            EditText senha = (EditText) view.findViewById(R.id.senha);
                            CheckBox acesso = (CheckBox) view.findViewById(R.id.acesso);

                            if ((nome.getText().toString().trim().length() != 0)
                                    && (usuario.getText().toString().trim().length() != 0)
                                    && (senha.getText().toString().trim().length() != 0)) {

                                String name = nome.getText().toString();
                                String user = usuario.getText().toString();
                                String pass = senha.getText().toString();

                                String access = null;

                                if (acesso.isChecked()) {
                                    access = "1";
                                } else {
                                    access = "0";
                                }

                                LedStockDB update_user = new LedStockDB(activity);
                                update_user.Update_User(String.valueOf(ID_USER), name, user, pass, access, "1");
                                LedService updateremote = new LedService();
                                updateremote.UpdateUserRemote(ID_USER);

                                Intent intent_broadcast = new Intent();
                                intent_broadcast.setAction("REFRESH_USERS");
                                getActivity().sendBroadcast(intent_broadcast);

                                activity.finish();
                                Intent intent = new Intent(activity, Activity_content.class);
                                intent.putExtra("usuario", Integer.parseInt(String.valueOf(ID_USER)));
                                startActivity(intent);

                                Toast.makeText(getActivity(), "Usuário Editado com Sucesso !", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Os campos não podem ser vazios !", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Dialog_User_fragment.this.getDialog().cancel();
                        }
                    });

            builder.setTitle("Editar Usuário");
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
