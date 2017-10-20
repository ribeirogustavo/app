package br.com.ledstock.led_stock.led_stock.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.com.ledstock.led_stock.led_stock.fragments.FragmentEstatisticasOrcamento;
import br.com.ledstock.led_stock.led_stock.fragments.FragmentListOrcamento;

/**
 * Created by Gustavo on 21/10/2016.
 */

public class TabsOrcamentoAdapter extends FragmentPagerAdapter {

    private Context context;
    private FragmentManager fm;

    public TabsOrcamentoAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f;

        if (position == 0) {
            f = FragmentListOrcamento.newInstance();
        } else if (position == 1) {
            f = FragmentEstatisticasOrcamento.newInstance ();
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