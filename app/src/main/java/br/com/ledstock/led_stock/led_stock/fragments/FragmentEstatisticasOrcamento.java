package br.com.ledstock.led_stock.led_stock.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.adapter.EstatisticaAdapter;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.domain.Stat;

import static java.lang.Double.isNaN;

public class FragmentEstatisticasOrcamento extends android.support.v4.app.Fragment {

    private View view_frag;
    private Long ID_ORCAMENTO;
    RecyclerView recyclerView;

    public FragmentEstatisticasOrcamento() {
        // Required empty public constructor
    }

    public static FragmentEstatisticasOrcamento newInstance() {
        FragmentEstatisticasOrcamento fragment = new FragmentEstatisticasOrcamento();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // mParam1 = getArguments().getString(ARG_PARAM1);
            // mParam2 = getArguments().getString(ARG_PARAM2);
            ID_ORCAMENTO = getParentFragment().getArguments().getLong("id_orcamento", 0);
        }

        //Registra o Receiver para Refresh em Lamps
        getActivity().registerReceiver(RefreshEstatisticasOrcamento, new IntentFilter("REFRESH_ESTATISTICAS_ORCAMENTO"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int qnt_leds = 0;
        double valor_leds = 0;
        double desconto_leds = 0;
        double valor_leds_com_desconto = 0;

        int qnt_hands = 0;
        double valor_hands = 0;
        double desconto_hands = 0;
        double valor_hands_com_desconto = 0;

        double desconto_total = 0;
        double valor_total = 0;

        View view = inflater.inflate(R.layout.fragment_statistics_orcamento, container, false);

        view_frag = view;

        LedStockDB db = new LedStockDB(getActivity());

        String ID_ORCAMENTO_REMOTE = String.valueOf(db.SelectOrcamentoRemoteIDById(String.valueOf(ID_ORCAMENTO)));
        Cursor c;

        c = db.Select_ListOfOrcamento(String.valueOf(ID_ORCAMENTO), ID_ORCAMENTO_REMOTE, 1);

        qnt_leds = c.getInt(c.getColumnIndex("quantidade"));
        valor_leds = c.getDouble(c.getColumnIndex("valor_total"));
        desconto_leds = c.getDouble(c.getColumnIndex("total_de_desconto"));
        valor_leds_com_desconto = c.getDouble(c.getColumnIndex("valor_com_desconto"));

        TextView textView_qnt_leds = (TextView) view.findViewById(R.id.qnt_leds);
        textView_qnt_leds.setText(String.valueOf(qnt_leds));

        TextView textView_valor_leds = (TextView) view.findViewById(R.id.valor_leds);
        String valor_leds_text = "R$ "+ String.format(Locale.getDefault(),"%.2f", valor_leds); //String.valueOf(valor_leds);
        textView_valor_leds.setText(valor_leds_text);

        TextView textView_desconto_leds = (TextView) view.findViewById(R.id.desconto_leds);
        String valor_desconto_text = "R$ "+  String.format(Locale.getDefault(),"%.2f", desconto_leds); //String.valueOf(desconto_leds);
        textView_desconto_leds.setText(valor_desconto_text);

        TextView textView_valor_led_com_desconto = (TextView) view.findViewById(R.id.valor_led_desconto);
        String valor_valor_led_text = "R$ "+ String.format(Locale.getDefault(),"%.2f", valor_leds_com_desconto);  //String.valueOf(valor_leds_com_desconto);
        textView_valor_led_com_desconto.setText(valor_valor_led_text);

        c = db.Select_ListOfOrcamento(String.valueOf(ID_ORCAMENTO), ID_ORCAMENTO_REMOTE, 2);

        qnt_hands = c.getInt(c.getColumnIndex("quantidade"));
        valor_hands = c.getDouble(c.getColumnIndex("valor_total"));
        desconto_hands = c.getDouble(c.getColumnIndex("total_de_desconto"));
        valor_hands_com_desconto = c.getDouble(c.getColumnIndex("valor_com_desconto"));

        TextView textView_qnt_hands = (TextView) view.findViewById(R.id.qnt_mao_de_obra);
        textView_qnt_hands.setText(String.valueOf(qnt_hands));

        TextView textView_valor_hands = (TextView) view.findViewById(R.id.valor_mao);
        String valor_hands_text = "R$ "+ String.format(Locale.getDefault(),"%.2f", valor_hands); //String.valueOf(valor_leds);
        textView_valor_hands.setText(valor_hands_text);

        TextView textView_desconto_hands = (TextView) view.findViewById(R.id.desconto_mao);
        String valor_desconto_text_hands = "R$ "+  String.format(Locale.getDefault(),"%.2f", desconto_hands); //String.valueOf(desconto_leds);
        textView_desconto_hands.setText(valor_desconto_text_hands);

        TextView textView_valor_hands_com_desconto = (TextView) view.findViewById(R.id.valor_mao_desconto);
        String valor_hands_com_desconto_text = "R$ "+ String.format(Locale.getDefault(),"%.2f", valor_hands_com_desconto);  //String.valueOf(valor_leds_com_desconto);
        textView_valor_hands_com_desconto.setText(valor_hands_com_desconto_text);

        TextView textView_valor_total = (TextView) view.findViewById(R.id.valor_total);
        Double valor = valor_leds_com_desconto + valor_hands_com_desconto;
        String valor_valor_text = "R$ "+ String.format(Locale.getDefault(),"%.2f", valor);
        textView_valor_total.setText(valor_valor_text);

        c = db.Select_ListOfOrcamento(String.valueOf(ID_ORCAMENTO), ID_ORCAMENTO_REMOTE, 3);

        if (c.getCount() > 0) {
            desconto_total = c.getDouble(c.getColumnIndex("descount"));
        }else{
            desconto_total = 0;
        }

        TextView textView_desconto_valor_total = (TextView) view.findViewById(R.id.desconto_valor_total);
        String desconto_total_hands_text = String.format(Locale.getDefault(),"%.2f", desconto_total) + "%";
        textView_desconto_valor_total.setText(desconto_total_hands_text);

        TextView textView_valor_total_com_desconto = (TextView) view.findViewById(R.id.valor_total_com_desconto);
        Double val_tot = valor - (valor * desconto_total/100);
        String valor_total_text = "R$ "+ String.format(Locale.getDefault(),"%.2f", val_tot);
        textView_valor_total_com_desconto.setText(valor_total_text);


        return view;
    }

    //BroadCast Receiver para Editar as Lamps Remotamente
    private BroadcastReceiver RefreshEstatisticasOrcamento = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {

            int qnt_leds = 0;
            double valor_leds = 0;
            double desconto_leds = 0;
            double valor_leds_com_desconto = 0;

            int qnt_hands = 0;
            double valor_hands = 0;
            double desconto_hands = 0;
            double valor_hands_com_desconto = 0;

            double desconto_total = 0;
            double valor_total = 0;

            LedStockDB db = new LedStockDB(getActivity());

            String ID_ORCAMENTO_REMOTE = String.valueOf(db.SelectOrcamentoRemoteIDById(String.valueOf(ID_ORCAMENTO)));
            Cursor c;

            c = db.Select_ListOfOrcamento(String.valueOf(ID_ORCAMENTO), ID_ORCAMENTO_REMOTE, 1);

            qnt_leds = c.getInt(c.getColumnIndex("quantidade"));
            valor_leds = c.getDouble(c.getColumnIndex("valor_total"));
            desconto_leds = c.getDouble(c.getColumnIndex("total_de_desconto"));
            valor_leds_com_desconto = c.getDouble(c.getColumnIndex("valor_com_desconto"));

            TextView textView_qnt_leds = (TextView) view_frag.findViewById(R.id.qnt_leds);
            textView_qnt_leds.setText(String.valueOf(qnt_leds));

            TextView textView_valor_leds = (TextView) view_frag.findViewById(R.id.valor_leds);
            String valor_leds_text = "R$ "+ String.format(Locale.getDefault(),"%.2f", valor_leds); //String.valueOf(valor_leds);
            textView_valor_leds.setText(valor_leds_text);

            TextView textView_desconto_leds = (TextView) view_frag.findViewById(R.id.desconto_leds);
            String valor_desconto_text = "R$ "+  String.format(Locale.getDefault(),"%.2f", desconto_leds); //String.valueOf(desconto_leds);
            textView_desconto_leds.setText(valor_desconto_text);

            TextView textView_valor_led_desconto_leds = (TextView) view_frag.findViewById(R.id.valor_led_desconto);
            String valor_valor_led_text = "R$ "+ String.format(Locale.getDefault(),"%.2f", valor_leds_com_desconto);  //String.valueOf(valor_leds_com_desconto);
            textView_valor_led_desconto_leds.setText(valor_valor_led_text);

            c = db.Select_ListOfOrcamento(String.valueOf(ID_ORCAMENTO), ID_ORCAMENTO_REMOTE, 2);

            qnt_hands = c.getInt(c.getColumnIndex("quantidade"));
            valor_hands = c.getDouble(c.getColumnIndex("valor_total"));
            desconto_hands = c.getDouble(c.getColumnIndex("total_de_desconto"));
            valor_hands_com_desconto = c.getDouble(c.getColumnIndex("valor_com_desconto"));

            TextView textView_qnt_hands = (TextView) view_frag.findViewById(R.id.qnt_mao_de_obra);
            textView_qnt_hands.setText(String.valueOf(qnt_hands));

            TextView textView_valor_hands = (TextView) view_frag.findViewById(R.id.valor_mao);
            String valor_hands_text = "R$ "+ String.format(Locale.getDefault(),"%.2f", valor_hands); //String.valueOf(valor_leds);
            textView_valor_hands.setText(valor_hands_text);

            TextView textView_desconto_hands = (TextView) view_frag.findViewById(R.id.desconto_mao);
            String valor_desconto_text_hands = "R$ "+  String.format(Locale.getDefault(),"%.2f", desconto_hands); //String.valueOf(desconto_leds);
            textView_desconto_hands.setText(valor_desconto_text_hands);

            TextView textView_valor_hands_com_desconto = (TextView) view_frag.findViewById(R.id.valor_mao_desconto);
            String valor_hands_com_desconto_text = "R$ "+ String.format(Locale.getDefault(),"%.2f", valor_hands_com_desconto);  //String.valueOf(valor_leds_com_desconto);
            textView_valor_hands_com_desconto.setText(valor_hands_com_desconto_text);

            TextView textView_valor_total = (TextView) view_frag.findViewById(R.id.valor_total);
            Double valor = valor_leds_com_desconto + valor_hands_com_desconto;
            String valor_valor_text = "R$ "+ String.format(Locale.getDefault(),"%.2f", valor);
            textView_valor_total.setText(valor_valor_text);

            c = db.Select_ListOfOrcamento(String.valueOf(ID_ORCAMENTO), ID_ORCAMENTO_REMOTE, 3);

            if (c.getCount() > 0) {
                desconto_total = c.getDouble(c.getColumnIndex("descount"));
            }else{
                desconto_total = 0;
            }

            TextView textView_desconto_valor_total = (TextView) view_frag.findViewById(R.id.desconto_valor_total);
            String desconto_total_hands_text = String.format(Locale.getDefault(),"%.2f", desconto_total) + "%";
            textView_desconto_valor_total.setText(desconto_total_hands_text);

            TextView textView_valor_total_com_desconto = (TextView) view_frag.findViewById(R.id.valor_total_com_desconto);
            Double val_tot = valor - (valor * desconto_total/100);
            String valor_total_text = "R$ "+ String.format(Locale.getDefault(),"%.2f", val_tot);
            textView_valor_total_com_desconto.setText(valor_total_text);


        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Cancela o Registro de Refresh Lamps
        getActivity().unregisterReceiver(RefreshEstatisticasOrcamento);
    }

}
