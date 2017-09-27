package br.com.ledstock.led_stock.led_stock.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import br.com.ledstock.led_stock.led_stock.domain.Array_Clients;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.utils.AlarmUtil;
import br.com.ledstock.led_stock.led_stock.utils.Global;
import br.com.ledstock.led_stock.led_stock.utils.HttpHelper;
import br.com.ledstock.led_stock.led_stock.configuration.*;


public class LedService extends Service {


    //Configura ambiente de Teste e ambiente de produção
    public static final String url = Configuration.url_test;
    //Fim da Configuração Teste/Produção

    private final String url_post = url + "?{get_method}";
    public static final String key = "cd5cdb37e1456baff8ce013c435981e84fc8ea80";
    private static String TAG = "LED_STOCK_SERVICE";
    private static Context context;
    long INTERVAL_A_MINUTE = 60 * 1000;
    long INTERVAL_TEN_SECONDS = 10 * 1000;
    private static boolean REFRESH_INTERVAL = false;

    //Variáveis para controle do Alarme
    //Variáveis para Clientes
    private static boolean ICLI = false;
    private static boolean ECLI = false;
    //Variáveis para Lamp
    private static boolean ILAMP = false;
    private static boolean ELAMP = false;
    private static boolean DLAMP = false;
    //Variáveis para Led
    private static boolean ILED = false;
    private static boolean ELED = false;
    private static boolean DLED = false;
    //Variáveis para Mão de Obra
    private static boolean IHAND = false;
    private static boolean EHAND = false;
    private static boolean DHAND = false;
    //Variáveis para Ambientes
    private static boolean IAMBI = false;
    private static boolean EAMBI = false;
    private static boolean DAMBI = false;
    //Variáveis para Usuários
    private static boolean IUSER = false;
    private static boolean EUSER = false;
    private static boolean DUSER = false;
    //Variáveis para Estudos
    private static boolean IEST = false;
    private static boolean EEST = false;
    private static boolean DEST = false;
    //Variável para KWh
    private static boolean EKWh = false;
    //Variáveis para Ambientes do Estudo
    private static boolean IAMBOEST = false;
    private static boolean DAMBOEST = false;
    //Variáveis para Itens do Estudo
    private static boolean IIOFEST = false;
    private static boolean DIOFEST = false;

    public int onStartCommand(Intent intent, int flags, int StartId) {
        return super.onStartCommand(intent, flags, StartId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        
        LoadContent();

        //Registra o Receiver para consultar o Banco de Dados Remotamente
        registerReceiver(ReceiverWatch, new IntentFilter("LS_WATCH"));

        //Intent para consultar a tabela Watch a cada 1 minuto
        Intent intent = new Intent("LS_WATCH");
        //Agenda um Alarme para executar a cada um minuto
        AlarmUtil alarm = new AlarmUtil();
        alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_A_MINUTE);

        //Chama a função de Inicialização
        Init();
    }

