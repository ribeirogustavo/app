package br.com.ledstock.led_stock.led_stock.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.adapter.TabsAdminAdapter;

public class Admin extends android.support.v4.app.Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //Cancela o Registro de Refresh Lamps
        getActivity().registerReceiver(MessageLamps, new IntentFilter("MESSAGE_LAMPS"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

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
        viewPager.setAdapter(new TabsAdminAdapter(getContext(), fm));

        /*
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            public void onPageSelected(int position) {
                //Salva o indice da pagina/tab selecionada
                //Prefs.setInteger(getContext(), "tabIdx", viewPager.getCurrentItem());
                //Faz o Backup
                //backupManager.dataChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        */
        //Seta as Tabs
        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.TabLayout);
        if (tabLayout != null) {
            //Cria as Tabs com o mesmo adapter utilizado pelo ViewPager
            tabLayout.setupWithViewPager(viewPager);
            //int cor = ContextCompat.getColor(getContext(), R.color.white);
            // tabLayout.setTabTextColors(cor,cor);
            tabLayout.getTabAt(0).setIcon(R.drawable.ic_bulb);
            tabLayout.getTabAt(1).setIcon(R.drawable.ic_led);
            tabLayout.getTabAt(2).setIcon(R.drawable.ic_content);
            tabLayout.getTabAt(3).setIcon(R.drawable.ic_more_horiz);
        }

        // Attach the page change listener inside the activity
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                if (position == 3){
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

        getActivity().findViewById(R.id.fab).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tabLayout != null) {
                            if (tabLayout.getSelectedTabPosition() == 0) {
                                Dialog_Lamp_fragment.show(getChildFragmentManager(), 0, getActivity());
                            } else if (tabLayout.getSelectedTabPosition() == 1) {
                                Dialog_LED_fragment.show(getChildFragmentManager(), 0, getActivity());
                            } else if (tabLayout.getSelectedTabPosition() == 2) {
                                Dialog_HandsOn_fragment.show(getChildFragmentManager(), 0, getActivity());
                            }

                            /*
                                Dialog_Place_fragment.show(getChildFragmentManager(), 0, getActivity());
                            }else if (tabLayout.getSelectedTabPosition() == 4) {
                                Dialog_User_fragment.show(getChildFragmentManager(), 0, getActivity());
                            }*/
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

    public void onPause(){
        super.onPause();

    }

    private BroadcastReceiver MessageLamps = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {

        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        //Cancela o Registro de Refresh Lamps
        getActivity().unregisterReceiver(MessageLamps);
        getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);

    }
}
