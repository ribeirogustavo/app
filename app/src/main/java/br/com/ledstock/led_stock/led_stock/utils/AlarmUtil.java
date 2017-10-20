package br.com.ledstock.led_stock.led_stock.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Gustavo on 13/09/2016.
 */
public class AlarmUtil {
    private static final String TAG = "Alarm";

    //Agenda um alarme com repetição
    public void scheduleRepeat(Context context, Intent intent, long triggerAtMillis, long intervalMillis){
        PendingIntent p = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,triggerAtMillis,intervalMillis,p);
       // Log.d(TAG, "Alarme agendado com sucesso !");
    }

    //Cancela o Alarme
    public void cancel(Context context, Intent intent){
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent p = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm.cancel(p);
       // Log.d(TAG, "Alarme cancelado com sucesso !");
    }
}
