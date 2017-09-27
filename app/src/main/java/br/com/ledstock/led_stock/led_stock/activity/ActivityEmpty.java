package br.com.ledstock.led_stock.led_stock.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.fragments.FragmentContentEstudo;

public class ActivityEmpty extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        registerReceiver(RefreshInfoEstudo, new IntentFilter("REFRESH_INFOESTUDO"));

        setUpToolbar();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Verifica se no Intent possui alguma puExtra
        if (getIntent().getExtras() != null) {
            if (getIntent().getStringExtra("action") != null) {
                switch (getIntent().getStringExtra("action")) {
                    case "content_estudo":
                        FragmentContentEstudo frag_estudo = new FragmentContentEstudo();
                        frag_estudo.setArguments(getIntent().getExtras());
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, frag_estudo).commit();

                        int id_estudo = getIntent().getIntExtra("id_estudo", 0);
                        LedStockDB db = new LedStockDB(getApplicationContext());

                        if (actionBar != null) {
                            //Seta o Título da ActionBar
                            actionBar.setTitle(db.Select_NameofEstudo(String.valueOf(id_estudo)));
                        }

                            /*
                            Snackbar mySnack = Snackbar.make(findViewById(R.id.mycoordinatorlayout), "Cliente Cadastrado !", Snackbar.LENGTH_LONG);
                            mySnack.setActionTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                            mySnack.show();
                            */
                        break;
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(RefreshInfoEstudo);
    }

    private BroadcastReceiver RefreshInfoEstudo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }

            if (actionBar != null) {
                //Seta o Título da ActionBar
                actionBar.setTitle(intent.getStringExtra("nome"));
            }

        }
    };
}
