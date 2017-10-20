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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.adapter.EstatisticaAdapter;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.domain.Stat;

import static java.lang.Double.isNaN;

public class FragmentEstatisticasOrcamento extends android.support.v4.app.Fragment {

    private View view_frag;
    private Long ID_ORCAMENTO;
    public List<Stat> Statistics;
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
        String ambiente;
        String ambiente_anterior = null;
        double potencia_led;
        double potencia_lamp;
        double Potencia_atual = 0;
        double Potencia_led = 0;
        double Conta_atual = 0;
        double Conta_ideal = 0;
        int horas, quantidade, i;
        double Preco_KWH;
        double valor;
        double investimento = 0;

        if (Statistics != null) {
            Statistics.clear();
        }

        View view = inflater.inflate(R.layout.fragment_statistics_orcamento, container, false);

        view_frag = view;

        Statistics = new ArrayList<>();

        /*
        LedStockDB db = new LedStockDB(getActivity());
        Preco_KWH = db.Select_KWh();

        String ID_ESTUDO_REMOTE = String.valueOf(db.SelectEstudoRemoteIDById(String.valueOf(ID_ESTUDO)));
        Cursor c;
        if (!ID_ESTUDO_REMOTE.equals("0")) {
            c = db.SelectEstatiLamps(String.valueOf(ID_ESTUDO), ID_ESTUDO_REMOTE);
        } else {
            c = db.SelectEstatiLamps(String.valueOf(ID_ESTUDO), "");
        }

        i = 1;
        if (c != null) {
            int lenght = c.getCount();
            do {
                ambiente = c.getString(c.getColumnIndex("descricao"));
                potencia_led = c.getDouble(c.getColumnIndex("pot_led"));
                potencia_lamp = c.getDouble(c.getColumnIndex("pot_lamp"));
                horas = c.getInt(c.getColumnIndex("horas"));
                quantidade = c.getInt(c.getColumnIndex("quantidade"));
                valor = c.getDouble(c.getColumnIndex("valor"));

                if (!ambiente.equals(ambiente_anterior)) {
                    if (ambiente_anterior == null) {
                        Potencia_atual = potencia_lamp * quantidade;
                        Potencia_led = potencia_led * quantidade;
                        Conta_atual = (((Potencia_atual / 1000) * horas * 30) * Preco_KWH);
                        Conta_ideal = (((Potencia_led / 1000) * horas * 30) * Preco_KWH);
                        investimento = valor * quantidade;
                        ambiente_anterior = ambiente;

                        if (lenght == 1) {
                            Stat Estatistica = new Stat();
                            Estatistica.ambi = ambiente_anterior;
                            Estatistica.Pot_Atual = String.valueOf(Potencia_atual);
                            Estatistica.conta_atual = Conta_atual;
                            Estatistica.Pot_Led = String.valueOf(Potencia_led);
                            Estatistica.conta_ideal = Conta_ideal;
                            Estatistica.investimento = investimento;
                            Estatistica.economia = (Conta_atual - Conta_ideal);
                            Estatistica.retorno = (investimento / Estatistica.economia);
                            Statistics.add(Estatistica);
                        }
                    } else {
                        Stat Estatistica = new Stat();
                        Estatistica.ambi = ambiente_anterior;
                        Estatistica.Pot_Atual = String.valueOf(Potencia_atual);
                        Estatistica.conta_atual = Conta_atual;
                        Estatistica.Pot_Led = String.valueOf(Potencia_led);
                        Estatistica.conta_ideal = Conta_ideal;
                        Estatistica.investimento = investimento;
                        Estatistica.economia = (Conta_atual - Conta_ideal);
                        Estatistica.retorno = (investimento / Estatistica.economia);
                        Statistics.add(Estatistica);

                        Potencia_atual = potencia_lamp * quantidade;
                        Potencia_led = potencia_led * quantidade;
                        Conta_atual = (((Potencia_atual / 1000) * horas * 30) * Preco_KWH);
                        Conta_ideal = (((Potencia_led / 1000) * horas * 30) * Preco_KWH);
                        investimento = valor * quantidade;
                        ambiente_anterior = ambiente;

                        if (lenght == i) {
                            Stat Final = new Stat();
                            Final.ambi = ambiente;
                            Final.Pot_Atual = String.valueOf(Potencia_atual);
                            Final.conta_atual = Conta_atual;
                            Final.Pot_Led = String.valueOf(Potencia_led);
                            Final.conta_ideal = Conta_ideal;
                            Final.investimento = investimento;
                            Final.economia = (Conta_atual - Conta_ideal);
                            Final.retorno = (investimento / Final.economia);
                            Statistics.add(Final);
                        }
                    }
                } else {
                    if (lenght != i) {
                        Potencia_atual = potencia_lamp * quantidade;
                        Potencia_led = potencia_led * quantidade;
                        Conta_atual += (((Potencia_atual / 1000) * horas * 30) * Preco_KWH);
                        Conta_ideal += (((Potencia_led / 1000) * horas * 30) * Preco_KWH);
                        investimento += valor *quantidade;
                        ambiente_anterior = ambiente;
                    } else {
                        Potencia_atual = potencia_lamp * quantidade;
                        Potencia_led = potencia_led * quantidade;
                        Conta_atual += (((Potencia_atual / 1000) * horas * 30) * Preco_KWH);
                        Conta_ideal += (((Potencia_led / 1000) * horas * 30) * Preco_KWH);
                        investimento += valor *quantidade;

                        Stat Estatistica = new Stat();
                        Estatistica.ambi = ambiente;
                        Estatistica.Pot_Atual = String.valueOf(Potencia_atual);
                        Estatistica.conta_atual = Conta_atual;
                        Estatistica.Pot_Led = String.valueOf(Potencia_led);
                        Estatistica.conta_ideal = Conta_ideal;
                        Estatistica.investimento = investimento;
                        Estatistica.economia = (Conta_atual - Conta_ideal);
                        Estatistica.retorno = (investimento / Estatistica.economia);
                        Statistics.add(Estatistica);

                        ambiente_anterior = ambiente;
                        Potencia_atual = 0;
                        Potencia_led = 0;
                        Conta_atual = 0;
                        Conta_ideal = 0;
                        investimento = 0;
                    }
                }
                i++;
            } while (c.moveToNext());
            c.close();
        }

        Cursor c1;
        if (!ID_ESTUDO_REMOTE.equals("0")) {
            c1 = db.SelectEstatiHandsOn(String.valueOf(ID_ESTUDO), ID_ESTUDO_REMOTE);
        } else {
            c1 = db.SelectEstatiHandsOn(String.valueOf(ID_ESTUDO), "");
        }

        if (c1 != null) {
            do {
                String amb = c1.getString(c1.getColumnIndex("descricao"));
                double valor_mob = c1.getDouble(c1.getColumnIndex("valor_mob"));

                for (Stat p : Statistics) {
                    if (p.ambi.equals(amb)) {
                        int index = Statistics.indexOf(p);
                        Stat Estat = new Stat();
                        Estat.ambi = p.ambi;
                        Estat.Pot_Atual = p.Pot_Atual;
                        Estat.Pot_Led = p.Pot_Led;
                        Estat.investimento = p.investimento;
                        Estat.conta_atual = p.conta_atual;
                        Estat.conta_ideal = p.conta_ideal;
                        Estat.economia = p.economia;
                        Estat.retorno = p.retorno;
                        Estat.mdo = valor_mob;
                        Statistics.set(index, Estat);
                    }
                }
            } while (c1.moveToNext());
            c1.close();
        }
        db.close();

        if (Statistics != null) {
            Stat Total = new Stat();
            Total.ambi = "Total";
            double potlamp = 0.0;
            double potled = 0.0;

            for (Stat p : Statistics) {
                potlamp += Double.parseDouble(p.Pot_Atual);
                potled += Double.parseDouble(p.Pot_Led);
                Total.investimento += p.investimento;
                Total.conta_atual += p.conta_atual;
                Total.conta_ideal += p.conta_ideal;
                Total.economia += p.economia;
                Total.mdo += p.mdo;
            }
            Total.Pot_Atual = String.valueOf(potlamp);
            Total.Pot_Led = String.valueOf(potled);
            Total.retorno = Total.investimento / Total.economia;
            if (isNaN(Total.retorno)){
                Total.retorno = 0.0;
            }
            Statistics.add(Total);
        }

        recyclerView = (RecyclerView) view_frag.findViewById(R.id.recyclerviewEstatistica);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManger);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(new EstatisticaAdapter(getActivity(), Statistics, null));*/