    public void LoadContent() {
        //Se tiver conecão com a internet, faça
        if (IsConected()) {
            //Tenta pegar todos os usuários
            try {
                GetUsers();
                GetClients();
                GetLamps();
                GetLeds();
                GetPlaces();
                GetHandsOn();
                GetKWh();
                GetEstudo();
                GetAmbientesOfEstudo();
                GetItensOfEstudo();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                //Libera a Função para atualizar a cada Um segundo
                REFRESH_INTERVAL = true;
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /***********************************************************************************************
     * FUNÇÕES PARA CAPTURAR OS DADOS DAS TABELAS REMOTAS
     ***********************************************************************************************/

    public void GetUsers() throws IOException {
        new Thread() {
            public void run() {
                UserService user = new UserService();
                try {
                    user.getUsers(context);
                    //Log.d(TAG, "Users was catch !");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void GetClients() throws IOException {
        new Thread() {
            public void run() {
                String json = "";
                Global global = new Global();
                LedStockDB db = new LedStockDB(context);

                Map<String, String> mapParams = new HashMap<String, String>();
                mapParams.put("KEY", key);
                mapParams.put("action", "getClients");
                mapParams.put("user", global.getUsuario());
                mapParams.put("last_id", db.LastIdClient());
                HttpHelper http = new HttpHelper();

                try {
                    json = http.doGet(url, mapParams, "UTF-8");
                    if (!json.equals("")) {
                        try {
                            //Carrega a Matriz JSON com os clientes para dentro do DB interno
                            JSONtoDBClients(parserJSON(json));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent();
                    intent.setAction("REFRESH_CLIENTS");
                    context.sendBroadcast(intent);
                }
                db.close();
            }
        }.start();
    }

    public void GetLamps() throws IOException {
        new Thread() {
            public void run() {
                String json = "";
                Global global = new Global();
                LedStockDB db = new LedStockDB(context);

                Map<String, String> mapParams = new HashMap<String, String>();
                mapParams.put("KEY", key);
                mapParams.put("action", "getLamps");
                mapParams.put("user", global.getUsuario());
                mapParams.put("last_id", db.SelectLastRemoteIDofLamp());
                HttpHelper http = new HttpHelper();
                try {
                    json = http.doGet(url, mapParams, "UTF-8");
                    if (!json.equals("")) {
                        try {
                            //Carrega a Matriz JSON com as Lamps para dentro do DB interno
                            JSONtoDBLamps(parserJSON(json));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent();
                    intent.setAction("REFRESH_LAMPS");
                    context.sendBroadcast(intent);
                }
                db.close();
            }
        }.start();
    }

    public void GetLeds() throws IOException {
        new Thread() {
            public void run() {
                String json = "";
                Global global = new Global();
                LedStockDB db = new LedStockDB(context);

                Map<String, String> mapParams = new HashMap<String, String>();
                mapParams.put("KEY", key);
                mapParams.put("action", "getLeds");
                mapParams.put("user", global.getUsuario());
                mapParams.put("last_id", db.SelectLastRemoteIDofLed());
                HttpHelper http = new HttpHelper();
                try {
                    json = http.doGet(url, mapParams, "UTF-8");
                    if (!json.equals("")) {
                        try {
                            //Carrega a Matriz JSON com os Leds para dentro do DB interno
                            JSONtoDBLeds(parserJSON(json));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent();
                    intent.setAction("REFRESH_LEDS");
                    context.sendBroadcast(intent);
                }
                db.close();
            }
        }.start();
    }

    public void GetPlaces() throws IOException {
        new Thread() {
            public void run() {
                String json = "";
                Global global = new Global();
                LedStockDB db = new LedStockDB(context);

                Map<String, String> mapParams = new HashMap<String, String>();
                mapParams.put("KEY", key);
                mapParams.put("action", "getPlaces");
                mapParams.put("user", global.getUsuario());
                mapParams.put("last_id", db.SelectLastRemoteIDofPlaces());
                HttpHelper http = new HttpHelper();
                try {
                    json = http.doGet(url, mapParams, "UTF-8");
                    if (!json.equals("")) {
                        try {
                            //Carrega a Matriz JSON com os Ambientes para dentro do DB interno
                            JSONtoDBAmbientes(parserJSON(json));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent();
                    intent.setAction("REFRESH_AMBIENTES");
                    context.sendBroadcast(intent);
                }
                db.close();
            }
        }.start();
    }

    public void GetHandsOn() throws IOException {
        new Thread() {
            public void run() {
                String json = "";
                Global global = new Global();
                LedStockDB db = new LedStockDB(context);

                Map<String, String> mapParams = new HashMap<String, String>();
                mapParams.put("KEY", key);
                mapParams.put("action", "getHandsOn");
                mapParams.put("user", global.getUsuario());
                mapParams.put("last_id", db.SelectLastRemoteIDofHandsOn());
                HttpHelper http = new HttpHelper();
                try {
                    json = http.doGet(url, mapParams, "UTF-8");
                    if (!json.equals("")) {
                        try {
                            //Carrega a Matriz JSON com os HandsOn para dentro do DB interno
                            JSONtoDBHandsOn(parserJSON(json));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent();
                    intent.setAction("REFRESH_HANDSON");
                    context.sendBroadcast(intent);
                }
                db.close();
            }
        }.start();
    }

    public void GetEstudo() throws IOException {
        new Thread() {
            public void run() {
                String json = "";
                Global global = new Global();
                LedStockDB db = new LedStockDB(context);

                Map<String, String> mapParams = new HashMap<String, String>();
                mapParams.put("KEY", key);
                mapParams.put("action", "getEstudo");
                mapParams.put("user", global.getUsuario());
                mapParams.put("last_id", db.SelectLastRemoteIDofEstudo());
                HttpHelper http = new HttpHelper();
                try {
                    json = http.doGet(url, mapParams, "UTF-8");
                    if (!json.equals("")) {
                        try {
                            //Carrega a Matriz JSON com os HandsOn para dentro do DB interno
                            JSONtoDBEstudo(parserJSON(json));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent();
                    intent.setAction("REFRESH_ESTUDOS");
                    context.sendBroadcast(intent);
                }
                db.close();
            }
        }.start();
    }

    public void GetAmbientesOfEstudo() throws IOException {
        new Thread() {
            public void run() {
                String json = "";
                Global global = new Global();
                LedStockDB db = new LedStockDB(context);

                Map<String, String> mapParams = new HashMap<String, String>();
                mapParams.put("KEY", key);
                mapParams.put("action", "getAmbientesOfEstudo");
                mapParams.put("user", global.getUsuario());
                mapParams.put("last_id", db.SelectLastRemoteIDofAmbientesOfEstudo());
                HttpHelper http = new HttpHelper();
                try {
                    json = http.doGet(url, mapParams, "UTF-8");
                    if (!json.equals("")) {
                        try {
                            //Carrega a Matriz JSON com os HandsOn para dentro do DB interno
                            JSONtoDBAmbientesOfEstudo(parserJSON(json));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent();
                    intent.setAction("REFRESH_AMBIENTESOFESTUDOS");
                    context.sendBroadcast(intent);
                }
                db.close();
            }
        }.start();
    }

    public void GetItensOfEstudo() throws IOException {
        new Thread() {
            public void run() {
                String json = "";
                Global global = new Global();
                LedStockDB db = new LedStockDB(context);

                Map<String, String> mapParams = new HashMap<String, String>();
                mapParams.put("KEY", key);
                mapParams.put("action", "getItensOfEstudo");
                mapParams.put("user", global.getUsuario());
                mapParams.put("last_id", db.SelectLastRemoteIDofItensOfEstudo());
                HttpHelper http = new HttpHelper();
                try {
                    json = http.doGet(url, mapParams, "UTF-8");
                    if (!json.equals("")) {
                        try {
                            //Carrega a Matriz JSON com os HandsOn para dentro do DB interno
                            JSONtoDBItensOfEstudo(parserJSON(json));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent();
                    intent.setAction("REFRESH_ITENS_LEDS");
                    context.sendBroadcast(intent);
                    intent.setAction("REFRESH_ITENS_LAMPS");
                    context.sendBroadcast(intent);
                    intent.setAction("REFRESH_ITENS_HANDSON");
                    context.sendBroadcast(intent);
                }
                db.close();
            }
        }.start();
    }

    public void GetKWh() throws IOException {
        new Thread() {
            public void run() {
                String json = "";

                Map<String, String> mapParams = new HashMap<String, String>();
                mapParams.put("KEY", key);
                mapParams.put("action", "getKWh");
                HttpHelper http = new HttpHelper();
                try {
                    json = http.doGet(url, mapParams, "UTF-8");
                    if (!json.equals("")) {
                        try {
                            //Carrega a Matriz JSON com os HandsOn para dentro do DB interno
                            JSONtoDBKWh(parserJSON(json));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private JSONArray parserJSON(String json) throws JSONException {
        if ((json != null) && (!json.equals(""))) {
            return new JSONArray(json);
        } else {
            return null;
        }
    }

    /***********************************************************************************************
     * FUNÇÕES REFERENTES AOS CLIENTES
     ***********************************************************************************************/

    //Insere Um Cliente no Banco de Dados remoto
    public void InsertClientRemote(final long id_client) {
        //Se tiver conexão com a internet, insere no banco de dados remoto
        if (IsConected()) {
            new Thread() {
                public void run() {
                    Global global = new Global();
                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=InsertClient";
                    String getmethod3 = "user=" + global.getUsuario();
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2 + "&" + getmethod3);
                    String json = "";

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectClientById(String.valueOf(id_client));

                    if (c != null) {
                        Map<String, String> mapParams = new HashMap<String, String>();
                        mapParams.put("nome", c.getString(c.getColumnIndex("nome")));
                        mapParams.put("cnpj_cpf", c.getString(c.getColumnIndex("cnpj_cpf")));
                        mapParams.put("endereco", c.getString(c.getColumnIndex("endereco")));
                        mapParams.put("numero", c.getString(c.getColumnIndex("numero")));
                        mapParams.put("comp", c.getString(c.getColumnIndex("comp")));
                        mapParams.put("cep", c.getString(c.getColumnIndex("cep")));
                        mapParams.put("bairro", c.getString(c.getColumnIndex("bairro")));
                        mapParams.put("cidade", c.getString(c.getColumnIndex("cidade")));
                        mapParams.put("estado", c.getString(c.getColumnIndex("uf")));
                        mapParams.put("contato", c.getString(c.getColumnIndex("contato")));
                        mapParams.put("email", c.getString(c.getColumnIndex("email")));
                        mapParams.put("email2", c.getString(c.getColumnIndex("email2")));
                        mapParams.put("tel", c.getString(c.getColumnIndex("tel")));
                        mapParams.put("tel2", c.getString(c.getColumnIndex("tel2")));
                        HttpHelper http = new HttpHelper();

                        try {
                            json = http.doPost(new_url, mapParams, "UTF-8");
                            if (!json.equals("")) {
                                try {
                                    //Carrega a Matriz JSON com os clientes para dentro do DB interno
                                    JSONtoDBInsertRemoteID(parserJSON(json), String.valueOf(id_client));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.d(TAG, "JSON é vazio !");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {
            if (!ICLI) {
                //Registra o Receiver para Inserir o Cliente Remotamente
                context.registerReceiver(ReceiverInsertClient, new IntentFilter("LS_INSERT_CLIENT_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_INSERT_CLIENT_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                ICLI = true;
            }
        }
    }

    //Edita o Cliente no Banco de Dados Remoto
    public void EditClientRemote(final long id_client) {
        //Se tiver conexão com a internet,edita no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {

                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=EditClient";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectClientById(String.valueOf(id_client));

                    if (c != null) {
                        if (db.SelectRemoteIDById(String.valueOf(id_client)) != 0) {
                            Map<String, String> mapParams = new HashMap<String, String>();
                            Global global = new Global();
                            mapParams.put("user", global.getUsuario());
                            mapParams.put("id_cliente", c.getString(c.getColumnIndex("_id_client_remote")));
                            mapParams.put("nome", c.getString(c.getColumnIndex("nome")));
                            mapParams.put("cnpj_cpf", c.getString(c.getColumnIndex("cnpj_cpf")));
                            mapParams.put("endereco", c.getString(c.getColumnIndex("endereco")));
                            mapParams.put("numero", c.getString(c.getColumnIndex("numero")));
                            mapParams.put("comp", c.getString(c.getColumnIndex("comp")));
                            mapParams.put("cep", c.getString(c.getColumnIndex("cep")));
                            mapParams.put("bairro", c.getString(c.getColumnIndex("bairro")));
                            mapParams.put("cidade", c.getString(c.getColumnIndex("cidade")));
                            mapParams.put("estado", c.getString(c.getColumnIndex("uf")));
                            mapParams.put("contato", c.getString(c.getColumnIndex("contato")));
                            mapParams.put("email", c.getString(c.getColumnIndex("email")));
                            mapParams.put("email2", c.getString(c.getColumnIndex("email2")));
                            mapParams.put("tel", c.getString(c.getColumnIndex("tel")));
                            mapParams.put("tel2", c.getString(c.getColumnIndex("tel2")));
                            HttpHelper http = new HttpHelper();

                            try {
                                http.doPost(new_url, mapParams, "UTF-8");
                                db.RemoveEditedfromClient(String.valueOf(id_client));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {

            if (!ECLI) {
                //Registra o Receiver para Editar o Cliente Remotamente
                context.registerReceiver(ReceiverEditClient, new IntentFilter("LS_EDIT_CLIENT_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_EDIT_CLIENT_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                ECLI = true;
            }
        }
    }

    //Remove o Cliente no Banco de Dados Remoto
    public void RemoveClientRemote(final long id_client_remote) {
        //Se tiver conexão com a internet, remove no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {
                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=RemoveClient";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    Map<String, String> mapParams = new HashMap<String, String>();
                    Global global = new Global();
                    mapParams.put("user", global.getUsuario());
                    mapParams.put("id_cliente", String.valueOf(id_client_remote));
                    HttpHelper http = new HttpHelper();

                    try {
                        http.doPost(new_url, mapParams, "UTF-8");
                        //db.RemoveEditedfromClient(String.valueOf(id_client));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    //Pega o registro do Cliente no Banco de Dados Remoto
    public void GetRegisterClientRemote(final long id_client_remote, final long id_watch) {
        //Se tiver conexão com a internet, remove no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {
                    String json = null;
                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=GetClientByID";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    Map<String, String> mapParams = new HashMap<String, String>();
                    mapParams.put("id_cliente", String.valueOf(id_client_remote));
                    HttpHelper http = new HttpHelper();

                    try {
                        json = http.doPost(new_url, mapParams, "UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = parserJSON(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jsonArray != null) {
                        //Insere cada Usuário no Banco de Dados
                        final int numberOfItemsInResp = jsonArray.length();
                        for (int i = 0; i < numberOfItemsInResp; i++) {
                            try {
                                JSONObject jsonClients = jsonArray.getJSONObject(i);
                                Array_Clients clients = new Array_Clients();
                                clients.id_remoto = jsonClients.optString("id_cliente");
                                clients.nome = jsonClients.optString("nome");
                                clients.cnpj_cpf = jsonClients.optString("cnpj_cpf");
                                clients.endereco = jsonClients.optString("endereco");
                                clients.numero = jsonClients.optString("numero");
                                clients.comp = jsonClients.optString("comp");
                                clients.cep = jsonClients.optString("cep");
                                clients.bairro = jsonClients.optString("bairro");
                                clients.cidade = jsonClients.optString("cidade");
                                clients.uf = jsonClients.optString("estado");
                                clients.contato = jsonClients.optString("contato");
                                clients.email = jsonClients.optString("email");
                                clients.email2 = jsonClients.optString("email2");
                                clients.tel1 = jsonClients.optString("tel");
                                clients.tel2 = jsonClients.optString("tel2");

                                LedStockDB db = new LedStockDB(context);
                                long id = db.SelectClientIDByIDRemote(String.valueOf(id_client_remote));
                                db.UpDate_ClientById(String.valueOf(id), clients);
                                db.WatchMarkDone(id_watch);

                                db.close();

                            } catch (JSONException e) {
                                e.printStackTrace();

                            } finally {
                                Intent intent = new Intent();
                                intent.setAction("REFRESH_CLIENTS");
                                context.sendBroadcast(intent);
                            }

                        }

                    }

                }
            }.start();
        }
    }

    private void JSONtoDBInsertRemoteID(JSONArray json, String id_client) throws IOException {
        if (json != null) {
            try {
                //Insere cada Usuário no Banco de Dados
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonClients = json.getJSONObject(i);
                    LedStockDB db = new LedStockDB(context);
                    db.InsertRemoteID(jsonClients.optString("id_cliente"), id_client);
                    db.close();
                }
            } catch (JSONException e) {
                //Log.e(TAG, e.getMessage());
            }
        }
    }

    private void JSONtoDBClients(JSONArray json) throws IOException {
        if (json != null) {
            try {
                //Insere cada Usuário no Banco de Dados
                for (int i = 0; i < json.length(); i++) {
                    Array_Clients clients = new Array_Clients();
                    JSONObject jsonClients = json.getJSONObject(i);
                    LedStockDB db = new LedStockDB(context);
                    clients.id_remoto = jsonClients.optString("id_cliente");
                    clients.nome = jsonClients.optString("nome");
                    clients.cnpj_cpf = jsonClients.optString("cnpj_cpf");
                    clients.endereco = jsonClients.optString("endereco");
                    clients.numero = jsonClients.optString("numero");
                    clients.comp = jsonClients.optString("comp");
                    clients.cep = jsonClients.optString("cep");
                    clients.bairro = jsonClients.optString("bairro");
                    clients.cidade = jsonClients.optString("cidade");
                    clients.uf = jsonClients.optString("estado");
                    clients.contato = jsonClients.optString("contato");
                    clients.email = jsonClients.optString("email");
                    clients.email2 = jsonClients.optString("email2");
                    clients.tel1 = jsonClients.optString("tel");
                    clients.tel2 = jsonClients.optString("tel2");
                    db.Insert_Client(clients);
                    db.close();
                }
            } catch (JSONException e) {
                //Log.d(TAG, "Não foi possível remover os usuarios !");
                throw new IOException(e.getMessage(), e);
            }
        }
    }

    /***********************************************************************************************
     * FUNÇÕES REFERENTES A LAMP
     ***********************************************************************************************/

    //Insere Uma Lamp no Banco de Dados remoto
    public void InsertLampRemote(final long id_lamp) {
        //Se tiver conexão com a internet, insere no banco de dados remoto
        if (IsConected()) {
            new Thread() {
                public void run() {
                    Global global = new Global();
                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=InsertLamp";
                    String getmethod3 = "user=" + global.getUsuario();
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2 + "&" + getmethod3);
                    String json = "";

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectLampByID(String.valueOf(id_lamp));

                    if (c != null) {
                        Map<String, String> mapParams = new HashMap<String, String>();
                        mapParams.put("descricao", c.getString(c.getColumnIndex("descricao")));
                        mapParams.put("potencia", c.getString(c.getColumnIndex("potencia")));
                        HttpHelper http = new HttpHelper();

                        try {
                            json = http.doPost(new_url, mapParams, "UTF-8");
                            if (!json.equals("")) {
                                try {
                                    //Carrega a Matriz JSON com os clientes para dentro do DB interno
                                    JSONtoDBInsertLampRemoteID(parserJSON(json), String.valueOf(id_lamp));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.d(TAG, "JSON é vazio !");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {
            if (!ILAMP) {
                //Registra o Receiver para Inserir uma Lamp Remotamente
                context.registerReceiver(ReceiverInsertLamp, new IntentFilter("LS_INSERT_LAMP_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_INSERT_LAMP_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                ILAMP = true;
            }
        }
    }

    //Edita Uma Lamp no Banco de Dados remoto
    public void UpdateLampRemote(final long id_lamp) {
        //Se tiver conexão com a internet,edita no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {

                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=EditLamp";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectLampByID(String.valueOf(id_lamp));

                    if (c != null) {
                        if (db.SelectLampRemoteIDById(String.valueOf(id_lamp)) != 0) {
                            Map<String, String> mapParams = new HashMap<String, String>();
                            Global global = new Global();
                            mapParams.put("user", global.getUsuario());
                            mapParams.put("id_lamp", c.getString(c.getColumnIndex("_id_lamp_remote")));
                            mapParams.put("descricao", c.getString(c.getColumnIndex("descricao")));
                            mapParams.put("potencia", c.getString(c.getColumnIndex("potencia")));

                            HttpHelper http = new HttpHelper();

                            try {
                                http.doPost(new_url, mapParams, "UTF-8");
                                db.RemoveEditedfromLamp(String.valueOf(id_lamp));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {

            if (!ELAMP) {
                //Registra o Receiver para Editar a Lamp Remotamente
                context.registerReceiver(ReceiverEditLamp, new IntentFilter("LS_EDIT_LAMP_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_EDIT_LAMP_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                ELAMP = true;
            }
        }
    }

    //Deleta Lampa Remotamente
    public void DeleteLampRemote(final String id_lamp) {
        //Se tiver conexão com a internet,edita no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {

                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=DeleteLamp";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectLampByID(id_lamp);

                    if (c != null) {
                        if (db.SelectLampRemoteIDById(id_lamp) != 0) {
                            Map<String, String> mapParams = new HashMap<String, String>();
                            Global global = new Global();
                            mapParams.put("user", global.getUsuario());
                            mapParams.put("id_lamp", c.getString(c.getColumnIndex("_id_lamp_remote")));
                            HttpHelper http = new HttpHelper();

                            try {
                                http.doPost(new_url, mapParams, "UTF-8");
                                //db.Remove EditedfromClient(String.valueOf(id_client));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {
            if (!DLAMP) {
                //Registra o Receiver para Deletar a Lamp Remotamente
                context.registerReceiver(ReceiverDeleteLamp, new IntentFilter("LS_DELETE_LAMP_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_DELETE_LAMP_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                DLAMP = true;
            }
        }
    }

    //Insert a Lamp Remote ID
    private void JSONtoDBInsertLampRemoteID(JSONArray json, String id_lamp) throws IOException {
        if (json != null) {
            try {
                //Insere cada Lamp no Banco de Dados
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonLamp = json.getJSONObject(i);
                    LedStockDB db = new LedStockDB(context);
                    db.InsertLampRemoteID(jsonLamp.optString("id_lamp"), id_lamp);
                    db.close();
                }
            } catch (JSONException e) {
                //Log.e(TAG, e.getMessage());
            }
        }
    }

    private void JSONtoDBLamps(JSONArray json) throws IOException {
        if (json != null) {
            String descricao;
            String potencia;
            String id_remoto;
            String enable;
            try {
                //Insere cada Lamp no Banco de Dados
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonLamps = json.getJSONObject(i);
                    LedStockDB db = new LedStockDB(context);
                    descricao = jsonLamps.optString("descricao");
                    potencia = jsonLamps.optString("potencia");
                    id_remoto = jsonLamps.optString("id_lamp");
                    enable = jsonLamps.optString("enable");
                    db.Insert_Lamp(descricao, potencia, id_remoto, enable);
                    db.close();
                }
            } catch (JSONException e) {
                throw new IOException(e.getMessage(), e);
            }
        }
    }

    //Pega o registro da Lamp no Banco de Dados Remoto
    public void GetRegisterLampRemote(final long id_lamp_remote, final long id_watch) {
        //Se tiver conexão com a internet, remove no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {
                    String json = null;
                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=GetLampByID";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    Map<String, String> mapParams = new HashMap<String, String>();
                    mapParams.put("id_lamp", String.valueOf(id_lamp_remote));
                    HttpHelper http = new HttpHelper();

                    try {
                        json = http.doPost(new_url, mapParams, "UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = parserJSON(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (jsonArray != null) {

                        //Insere cada Usuário no Banco de Dados
                        final int numberOfItemsInResp = jsonArray.length();
                        for (int i = 0; i < numberOfItemsInResp; i++) {
                            try {
                                JSONObject jsonLamps = jsonArray.getJSONObject(i);
                                String descricao = jsonLamps.optString("descricao");
                                String potencia = jsonLamps.optString("potencia");

                                LedStockDB db = new LedStockDB(context);
                                long id = db.SelectLampIDByIDRemote(String.valueOf(id_lamp_remote));
                                db.Update_Lamp(String.valueOf(id), descricao, potencia, "0");
                                db.WatchMarkDone(id_watch);
                                db.close();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                Intent intent = new Intent();
                                intent.setAction("REFRESH_LAMPS");
                                context.sendBroadcast(intent);
                            }
                        }
                    }
                }
            }.start();
        }
    }


    /***********************************************************************************************
     * FUNÇÕES REFERENTES A SOLUÇÃO LED
     ***********************************************************************************************/

    //Insere Uma Lamp no Banco de Dados remoto
    public void InsertLEDRemote(final long id_led) {
        //Se tiver conexão com a internet, insere no banco de dados remoto
        if (IsConected()) {
            new Thread() {
                public void run() {
                    Global global = new Global();
                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=InsertLed";
                    String getmethod3 = "user=" + global.getUsuario();
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2 + "&" + getmethod3);
                    String json = "";

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectLEDByID(String.valueOf(id_led));

                    if (c != null) {
                        Map<String, String> mapParams = new HashMap<String, String>();
                        mapParams.put("descricao", c.getString(c.getColumnIndex("descricao")));
                        mapParams.put("potencia", c.getString(c.getColumnIndex("potencia")));
                        mapParams.put("valor", c.getString(c.getColumnIndex("valor")));
                        mapParams.put("valor_revenda", c.getString(c.getColumnIndex("valor_revenda")));
                        HttpHelper http = new HttpHelper();

                        try {
                            json = http.doPost(new_url, mapParams, "UTF-8");
                            if (!json.equals("")) {
                                try {
                                    //Carrega a Matriz JSON com os LEDS para dentro do DB interno
                                    JSONtoDBInsertLEDRemoteID(parserJSON(json), String.valueOf(id_led));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.d(TAG, "JSON é vazio !");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {
            if (!ILED) {
                //Registra o Receiver para Inserir um LED Remotamente
                context.registerReceiver(ReceiverInsertLED, new IntentFilter("LS_INSERT_LED_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_INSERT_LED_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                ILED = true;
            }
        }
    }

    //Edita Uma Lamp no Banco de Dados remoto
    public void UpdateLEDRemote(final long id_led) {
        //Se tiver conexão com a internet,edita no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {

                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=EditLed";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectLEDByID(String.valueOf(id_led));

                    if (c != null) {
                        if (db.SelectLedRemoteIDById(String.valueOf(id_led)) != 0) {
                            Map<String, String> mapParams = new HashMap<String, String>();
                            Global global = new Global();
                            mapParams.put("user", global.getUsuario());
                            mapParams.put("id_led", c.getString(c.getColumnIndex("_id_led_remote")));
                            mapParams.put("descricao", c.getString(c.getColumnIndex("descricao")));
                            mapParams.put("potencia", c.getString(c.getColumnIndex("potencia")));
                            mapParams.put("valor", c.getString(c.getColumnIndex("valor")));
                            mapParams.put("valor_revenda", c.getString(c.getColumnIndex("valor_revenda")));

                            HttpHelper http = new HttpHelper();

                            try {
                                http.doPost(new_url, mapParams, "UTF-8");
                                db.RemoveEditedfromLed(String.valueOf(id_led));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {
            if (!ELED) {
                //Registra o Receiver para Editar a Lamp Remotamente
                context.registerReceiver(ReceiverEditLed, new IntentFilter("LS_EDIT_LED_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_EDIT_LED_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                ELED = true;
            }
        }
    }

    //Deleta Lampa Remotamente
    public void DeleteLedRemote(final String id_led) {
        //Se tiver conexão com a internet,edita no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {

                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=DeleteLed";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectLEDByID(id_led);

                    if (c != null) {
                        if (db.SelectLedRemoteIDById(id_led) != 0) {
                            Map<String, String> mapParams = new HashMap<String, String>();
                            Global global = new Global();
                            mapParams.put("user", global.getUsuario());
                            mapParams.put("id_led", c.getString(c.getColumnIndex("_id_led_remote")));
                            HttpHelper http = new HttpHelper();

                            try {
                                http.doPost(new_url, mapParams, "UTF-8");
                                //db.Remove EditedfromClient(String.valueOf(id_client));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {
            if (!DLED) {
                //Registra o Receiver para Deletar a Lamp Remotamente
                context.registerReceiver(ReceiverDeleteLed, new IntentFilter("LS_DELETE_LED_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_DELETE_LED_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                DLED = true;
            }
        }
    }

    //Insert um Led Remote ID
    private void JSONtoDBInsertLEDRemoteID(JSONArray json, String id_led) throws IOException {
        if (json != null) {
            try {
                //Insere cada LED no Banco de Dados
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonLED = json.getJSONObject(i);
                    LedStockDB db = new LedStockDB(context);
                    db.InsertLEDRemoteID(jsonLED.optString("id_led"), id_led);
                    db.close();
                }
            } catch (JSONException e) {
                //Log.e(TAG, e.getMessage());
            }
        }
    }

    private void JSONtoDBLeds(JSONArray json) throws IOException {
        if (json != null) {
            String descricao;
            String potencia;
            String id_remoto;
            String valor;
            String valor_revenda;
            String enable;
            try {
                //Insere cada Led no Banco de Dados
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonLeds = json.getJSONObject(i);
                    LedStockDB db = new LedStockDB(context);
                    descricao = jsonLeds.optString("descricao");
                    potencia = jsonLeds.optString("potencia");
                    id_remoto = jsonLeds.optString("id_led");
                    valor = jsonLeds.optString("valor");
                    valor_revenda = jsonLeds.optString("valor_revenda");
                    enable = jsonLeds.optString("enable");
                    db.Insert_LED(descricao, potencia, Double.parseDouble(valor), Double.parseDouble(valor_revenda), id_remoto, enable);
                    db.close();
                }
            } catch (JSONException e) {
                throw new IOException(e.getMessage(), e);
            }
        }
    }

    //Pega o registro da Led no Banco de Dados Remoto
    public void GetRegisterLedRemote(final long id_led_remote, final long id_watch) {
        //Se tiver conexão com a internet, remove no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {
                    String json = null;
                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=GetLedByID";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    Map<String, String> mapParams = new HashMap<String, String>();
                    mapParams.put("id_led", String.valueOf(id_led_remote));
                    HttpHelper http = new HttpHelper();

                    try {
                        json = http.doPost(new_url, mapParams, "UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = parserJSON(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (jsonArray != null) {
                        //Insere cada Usuário no Banco de Dados
                        final int numberOfItemsInResp = jsonArray.length();
                        for (int i = 0; i < numberOfItemsInResp; i++) {
                            try {
                                JSONObject jsonLeds = jsonArray.getJSONObject(i);
                                String descricao = jsonLeds.optString("descricao");
                                String potencia = jsonLeds.optString("potencia");
                                String valor = jsonLeds.optString("valor");
                                String valor_revenda = jsonLeds.optString("valor_revenda");

                                LedStockDB db = new LedStockDB(context);
                                long id = db.SelectLedIDByIDRemote(String.valueOf(id_led_remote));
                                db.Update_Led(String.valueOf(id), descricao, potencia, Double.parseDouble(valor),
                                        Double.parseDouble(valor_revenda), "0");
                                db.WatchMarkDone(id_watch);
                                db.close();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                Intent intent = new Intent();
                                intent.setAction("REFRESH_LEDS");
                                context.sendBroadcast(intent);
                            }
                        }
                    }
                }
            }.start();
        }
    }

    /***********************************************************************************************
     * FUNÇÕES REFERENTES AOS AMBIENTES
     ***********************************************************************************************/

    //Insere Uma Lamp no Banco de Dados remoto
    public void InsertAmbienteRemote(final long id_ambiente) {
        //Se tiver conexão com a internet, insere no banco de dados remoto
        if (IsConected()) {
            new Thread() {
                public void run() {
                    Global global = new Global();
                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=InsertAmbiente";
                    String getmethod3 = "user=" + global.getUsuario();
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2 + "&" + getmethod3);
                    String json = "";

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectAmbienteByID(String.valueOf(id_ambiente));

                    if (c != null) {
                        if (db.SelectAmbienteRemoteIDById(String.valueOf(id_ambiente)) == 0) {
                            Map<String, String> mapParams = new HashMap<String, String>();
                            mapParams.put("descricao", c.getString(c.getColumnIndex("descricao")));
                            HttpHelper http = new HttpHelper();

                            try {
                                json = http.doPost(new_url, mapParams, "UTF-8");
                                if (!json.equals("")) {
                                    try {
                                        //Carrega a Matriz JSON com os ambientes para dentro do DB interno
                                        JSONtoDBInsertAmbienteRemoteID(parserJSON(json), String.valueOf(id_ambiente));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Log.d(TAG, "JSON é vazio !");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {
            if (!IAMBI) {
                //Registra o Receiver para Inserir um Ambiente Remotamente
                context.registerReceiver(ReceiverInsertAmbiente, new IntentFilter("LS_INSERT_AMBIENTE_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_INSERT_AMBIENTE_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                IAMBI = true;
            }
        }
    }

    //Insert a Ambient Remote ID
    private void JSONtoDBInsertAmbienteRemoteID(JSONArray json, String id_ambiente) throws
            IOException {
        if (json != null) {
            try {
                //Insere cada Ambiente no Banco de Dados
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonAmbiente = json.getJSONObject(i);
                    LedStockDB db = new LedStockDB(context);
                    db.InsertAmbienteRemoteID(jsonAmbiente.optString("id_ambiente"), id_ambiente);
                    db.close();
                }
            } catch (JSONException e) {
                //Log.e(TAG, e.getMessage());
            }
        }
    }

    //Edita Um Ambiente no Banco de Dados remoto
    public void UpdateAmbienteRemote(final long id_ambiente) {
        //Se tiver conexão com a internet,edita no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {

                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=EditAmbiente";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectAmbienteByID(String.valueOf(id_ambiente));

                    if (c != null) {
                        if (db.SelectAmbienteRemoteIDById(String.valueOf(id_ambiente)) != 0) {
                            Map<String, String> mapParams = new HashMap<String, String>();
                            Global global = new Global();
                            mapParams.put("user", global.getUsuario());
                            mapParams.put("id_ambiente", c.getString(c.getColumnIndex("_id_ambiente_remote")));
                            mapParams.put("descricao", c.getString(c.getColumnIndex("descricao")));

                            HttpHelper http = new HttpHelper();

                            try {
                                http.doPost(new_url, mapParams, "UTF-8");
                                db.RemoveEditedfromAmbientes(String.valueOf(id_ambiente));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {
            if (!EAMBI) {
                //Registra o Receiver para Editar o Ambiente Remotamente
                context.registerReceiver(ReceiverEditAmbiente, new IntentFilter("LS_EDIT_AMBIENTE_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_EDIT_AMBIENTE_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                EAMBI = true;
            }
        }
    }

    //Deleta Lampa Remotamente
    public void DeleteAmbienteRemote(final String id_ambiente) {
        //Se tiver conexão com a internet,edita no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {

                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=DeleteAmbiente";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectAmbienteByID(id_ambiente);

                    if (c != null) {
                        if (db.SelectAmbienteRemoteIDById(id_ambiente) != 0) {
                            Map<String, String> mapParams = new HashMap<String, String>();
                            Global global = new Global();
                            mapParams.put("user", global.getUsuario());
                            mapParams.put("id_ambiente", c.getString(c.getColumnIndex("_id_ambiente_remote")));
                            HttpHelper http = new HttpHelper();

                            try {
                                http.doPost(new_url, mapParams, "UTF-8");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {
            if (!DAMBI) {
                //Registra o Receiver para Deletar o Ambiente Remotamente
                context.registerReceiver(ReceiverDeleteAmbiente, new IntentFilter("LS_DELETE_AMBIENTE_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_DELETE_AMBIENTE_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                DAMBI = true;
            }
        }
    }

    private void JSONtoDBAmbientes(JSONArray json) throws IOException {
        if (json != null) {
            String descricao;
            String id_remoto;
            String enable;
            try {
                //Insere cada Ambiente no Banco de Dados
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonAmbiente = json.getJSONObject(i);
                    LedStockDB db = new LedStockDB(context);
                    descricao = jsonAmbiente.optString("descricao");
                    id_remoto = jsonAmbiente.optString("id_ambiente");
                    enable = jsonAmbiente.optString("enable");
                    db.Insert_Place(descricao, id_remoto, enable);
                    db.close();
                }
            } catch (JSONException e) {
                throw new IOException(e.getMessage(), e);
            }
        }
    }

    //Pega o registro da Led no Banco de Dados Remoto
    public void GetRegisterAmbienteRemote(final long id_ambiente_remote, final long id_watch) {
        //Se tiver conexão com a internet, remove no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {
                    String json = null;
                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=GetAmbienteByID";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    Map<String, String> mapParams = new HashMap<String, String>();
                    mapParams.put("id_ambiente", String.valueOf(id_ambiente_remote));
                    HttpHelper http = new HttpHelper();

                    try {
                        json = http.doPost(new_url, mapParams, "UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = parserJSON(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jsonArray != null) {
                        //Insere cada Usuário no Banco de Dados
                        final int numberOfItemsInResp = jsonArray.length();
                        for (int i = 0; i < numberOfItemsInResp; i++) {
                            try {
                                JSONObject jsonPlaces = jsonArray.getJSONObject(i);
                                String descricao = jsonPlaces.optString("descricao");

                                LedStockDB db = new LedStockDB(context);
                                long id = db.SelectAmbienteIDByIDRemote(String.valueOf(id_ambiente_remote));
                                db.Update_Ambiente(String.valueOf(id), descricao, "0");
                                db.WatchMarkDone(id_watch);
                                db.close();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                Intent intent = new Intent();
                                intent.setAction("REFRESH_AMBIENTES");
                                context.sendBroadcast(intent);
                            }
                        }
                    }
                }
            }.start();
        }
    }

    /***********************************************************************************************
     * FUNÇÕES REFERENTES AOS USUÁRIOS
     ***********************************************************************************************/

    //Insere Um Usuário no Banco de Dados remoto
    public void InsertUserRemote(final long id_user) {
        //Se tiver conexão com a internet, insere no banco de dados remoto
        if (IsConected()) {
            new Thread() {
                public void run() {
                    Global global = new Global();
                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=InsertUser";
                    String getmethod3 = "user=" + global.getUsuario();
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2 + "&" + getmethod3);
                    String json = "";

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectUserByID(String.valueOf(id_user));

                    if (c != null) {
                        Map<String, String> mapParams = new HashMap<String, String>();
                        mapParams.put("nome", c.getString(c.getColumnIndex("nome")));
                        mapParams.put("usuario", c.getString(c.getColumnIndex("usuario")));
                        mapParams.put("senha", c.getString(c.getColumnIndex("senha")));
                        mapParams.put("acesso", c.getString(c.getColumnIndex("acesso")));
                        HttpHelper http = new HttpHelper();

                        try {
                            json = http.doPost(new_url, mapParams, "UTF-8");
                            if (!json.equals("")) {
                                try {
                                    //Carrega a Matriz JSON com os ambientes para dentro do DB interno
                                    JSONtoDBInsertUserRemoteID(parserJSON(json), String.valueOf(id_user));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {
            if (!IUSER) {
                //Registra o Receiver para Inserir um Usuario Remotamente
                context.registerReceiver(ReceiverInsertUsers, new IntentFilter("LS_INSERT_USER_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_INSERT_USER_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                IUSER = true;
            }
        }
    }

    //Insert a Ambient Remote ID
    private void JSONtoDBInsertUserRemoteID(JSONArray json, String id_user) throws IOException {
        if (json != null) {
            try {
                //Insere cada Ambiente no Banco de Dados
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonUser = json.getJSONObject(i);
                    LedStockDB db = new LedStockDB(context);
                    db.InsertUserRemoteID(jsonUser.optString("id_usuario"), id_user);
                    db.close();
                }
            } catch (JSONException e) {
                //Log.e(TAG, e.getMessage());
            }
        }
    }

    //Edita Um Ambiente no Banco de Dados remoto
    public void UpdateUserRemote(final long id_user) {
        //Se tiver conexão com a internet,edita no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {

                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=EditUser";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectUserByID(String.valueOf(id_user));

                    if (c != null) {
                        if (db.SelectUserRemoteIDById(String.valueOf(id_user)) != 0) {
                            Map<String, String> mapParams = new HashMap<String, String>();
                            Global global = new Global();
                            mapParams.put("user", global.getUsuario());
                            mapParams.put("id_usuario", c.getString(c.getColumnIndex("_id_usuario_remote")));
                            mapParams.put("nome", c.getString(c.getColumnIndex("nome")));
                            mapParams.put("usuario", c.getString(c.getColumnIndex("usuario")));
                            mapParams.put("senha", c.getString(c.getColumnIndex("senha")));
                            mapParams.put("acesso", c.getString(c.getColumnIndex("acesso")));

                            HttpHelper http = new HttpHelper();

                            try {
                                http.doPost(new_url, mapParams, "UTF-8");
                                db.RemoveEditedfromUsers(String.valueOf(id_user));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {
            if (!EUSER) {
                //Registra o Receiver para Editar o Usuarios Remotamente
                context.registerReceiver(ReceiverEditUsers, new IntentFilter("LS_EDIT_USER_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_EDIT_USER_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                EUSER = true;
            }
        }
    }

    //Deleta Lampa Remotamente
    public void DeleteUserRemote(final String id_user) {
        //Se tiver conexão com a internet,edita no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {

                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=DeleteUser";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectUserByID(id_user);

                    if (c != null) {
                        if (db.SelectUserRemoteIDById(id_user) != 0) {
                            Map<String, String> mapParams = new HashMap<String, String>();
                            Global global = new Global();
                            mapParams.put("user", global.getUsuario());
                            mapParams.put("id_usuario", c.getString(c.getColumnIndex("_id_usuario_remote")));
                            HttpHelper http = new HttpHelper();

                            try {
                                http.doPost(new_url, mapParams, "UTF-8");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {
            if (!DUSER) {
                //Registra o Receiver para Deletar o Ambiente Remotamente
                context.registerReceiver(ReceiverDeleteUsers, new IntentFilter("LS_DELETE_USER_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_DELETE_USER_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                DUSER = true;
            }
        }
    }

    //Pega o registro da Led no Banco de Dados Remoto
    public void GetRegisterUsersRemote(final long id_user_remote, final long id_watch) {
        //Se tiver conexão com a internet, remove no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {
                    String json = null;
                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=GetUserByID";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    Map<String, String> mapParams = new HashMap<String, String>();
                    mapParams.put("id_usuario", String.valueOf(id_user_remote));
                    HttpHelper http = new HttpHelper();

                    try {
                        json = http.doPost(new_url, mapParams, "UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = parserJSON(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jsonArray != null) {
                        //Insere cada Usuário no Banco de Dados
                        final int numberOfItemsInResp = jsonArray.length();
                        for (int i = 0; i < numberOfItemsInResp; i++) {
                            try {
                                JSONObject jsonUsers = jsonArray.getJSONObject(i);
                                String nome = jsonUsers.optString("nome");
                                String usuario = jsonUsers.optString("usuario");
                                String senha = jsonUsers.optString("senha");
                                String acesso = jsonUsers.optString("acesso");

                                LedStockDB db = new LedStockDB(context);
                                long id = db.SelectUserIDByIDRemote(String.valueOf(id_user_remote));
                                db.Update_User(String.valueOf(id), nome, usuario, senha, acesso, "0");
                                db.WatchMarkDone(id_watch);
                                db.close();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                Intent intent = new Intent();
                                intent.setAction("REFRESH_USERS");
                                context.sendBroadcast(intent);
                            }
                        }
                    }
                }
            }.start();
        }
    }

    /***********************************************************************************************
     * FUNÇÕES REFERENTES A MAO DE OBRA
     ***********************************************************************************************/

    //Insere Uma Mão de Obra no Banco de Dados remoto
    public void InsertHandsOnRemote(final long id_handson) {
        //Se tiver conexão com a internet, insere no banco de dados remoto
        if (IsConected()) {
            new Thread() {
                public void run() {
                    Global global = new Global();
                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=InsertHandsOn";
                    String getmethod3 = "user=" + global.getUsuario();
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2 + "&" + getmethod3);
                    String json = "";

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectHandsOnByID(String.valueOf(id_handson));

                    if (c != null) {
                        Map<String, String> mapParams = new HashMap<String, String>();
                        mapParams.put("descricao", c.getString(c.getColumnIndex("descricao")));
                        mapParams.put("valor", c.getString(c.getColumnIndex("valor")));
                        HttpHelper http = new HttpHelper();

                        try {
                            json = http.doPost(new_url, mapParams, "UTF-8");
                            if (!json.equals("")) {
                                try {
                                    //Carrega a Matriz JSON com os HandsOn para dentro do DB interno
                                    JSONtoDBInsertHandsOnRemoteID(parserJSON(json), String.valueOf(id_handson));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {
            if (!IHAND) {
                //Registra o Receiver para Inserir uma Mão de Obra Remotamente
                context.registerReceiver(ReceiverInsertHandsOn, new IntentFilter("LS_INSERT_HANDSON_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_INSERT_HANDSON_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                IHAND = true;
            }
        }
    }

    //Insert a HandsOn Remote ID
    private void JSONtoDBInsertHandsOnRemoteID(JSONArray json, String id_handson) throws
            IOException {
        if (json != null) {
            try {
                //Insere cada HandsOn no Banco de Dados
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonHandsOn = json.getJSONObject(i);
                    LedStockDB db = new LedStockDB(context);
                    db.InsertHandsOnRemoteID(jsonHandsOn.optString("id_handson"), id_handson);
                    db.close();
                }
            } catch (JSONException e) {
                //Log.e(TAG, e.getMessage());
            }
        }
    }

    //Edita Um Ambiente no Banco de Dados remoto
    public void UpdateHandsOnRemote(final long id_handson) {
        //Se tiver conexão com a internet,edita no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {

                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=EditHandsOn";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectHandsOnByID(String.valueOf(id_handson));

                    if (c != null) {
                        if (db.SelectHandsOnRemoteIDById(String.valueOf(id_handson)) != 0) {
                            Map<String, String> mapParams = new HashMap<String, String>();
                            Global global = new Global();
                            mapParams.put("user", global.getUsuario());
                            mapParams.put("id_handson", c.getString(c.getColumnIndex("_id_handson_remote")));
                            mapParams.put("descricao", c.getString(c.getColumnIndex("descricao")));
                            mapParams.put("valor", c.getString(c.getColumnIndex("valor")));

                            HttpHelper http = new HttpHelper();

                            try {
                                http.doPost(new_url, mapParams, "UTF-8");
                                db.RemoveEditedfromHandsOn(String.valueOf(id_handson));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {
            if (!EHAND) {
                //Registra o Receiver para Editar a Mão de Obra Remotamente
                context.registerReceiver(ReceiverEditHandsOn, new IntentFilter("LS_EDIT_HANDSON_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_EDIT_HANDSON_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                EHAND = true;
            }
        }
    }

    //Deleta HandsOn Remotamente
    public void DeleteHandsOnRemote(final String id_handson) {
        //Se tiver conexão com a internet,edita no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {

                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=DeleteHandsOn";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectHandsOnByID(id_handson);

                    if (c != null) {
                        if (db.SelectHandsOnRemoteIDById(id_handson) != 0) {
                            Map<String, String> mapParams = new HashMap<String, String>();
                            Global global = new Global();
                            mapParams.put("user", global.getUsuario());
                            mapParams.put("id_handson", c.getString(c.getColumnIndex("_id_handson_remote")));
                            HttpHelper http = new HttpHelper();

                            try {
                                http.doPost(new_url, mapParams, "UTF-8");
                                //db.Remove EditedfromClient(String.valueOf(id_client));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {
            if (!DHAND) {
                //Registra o Receiver para Deletar a Mão de Obra Remotamente
                context.registerReceiver(ReceiverDeleteHandsOn, new IntentFilter("LS_DELETE_HANDSON_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_DELETE_HANDSON_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                DHAND = true;
            }
        }
    }

    private void JSONtoDBHandsOn(JSONArray json) throws IOException {
        if (json != null) {
            String descricao;
            String id_remoto;
            String enable;
            String valor;
            try {
                //Insere cada HandsOn no Banco de Dados
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonHandsOn = json.getJSONObject(i);
                    LedStockDB db = new LedStockDB(context);
                    descricao = jsonHandsOn.optString("descricao");
                    valor = jsonHandsOn.optString("valor");
                    id_remoto = jsonHandsOn.optString("id_handson");
                    enable = jsonHandsOn.optString("enable");
                    db.Insert_HandsOn(descricao, Double.parseDouble(valor), id_remoto, enable);
                    db.close();
                }
            } catch (JSONException e) {
                throw new IOException(e.getMessage(), e);
            }
        }
    }

    //Pega o registro da Led no Banco de Dados Remoto
    public void GetRegisterHandsOnRemote(final long id_handson_remote, final long id_watch) {
        //Se tiver conexão com a internet, remove no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {
                    String json = null;
                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=GetHandsOnByID";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    Map<String, String> mapParams = new HashMap<String, String>();
                    mapParams.put("id_handson", String.valueOf(id_handson_remote));
                    HttpHelper http = new HttpHelper();

                    try {
                        json = http.doPost(new_url, mapParams, "UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = parserJSON(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jsonArray != null) {
                        //Insere cada Usuário no Banco de Dados
                        final int numberOfItemsInResp = jsonArray.length();
                        for (int i = 0; i < numberOfItemsInResp; i++) {
                            try {
                                JSONObject jsonHandsOn = jsonArray.getJSONObject(i);
                                String descricao = jsonHandsOn.optString("descricao");
                                String valor = jsonHandsOn.optString("valor");

                                LedStockDB db = new LedStockDB(context);
                                long id = db.SelectHandsOnIDByIDRemote(String.valueOf(id_handson_remote));
                                db.Update_HandsOn(String.valueOf(id), descricao, Double.parseDouble(valor), "0");
                                db.WatchMarkDone(id_watch);
                                db.close();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                Intent intent = new Intent();
                                intent.setAction("REFRESH_HANDSON");
                                context.sendBroadcast(intent);
                            }
                        }
                    }
                }
            }.start();
        }
    }

    /***********************************************************************************************
     * FUNÇÕES REFERENTES AS FUNÇÕES COM KWh
     ***********************************************************************************************/

    private void JSONtoDBKWh(JSONArray json) throws IOException {
        if (json != null) {
            String valor;
            try {
                //Insere no Banco de Dados
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonKWh = json.getJSONObject(i);
                    LedStockDB db = new LedStockDB(context);
                    valor = jsonKWh.optString("valor");

                    if ((valor == null) || (valor.equals(""))) {
                        db.Update_KWh(0.0);
                        db.close();
                    } else {
                        db.Update_KWh(Double.parseDouble(valor));
                        db.close();
                    }
                }
            } catch (JSONException e) {
                throw new IOException(e.getMessage(), e);
            }
        }
    }

    public void UpdateKWhRemote(final Double valor_atual) {
        //Se tiver conexão com a internet,edita no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {
                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=EditKWh";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    Map<String, String> mapParams = new HashMap<String, String>();
                    Global global = new Global();
                    mapParams.put("user", global.getUsuario());
                    mapParams.put("valor", String.valueOf(valor_atual));
                    HttpHelper http = new HttpHelper();
                    try {
                        http.doPost(new_url, mapParams, "UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            if (!EKWh) {
                //Registra o Receiver para Editar o Preço do KWh Remotamente
                context.registerReceiver(ReceiverEditKWh, new IntentFilter("LS_EDIT_KWH_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_EDIT_KWH_TO_DB");
                intent.putExtra("valor", valor_atual);
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                EKWh = true;
            }
        }
    }

    //Pega o registro da Led no Banco de Dados Remoto
    public void GetRegisteKWhRemote(final long id_watch) {
        //Se tiver conexão com a internet, remove no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {
                    String json = null;
                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=GetKWhbyID";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    Map<String, String> mapParams = new HashMap<String, String>();
                    mapParams.put("id_wkh", "1");
                    HttpHelper http = new HttpHelper();

                    try {
                        json = http.doPost(new_url, mapParams, "UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = parserJSON(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jsonArray != null) {
                        //Insere cada Usuário no Banco de Dados
                        final int numberOfItemsInResp = jsonArray.length();
                        for (int i = 0; i < numberOfItemsInResp; i++) {
                            try {
                                JSONObject jsonkWh = jsonArray.getJSONObject(i);
                                String valor = jsonkWh.optString("valor");
                                LedStockDB db = new LedStockDB(context);
                                db.Update_KWh(Double.parseDouble(valor));
                                db.WatchMarkDone(id_watch);
                                db.close();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }.start();
        }
    }

    /***********************************************************************************************
     * FUNÇÕES REFERENTES AO WATCHER
     ***********************************************************************************************/

    private void JSONtoDBWatch(JSONArray json) throws IOException {
        if (json != null) {
            String id_watch_remote;
            String action;
            String table;
            String table_id;
            int done;

            LedStockDB db = new LedStockDB(context);
            if (db.CountWatch() == 0) {
                done = 1;
            } else {
                done = 0;
            }

            try {
                //Insere no Banco de Dados
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonWatch = json.getJSONObject(i);

                    id_watch_remote = jsonWatch.optString("id_watch");
                    action = jsonWatch.optString("action");
                    table = jsonWatch.optString("_table");
                    table_id = jsonWatch.optString("table_id");

                    db.Insert_Watch(Long.parseLong(id_watch_remote), Integer.parseInt(action),
                            table, Integer.parseInt(table_id),
                            done);
                }
            } catch (JSONException e) {
                throw new IOException(e.getMessage(), e);
            }
            db.close();
        }
    }

    /***********************************************************************************************
     * FUNÇÕES REFERENTES AO ESTUDO
     ***********************************************************************************************/

    //Insere Uma Lamp no Banco de Dados remoto
    public void InsertEstudoRemote(final long id_estudo) {
        //Se tiver conexão com a internet, insere no banco de dados remoto
        if (IsConected()) {
            new Thread() {
                public void run() {
                    Global global = new Global();
                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=InsertEstudo";
                    String getmethod3 = "user=" + global.getUsuario();
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2 + "&" + getmethod3);
                    String json = "";

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectEstudoByID(String.valueOf(id_estudo));

                    if (c != null) {
                        String id_cliente_remoto = c.getString(c.getColumnIndex("_id_client_remote"));

                        if ((id_cliente_remoto != null) && (!id_cliente_remoto.equals(""))) {
                            Map<String, String> mapParams = new HashMap<String, String>();
                            mapParams.put("descricao", c.getString(c.getColumnIndex("descricao")));
                            mapParams.put("id_cliente", id_cliente_remoto);
                            mapParams.put("data", c.getString(c.getColumnIndex("data")));
                            HttpHelper http = new HttpHelper();

                            try {
                                json = http.doPost(new_url, mapParams, "UTF-8");
                                if (!json.equals("")) {
                                    try {
                                        //Carrega a Matriz JSON com os clientes para dentro do DB interno
                                        JSONtoDBInsertEstudoRemoteID(parserJSON(json), String.valueOf(id_estudo));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {

                            String id_client = c.getString(c.getColumnIndex("_id_cliente"));
                            String id_client_remote = db.SelectClientRemoteIDbyID(id_client);

                            if (id_client_remote != null) {
                                db.InsertEstudoClientRemoteID(id_client_remote, String.valueOf(id_estudo));
                                InsertEstudoRemote(id_estudo);
                            }
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {
            if (!IEST) {
                //Registra o Receiver para Inserir uma Lamp Remotamente
                context.registerReceiver(ReceiverInsertEstudo, new IntentFilter("LS_INSERT_ESTUDO_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_INSERT_ESTUDO_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                IEST = true;
            }
        }
    }

    //Insert a Lamp Remote ID
    private void JSONtoDBInsertEstudoRemoteID(JSONArray json, String id_estudo) throws IOException {
        if (json != null) {
            try {
                //Insere cada Lamp no Banco de Dados
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonEstudo = json.getJSONObject(i);
                    LedStockDB db = new LedStockDB(context);
                    db.InsertEstudoRemoteID(jsonEstudo.optString("id_estudo"), id_estudo);
                    db.close();
                }
            } catch (JSONException e) {
                //Log.e(TAG, e.getMessage());
            }
        }
    }

    //Edita Uma Lamp no Banco de Dados remoto
    public void UpdateEstudoRemote(final long id_estudo) {
        //Se tiver conexão com a internet,edita no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {

                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=EditEstudo";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectEstudoByID(String.valueOf(id_estudo));

                    if (c != null) {
                        if (db.SelectEstudoRemoteIDById(String.valueOf(id_estudo)) != 0) {
                            Map<String, String> mapParams = new HashMap<String, String>();
                            Global global = new Global();
                            mapParams.put("user", global.getUsuario());
                            mapParams.put("id_estudo", c.getString(c.getColumnIndex("_id_estudo_remote")));
                            mapParams.put("descricao", c.getString(c.getColumnIndex("descricao")));
                            mapParams.put("pcm", c.getString(c.getColumnIndex("pcm")));
                            mapParams.put("psm", c.getString(c.getColumnIndex("psm")));
                            mapParams.put("data_pedido", c.getString(c.getColumnIndex("data_pedido")));

                            HttpHelper http = new HttpHelper();

                            try {
                                http.doPost(new_url, mapParams, "UTF-8");
                                db.RemoveEditedfromEstudo(String.valueOf(id_estudo));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {
            if (!EEST) {
                //Registra o Receiver para Editar a Lamp Remotamente
                context.registerReceiver(ReceiverEditEstudo, new IntentFilter("LS_EDIT_ESTUDO_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_EDIT_ESTUDO_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                EEST = true;
            }
        }
    }

    private void JSONtoDBEstudo(JSONArray json) throws IOException {
        if (json != null) {
            String descricao;
            String id_remoto;
            String enable;
            String psm;
            String pcm;
            String data;
            String data_pedido;
            String id_cliente;
            try {
                //Insere cada HandsOn no Banco de Dados
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonEstudo = json.getJSONObject(i);
                    LedStockDB db = new LedStockDB(context);
                    descricao = jsonEstudo.optString("descricao");
                    id_remoto = jsonEstudo.optString("id_estudo");
                    id_cliente = jsonEstudo.optString("id_cliente");
                    enable = jsonEstudo.optString("enable");
                    psm = jsonEstudo.optString("psm");
                    pcm = jsonEstudo.optString("pcm");
                    data = jsonEstudo.optString("data");
                    data_pedido = jsonEstudo.optString("data_pedido");
                    db.Insert_Estudo(descricao, null, id_cliente, id_remoto, data, psm, pcm, data_pedido, enable);
                    db.close();
                }
            } catch (JSONException e) {
                throw new IOException(e.getMessage(), e);
            }
        }
    }

    //Deleta Lampa Remotamente
    public void DeleteEstudoRemote(final String id_estudo) {
        //Se tiver conexão com a internet,edita no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {

                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=DeleteEstudo";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectEstudoByID(id_estudo);

                    if (c != null) {
                        if (db.SelectEstudoRemoteIDById(id_estudo) != 0) {
                            Map<String, String> mapParams = new HashMap<String, String>();
                            Global global = new Global();
                            mapParams.put("user", global.getUsuario());
                            mapParams.put("id_estudo", c.getString(c.getColumnIndex("_id_estudo_remote")));
                            HttpHelper http = new HttpHelper();

                            try {
                                http.doPost(new_url, mapParams, "UTF-8");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {
            if (!DEST) {
                //Registra o Receiver para Deletar o Ambiente Remotamente
                context.registerReceiver(ReceiverDeleteEstudo, new IntentFilter("LS_DELETE_ESTUDO_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_DELETE_ESTUDO_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                DEST = true;
            }
        }
    }

    //Pega o registro da Led no Banco de Dados Remoto
    public void GetRegisterEstudoRemote(final long id_estudo_remote, final long id_watch) {
        //Se tiver conexão com a internet, remove no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {
                    String json = null;
                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=GetEstudoByID";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    Map<String, String> mapParams = new HashMap<String, String>();
                    mapParams.put("id_estudo", String.valueOf(id_estudo_remote));
                    HttpHelper http = new HttpHelper();

                    try {
                        json = http.doPost(new_url, mapParams, "UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = parserJSON(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jsonArray != null) {
                        //Insere cada Usuário no Banco de Dados
                        final int numberOfItemsInResp = jsonArray.length();
                        for (int i = 0; i < numberOfItemsInResp; i++) {
                            try {
                                JSONObject jsonEstudo = jsonArray.getJSONObject(i);
                                String descricao = jsonEstudo.optString("descricao");
                                String id_cliente = jsonEstudo.optString("id_cliente");
                                String data = jsonEstudo.optString("data");
                                String data_pedido = jsonEstudo.optString("data_pedido");
                                String psm = jsonEstudo.optString("psm");
                                String pcm = jsonEstudo.optString("pcm");
                                String enable = jsonEstudo.optString("enable");

                                LedStockDB db = new LedStockDB(context);
                                long id = db.SelectEstudoIDByIDRemote(String.valueOf(id_estudo_remote));
                                db.Update_Estudo(String.valueOf(id), descricao, "0", id_cliente, data, data_pedido, psm, pcm, enable);
                                db.WatchMarkDone(id_watch);
                                db.close();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                Intent intent = new Intent();
                                intent.setAction("REFRESH_ESTUDOS");
                                context.sendBroadcast(intent);
                            }
                        }
                    }
                }
            }.start();
        }
    }

    /***********************************************************************************************
     * FUNÇÕES REFERENTES AO AMBIENTE DO ESTUDO
     ***********************************************************************************************/

    //Insere Uma Lamp no Banco de Dados remoto
    public void InsertAmbienteOfEstudoRemote(final long id_AmbienteOfEstudo, final long ID_ESTUDO) {
        //Se tiver conexão com a internet, insere no banco de dados remoto
        if (IsConected()) {
            new Thread() {
                public void run() {
                    Global global = new Global();
                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=InsertAmbienteOfEstudo";
                    String getmethod3 = "user=" + global.getUsuario();
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2 + "&" + getmethod3);
                    String json = "";

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectAmbienteOfEstudoByID(String.valueOf(id_AmbienteOfEstudo));

                    if (c != null) {
                        String id_estudo_remoto = c.getString(c.getColumnIndex("_id_estudo_remote"));
                        if (id_estudo_remoto != null) {
                            String id_ambiente_remoto = c.getString(c.getColumnIndex("_id_ambiente_remote"));
                            if (id_ambiente_remoto != null) {
                                if (db.SelectAmbienteOfEstudoRemoteIDById(String.valueOf(id_AmbienteOfEstudo)) == 0) {
                                    Map<String, String> mapParams = new HashMap<String, String>();
                                    mapParams.put("id_estudo", id_estudo_remoto);
                                    mapParams.put("id_ambiente", id_ambiente_remoto);
                                    HttpHelper http = new HttpHelper();

                                    try {
                                        json = http.doPost(new_url, mapParams, "UTF-8");
                                        if (!json.equals("")) {
                                            try {
                                                //Carrega a Matriz JSON com os clientes para dentro do DB interno
                                                JSONtoDBInsertAmbienteOfEstudoRemoteID(parserJSON(json), String.valueOf(id_AmbienteOfEstudo));

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                String id_ambiente = c.getString(c.getColumnIndex("_id_ambiente"));
                                String id_ambiente_remote = String.valueOf(db.SelectAmbienteRemoteIDById(id_ambiente));

                                if (!id_ambiente_remote.equals("0")) {
                                    db.InsertAmbienteOfEstudoAmbienteRemoteID(id_ambiente_remote, String.valueOf(id_AmbienteOfEstudo));
                                    InsertAmbienteOfEstudoRemote(id_AmbienteOfEstudo, ID_ESTUDO);
                                }
                            }
                        } else {
                            String id_estudo = c.getString(c.getColumnIndex("_id_estudo"));
                            if (id_estudo != null) {
                                String id_estudo_remote = String.valueOf(db.SelectEstudoRemoteIDById(id_estudo));
                                if (!id_estudo_remote.equals("0")) {
                                    db.InsertEstudoOfEstudoAmbienteRemoteID(id_estudo_remote, String.valueOf(id_AmbienteOfEstudo));
                                    InsertAmbienteOfEstudoRemote(id_AmbienteOfEstudo, ID_ESTUDO);
                                }
                            } else {
                                String RemoteID = String.valueOf(db.SelectEstudoRemoteIDById(String.valueOf(ID_ESTUDO)));
                                if (!RemoteID.equals("0")) {
                                    db.InsertEstudoOfEstudoAmbienteRemoteID(RemoteID, String.valueOf(id_AmbienteOfEstudo));
                                    InsertAmbienteOfEstudoRemote(id_AmbienteOfEstudo, ID_ESTUDO);
                                }
                            }
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {
            if (!IAMBOEST) {
                //Registra o Receiver para Inserir uma Lamp Remotamente
                context.registerReceiver(ReceiverInsertAmbienteOfEstudo, new IntentFilter("LS_INSERT_AMBIENTETOESTUDO_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_INSERT_AMBIENTETOESTUDO_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                IAMBOEST = true;
            }
        }
    }

    //Insert a Lamp Remote ID
    private void JSONtoDBInsertAmbienteOfEstudoRemoteID(JSONArray json, String id_AmbienteOfEstudo) throws IOException {
        if (json != null) {
            try {
                //Insere cada Lamp no Banco de Dados
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonAmbienteOfEstudo = json.getJSONObject(i);
                    LedStockDB db = new LedStockDB(context);
                    db.InsertAmbienteOfEstudoRemoteID(jsonAmbienteOfEstudo.optString("id_ambiente_estudo"), id_AmbienteOfEstudo);
                    db.close();
                }
            } catch (JSONException e) {
                //Log.e(TAG, e.getMessage());
            }
        }
    }

    private void JSONtoDBAmbientesOfEstudo(JSONArray json) throws IOException {
        if (json != null) {
            String id_remoto;
            String enable;
            String id_ambiente;
            String id_estudo;
            try {
                //Insere cada HandsOn no Banco de Dados
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonEstudo = json.getJSONObject(i);
                    LedStockDB db = new LedStockDB(context);
                    id_remoto = jsonEstudo.optString("id_ambiente_estudo");
                    id_ambiente = jsonEstudo.optString("id_ambiente");
                    id_estudo = jsonEstudo.optString("id_estudo");
                    enable = jsonEstudo.optString("enable");
                    db.Insert_AmbienteOfEstudo(id_remoto, null, id_estudo, null, id_ambiente, enable);
                    db.close();
                }
            } catch (JSONException e) {
                throw new IOException(e.getMessage(), e);
            }
        }
    }

    public void DeleteAmbienteOfEstudoRemote(final String id_ambiente_estudo) {
        //Se tiver conexão com a internet,edita no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {
                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=DeleteAmbienteOfEstudo";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectAmbienteOfEstudoByID(id_ambiente_estudo);

                    if (c != null) {
                        if (db.SelectAmbienteOfEstudoRemoteIDById(id_ambiente_estudo) != 0) {
                            Map<String, String> mapParams = new HashMap<String, String>();
                            Global global = new Global();
                            mapParams.put("user", global.getUsuario());
                            mapParams.put("id_ambiente_estudo", c.getString(c.getColumnIndex("_id_ambiente_estudo_remote")));
                            HttpHelper http = new HttpHelper();

                            try {
                                http.doPost(new_url, mapParams, "UTF-8");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {
            if (!DAMBOEST) {
                //Registra o Receiver para Deletar o Ambiente Remotamente
                context.registerReceiver(ReceiverDeleteAmbienteOfEstudo, new IntentFilter("LS_DELETE_AMBIENTEOFESTUDO_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_DELETE_AMBIENTEOFESTUDO_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                DAMBOEST = true;
            }
        }
    }

    /***********************************************************************************************
     * FUNÇÕES REFERENTES AOS ITENS DO ESTUDO
     ***********************************************************************************************/

    //Insere Uma Lamp no Banco de Dados remoto
    public void InsertItensOfEstudoRemote(final long id_ItemOfEstudo, final long id_AmbienteOfEstudo, final long ID_ESTUDO) {
        //Se tiver conexão com a internet, insere no banco de dados remoto
        if (IsConected()) {
            new Thread() {
                public void run() {
                    Global global = new Global();
                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=InsertItensOfEstudo";
                    String getmethod3 = "user=" + global.getUsuario();
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2 + "&" + getmethod3);
                    String json;

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectItemOfEstudoByID(String.valueOf(id_ItemOfEstudo));

                    if (c != null) {
                        String key_table = c.getString(c.getColumnIndex("key_table"));
                        String id_estudo_remoto = c.getString(c.getColumnIndex("_id_estudo_remote"));
                        String quant = c.getString(c.getColumnIndex("quantidade"));
                        String hrs = c.getString(c.getColumnIndex("horas"));
                        String _id_table = c.getString(c.getColumnIndex("_id_table"));

                        if (id_estudo_remoto != null) {
                            String id_ambiente_estudo_remoto = c.getString(c.getColumnIndex("_id_ambiente_estudo_remote"));

                            if (id_ambiente_estudo_remoto != null) {
                                String id_lamp_remoto = c.getString(c.getColumnIndex("_id_lamp_remote"));
                                String id_led_remoto = c.getString(c.getColumnIndex("_id_led_remote"));
                                String id_table_remoto = c.getString(c.getColumnIndex("_id_table_remote"));

                                if (key_table.equals("2") && (id_table_remoto == null)) {
                                    String _id_remote = String.valueOf(db.SelectHandsOnRemoteIDById(_id_table));
                                    if (!_id_remote.equals("0")) {
                                        db.InsertTableRemoteIDInItensOfEstudo(_id_remote, String.valueOf(id_ItemOfEstudo));
                                        InsertItensOfEstudoRemote(id_ItemOfEstudo, id_AmbienteOfEstudo, ID_ESTUDO);
                                    }
                                } else {
                                    if (((id_lamp_remoto != null) && (id_led_remoto != null)) || (key_table.equals("2"))) {
                                        if (db.SelectItensOfEstudoRemoteIDById(String.valueOf(id_ItemOfEstudo)) == 0) {
                                            Map<String, String> mapParams = new HashMap<String, String>();
                                            mapParams.put("id_estudo", id_estudo_remoto);
                                            mapParams.put("id_ambiente_estudo", id_ambiente_estudo_remoto);
                                            mapParams.put("key_table", key_table);
                                            mapParams.put("id_lamp", id_lamp_remoto);
                                            mapParams.put("id_led", id_led_remoto);
                                            mapParams.put("id_table", id_table_remoto);
                                            mapParams.put("quantidade", quant);
                                            mapParams.put("horas", hrs);
                                            HttpHelper http = new HttpHelper();

                                            try {
                                                json = http.doPost(new_url, mapParams, "UTF-8");
                                                if (!json.equals("")) {
                                                    try {
                                                        JSONtoDBInsertItensOfEstudoRemoteID(parserJSON(json), String.valueOf(id_ItemOfEstudo));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } else {

                                        String _id_led = c.getString(c.getColumnIndex("_id_led"));
                                        String _id_lamp = c.getString(c.getColumnIndex("_id_lamp"));
                                        String _id_remote;

                                        switch (key_table) {
                                            case "0":
                                                int x = 0;
                                                _id_remote = String.valueOf(db.SelectLampRemoteIDById(_id_lamp));
                                                if (!_id_remote.equals("0")) {
                                                    db.InsertLampRemoteIDInItensOfEstudo(_id_remote, String.valueOf(id_ItemOfEstudo));
                                                    x++;
                                                }
                                                _id_remote = String.valueOf(db.SelectLedRemoteIDById(_id_led));
                                                if (!_id_remote.equals("0")) {
                                                    db.InsertLedRemoteIDInItensOfEstudo(_id_remote, String.valueOf(id_ItemOfEstudo));
                                                    x++;

                                                }
                                                if (x == 2) {
                                                    InsertItensOfEstudoRemote(id_ItemOfEstudo, id_AmbienteOfEstudo, ID_ESTUDO);
                                                }
                                                break;
                                            case "2":
                                                _id_remote = String.valueOf(db.SelectHandsOnRemoteIDById(_id_table));
                                                if (!_id_remote.equals("0")) {
                                                    db.InsertTableRemoteIDInItensOfEstudo(_id_remote, String.valueOf(id_ItemOfEstudo));
                                                    InsertItensOfEstudoRemote(id_ItemOfEstudo, id_AmbienteOfEstudo, ID_ESTUDO);
                                                }
                                                break;
                                        }

                                    }
                                }
                            } else {
                                String id_ambiente_estudo = c.getString(c.getColumnIndex("_id_ambiente_estudo"));

                                if (id_ambiente_estudo != null) {
                                    String id_ambiente_estudo_remote = String.valueOf(db.SelectAmbienteOfEstudoRemoteIDById(id_ambiente_estudo));

                                    if (!id_ambiente_estudo_remote.equals("0")) {
                                        db.InsertAmbienteOfEstudoRemoteIDInItensOfEstudo(id_ambiente_estudo_remote, String.valueOf(id_ItemOfEstudo));
                                        InsertItensOfEstudoRemote(id_ItemOfEstudo, id_AmbienteOfEstudo, ID_ESTUDO);
                                    }
                                } else {
                                    String RemoteID = String.valueOf(db.SelectAmbienteOfEstudoRemoteIDById(String.valueOf(id_AmbienteOfEstudo)));
                                    if (!RemoteID.equals("0")) {
                                        db.InsertAmbienteOfEstudoRemoteIDInItensOfEstudo(RemoteID, String.valueOf(id_ItemOfEstudo));
                                        InsertItensOfEstudoRemote(id_ItemOfEstudo, id_AmbienteOfEstudo, ID_ESTUDO);
                                    }
                                }
                            }
                        } else {
                            String id_estudo = c.getString(c.getColumnIndex("_id_estudo"));
                            if (id_estudo != null) {
                                String id_estudo_remote = String.valueOf(db.SelectEstudoRemoteIDById(id_estudo));
                                if (!id_estudo_remote.equals("0")) {
                                    db.InsertEstudoRemoteInItensOfEstudo(id_estudo_remote, String.valueOf(id_ItemOfEstudo));
                                    InsertItensOfEstudoRemote(id_ItemOfEstudo, id_AmbienteOfEstudo, ID_ESTUDO);
                                }
                            } else {
                                String RemoteID = String.valueOf(db.SelectEstudoRemoteIDById(String.valueOf(ID_ESTUDO)));
                                if (!RemoteID.equals("0")) {
                                    db.InsertEstudoRemoteInItensOfEstudo(RemoteID, String.valueOf(id_ItemOfEstudo));
                                    InsertItensOfEstudoRemote(id_ItemOfEstudo, id_AmbienteOfEstudo, ID_ESTUDO);
                                }
                            }
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {
            if (!IIOFEST) {
                //Registra o Receiver para Inserir uma Lamp Remotamente
                context.registerReceiver(ReceiverInsertItensOfEstudo, new IntentFilter("LS_INSERT_ITENS_OF_ESTUDO_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_INSERT_ITENS_OF_ESTUDO_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                IIOFEST = true;
            }
        }
    }

    private void JSONtoDBInsertItensOfEstudoRemoteID(JSONArray json, String id_ItemOfEstudo) throws IOException {
        if (json != null) {
            try {
                //Insere cada Lamp no Banco de Dados
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonItemOfEstudo = json.getJSONObject(i);
                    LedStockDB db = new LedStockDB(context);
                    db.InsertItemOfEstudoRemoteID(jsonItemOfEstudo.optString("id_itens"), id_ItemOfEstudo);
                    db.close();
                }
            } catch (JSONException e) {
                //Log.e(TAG, e.getMessage());
            }
        }
    }

    private void JSONtoDBItensOfEstudo(JSONArray json) throws IOException {
        if (json != null) {
            String id_remoto;
            String id_estudo;
            String id_ambiente_estudo;
            String key_table;
            String id_table;
            String id_lamp;
            String id_led;
            String quantidade;
            String horas;
            String enable;

            try {
                //Insere cada HandsOn no Banco de Dados
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonItensOfEstudo = json.getJSONObject(i);
                    LedStockDB db = new LedStockDB(context);
                    id_remoto = jsonItensOfEstudo.optString("id_itens");
                    id_estudo = jsonItensOfEstudo.optString("id_estudo");
                    id_ambiente_estudo = jsonItensOfEstudo.optString("id_ambiente_estudo");
                    key_table = jsonItensOfEstudo.optString("key_table");
                    id_table = jsonItensOfEstudo.optString("id_table");
                    id_lamp = jsonItensOfEstudo.optString("id_lamp");
                    id_led = jsonItensOfEstudo.optString("id_led");
                    quantidade = jsonItensOfEstudo.optString("quantidade");
                    horas = jsonItensOfEstudo.optString("horas");
                    enable = jsonItensOfEstudo.optString("enable");
                    String quant;
                    String hrs;
                    if ((quantidade == null) || (quantidade.equals(""))) {
                        quant = "";
                    } else {
                        quant = quantidade;
                    }
                    if ((horas == null) || (horas.equals(""))) {
                        hrs = "";
                    } else {
                        hrs = horas;
                    }
                    db.Insert_Itens_Of_Estudo(id_remoto, null, id_estudo, null, id_ambiente_estudo, Integer.parseInt(key_table),
                            null, id_table, null, id_lamp, null, id_led, quant, hrs, enable);
                    db.close();
                }
            } catch (JSONException e) {
                throw new IOException(e.getMessage(), e);
            }
        }
    }

    public void DeleteItensOfEstudoRemote(final String id_itens) {
        //Se tiver conexão com a internet,edita no DB remoto
        if (IsConected()) {
            new Thread() {
                public void run() {
                    String getmethod1 = "KEY=" + key;
                    String getmethod2 = "action=DeleteItensOfEstudo";
                    String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2);

                    LedStockDB db = new LedStockDB(context);
                    Cursor c = db.SelectItemOfEstudoByID(id_itens);

                    if (c != null) {
                        if (db.SelectItensOfEstudoRemoteIDById(id_itens) != 0) {
                            Map<String, String> mapParams = new HashMap<String, String>();
                            Global global = new Global();
                            mapParams.put("user", global.getUsuario());
                            mapParams.put("id_itens", c.getString(c.getColumnIndex("_id_itens_remote")));
                            HttpHelper http = new HttpHelper();

                            try {
                                http.doPost(new_url, mapParams, "UTF-8");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        c.close();
                    }
                    db.close();
                }
            }.start();
        } else {
            if (!DIOFEST) {
                //Registra o Receiver para Deletar o Ambiente Remotamente
                context.registerReceiver(ReceiverDeleteItensOfEstudo, new IntentFilter("LS_DELETE_ITENSOFESTUDO_TO_DB"));

                //Intent para receber futuramente
                Intent intent = new Intent("LS_DELETE_ITENSOFESTUDO_TO_DB");
                //Agenda um Alarme para executar posteriormente
                AlarmUtil alarm = new AlarmUtil();
                alarm.scheduleRepeat(context, intent, getTime(), INTERVAL_TEN_SECONDS);
                DIOFEST = true;
            }
        }
    }

    /***********************************************************************************************
     * FUNÇÕES REFERENTES A CONEXÃO COM A INTERNET E CAPTURAR A HORA
     ***********************************************************************************************/
    private boolean IsConected() {

        try {
            ConnectivityManager connMgr = (ConnectivityManager)
                    context.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            } else {
                return false;
            }
        } catch (SecurityException e2) {
            //Log.e(TAG, "Sem conexão com a internet !");
            return false;
        }
    }

    public long getTime() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 5);
        return c.getTimeInMillis();
    }

    /***********************************************************************************************
     * FUNÇÕES REFERENTES AOS BROADCASTS RECEIVERS
     ***********************************************************************************************/

    //BroadCast Receiver para Acompanhar as atualizações remotas
    private BroadcastReceiver ReceiverWatch = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            if (IsConected()) {
                new Thread() {
                    public void run() {
                        Global global = new Global();
                        String json;
                        String getmethod1 = "KEY=" + key;
                        String getmethod2 = "action=Watch";
                        String getmethod3 = "user=" + global.getUsuario();
                        String new_url = url_post.replace("{get_method}", getmethod1 + "&" + getmethod2 + "&" + getmethod3);

                        LedStockDB db = new LedStockDB(context);
                        Map<String, String> mapParams = new HashMap<String, String>();

                        if (db.CountWatch() == 0) {
                            mapParams.put("last_id", "");
                        } else {
                            mapParams.put("last_id", db.SelectLastRemoteIDofWatch());
                        }
                        HttpHelper http = new HttpHelper();
                        try {
                            json = http.doPost(new_url, mapParams, "UTF-8");
                            if (!json.equals("")) {
                                try {
                                    JSONtoDBWatch(parserJSON(json));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (IOException ej) {
                            ej.printStackTrace();
                        }
                        LedService service = new LedService();
                        try {
                            service.UpdateDataBase();
                        } catch (IOException eup) {
                            eup.getStackTrace();
                        }
                        db.close();
                    }
                }.start();
            }
        }
    };

    //BroadCast Receiver para Inserir os Clientes Remotamente
    private BroadcastReceiver ReceiverInsertClient = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            if (IsConected()) {
                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectClientsPending();

                if (c != null) {
                    do {
                        // Log.d(TAG, "Inserido contato com ID: " + c.getString(c.getColumnIndex("_id_cliente")));
                        InsertClientRemote(Integer.parseInt(c.getString(c.getColumnIndex("_id_cliente"))));
                    } while (c.moveToNext());
                    c.close();
                } else {
                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    ICLI = false;

                    //Cancela o Registro do Receiver
                    context.unregisterReceiver(ReceiverInsertClient);
                }
                db.close();
            }
        }
    };

    //Broadcast Receiver para Editar o Cliente no banco de dados Remoto
    private BroadcastReceiver ReceiverEditClient = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {

                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectClientsEditPending();

                if (c != null) {
                    do {
                        EditClientRemote(Integer.parseInt(c.getString(c.getColumnIndex("_id_cliente"))));
                    } while (c.moveToNext());
                    c.close();
                } else {

                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    ECLI = false;

                    //Cancela o Registro de Editar o Cliente
                    context.unregisterReceiver(ReceiverEditClient);
                }
                db.close();
            }
        }
    };

    //BroadCast Receiver para Inserir as Lamps Remotamente
    private BroadcastReceiver ReceiverInsertLamp = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {
                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectLampsInsertPending();

                if (c != null) {
                    do {
                        // Log.d(TAG, "Inserido contato com ID: " + c.getString(c.getColumnIndex("_id_cliente")));
                        InsertLampRemote(Integer.parseInt(c.getString(c.getColumnIndex("_id_lamp"))));
                    } while (c.moveToNext());
                    c.close();
                } else {

                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    ILAMP = false;

                    //Cancela o Registro de Inserir uma Lamp
                    context.unregisterReceiver(ReceiverInsertLamp);
                }
                db.close();
            }
        }
    };

    //BroadCast Receiver para Editar as Lamps Remotamente
    private BroadcastReceiver ReceiverEditLamp = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {
                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectLampsEditPending();

                if (c != null) {
                    do {
                        UpdateLampRemote(Integer.parseInt(c.getString(c.getColumnIndex("_id_lamp"))));
                    } while (c.moveToNext());
                    c.close();
                } else {

                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    ELAMP = false;

                    //Cancela o Registro de Editar o lAMP
                    context.unregisterReceiver(ReceiverEditLamp);
                }
                db.close();
            }
        }
    };

    //BroadCast Receiver para Deletar as Lamps Remotamente
    private BroadcastReceiver ReceiverDeleteLamp = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {
                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectLampsDeletePending();
                if (c != null) {
                    do {
                        DeleteLampRemote(c.getString(c.getColumnIndex("_id_lamp")));
                    } while (c.moveToNext());
                    c.close();
                } else {
                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    DLAMP = false;

                    //Cancela o Registro de Deletar a lAMP
                    context.unregisterReceiver(ReceiverDeleteLamp);
                }
                db.close();
            }
        }
    };

    //BroadCast Receiver para Inserir os LEDS Remotamente
    private BroadcastReceiver ReceiverInsertLED = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {
                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectLEDsInsertPending();

                if (c != null) {
                    do {
                        // Log.d(TAG, "Inserido contato com ID: " + c.getString(c.getColumnIndex("_id_cliente")));
                        InsertLEDRemote(Integer.parseInt(c.getString(c.getColumnIndex("_id_led"))));
                    } while (c.moveToNext());
                    c.close();
                } else {
                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    ILED = false;

                    //Cancela o Registro de Inserir um LED
                    context.unregisterReceiver(ReceiverInsertLED);
                }
                db.close();
            }
        }
    };

    //BroadCast Receiver para Editar os Leds Remotamente
    private BroadcastReceiver ReceiverEditLed = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {
                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectLedEditPending();

                if (c != null) {
                    do {
                        UpdateLEDRemote(Integer.parseInt(c.getString(c.getColumnIndex("_id_led"))));
                    } while (c.moveToNext());
                    c.close();
                } else {
                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    ELED = false;

                    //Cancela o Registro de Editar o lED
                    context.unregisterReceiver(ReceiverEditLed);
                }
                db.close();
            }
        }
    };

    //BroadCast Receiver para Deletar as Lamps Remotamente
    private BroadcastReceiver ReceiverDeleteLed = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {
                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectLedDeletePending();
                if (c != null) {
                    do {
                        DeleteLedRemote(c.getString(c.getColumnIndex("_id_led")));
                    } while (c.moveToNext());
                    c.close();
                } else {
                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    DLED = false;

                    //Cancela o Registro de Deletar o LED
                    context.unregisterReceiver(ReceiverDeleteLed);
                }
                db.close();
            }
        }
    };

    //BroadCast Receiver para Inserir os LEDS Remotamente
    private BroadcastReceiver ReceiverInsertAmbiente = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {
                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectAmbientesInsertPending();

                if (c != null) {
                    do {
                        InsertAmbienteRemote(Integer.parseInt(c.getString(c.getColumnIndex("_id_ambiente"))));
                    } while (c.moveToNext());
                    c.close();
                } else {
                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    IAMBI = false;

                    //Cancela o Registro de Inserir um Ambiente
                    context.unregisterReceiver(ReceiverInsertAmbiente);
                }
                db.close();
            }
        }
    };

    //BroadCast Receiver para Editar o Ambiente Remotamente
    private BroadcastReceiver ReceiverEditAmbiente = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {
                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectAmbientesEditPending();

                if (c != null) {
                    do {
                        UpdateAmbienteRemote(Integer.parseInt(c.getString(c.getColumnIndex("_id_ambiente"))));
                    } while (c.moveToNext());
                    c.close();
                } else {
                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    EAMBI = false;

                    //Cancela o Registro de Editar o Ambiente
                    context.unregisterReceiver(ReceiverEditAmbiente);
                }
                db.close();
            }
        }
    };

    //BroadCast Receiver para Deletar o Ambiente Remotamente
    private BroadcastReceiver ReceiverDeleteAmbiente = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {
                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectAmbientesDeletePending();
                if (c != null) {
                    do {
                        DeleteAmbienteRemote(c.getString(c.getColumnIndex("_id_ambiente")));
                    } while (c.moveToNext());
                    c.close();
                } else {
                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    DAMBI = false;

                    //Cancela o Registro de Deletar o Ambiente
                    context.unregisterReceiver(ReceiverDeleteAmbiente);
                }
                db.close();
            }
        }
    };

    //BroadCast Receiver para Inserir os Usuarios Remotamente
    private BroadcastReceiver ReceiverInsertUsers = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {
                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectUsersInsertPending();

                if (c != null) {
                    do {
                        InsertUserRemote(Integer.parseInt(c.getString(c.getColumnIndex("_id_usuario"))));
                    } while (c.moveToNext());
                    c.close();
                } else {
                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    IUSER = false;

                    //Cancela o Registro de Inserir um Usuário
                    context.unregisterReceiver(ReceiverInsertUsers);
                }
                db.close();
            }
        }
    };

    //BroadCast Receiver para Editar o Usuário Remotamente
    private BroadcastReceiver ReceiverEditUsers = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {
                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectUsersEditPending();

                if (c != null) {
                    do {
                        UpdateUserRemote(Integer.parseInt(c.getString(c.getColumnIndex("_id_usuario"))));
                    } while (c.moveToNext());
                    c.close();
                } else {
                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    EUSER = false;

                    //Cancela o Registro de Editar o Usuarios
                    context.unregisterReceiver(ReceiverEditUsers);
                }
                db.close();
            }
        }
    };

    //BroadCast Receiver para Deletar o Usuário Remotamente
    private BroadcastReceiver ReceiverDeleteUsers = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {
                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectUsersDeletePending();
                if (c != null) {
                    do {
                        DeleteUserRemote(c.getString(c.getColumnIndex("_id_usuario")));
                    } while (c.moveToNext());
                    c.close();
                } else {
                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    DUSER = false;

                    //Cancela o Registro de Deletar o Usuario
                    context.unregisterReceiver(ReceiverDeleteUsers);
                }
                db.close();
            }
        }
    };

    //BroadCast Receiver para Inserir as Mãos de Obra Remotamente
    private BroadcastReceiver ReceiverInsertHandsOn = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {
                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectHandsOnInsertPending();

                if (c != null) {
                    do {
                        InsertHandsOnRemote(Integer.parseInt(c.getString(c.getColumnIndex("_id_handson"))));
                    } while (c.moveToNext());
                    c.close();
                } else {
                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    IHAND = false;

                    //Cancela o Registro de Inserir uma Mão de Obra
                    context.unregisterReceiver(ReceiverInsertHandsOn);
                }
                db.close();
            }
        }
    };

    //BroadCast Receiver para Editar a Mão de Obra Remotamente
    private BroadcastReceiver ReceiverEditHandsOn = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {
                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectHandsOnEditPending();

                if (c != null) {
                    do {
                        UpdateHandsOnRemote(Integer.parseInt(c.getString(c.getColumnIndex("_id_handson"))));
                    } while (c.moveToNext());
                    c.close();
                } else {
                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    EHAND = false;

                    //Cancela o Registro de Editar a Mão de Obra
                    context.unregisterReceiver(ReceiverEditHandsOn);
                }
                db.close();
            }
        }
    };

    //BroadCast Receiver para Deletar a Mão de Obra Remotamente
    private BroadcastReceiver ReceiverDeleteHandsOn = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {
                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectHandsOnDeletePending();

                if (c != null) {
                    do {
                        DeleteHandsOnRemote(c.getString(c.getColumnIndex("_id_handson")));
                    } while (c.moveToNext());
                    c.close();
                } else {
                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    DHAND = false;

                    //Cancela o Registro de Deletar a Mão de Obra
                    context.unregisterReceiver(ReceiverDeleteHandsOn);
                }
                db.close();
            }
        }
    };

    //BroadCast Receiver para Editar o Preço do KWh Remotamente
    private BroadcastReceiver ReceiverEditKWh = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {
                Double value = intent.getDoubleExtra("valor", 0);
                if (value != 0) {
                    UpdateKWhRemote(value);
                } else {
                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    EKWh = false;

                    //Cancela o Registro de Editar a Mão de Obra
                    context.unregisterReceiver(ReceiverEditKWh);
                }
            }
        }
    };

    //BroadCast Receiver para Inserir as Lamps Remotamente
    private BroadcastReceiver ReceiverInsertEstudo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {
                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectEstudoInsertPending();

                if (c != null) {
                    do {
                        InsertEstudoRemote(Integer.parseInt(c.getString(c.getColumnIndex("_id_estudo"))));
                    } while (c.moveToNext());
                    c.close();
                } else {
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    IEST = false;

                    //Cancela o Registro de Inserir uma Lamp
                    context.unregisterReceiver(ReceiverInsertEstudo);
                }
                db.close();
            }
        }
    };

    //BroadCast Receiver para Editar o Estudo
    private BroadcastReceiver ReceiverEditEstudo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {
                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectEstudoEditPending();

                if (c != null) {
                    do {
                        UpdateEstudoRemote(Integer.parseInt(c.getString(c.getColumnIndex("_id_estudo"))));
                    } while (c.moveToNext());
                    c.close();
                } else {
                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    EEST = false;

                    //Cancela o Registro de Editar a Mão de Obra
                    context.unregisterReceiver(ReceiverEditEstudo);
                }
                db.close();
            }
        }
    };

    //BroadCast Receiver para Deletar a Mão de Obra Remotamente
    private BroadcastReceiver ReceiverDeleteEstudo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {
                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectEstudoDeletePending();

                if (c != null) {
                    do {
                        DeleteEstudoRemote(c.getString(c.getColumnIndex("_id_estudo")));
                    } while (c.moveToNext());
                    c.close();
                } else {
                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    DEST = false;

                    //Cancela o Registro de Deletar a Mão de Obra
                    context.unregisterReceiver(ReceiverDeleteEstudo);
                }
                db.close();
            }
        }
    };

    //BroadCast Receiver para Inserir as Lamps Remotamente
    private BroadcastReceiver ReceiverInsertAmbienteOfEstudo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {
                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectAmbienteOfEstudoInsertPending();

                if (c != null) {
                    do {
                        InsertAmbienteOfEstudoRemote(Integer.parseInt(c.getString(c.getColumnIndex("_id_ambiente_estudo"))), 0);
                    } while (c.moveToNext());
                    c.close();
                } else {
                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    IAMBOEST = false;

                    //Cancela o Registro de Inserir uma Lamp
                    context.unregisterReceiver(ReceiverInsertAmbienteOfEstudo);
                }
                db.close();
            }
        }
    };

    //BroadCast Receiver para Deletar o Ambiente Remotamente
    private BroadcastReceiver ReceiverDeleteAmbienteOfEstudo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {
                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectAmbienteofEstudoDeletePending();
                if (c != null) {
                    do {
                        DeleteAmbienteOfEstudoRemote(c.getString(c.getColumnIndex("_id_ambiente_estudo")));
                    } while (c.moveToNext());
                    c.close();
                } else {
                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    DAMBOEST = false;

                    //Cancela o Registro de Deletar o Ambiente
                    context.unregisterReceiver(ReceiverDeleteAmbienteOfEstudo);
                }
                db.close();
            }
        }
    };


    private BroadcastReceiver ReceiverInsertItensOfEstudo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {
                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectItensOfEstudoInsertPending();

                if (c != null) {
                    do {
                        InsertItensOfEstudoRemote(Integer.parseInt(c.getString(c.getColumnIndex("_id_itens"))), 0, 0);
                    } while (c.moveToNext());
                    c.close();
                } else {
                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    IIOFEST = false;

                    //Cancela o Registro de Inserir uma Lamp
                    context.unregisterReceiver(ReceiverInsertItensOfEstudo);
                }
                db.close();
            }
        }
    };

    private BroadcastReceiver ReceiverDeleteItensOfEstudo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context_receiver, Intent intent) {
            //Log.d(TAG, "onReceive Acionado !");
            if (IsConected()) {
                LedStockDB db = new LedStockDB(context);
                Cursor c = db.SelectItensOfEstudoDeletePending();
                if (c != null) {
                    do {
                        DeleteItensOfEstudoRemote(c.getString(c.getColumnIndex("_id_itens")));
                    } while (c.moveToNext());
                    c.close();
                } else {
                    //Caso consiga efetuar a inserção remota, cancela o Alarme
                    AlarmUtil alarm = new AlarmUtil();
                    alarm.cancel(context_receiver, intent);
                    DIOFEST = false;

                    //Cancela o Registro de Deletar o Ambiente
                    context.unregisterReceiver(ReceiverDeleteItensOfEstudo);
                }
                db.close();
            }
        }
    };


    /***********************************************************************************************
     * FUNÇÃO PARA EDITAR OS REGISTROS PENDENTES E ADICIONAR OS NOVOS NO BANCO DE DADOS
     ***********************************************************************************************/
    public void UpdateDataBase() throws IOException {

        int action;
        String table, id_table;
        int ACTION_ADD = 0;
        int ACTION_EDIT = 1;
        int ACTION_DELETE = 2;
        long id, id_watch;
        int DONE = 0;
        boolean GET_CLIENTS = false;
        boolean GET_HANDSON = false;
        boolean GET_KWH = false;
        boolean GET_LAMP = false;
        boolean GET_LED = false;
        boolean GET_PLACE = false;
        boolean GET_USER = false;
        boolean GET_ESTUDO = false;
        boolean GET_AMBIENTE_ESTUDO = false;
        boolean GET_ITENS_ESTUDO = false;

        LedStockDB db = new LedStockDB(context);
        Cursor c = db.SelectWatchPending();

        if ((c != null) && (REFRESH_INTERVAL)) {
            do {
                action = c.getInt(c.getColumnIndex("action"));
                table = c.getString(c.getColumnIndex("_table"));
                id_table = c.getString(c.getColumnIndex("table_id"));
                id_watch = c.getLong(c.getColumnIndex("_id_watch"));

                switch (table) {
                    case "cliente":
                        if (ACTION_ADD == action) {
                            GET_CLIENTS = true;
                            //GetClients();
                            DONE = 1;
                        } else if (ACTION_EDIT == action) {
                            GetRegisterClientRemote(Long.parseLong(id_table), id_watch);
                        } else if (ACTION_DELETE == action) {
                            id = db.SelectClientIDByIDRemote(id_table);
                            db.DeleteClientById(String.valueOf(id));
                            Intent intent = new Intent();
                            intent.setAction("REFRESH_CLIENTS");
                            context.sendBroadcast(intent);
                            DONE = 1;
                        }
                        break;
                    case "handson":
                        if (ACTION_ADD == action) {
                            GET_HANDSON = true;
                            //GetHandsOn();
                            DONE = 1;
                        } else if (ACTION_EDIT == action) {
                            GetRegisterHandsOnRemote(Long.parseLong(id_table), id_watch);
                        } else if (ACTION_DELETE == action) {
                            id = db.SelectHandsOnIDByIDRemote(id_table);
                            db.DeleteHandsOn(String.valueOf(id));
                            Intent intent = new Intent();
                            intent.setAction("REFRESH_HANDSON");
                            context.sendBroadcast(intent);
                            DONE = 1;
                        }
                        break;
                    case "kwh":
                        if (ACTION_ADD == action) {
                            GET_KWH = true;
                            //GetKWh();
                            DONE = 1;
                        } else if (ACTION_EDIT == action) {
                            GetRegisteKWhRemote(id_watch);
                        }
                        break;
                    case "lamp":
                        if (ACTION_ADD == action) {
                            GET_LAMP = true;
                            //GetLamps();
                            DONE = 1;
                        } else if (ACTION_EDIT == action) {
                            GetRegisterLampRemote(Long.parseLong(id_table), id_watch);
                        } else if (ACTION_DELETE == action) {
                            id = db.SelectLampIDByIDRemote(id_table);
                            db.DeleteLamp(String.valueOf(id));
                            Intent intent = new Intent();
                            intent.setAction("REFRESH_LAMPS");
                            context.sendBroadcast(intent);
                            DONE = 1;
                        }
                        break;
                    case "led":
                        if (ACTION_ADD == action) {
                            GET_LED = true;
                            //GetLeds();
                            DONE = 1;
                        } else if (ACTION_EDIT == action) {
                            GetRegisterLedRemote(Long.parseLong(id_table), id_watch);
                        } else if (ACTION_DELETE == action) {
                            id = db.SelectLedIDByIDRemote(id_table);
                            db.DeleteLed(String.valueOf(id));
                            Intent intent = new Intent();
                            intent.setAction("REFRESH_LEDS");
                            context.sendBroadcast(intent);
                            DONE = 1;
                        }
                        break;
                    case "place":
                        if (ACTION_ADD == action) {
                            GET_PLACE = true;
                            //GetPlaces();
                            DONE = 1;
                        } else if (ACTION_EDIT == action) {
                            GetRegisterAmbienteRemote(Long.parseLong(id_table), id_watch);
                        } else if (ACTION_DELETE == action) {
                            id = db.SelectAmbienteIDByIDRemote(id_table);
                            db.DeleteAmbiente(String.valueOf(id));
                            Intent intent = new Intent();
                            intent.setAction("REFRESH_AMBIENTES");
                            context.sendBroadcast(intent);
                            DONE = 1;
                        }
                        break;
                    case "user":
                        if (ACTION_ADD == action) {
                            GET_USER = true;
                            //GetUsers();
                            DONE = 1;
                        } else if (ACTION_EDIT == action) {
                            GetRegisterUsersRemote(Long.parseLong(id_table), id_watch);
                        } else if (ACTION_DELETE == action) {
                            id = db.SelectUserIDByIDRemote(id_table);
                            db.DeleteUser(String.valueOf(id));
                            Intent intent = new Intent();
                            intent.setAction("REFRESH_USERS");
                            context.sendBroadcast(intent);
                            DONE = 1;
                        }
                        break;
                    case "estudo":
                        if (ACTION_ADD == action) {
                            GET_ESTUDO = true;
                            //GetEstudo();
                            DONE = 1;
                        } else if (ACTION_EDIT == action) {
                            GetRegisterEstudoRemote(Long.parseLong(id_table), id_watch);
                        } else if (ACTION_DELETE == action) {
                            id = db.SelectEstudoIDByIDRemote(id_table);
                            db.DeleteEstudo(String.valueOf(id));
                            Intent intent = new Intent();
                            intent.setAction("REFRESH_ESTUDOS");
                            context.sendBroadcast(intent);
                            DONE = 1;
                        }
                        break;
                    case "ambiente_estudo":
                        if (ACTION_ADD == action) {
                            GET_AMBIENTE_ESTUDO = true;
                            //GetAmbientesOfEstudo();
                            DONE = 1;
                        } else if (ACTION_DELETE == action) {
                            id = db.SelectAmbienteOfEstudoIDByIDRemote(id_table);
                            db.DeleteAmbienteOfEstudo(String.valueOf(id));
                            Intent intent = new Intent();
                            intent.setAction("REFRESH_AMBIENTESOFESTUDOS");
                            context.sendBroadcast(intent);
                            DONE = 1;
                        }
                        break;
                    case "itens_estudo":
                        if (ACTION_ADD == action) {
                            GET_ITENS_ESTUDO = true;
                            DONE = 1;
                        } else if (ACTION_DELETE == action) {
                            id = db.SelectItensOfEstudoIDByIDRemote(id_table);
                            db.DeleteItensOfEstudo(String.valueOf(id));
                            Intent intent = new Intent();
                            intent.setAction("REFRESH_ITENS_LEDS");
                            context.sendBroadcast(intent);
                            intent.setAction("REFRESH_ITENS_LAMPS");
                            context.sendBroadcast(intent);
                            intent.setAction("REFRESH_ITENS_HANDSON");
                            context.sendBroadcast(intent);
                            DONE = 1;
                        }
                        break;
                }
                if (DONE == 1) {
                    db.WatchMarkDone(id_watch);
                }
            } while (c.moveToNext());

            if (GET_CLIENTS) {
                GetClients();
            }
            if (GET_HANDSON) {
                GetHandsOn();
            }
            if (GET_KWH) {
                GetKWh();
            }
            if (GET_LAMP) {
                GetLamps();
            }
            if (GET_LED) {
                GetLeds();
            }
            if (GET_USER) {
                GetUsers();
            }
            if (GET_PLACE) {
                GetPlaces();
            }
            if (GET_ESTUDO) {
                GetEstudo();
            }
            if (GET_AMBIENTE_ESTUDO) {
                GetAmbientesOfEstudo();
            }
            if (GET_ITENS_ESTUDO) {
                GetItensOfEstudo();
            }
            c.close();
            db.close();
        }
    }

    /***********************************************************************************************
     * INICIALIZA OS REGISTROS CASO TENHA ALGUM ITEM PENDENTE PARA ENVIAR AO DB REMOTO
     ***********************************************************************************************/

    public void Init() {
        LedStockDB db = new LedStockDB(context);

        //Verifica se Existe algum cliente pendente de inserção
        Cursor pendingInsertClient = db.SelectClientsPending();
        if (pendingInsertClient != null) {
            ICLI = true;
            context.registerReceiver(ReceiverInsertClient, new IntentFilter("LS_INSERT_CLIENT_TO_DB"));
            pendingInsertClient.close();
        }
        //Verifica se Existe algum cliente pendente de edição
        Cursor pendingEditClient = db.SelectClientsEditPending();
        if (pendingEditClient != null) {
            ECLI = true;
            context.registerReceiver(ReceiverEditClient, new IntentFilter("LS_EDIT_CLIENT_TO_DB"));
            pendingEditClient.close();
        }
        //Verifica se Existe alguma lampada pendente de inserção
        Cursor pendingInsertLamp = db.SelectLampsInsertPending();
        if (pendingInsertLamp != null) {
            ILAMP = true;
            context.registerReceiver(ReceiverInsertLamp, new IntentFilter("LS_INSERT_LAMP_TO_DB"));
            pendingInsertLamp.close();
        }
        //Verifica se Existe alguma lampada pendente de edição
        Cursor pendingEditLamp = db.SelectLampsEditPending();
        if (pendingEditLamp != null) {
            ELAMP = true;
            context.registerReceiver(ReceiverEditLamp, new IntentFilter("LS_EDIT_LAMP_TO_DB"));
            pendingEditLamp.close();
        }
        //Verifica se Existe alguma lampada pendente de exclusão
        Cursor pendingDeleteLamp = db.SelectLampsDeletePending();
        if (pendingDeleteLamp != null) {
            DLAMP = true;
            context.registerReceiver(ReceiverDeleteLamp, new IntentFilter("LS_DELETE_LAMP_TO_DB"));
            pendingDeleteLamp.close();
        }
        //Verifica se Existe algum LED pendente de inserção
        Cursor pendingInsertLed = db.SelectLEDsInsertPending();
        if (pendingInsertLed != null) {
            ILED = true;
            context.registerReceiver(ReceiverInsertLED, new IntentFilter("LS_INSERT_LED_TO_DB"));
            pendingInsertLed.close();
        }
        //Verifica se Existe algum LED pendente de edição
        Cursor pendingEditLed = db.SelectLedEditPending();
        if (pendingEditLed != null) {
            ELED = true;
            context.registerReceiver(ReceiverEditLed, new IntentFilter("LS_EDIT_LED_TO_DB"));
            pendingEditLed.close();
        }
        //Verifica se Existe algum LED pendente de exclusão
        Cursor pendingDeleteLed = db.SelectLedDeletePending();
        if (pendingDeleteLed != null) {
            DLED = true;
            context.registerReceiver(ReceiverDeleteLed, new IntentFilter("LS_DELETE_LED_TO_DB"));
            pendingDeleteLed.close();
        }
        //Verifica se Existe alguma Mão de Obra pendente de inserção
        Cursor pendingInsertHandsOn = db.SelectHandsOnInsertPending();
        if (pendingInsertHandsOn != null) {
            IHAND = true;
            context.registerReceiver(ReceiverInsertHandsOn, new IntentFilter("LS_INSERT_HANDSON_TO_DB"));
            pendingInsertHandsOn.close();
        }
        //Verifica se Existe alguma Mão de Obra pendente de Edição
        Cursor pendingEditHandsOn = db.SelectHandsOnEditPending();
        if (pendingEditHandsOn != null) {
            EHAND = true;
            context.registerReceiver(ReceiverEditHandsOn, new IntentFilter("LS_EDIT_HANDSON_TO_DB"));
            pendingEditHandsOn.close();
        }
        //Verifica se Existe alguma Mão de Obra pendente de Exclusão
        Cursor pendingDeleteHandsOn = db.SelectHandsOnDeletePending();
        if (pendingDeleteHandsOn != null) {
            DHAND = true;
            context.registerReceiver(ReceiverDeleteHandsOn, new IntentFilter("LS_DELETE_HANDSON_TO_DB"));
            pendingDeleteHandsOn.close();
        }
        //Verifica se Existe algum Ambiente pendente de inserção
        Cursor pendingInsertAmbiente = db.SelectAmbientesInsertPending();
        if (pendingInsertAmbiente != null) {
            IAMBI = true;
            context.registerReceiver(ReceiverInsertAmbiente, new IntentFilter("LS_INSERT_AMBIENTE_TO_DB"));
            pendingInsertAmbiente.close();
        }
        //Verifica se Existe algum Ambiente pendente de Edição
        Cursor pendingEditHAmbiente = db.SelectAmbientesEditPending();
        if (pendingEditHAmbiente != null) {
            EAMBI = true;
            context.registerReceiver(ReceiverEditAmbiente, new IntentFilter("LS_EDIT_AMBIENTE_TO_DB"));
            pendingEditHAmbiente.close();
        }
        //Verifica se Existe algum Ambiente pendente de Edição
        Cursor pendingDeleteAmbiente = db.SelectAmbientesDeletePending();
        if (pendingDeleteAmbiente != null) {
            DAMBI = true;
            context.registerReceiver(ReceiverDeleteAmbiente, new IntentFilter("LS_DELETE_AMBIENTE_TO_DB"));
            pendingDeleteAmbiente.close();
        }
        //Verifica se Existe algum Usuário pendente de inserção
        Cursor pendingInsertUsuario = db.SelectUsersInsertPending();
        if (pendingInsertUsuario != null) {
            IUSER = true;
            context.registerReceiver(ReceiverInsertUsers, new IntentFilter("LS_INSERT_USER_TO_DB"));
            pendingInsertUsuario.close();
        }
        //Verifica se Existe algum Usuário pendente de edição
        Cursor pendingEditUsuario = db.SelectUsersEditPending();
        if (pendingEditUsuario != null) {
            EUSER = true;
            context.registerReceiver(ReceiverEditUsers, new IntentFilter("LS_EDIT_USER_TO_DB"));
            pendingEditUsuario.close();
        }
        //Verifica se Existe algum Usuário pendente de exclusão
        Cursor pendingDeleteUsuario = db.SelectUsersDeletePending();
        if (pendingDeleteUsuario != null) {
            DUSER = true;
            context.registerReceiver(ReceiverDeleteUsers, new IntentFilter("LS_DELETE_USER_TO_DB"));
            pendingDeleteUsuario.close();
        }
        //Verifica se Existe algum Estudo pendente de inserção
        Cursor pendingInsertEstudo = db.SelectEstudoInsertPending();
        if (pendingInsertEstudo != null) {
            IEST = true;
            context.registerReceiver(ReceiverInsertEstudo, new IntentFilter("LS_INSERT_ESTUDO_TO_DB"));
            pendingInsertEstudo.close();
        }
        //Verifica se Existe algum Estudo pendente de edição
        Cursor pendingEditEstudo = db.SelectEstudoEditPending();
        if (pendingEditEstudo != null) {
            EEST = true;
            context.registerReceiver(ReceiverEditEstudo, new IntentFilter("LS_EDIT_ESTUDO_TO_DB"));
            pendingEditEstudo.close();
        }
        //Verifica se Existe algum Estudo pendente de exclusão
        Cursor pendingDeleteEstudo = db.SelectEstudoDeletePending();
        if (pendingDeleteEstudo != null) {
            DEST = true;
            context.registerReceiver(ReceiverDeleteEstudo, new IntentFilter("LS_DELETE_ESTUDO_TO_DB"));
            pendingDeleteEstudo.close();
        }
        //Verifica se Existe algum Ambiente do Estudo pendente de inserção
        Cursor pendingInsertIOA = db.SelectAmbienteOfEstudoInsertPending();
        if (pendingInsertIOA != null) {
            IAMBOEST = true;
            context.registerReceiver(ReceiverInsertAmbienteOfEstudo, new IntentFilter("LS_INSERT_AMBIENTETOESTUDO_TO_DB"));
            pendingInsertIOA.close();
        }
        //Verifica se Existe algum Ambiente do Estudo pendente de edição
        Cursor pendingDeleteIOA = db.SelectAmbienteofEstudoDeletePending();
        if (pendingDeleteIOA != null) {
            DAMBOEST = true;
            context.registerReceiver(ReceiverDeleteAmbienteOfEstudo, new IntentFilter("LS_DELETE_AMBIENTEOFESTUDO_TO_DB"));
            pendingDeleteIOA.close();
        }
        //Verifica se Existe algum Item do Estudo pendente de inserção
        Cursor pendingInsertIOE = db.SelectItensOfEstudoInsertPending();
        if (pendingInsertIOE != null) {
            IIOFEST = true;
            context.registerReceiver(ReceiverInsertItensOfEstudo, new IntentFilter("LS_INSERT_ITENS_OF_ESTUDO_TO_DB"));
            pendingInsertIOE.close();
        }
        //Verifica se Existe algum Ambiente do Estudo pendente de edição
        Cursor pendingDeleteIOE = db.SelectItensOfEstudoDeletePending();
        if (pendingDeleteIOE != null) {
            DIOFEST = true;
            context.registerReceiver(ReceiverDeleteItensOfEstudo, new IntentFilter("LS_DELETE_ITENSOFESTUDO_TO_DB"));
            pendingDeleteIOE.close();
        }
        db.close();
    }

    /***********************************************************************************************
     * FINALIZA OS PROCESSOS AOS FECHAR O SERVICE
     ***********************************************************************************************/

    @Override
    public void onDestroy() {
        //Cancela o Registro do Receiver
        unregisterReceiver(ReceiverWatch);
    }
}
