package br.com.ledstock.led_stock.led_stock.services;

import android.content.Context;
import android.content.Intent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import br.com.ledstock.led_stock.led_stock.activity.MainActivity;
import br.com.ledstock.led_stock.led_stock.domain.Array_Users;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.utils.Global;
import br.com.ledstock.led_stock.led_stock.utils.HttpHelper;

public class UserService {

    private final String TAG = "USER_SERVICE";

    public UserService() {
    }

    public boolean getUsers(Context context) throws IOException {

        String url = LedService.url;
        String key = LedService.key;

        String json;
        LedStockDB db = new LedStockDB(context);
        Global global = new Global();

        Map<String, String> mapParams = new HashMap<String, String>();
        mapParams.put("KEY", key);
        mapParams.put("action", "users_all");
        mapParams.put("last_id", db.SelectLastRemoteIDofUsers());

        if (global.getUsuario() != null) {
            mapParams.put("user", global.getUsuario());
        } else {
            mapParams.put("user", "");
        }

        HttpHelper http = new HttpHelper();
        try {
            json = http.doGet(url, mapParams, "UTF-8");
            if (!json.equals("")) {
                try {
                    InsertUser(parserJSON(json), context);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private JSONArray parserJSON(String json) throws JSONException {
        if ((json != null) && (!json.equals(""))) {
            return new JSONArray(json);
        } else {
            return null;
        }
    }

    private void InsertUser(JSONArray json, Context context) throws IOException, JSONException {

        LedStockDB db = new LedStockDB(context);

        try {
            //Insere cada Usuário no Banco de Dados
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonUser = json.getJSONObject(i);
                Array_Users Users = new Array_Users();
                Users.id = jsonUser.optString("id_usuario");
                Users.nome = jsonUser.optString("nome");
                Users.usuario = jsonUser.optString("usuario");
                Users.senha = jsonUser.optString("senha");
                Users.acesso = jsonUser.optString("acesso");

                if (!db.SelectUserByRemoteID(Users.id)){
                    db.Insert_User(Users);
                }
            }
        } catch (JSONException e) {
            //Log.d(TAG, "Não foi possível remover os usuarios !");
            throw new IOException(e.getMessage(), e);
        } finally {
            Global global = new Global();
            String usuario = global.getUsuario();
            String senha = global.getSenha();
            String arquivo = "config.txt";

            if ((usuario != null) && (senha != null)) {
                if (!db.SelectUser(usuario, senha)) {
                    File file = new File(context.getFilesDir().getPath() + "/" + arquivo);
                    if (file.delete()) {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            }
        }
        db.close();
    }
}




