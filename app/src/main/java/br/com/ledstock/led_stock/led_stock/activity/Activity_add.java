package br.com.ledstock.led_stock.led_stock.activity;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;

import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.fragments.Add_clientes;
import br.com.ledstock.led_stock.led_stock.fragments.Add_estudo;

public class Activity_add extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //Seta a ToolBar como ActionBar
        // setUpToolbar();
        //Instancia o ToolBar

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

        //Verifica o Intent para adicionar o Fragment Correto

        String result_frag = getIntent().getStringExtra("add_fragment");

        switch (result_frag) {
            case "fragment_add_client":
                Add_clientes frag_clientes = new Add_clientes();
                frag_clientes.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.container_add, frag_clientes).commit();
                break;
            case "fragment_add_estudo":
                Add_estudo frag_estudo = new Add_estudo();
                frag_estudo.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(R.id.container_add, frag_estudo).commit();
                break;
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
