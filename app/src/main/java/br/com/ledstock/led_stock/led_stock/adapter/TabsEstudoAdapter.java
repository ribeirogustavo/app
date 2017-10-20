package br.com.ledstock.led_stock.led_stock.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.com.ledstock.led_stock.led_stock.fragments.FragmentAmbientesEstudo;
import br.com.ledstock.led_stock.led_stock.fragments.FragmentEstatisticasEstudo;

/**
 * Created by Gustavo on 21/10/2016.
 */

public class TabsEstudoAdapter extends FragmentPagerAdapter {

    private Context context;
    private FragmentManager fm;

    public TabsEstudoAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f;

        if (position == 0) {
            f = FragmentAmbientesEstudo.newInstance();
        } else if (position == 1) {
            f = FragmentEstatisticasEstudo.newInstance ();
        }else{
            f = null;
        }
        return f;
    }

    @Override
    public int getCount() {
        return 2;
    }

}