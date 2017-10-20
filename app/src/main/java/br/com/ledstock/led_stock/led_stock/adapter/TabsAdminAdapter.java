package br.com.ledstock.led_stock.led_stock.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.com.ledstock.led_stock.led_stock.fragments.HandsOn;
import br.com.ledstock.led_stock.led_stock.fragments.LED_Solution;
import br.com.ledstock.led_stock.led_stock.fragments.Lamp_actual;
import br.com.ledstock.led_stock.led_stock.fragments.More;


/**
 * Created by Gustavo on 14/09/2016.
 */
public class TabsAdminAdapter extends FragmentPagerAdapter {

    private Context context;
    private FragmentManager fm;

    public TabsAdminAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;

        if (position == 0) {
            f = Lamp_actual.newInstance();
        } else if (position == 1) {
            f = LED_Solution.newInstance ();
        } else if (position == 2) {
            f = HandsOn.newInstance();
        } else if (position == 3) {
            f = More.newInstance();
        }else{
            f = null;
        }
        return f;
    }

    @Override
    public int getCount() {
        return 4;
    }

}
