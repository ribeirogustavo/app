package br.com.ledstock.led_stock.led_stock.activity;

import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.fragments.Admin;
import br.com.ledstock.led_stock.led_stock.fragments.Clientes;
import br.com.ledstock.led_stock.led_stock.fragments.Estudo;
import br.com.ledstock.led_stock.led_stock.fragments.Luximetro;
import br.com.ledstock.led_stock.led_stock.fragments.Orcamento;
import br.com.ledstock.led_stock.led_stock.fragments.Pedido;
import br.com.ledstock.led_stock.led_stock.utils.Global;

import static android.support.design.widget.NavigationView.*;

public class BaseActivity extends android.support.v7.app.AppCompatActivity {

    protected DrawerLayout drawerLayout;
    private String TAG = "LED_STOCK_BASE_ACTIVITY";

    //Setup do ToolBar
    protected void setUpToolbar() {
        //Instancia a TooBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //Se encontrar a ToolBar no Layout Faça
        if (toolbar != null) {
            //Transforma a ToolBar em ActionBar
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        }
    }

    //Setup do DrawerLayout (Menu Lateral)
    protected void setUpDrawerLayout() {

        //Instancia a ActionBar (Que na verdade é a ToolBar)
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //Adicionar o Icone de Menu
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            //Habilita a visualização do icone do Menu ic_menu
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
        //Instancia o DrawerLayout (Menu Lateral)
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Instancia o navigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView != null && drawerLayout != null) {
            Global global = new Global();
            if (global.getAcesso() != null) {
                if (global.getAcesso().equals("1")) {
                    Menu menu = navigationView.getMenu();
                    menu.add(R.id.group_menu_nav, R.id.Nav_Menu_ADM, 0, "ADM")
                            .setIcon(android.R.drawable.ic_menu_preferences)
                            .setCheckable(true);
                }
            }

            navigationView.setNavigationItemSelectedListener(
                    new OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem item) {
                            item.setChecked(true);
                            drawerLayout.closeDrawers();
                            onNavDrawerItemSelected(item);
                            return true;
                        }
                    }
            );
        }
    }

    private void onNavDrawerItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.Nav_Menu_Cliente:

                //Instancia a ActionBar
                ActionBar actionBar = getSupportActionBar();
                //Instancia o Navigation View
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                if (actionBar != null) {
                    //Seta o Título da Toolbar
                    actionBar.setTitle(R.string.clientes);
                }
                //Checkar o item Clientes no NavDrawer
                navigationView.setCheckedItem(R.id.Nav_Menu_Cliente);

                Clientes frag_clientes = new Clientes();
                frag_clientes.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().replace(R.id.container, frag_clientes).commit();

                break;
            case R.id.Nav_Menu_Estudo:

                //Instancia o Toolbar
                actionBar = getSupportActionBar();
                //Instancia o Navigation View
                navigationView = (NavigationView) findViewById(R.id.nav_view);
                if (actionBar != null) {
                    //Seta o Título da Toolbar
                    actionBar.setTitle(R.string.estudo);
                }
                //Checkar o item Clientes no NavDrawer
                navigationView.setCheckedItem(R.id.Nav_Menu_Estudo);

                Estudo frag_estudo = new Estudo();
                frag_estudo.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().replace(R.id.container, frag_estudo).commit();
                break;
            case R.id.Orcamento:

                //Instancia o Toolbar
                actionBar = getSupportActionBar();
                //Instancia o Navigation View
                navigationView = (NavigationView) findViewById(R.id.nav_view);
                if (actionBar != null) {
                    //Seta o Título da Toolbar
                    actionBar.setTitle(R.string.orcamento);
                }
                //Checkar o item Orçamentos no NavDrawer
                navigationView.setCheckedItem(R.id.Orcamento);

                Orcamento frag_orcamento = new Orcamento();
                frag_orcamento.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().replace(R.id.container, frag_orcamento).commit();
                break;

            case R.id.Pedido:

                //Instancia o Toolbar
                actionBar = getSupportActionBar();
                //Instancia o Navigation View
                navigationView = (NavigationView) findViewById(R.id.nav_view);
                if (actionBar != null) {
                    //Seta o Título da Toolbar
                    actionBar.setTitle(R.string.pedidos);
                }
                //Checkar o item Clientes no NavDrawer
                navigationView.setCheckedItem(R.id.Pedido);

                Pedido frag_pedido = new Pedido();
                frag_pedido.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().replace(R.id.container, frag_pedido).commit();
                break;
            case R.id.Nav_Menu_Luximetro:

                //Instancia o Toolbar
                actionBar = getSupportActionBar();
                //Instancia o Navigation View
                navigationView = (NavigationView) findViewById(R.id.nav_view);
                if (actionBar != null) {
                    //Seta o Título da Toolbar
                    actionBar.setTitle(R.string.luximetro);
                }
                //Checkar o item Clientes no NavDrawer
                navigationView.setCheckedItem(R.id.Nav_Menu_Luximetro);

                Luximetro frag_luximetro = new Luximetro();
                frag_luximetro.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().replace(R.id.container, frag_luximetro).commit();
                break;
            case R.id.Nav_Menu_ADM:
                //Instancia o Toolbar
                actionBar = getSupportActionBar();
                //Instancia o Navigation View
                navigationView = (NavigationView) findViewById(R.id.nav_view);
                if (actionBar != null) {
                    //Seta o Título da Toolbar
                    actionBar.setTitle(R.string.adm);
                }
                //Checkar o item Clientes no NavDrawer
                navigationView.setCheckedItem(R.id.Nav_Menu_ADM);

                Admin frag_admin = new Admin();
                frag_admin.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().replace(R.id.container, frag_admin).commit();

                break;
            /*
            case R.id.nav_item_settings:
                if(AndroidUtils.isAndroid3Honeycomb()){
                    startActivity(new Intent(this, ConfiguracoesV11Activity.class));
                }else{
                    startActivity(new Intent(this, ConfiguracoesActivity.class));
                }
                break;
            */
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerLayout != null) {
                    openDrawer();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    protected void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    protected void closeDrawer() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
}

