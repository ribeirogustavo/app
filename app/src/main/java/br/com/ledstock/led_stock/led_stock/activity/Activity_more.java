package br.com.ledstock.led_stock.led_stock.activity;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.fragments.Add_clientes;
import br.com.ledstock.led_stock.led_stock.fragments.Add_estudo;
import br.com.ledstock.led_stock.led_stock.fragments.ItensOfEstudo;
import br.com.ledstock.led_stock.led_stock.fragments.Places;
import br.com.ledstock.led_stock.led_stock.fragments.Sync;
import br.com.ledstock.led_stock.led_stock.fragments.Users;
import br.com.ledstock.led_stock.led_stock.fragments.Watts_hora;

public class Activity_more extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        String name_actionBar = null;

        //Seta a ToolBar como ActionBar
        setUpToolbar();
        //Instancia o ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //Seta a Cor do Título para Branco
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));


        //Verifica o Intent para adicionar o Fragment Correto
        String result_frag = getIntent().getStringExtra("add_fragment");

        if (result_frag != null) {

            switch (result_frag) {
                case "fragment_add_ambientes":
                    Places frag_places = new Places();
                    frag_places.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.container, frag_places).commit();
                    name_actionBar = "Ambientes";
                    break;
                case "fragment_add_KWh":
                    Watts_hora frag_KWh = new Watts_hora();
                    frag_KWh.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.container, frag_KWh).commit();
                    name_actionBar = "Preço KWh";
                    break;
                case "fragment_add_usuarios":
                    Users frag_users = new Users();
                    frag_users.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.container, frag_users).commit();
                    name_actionBar = "Usuários";
                    break;
                case "fragment_sync":
                    Sync frag_sync = new Sync();
                    frag_sync.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.container, frag_sync).commit();
                    name_actionBar = "Estado de Sincronismo";
                    break;
                case "fragment_add_itens_estudo":
                    ItensOfEstudo frag_itensOfEstudo = new ItensOfEstudo();
                    frag_itensOfEstudo.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.container, frag_itensOfEstudo).commit();
                    String id_ambiente_estudo = String.valueOf(getIntent().getIntExtra("ambiente_estudo", 0));
                    LedStockDB db = new LedStockDB(getApplicationContext());
                    //long id_ambiente_remoto = db.SelectAmbienteRemoteOfAmbienteOfEstudo(id_ambiente_estudo);
                    String descricao = db.SelectAmbienteofAmbienteOfEstudo(id_ambiente_estudo);
                    name_actionBar = descricao;
                    break;

            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(name_actionBar);
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
