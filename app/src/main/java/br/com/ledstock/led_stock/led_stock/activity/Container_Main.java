package br.com.ledstock.led_stock.led_stock.activity;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.fragments.Clientes;
import br.com.ledstock.led_stock.led_stock.services.LedService;

public class Container_Main extends BaseActivity {

    final int ITEM_CLIENTE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        //Seta a ToolBar como ActionBar
        setUpToolbar();
        //Instancia o ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //Seta a Cor do Título para Branco
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        //Instancia a ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //Seta o Título da ActionBar
            actionBar.setTitle("Clientes");
        }
        //Seta o DrawerLayout na Activity
        setUpDrawerLayout();
        //Instancia o Navigation View
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //Checkar o item Clientes no NavDrawer
        navigationView.setCheckedItem(R.id.Nav_Menu_Cliente);

        if (savedInstanceState == null) {

            //Verifica se no Intent possui alguma puExtra
            if (getIntent().getExtras() != null) {
                if (getIntent().getStringExtra("action") != null) {
                    switch (getIntent().getStringExtra("action")) {
                        case "add_client":
                            Clientes frag_clientes = new Clientes();
                            frag_clientes.setArguments(getIntent().getExtras());
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, frag_clientes).commit();

                            Snackbar mySnack = Snackbar.make(findViewById(R.id.mycoordinatorlayout), "Cliente Cadastrado !", Snackbar.LENGTH_LONG);
                            mySnack.setActionTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                            mySnack.show();
                            break;
                        case "del_client":
                            frag_clientes = new Clientes();
                            frag_clientes.setArguments(getIntent().getExtras());
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, frag_clientes).commit();

                            mySnack = Snackbar.make(findViewById(R.id.mycoordinatorlayout), "Cliente Excluido !", Snackbar.LENGTH_LONG);
                            mySnack.setActionTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                            mySnack.show();
                            break;
                    }
                }
            } else {
                Clientes frag_clientes = new Clientes();
                frag_clientes.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.container, frag_clientes).commit();
            }
        }
        startService(new Intent(this, LedService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, LedService.class));
    }
}


