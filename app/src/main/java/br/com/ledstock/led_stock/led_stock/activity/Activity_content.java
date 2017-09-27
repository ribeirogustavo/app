package br.com.ledstock.led_stock.led_stock.activity;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.fragments.Info_Client;
import br.com.ledstock.led_stock.led_stock.fragments.Info_HandsOn;
import br.com.ledstock.led_stock.led_stock.fragments.Info_LED;
import br.com.ledstock.led_stock.led_stock.fragments.Info_lamp;
import br.com.ledstock.led_stock.led_stock.fragments.Info_place;
import br.com.ledstock.led_stock.led_stock.fragments.Info_users;

public class Activity_content extends BaseActivity {

    public CollapsingToolbarLayout ToolBarCollapse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        //Configura a Toolbar como ActionBar
        // setUpToolbar();

        //Instancia a TooBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //Se encontrar a ToolBar no Layout Faça
        if (toolbar != null) {
            //Transforma a ToolBar em ActionBar
            setSupportActionBar(toolbar);
        }

        ToolBarCollapse = (CollapsingToolbarLayout) findViewById(R.id.CollapsingToolBar);
        ToolBarCollapse.setCollapsedTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        ToolBarCollapse.setExpandedTitleColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            if (getIntent().getExtras() != null) {
                if (getIntent().getIntExtra("cliente", 0) != 0) {
                    Info_Client frag_info_clientes = new Info_Client();
                    frag_info_clientes.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContent, frag_info_clientes).commit();
                } else if (getIntent().getIntExtra("lamp", 0) != 0) {
                    Info_lamp frag_info_lamp = new Info_lamp();
                    frag_info_lamp.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContent, frag_info_lamp).commit();
                } else if (getIntent().getIntExtra("led", 0) != 0) {
                    Info_LED frag_info_led = new Info_LED();
                    frag_info_led.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContent, frag_info_led).commit();
                } else if (getIntent().getIntExtra("ambiente", 0) != 0) {
                    Info_place frag_info_place = new Info_place();
                    frag_info_place.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContent, frag_info_place).commit();
                } else if (getIntent().getIntExtra("usuario", 0) != 0) {
                    Info_users frag_info_users = new Info_users();
                    frag_info_users.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContent, frag_info_users).commit();
                } else if (getIntent().getIntExtra("handson", 0) != 0) {
                    Info_HandsOn frag_info_handson = new Info_HandsOn();
                    frag_info_handson.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContent, frag_info_handson).commit();
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
}