        return view;
    }

    //BroadCast Receiver para Editar as Lamps Remotamente
    private BroadcastReceiver RefreshEstatisticasOrcamento = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            /*
            String ambiente;
            String ambiente_anterior = null;
            double potencia_led;
            double potencia_lamp;
            double Potencia_atual = 0;
            double Potencia_led = 0;
            double Conta_atual = 0;
            double Conta_ideal = 0;
            int horas, quantidade, i;
            double Preco_KWH;
            double valor;
            double investimento = 0;

            if (Statistics != null) {
                Statistics.clear();
            }

            LedStockDB db = new LedStockDB(getActivity());
            Preco_KWH = db.Select_KWh();

            String ID_ESTUDO_REMOTE = String.valueOf(db.SelectEstudoRemoteIDById(String.valueOf(ID_ESTUDO)));
            Cursor c;
            if (!ID_ESTUDO_REMOTE.equals("0")) {
                c = db.SelectEstatiLamps(String.valueOf(ID_ESTUDO), ID_ESTUDO_REMOTE);
            } else {
                c = db.SelectEstatiLamps(String.valueOf(ID_ESTUDO), "");
            }

            i = 1;
            if (c != null) {
                int lenght = c.getCount();
                do {
                    ambiente = c.getString(c.getColumnIndex("descricao"));
                    potencia_led = c.getDouble(c.getColumnIndex("pot_led"));
                    potencia_lamp = c.getDouble(c.getColumnIndex("pot_lamp"));
                    horas = c.getInt(c.getColumnIndex("horas"));
                    quantidade = c.getInt(c.getColumnIndex("quantidade"));
                    valor = c.getDouble(c.getColumnIndex("valor"));

                    if (!ambiente.equals(ambiente_anterior)) {
                        if (ambiente_anterior == null) {
                            Potencia_atual = potencia_lamp * quantidade;
                            Potencia_led = potencia_led * quantidade;
                            Conta_atual = (((Potencia_atual / 1000) * horas * 30) * Preco_KWH);
                            Conta_ideal = (((Potencia_led / 1000) * horas * 30) * Preco_KWH);
                            investimento = valor * quantidade;
                            ambiente_anterior = ambiente;

                            if (lenght == 1) {
                                Stat Estatistica = new Stat();
                                Estatistica.ambi = ambiente_anterior;
                                Estatistica.Pot_Atual = String.valueOf(Potencia_atual);
                                Estatistica.conta_atual = Conta_atual;
                                Estatistica.Pot_Led = String.valueOf(Potencia_led);
                                Estatistica.conta_ideal = Conta_ideal;
                                Estatistica.investimento = investimento;
                                Estatistica.economia = (Conta_atual - Conta_ideal);
                                Estatistica.retorno = (investimento / Estatistica.economia);
                                Statistics.add(Estatistica);
                            }
                        } else {
                            Stat Estatistica = new Stat();
                            Estatistica.ambi = ambiente_anterior;
                            Estatistica.Pot_Atual = String.valueOf(Potencia_atual);
                            Estatistica.conta_atual = Conta_atual;
                            Estatistica.Pot_Led = String.valueOf(Potencia_led);
                            Estatistica.conta_ideal = Conta_ideal;
                            Estatistica.investimento = investimento;
                            Estatistica.economia = (Conta_atual - Conta_ideal);
                            Estatistica.retorno = (investimento / Estatistica.economia);
                            Statistics.add(Estatistica);

                            Potencia_atual = potencia_lamp * quantidade;
                            Potencia_led = potencia_led * quantidade;
                            Conta_atual = (((Potencia_atual / 1000) * horas * 30) * Preco_KWH);
                            Conta_ideal = (((Potencia_led / 1000) * horas * 30) * Preco_KWH);
                            investimento = valor * quantidade;
                            ambiente_anterior = ambiente;

                            if (lenght == i) {
                                Stat Final = new Stat();
                                Final.ambi = ambiente;
                                Final.Pot_Atual = String.valueOf(Potencia_atual);
                                Final.conta_atual = Conta_atual;
                                Final.Pot_Led = String.valueOf(Potencia_led);
                                Final.conta_ideal = Conta_ideal;
                                Final.investimento = investimento;
                                Final.economia = (Conta_atual - Conta_ideal);
                                Final.retorno = (investimento / Final.economia);
                                Statistics.add(Final);
                            }
                        }
                    } else {
                        if (lenght != i) {
                            Potencia_atual = potencia_lamp * quantidade;
                            Potencia_led = potencia_led * quantidade;
                            Conta_atual += (((Potencia_atual / 1000) * horas * 30) * Preco_KWH);
                            Conta_ideal += (((Potencia_led / 1000) * horas * 30) * Preco_KWH);
                            investimento += valor *quantidade;
                            ambiente_anterior = ambiente;
                        } else {
                            Potencia_atual = potencia_lamp * quantidade;
                            Potencia_led = potencia_led * quantidade;
                            Conta_atual += (((Potencia_atual / 1000) * horas * 30) * Preco_KWH);
                            Conta_ideal += (((Potencia_led / 1000) * horas * 30) * Preco_KWH);
                            investimento += valor *quantidade;

                            Stat Estatistica = new Stat();
                            Estatistica.ambi = ambiente;
                            Estatistica.Pot_Atual = String.valueOf(Potencia_atual);
                            Estatistica.conta_atual = Conta_atual;
                            Estatistica.Pot_Led = String.valueOf(Potencia_led);
                            Estatistica.conta_ideal = Conta_ideal;
                            Estatistica.investimento = investimento;
                            Estatistica.economia = (Conta_atual - Conta_ideal);
                            Estatistica.retorno = (investimento / Estatistica.economia);
                            Statistics.add(Estatistica);

                            ambiente_anterior = ambiente;
                            Potencia_atual = 0;
                            Potencia_led = 0;
                            Conta_atual = 0;
                            Conta_ideal = 0;
                            investimento = 0;
                        }
                    }
                    i++;
                } while (c.moveToNext());
                c.close();
            }

            Cursor c1;
            if (!ID_ESTUDO_REMOTE.equals("0")) {
                c1 = db.SelectEstatiHandsOn(String.valueOf(ID_ESTUDO), ID_ESTUDO_REMOTE);
            } else {
                c1 = db.SelectEstatiHandsOn(String.valueOf(ID_ESTUDO), "");
            }

            if (c1 != null) {
                do {
                    String amb = c1.getString(c1.getColumnIndex("descricao"));
                    double valor_mob = c1.getDouble(c1.getColumnIndex("valor_mob"));

                    for (Stat p : Statistics) {
                        if (p.ambi.equals(amb)) {
                            int index = Statistics.indexOf(p);
                            Stat Estat = new Stat();
                            Estat.ambi = p.ambi;
                            Estat.Pot_Atual = p.Pot_Atual;
                            Estat.Pot_Led = p.Pot_Led;
                            Estat.investimento = p.investimento;
                            Estat.conta_atual = p.conta_atual;
                            Estat.conta_ideal = p.conta_ideal;
                            Estat.economia = p.economia;
                            Estat.retorno = p.retorno;
                            Estat.mdo = valor_mob;
                            Statistics.set(index, Estat);
                        }
                    }
                } while (c1.moveToNext());
                c1.close();
            }
            db.close();

            if (Statistics != null) {
                Stat Total = new Stat();
                Total.ambi = "Total";
                double potlamp = 0.0;
                double potled = 0.0;

                for (Stat p : Statistics) {
                    potlamp += Double.parseDouble(p.Pot_Atual);
                    potled += Double.parseDouble(p.Pot_Led);
                    Total.investimento += p.investimento;
                    Total.conta_atual += p.conta_atual;
                    Total.conta_ideal += p.conta_ideal;
                    Total.economia += p.economia;
                    Total.mdo += p.mdo;
                }
                Total.Pot_Atual = String.valueOf(potlamp);
                Total.Pot_Led = String.valueOf(potled);
                Total.retorno = Total.investimento / Total.economia;
                if (isNaN(Total.retorno)){
                    Total.retorno = 0.0;
                }
                Statistics.add(Total);
            }

            recyclerView = (RecyclerView) view_frag.findViewById(R.id.recyclerviewEstatistica);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManger);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            recyclerView.setAdapter(new EstatisticaAdapter(getActivity(), Statistics, null));*/

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
