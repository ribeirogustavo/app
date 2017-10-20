package br.com.ledstock.led_stock.led_stock.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.ledstock.led_stock.R;

/**
 * Created by Gustavo on 03/11/2016.
 */
public class DeletarAmbienteOfEstudo extends DialogFragment {

    private DeletarAmbienteOfEstudo.Callback callback;

    public interface Callback {
        void onClickYes();
    }

    public DeletarAmbienteOfEstudo() {
        // Required empty public constructor
    }

    public static void show(FragmentManager fm, DeletarAmbienteOfEstudo.Callback callback) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("deletar_AmbienteOfEstudo");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DeletarAmbienteOfEstudo frag = new DeletarAmbienteOfEstudo();
        frag.callback = callback;
        frag.show(ft, "deletar_AmbienteOfEstudo");
    }

    public static DeletarAmbienteOfEstudo newInstance() {
        DeletarAmbienteOfEstudo fragment = new DeletarAmbienteOfEstudo();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DialogInterface.OnClickListener dialogClickListener = new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if (callback != null) {
                                    // Retorno do CallBack
                                    callback.onClickYes();
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Deletar Ambiente deste Estudo ?");
        builder.setPositiveButton("Sim", dialogClickListener);
        builder.setNegativeButton("Não", dialogClickListener);
        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_deletar_ambiente_estudo_dialog, container, false);
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