package br.com.ledstock.led_stock.led_stock.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.services.UserService;
import br.com.ledstock.led_stock.led_stock.utils.Global;

/**
 * Created by Gustavo on 05/09/2016.
 */
public class MainActivity extends BaseActivity {

    private final String TAG = "LED_STOCK_ACCESS";
    private final String arquivo = "config.txt";
    private final String fs = "FIRST_ACCESS=1";
   // private ProgressDialog dialog;

    private void GetUsersTask() {
        //Busca os usuários, dispara a tarefa
        new GetUsersTask().execute();
    }

    private class GetUsersTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                UserService user_service = new UserService();
                user_service.getUsers(getApplicationContext());
            } catch (IOException e) {
                //Log.e(TAG, "Não foi possível obter os contatos: " + e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void teste) {
            int i = 0;
            String usuario = null, senha = null, acesso = null;
            try {
                FileReader arq = new FileReader(getFilesDir().getPath() + "/" + arquivo);
                BufferedReader buff = new BufferedReader(arq);

                //Lê o conteudo da memoria interna, usuário/senha e compara com a o Banco de Dados Local, já atualizado
                String line = buff.readLine();

                while (line != null) {
                    switch (i) {
                        case 1:
                            usuario = line;
                            break;
                        case 2:
                            senha = line;
                            break;
                        case 3:
                            acesso = line;
                            break;
                    }
                    i++;
                    line = buff.readLine();
                }
                buff.close();
                arq.close();

                LedStockDB db = new LedStockDB(getApplicationContext());

                if ((usuario != null) && (senha != null)) {
                    if (db.SelectUser(usuario, senha)) {
                        Global var_global = new Global();
                        var_global.setUsuario(usuario);
                        var_global.setSenha(senha);
                        var_global.setAcesso(acesso);

                        Intent intent = new Intent(getApplicationContext(), Container_Main.class);
                        startActivity(intent);
                        finish();
                        //dialog.dismiss();
                    } else {

                        File file = new File(getFilesDir().getPath() + "/" + arquivo);
                        if (file.delete()) {
                            LedStockDB db_delete = new LedStockDB(getApplicationContext());
                            db_delete.DeleteAllUsers();
                            //Log.d(TAG, "Arquivo deletado com sucesso !");
                            //dialog.dismiss();
                            finish();
                        }
                    }
                } else {
                    File file = new File(getFilesDir().getPath() + "/" + arquivo);
                    if (file.delete()) {
                        Log.d(TAG, "Arquivo deletado com sucesso !");
                        //dialog.dismiss();
                    }
                }

            } catch (IOException ee) {
                //Log.e(TAG, ee.getMessage());
                try {
                    //Log.d(TAG, "Tentando criar o arquivo na memoria interna !");
                    FileOutputStream fos = openFileOutput(arquivo, MODE_PRIVATE);
                    fos.write(fs.getBytes());
                    fos.close();
                    //Log.d(TAG, "Arquivo criado com sucesso !");
                } catch (IOException e) {
                    // Log.e(TAG, e.getMessage());
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.blank);
        setContentView(R.layout.activity_main);
        Button button_login = (Button) findViewById(R.id.ButtonLogin);
        button_login.setOnClickListener(onClickButtonLogin());

        int i = 0;
        String usuario = null, senha = null, acesso = null;

        try {
            FileReader arq = new FileReader(getFilesDir().getPath() + "/" + arquivo);
            BufferedReader buff = new BufferedReader(arq);

            //Compara o usuário e senha da memoria interna com o Banco Local
            String line = buff.readLine();
            while (line != null) {
                switch (i) {
                    case 1:
                        usuario = line;
                        break;
                    case 2:
                        senha = line;
                        break;
                    case 3:
                        acesso = line;
                        break;
                }
                i++;
                line = buff.readLine();
            }
            buff.close();
            arq.close();
            LedStockDB db = new LedStockDB(getApplicationContext());
            if ((usuario != null) && (senha != null)) {
                if (db.SelectUser(usuario, senha)) {
                    Global var_global = new Global();

                    var_global.setUsuario(usuario);
                    var_global.setSenha(senha);
                    var_global.setAcesso(acesso);

                    Intent intent = new Intent(getApplicationContext(), Container_Main.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Usuário/Senha Inválida !", Toast.LENGTH_SHORT).show();
                }
            } else {
                File file = new File(getFilesDir().getPath() + "/" + arquivo);
                if (file.delete()) {
                    LedStockDB db_delete = new LedStockDB(getApplicationContext());
                    db_delete.DeleteAllUsers();
                    Log.d(TAG, "Arquivo deletado com sucesso !");
                    //dialog.dismiss();
                }
                Toast.makeText(getApplicationContext(), "Não foi possível se Logar !", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e2) {
            try {
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    //Log.d(TAG, "Carregando Usuários para o Banco de Dados !");
                    GetUsersTask();
                } else {
                    Toast.makeText(getApplicationContext(), "Para o primeiro acesso, é necessário conexão com a internet !", Toast.LENGTH_LONG).show();
                }
            } catch (SecurityException es) {
                Log.e(TAG, es.getMessage());
            }
        }
    }

    private View.OnClickListener onClickButtonLogin() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user;
                String pass;
                String access;

                EditText usuario = (EditText) findViewById(R.id.usuario);
                EditText senha = (EditText) findViewById(R.id.senha);

                user = usuario.getText().toString();
                pass = senha.getText().toString();

                LedStockDB db = new LedStockDB(getApplicationContext());

                if (db.SelectUser(user, pass)) {
                    try {
                        //Log.d(TAG, "Tentando editar o arquivo na memoria interna !");
                        FileWriter fw = new FileWriter(getFilesDir().getPath() + "/" + arquivo, false);
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write(fs);
                        bw.newLine();
                        bw.write(user);
                        bw.newLine();
                        bw.write(pass);
                        bw.newLine();
                        access = db.SelectAccess(user, pass);
                        bw.write(access);
                        bw.newLine();
                        bw.flush();
                        bw.close();
                        fw.close();
                        //Log.d(TAG, "Arquivo editado com sucesso !");
                        Global var_global = new Global();
                        var_global.setUsuario(user);
                        var_global.setSenha(pass);
                        var_global.setAcesso(access);
                    } catch (IOException e) {
                        //Log.e(TAG, e.getMessage());
                    }

                    Intent intent = new Intent(getApplicationContext(), Container_Main.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this.getApplicationContext(), "Login/Senha Incorreta !", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }
}