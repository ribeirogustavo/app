package br.com.ledstock.led_stock.led_stock;

import android.app.Application;

/**
 * Created by Gustavo on 15/08/2016.
 */
public class LedStockApplication extends Application {

    private static final String TAG = "LedStockApplication";
    private static LedStockApplication instance = null;
    //private Bus bus = new Bus();


    public static LedStockApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate(){
        super.onCreate();
       // Log.d(TAG,"CarrosApplication.onCreate()");
        instance = this;
    }

    @Override
    public void onTerminate(){
        super.onTerminate();
       // Log.d(TAG,"CarrosApplication.onTerminate()");
    }


}
