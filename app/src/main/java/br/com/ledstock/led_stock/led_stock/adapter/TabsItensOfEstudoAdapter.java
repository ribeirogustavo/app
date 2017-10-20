package br.com.ledstock.led_stock.led_stock.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import br.com.ledstock.led_stock.led_stock.fragments.ItensOfEstudo_HandsOn;
import br.com.ledstock.led_stock.led_stock.fragments.ItensOfEstudo_Lamps;
import br.com.ledstock.led_stock.led_stock.fragments.ItensOfEstudo_Leds;

/**
 * Created by Gustavo on 14/09/2016.
 */
public class TabsItensOfEstudoAdapter extends FragmentPagerAdapter {

    private Context context;
    private FragmentManager fm;

    public TabsItensOfEstudoAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f;

        if (position == 0) {
            f = ItensOfEstudo_Lamps.newInstance();
        } else if (position == 1) {
            f = ItensOfEstudo_Leds.newInstance();
        } else if (position == 2) {
            f = ItensOfEstudo_HandsOn.newInstance();
        } else {
            f = null;
        }
        return f;
    }

    @Override
    public int getCount() {
        return 3;
    }

}
