package br.com.ledstock.led_stock.led_stock.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.utils.AlertUtils;
import br.com.ledstock.led_stock.led_stock.utils.PermissionUtils;

import static android.R.attr.name;
import static android.R.attr.permission;
import static java.security.AccessController.getContext;

public class PermissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        // Lista de permissões necessárias.
        String permissions[] = new String[]{
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };

        // Valida lista de permissões.
        boolean ok = PermissionUtils.validate(this, 0, permissions);

        if (ok) {
            // Tudo OK, pode entrar.
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                // Negou a permissão. Mostra alerta e fecha.
                AlertUtils.alert(getApplicationContext(), R.string.app_name, R.string.msg_alerta_permissao, R.string.ok, new Runnable() {
                    @Override
                    public void run() {
                        // Negou permissão. Sai do app.
                        finish();
                    }
                });
                return;
            }
        }

        // Permissões concedidas, pode entrar.
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
