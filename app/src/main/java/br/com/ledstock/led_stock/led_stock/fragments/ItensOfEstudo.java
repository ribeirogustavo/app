package br.com.ledstock.led_stock.led_stock.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.adapter.TabsItensOfEstudoAdapter;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.services.LedService;

public class ItensOfEstudo extends android.support.v4.app.Fragment {

    public static int ID_AMBIENTE_OF_ESTUDO;
    public static int ID_ESTUDO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ID_AMBIENTE_OF_ESTUDO = getArguments().getInt("ambiente_estudo", 0);
            ID_ESTUDO = getArguments().getInt("estudo", 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_itens_estudo, container, false);

        //Informar que este fragment contem menu na ToolBar
        setHasOptionsMenu(true);

        //Instancia a ActionBar (Que na verdade é a ToolBar)
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            //Adicionar o Icone de Menu
            actionBar.setElevation(0);
        }

        //FragmentManager precisa ser Child porque é um Fragment dentro de um fragment
        FragmentManager fm = getChildFragmentManager();
        //Seta ViewPager
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.ViewPager);
        viewPager.setAdapter(new TabsItensOfEstudoAdapter(getContext(), fm));

        //Seta as Tabs
        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.TabLayout);
        if (tabLayout != null) {
            //Cria as Tabs com o mesmo adapter utilizado pelo ViewPager
            tabLayout.setupWithViewPager(viewPager);
            //int cor = ContextCompat.getColor(getContext(), R.color.white);
            // tabLayout.setTabTextColors(cor,cor);
            tabLayout.getTabAt(0).setText("LAMPADA");
            tabLayout.getTabAt(1).setText("LED");
            tabLayout.getTabAt(2).setText("MÃO DE OBRA");
        }

        // Attach the page change listener inside the activity
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                RemoveSelectionOfItens();

                if (position == 1) {
                    getActivity().findViewById(R.id.fab).setVisibility(View.INVISIBLE);
                } else {
                    getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
                }
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

        LedStockDB db = new LedStockDB(getActivity());
        Cursor c = db.SelectAmbienteOfEstudoByID(String.valueOf(ID_AMBIENTE_OF_ESTUDO));

        getActivity().findViewById(R.id.fab).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tabLayout != null) {
                            if (tabLayout.getSelectedTabPosition() == 0) {
                                Dialog_Lamp_Of_Estudo.show(getChildFragmentManager(), ID_AMBIENTE_OF_ESTUDO, ID_ESTUDO, getActivity());
                            } else if (tabLayout.getSelectedTabPosition() == 1) {
                                Dialog_LED_Of_Estudo.show(getChildFragmentManager(), ID_AMBIENTE_OF_ESTUDO, ID_ESTUDO, getActivity());
                            } else if (tabLayout.getSelectedTabPosition() == 2) {
                                Dialog_HandsOn_Of_Estudo.show(getChildFragmentManager(), ID_AMBIENTE_OF_ESTUDO, ID_ESTUDO, getActivity());
                            }
                        }
                    }
                }
        );

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void RemoveSelectionOfItens() {
        if (ItensOfEstudo_Lamps.Lamps != null) {
            ItensOfEstudo_Lamps.Lamps = null;
            if (ItensOfEstudo_Lamps.actionMode != null) {
                ItensOfEstudo_Lamps.actionMode.finish();
            }
        }

        if (ItensOfEstudo_Leds.Leds != null) {
            ItensOfEstudo_Leds.Leds = null;
            if (ItensOfEstudo_Leds.actionMode != null) {
                ItensOfEstudo_Leds.actionMode.finish();
            }
        }

        if (ItensOfEstudo_HandsOn.HandsOn != null) {
            ItensOfEstudo_HandsOn.HandsOn = null;
            if (ItensOfEstudo_HandsOn.actionMode != null) {
                ItensOfEstudo_HandsOn.actionMode.finish();
            }
        }
    }

    public void onPause() {
        super.onPause();
        RemoveSelectionOfItens();
        getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
    }

    /*
    private BroadcastReceiver MessageLamps = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {

        }
    };*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Cancela o Registro de Refresh Lamps
        //getActivity().unregisterReceiver(MessageLamps);
        getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_delete, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.excluir) {

            DeletarAmbienteOfEstudo.show(getFragmentManager(), new DeletarAmbienteOfEstudo.Callback() {
                public void onClickYes() {
                    //Instancia o Banco de Dados
                    LedStockDB delete_ambiente_estudo = new LedStockDB(getActivity());
                    delete_ambiente_estudo.DeleteAmbienteOfEstudo(String.valueOf(ID_AMBIENTE_OF_ESTUDO));

                    //Instancia o Serviço para Deletar Remotamente
                    LedService service = new LedService();
                    service.DeleteAmbienteOfEstudoRemote(String.valueOf(ID_AMBIENTE_OF_ESTUDO));

                    Intent intent = new Intent();
                    intent.setAction("REFRESH_AMBIENTESOFESTUDOS");
                    getActivity().sendBroadcast(intent);

                    //Fecha a Activity
                    getActivity().finish();
                }
            });

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
