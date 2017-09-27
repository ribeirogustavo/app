package br.com.ledstock.led_stock.led_stock.domain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.renderscript.Double2;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.R.attr.id;
import static br.com.ledstock.led_stock.R.string.ambientes;
import static br.com.ledstock.led_stock.R.string.clientes;
import static br.com.ledstock.led_stock.R.string.estudo;
import static br.com.ledstock.led_stock.R.string.psm;


/**
 * Created by Gustavo on 15/08/2016.
 */
public class LedStockDB extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "LedStock.SQLite";
    private static final String TAG = "sql";
    private static final int VERSAO_BANCO = 2;

    public LedStockDB(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //Cria a tabela de clientes
        sqLiteDatabase.execSQL("create table if not exists clientes (_id_cliente integer primary key autoincrement, _id_client_remote integer default null,raw_contact_id text, nome text,cnpj_cpf text, endereco text,numero text,comp text,cep text, bairro text,cidade text,uf text,contato text,email text,email2 text, tel text,tel2 text, edited text);");
        //Cria a tabela de usuarios
        sqLiteDatabase.execSQL("create table if not exists usuario (_id_usuario integer primary key autoincrement, _id_usuario_remote integer default null, nome text, usuario text, senha text, acesso text, edited text, enable text default 1);");
        //Cria a tabela de Lampadas atuais
        sqLiteDatabase.execSQL("create table if not exists lamp (_id_lamp integer primary key autoincrement, _id_lamp_remote integer default null, descricao text, potencia text, edited text, enable text default 1);");
        //Cria a tabela de solução Led
        sqLiteDatabase.execSQL("create table if not exists led_solution (_id_led integer primary key autoincrement, _id_led_remote integer default null, descricao text, potencia text, valor double, valor_revenda double, edited text, enable text default 1);");
        //Cria a Tabela de Ambientes
        sqLiteDatabase.execSQL("create table if not exists ambientes (_id_ambiente integer primary key autoincrement, _id_ambiente_remote integer default null, descricao text, edited text, enable text default 1);");
        //Cria a tabela de Mão de Obra
        sqLiteDatabase.execSQL("create table if not exists handson (_id_handson integer primary key autoincrement, _id_handson_remote integer default null, descricao text, valor double, edited text, enable text default 1);");
        //Cria a tabela do Preço KWh
        sqLiteDatabase.execSQL("create table if not exists kwh (_id_kwh integer primary key autoincrement, valor double, edited text);");
        //Cria a tabela Watch
        sqLiteDatabase.execSQL("create table if not exists watch (_id_watch integer primary key autoincrement, _id_watch_remote integer default null, action integer, _table text, table_id integer, done integer default 0);");
        //Cria a tabela Estudo
        sqLiteDatabase.execSQL("create table if not exists estudo (_id_estudo integer primary key autoincrement, _id_estudo_remote integer default null, _id_cliente integer default null, _id_client_remote integer default null, descricao text, data text, psm integer default 0, pcm integer default 0, edited text, data_pedido text default null, enable text default 1);");
        //Cria a tabela Ambientes_Estudo
        sqLiteDatabase.execSQL("create table if not exists ambientes_estudo (_id_ambiente_estudo integer primary key autoincrement, _id_ambiente_estudo_remote integer default null, _id_estudo integer default null, _id_estudo_remote integer default null, _id_ambiente integer default null, _id_ambiente_remote integer default null, enable text default 1);");
        //Cria a tabela Itens_Of_Estudo
        sqLiteDatabase.execSQL("create table if not exists itens_of_estudo (_id_itens integer primary key autoincrement, _id_itens_remote integer default null, _id_estudo integer default null, _id_estudo_remote integer default null, _id_ambiente_estudo integer default null, _id_ambiente_estudo_remote integer default null,key_table integer default null, _id_table integer default null, _id_table_remote integer default null,_id_lamp integer default null, _id_lamp_remote integer default null, _id_led integer default null, _id_led_remote integer default null, quantidade integer default 0, horas integer, enable integer default 1);");
        //Cria a tabela Orçamentos
        sqLiteDatabase.execSQL("create table if not exists orcamento (_id_orcamento integer primary key autoincrement, _id_orcamento_remote integer default null, _id_cliente integer default null, _id_client_remote integer default null, orcamento text, data text, psm integer default 0, pcm integer default 0, edited text, data_pedido text default null, enable text default 1);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /***********************************************************************************************
     * FUNÇÕES RELACIONADAS AOS CLIENTES
     ***********************************************************************************************/

    // Insere um novo Cliente
    public long Insert_Client(Array_Clients clients) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            if ((!clients.id_remoto.equals("") && (clients.id_remoto != null))) {
                values.put("_id_client_remote", clients.id_remoto);
            } else {
                values.put("_id_client_remote", (byte[]) null);
            }
            values.put("nome", clients.nome);
            values.put("endereco", clients.endereco);
            values.put("numero", clients.numero);
            values.put("comp", clients.comp);
            values.put("cep", clients.cep);
            values.put("bairro", clients.bairro);
            values.put("cidade", clients.cidade);
            values.put("uf", clients.uf);
            values.put("contato", clients.contato);
            values.put("email", clients.email);
            values.put("tel", clients.tel1);
            values.put("tel2", clients.tel2);
            values.put("email2", clients.email2);
            values.put("cnpj_cpf", clients.cnpj_cpf);

            // insert into Clientes values (...)
            // Log.d(TAG, "Cadastro Inserido com sucesso !");
            return db.insert("clientes", "", values);
        } finally {
            db.close();
        }
    }

    public String SelectRawContactIDById(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("clientes", new String[]{"raw_contact_id"}, "_id_cliente=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                //c.moveToFirst();
                return c.getString(c.getColumnIndex("raw_contact_id"));
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    public Cursor SelectClientById(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("clientes", null, "_id_cliente=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    public long SelectRemoteIDById(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("clientes", new String[]{"_id_client_remote"}, "_id_cliente=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return Long.parseLong(c.getString(c.getColumnIndex("_id_client_remote")));
            } else {
                return 0;
            }
        } finally {
            db.close();
        }
    }

    public String LastIdClient() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("clientes", new String[]{"max(_id_client_remote)"}, null, null, null, null, null, null);
            c.moveToFirst();
            return c.getString(c.getColumnIndex("max(_id_client_remote)"));
        } finally {
            db.close();
        }
    }

    public Cursor SelectClientsPending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("clientes", null, "_id_client_remote is null", null, null, null, "_id_cliente", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    public Cursor SelectClientsEditPending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("clientes", null, "edited=?", new String[]{"1"}, null, null, "_id_cliente", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    public int RemoveEditedfromClient(String id) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("edited", "");

            return db.update("clientes", values, "_id_cliente=?", new String[]{id});
        } finally {
            db.close();
        }
    }

    public int InsertRemoteID(String RemoteID, String id_client) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("_id_client_remote", Integer.parseInt(RemoteID));

            // insert into Clientes values (...)
            //Log.d(TAG, "ID Remoto inserido com sucesso !");
            //return db.insert("clientes", "", values);
            return db.update("clientes", values, "_id_cliente=?", new String[]{id_client});
        } finally {
            db.close();
        }
    }

    public boolean DeleteClientById(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("_id_cliente", id);
            // insert into Clientes values (...)
            // Log.d(TAG, "Cadastro Removido com sucesso !");
            db.delete("clientes", "_id_cliente=?", new String[]{id});
            return true;
        } finally {
            db.close();
        }
    }

    public int UpDate_ClientById(String id, Array_Clients clients) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("nome", clients.nome);
            values.put("endereco", clients.endereco);
            values.put("numero", clients.numero);
            values.put("comp", clients.comp);
            values.put("cep", clients.cep);
            values.put("bairro", clients.bairro);
            values.put("cidade", clients.cidade);
            values.put("uf", clients.uf);
            values.put("contato", clients.contato);
            values.put("email", clients.email);
            values.put("tel", clients.tel1);
            values.put("tel2", clients.tel2);
            values.put("email2", clients.email2);
            values.put("cnpj_cpf", clients.cnpj_cpf);
            if (clients.id_remoto == null) {
                values.put("edited", "1");
            } else {
                values.put("edited", "0");
            }
            // insert into Clientes values (...)
            // Log.d(TAG, "Cadastro Atualizados com sucesso !");
            //return db.insert("clientes", "", values);
            return db.update("clientes", values, "_id_cliente=?", new String[]{id});
        } finally {
            db.close();
        }
    }

    public int InsertRaw_Contact_ID(long id, String Raw_Contact_id) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("raw_contact_id", Raw_Contact_id);

            // insert into Clientes values (...)
            //   Log.d(TAG, "RAW_CONTACT_ID inserido com sucesso !");
            //return db.insert("clientes", "", values);
            return db.update("clientes", values, "_id_cliente=?", new String[]{String.valueOf(id)});
        } finally {
            db.close();
        }
    }

    // Insere um novo Cliente, ou atualiza se já existe
    public Cursor Select_ListClients() {
        SQLiteDatabase db = getReadableDatabase();
        try {

            /*
            Cursor query (boolean distinct,
            String table,
            String[] columns,
            String selection,
            String[] selectionArgs,
            String groupBy,
            String having,
            String orderBy,
            String limit)
            */

            Cursor c = db.query("clientes", null, null, null, null, null, "nome", null);
            c.moveToFirst();
            return c;
        } finally {
            db.close();
        }
    }

    // Insere um novo Cliente, ou atualiza se já existe
    public Cursor Select_ListClientsAutoComplete() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            /*
            Cursor query (boolean distinct,
            String table,
            String[] columns,
            String selection,
            String[] selectionArgs,
            String groupBy,
            String having,
            String orderBy,
            String limit)
            */

            Cursor c = db.query("clientes", new String[]{"_id_cliente as _id, nome"}, null, null, null, null, "nome", null);
            c.moveToFirst();
            return c;
        } finally {
            db.close();
        }
    }

    // Insere um novo Cliente, ou atualiza se já existe
    public int getNumClients() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("clientes", new String[]{"max(_id_cliente) as _id_cliente"}, null, null, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c.getInt(c.getColumnIndex("_id_cliente"));
            } else {
                return 0;
            }
        } finally {
            db.close();
        }
    }

    public long SelectClientIDByIDRemote(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("clientes", new String[]{"_id_cliente"}, "_id_client_remote=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return Long.parseLong(c.getString(c.getColumnIndex("_id_cliente")));
            } else {
                return 0;
            }
        } finally {
            db.close();
        }
    }

    public String SelectClientRemoteIDbyID(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("clientes", new String[]{"_id_client_remote"}, "_id_cliente=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c.getString(c.getColumnIndex("_id_client_remote"));
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    public Cursor getMatchingClient(String s) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("clientes", new String[]{"_id_cliente as _id, nome"}, "nome like ?", new String[]{"%" + s + "%"}, null, null, "nome", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    /***********************************************************************************************
     * FUNÇÕES RELACIONADAS AS LÂMPADAS ATUAIS
     ***********************************************************************************************/

    //Insert Remote ID from Lamp
    public int InsertLampRemoteID(String RemoteID, String id_lamp) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("_id_lamp_remote", RemoteID);

            // insert into Clientes values (...)
            //Log.d(TAG, "ID Remoto inserido com sucesso !");
            //return db.insert("clientes", "", values);
            return db.update("lamp", values, "_id_lamp=?", new String[]{id_lamp});
        } finally {
            db.close();
        }
    }

    //Select Lamp pendentes a inserção
    public Cursor SelectLampsInsertPending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("lamp", null, "_id_lamp_remote is null", null, null, null, "_id_lamp", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Seleciona a Lamp pelo ID
    public Cursor SelectLampByID(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("lamp", null, "_id_lamp=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Select List of Lamps
    public Cursor Select_ListLamps() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("lamp", null, "enable=?", new String[]{"1"}, null, null, "descricao", null);
            c.moveToFirst();
            return c;
        } finally {
            db.close();
        }
    }

    // Insere Lampada Atual
    public long Insert_Lamp(String descricao, String potencia, String id_remoto, String enable) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            if ((id_remoto != null) && (!id_remoto.equals(""))) {
                values.put("_id_lamp_remote", id_remoto);
            } else {
                values.put("_id_lamp_remote", (byte[]) null);
            }
            values.put("descricao", descricao);
            values.put("potencia", potencia);
            if (enable == null) {
                values.put("enable", "1");
            } else {
                values.put("enable", enable);
            }
            return db.insert("lamp", "", values);
        } finally {
            db.close();
        }
    }

    // Update Lampada Atual
    public long Update_Lamp(String id, String descricao, String potencia, String edited) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("descricao", descricao);
            values.put("potencia", potencia);
            values.put("edited", edited);
            // insert into Clientes values (...)
            // Log.d(TAG, "Cadastro Atualizados com sucesso !");
            //return db.insert("clientes", "", values);
            return db.update("lamp", values, "_id_lamp=?", new String[]{id});
        } finally {
            db.close();
        }
    }

    //Seleciona as Lamps pendentes para inserir no DB remoto
    public Cursor SelectLampsEditPending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("lamp", null, "edited=?", new String[]{"1"}, null, null, "_id_lamp", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Remove o editado da tabela Lamp
    public int RemoveEditedfromLamp(String id) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("edited", "");

            return db.update("lamp", values, "_id_lamp=?", new String[]{id});
        } finally {
            db.close();
        }
    }

    //Seleciona o Id remoto da tabela Lamp
    public long SelectLampRemoteIDById(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("lamp", new String[]{"_id_lamp_remote"}, "_id_lamp=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                if (c.getString(c.getColumnIndex("_id_lamp_remote")) != null) {
                    return Integer.parseInt(c.getString(c.getColumnIndex("_id_lamp_remote")));
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } finally {
            db.close();
        }
    }

    //Deleta Lampa do DB
    public int DeleteLamp(String id) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("enable", "0");

            return db.update("lamp", values, "_id_lamp=?", new String[]{id});
        } finally {
            db.close();
        }
    }

    //Seleciona as Lamps pendentes para Deletar no DB remoto
    public Cursor SelectLampsDeletePending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("lamp", null, "enable=?", new String[]{"0"}, null, null, "_id_lamp", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Seleciona o ultimo ID Remoto Inserido
    public String SelectLastRemoteIDofLamp() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("lamp", new String[]{"max(_id_lamp_remote) as _id_lamp_remote"}, null, null, null, null, null, null);
            c.moveToFirst();
            return c.getString(c.getColumnIndex("_id_lamp_remote"));
        } finally {
            db.close();
        }
    }

    //Seleciona o Id remoto da tabela Lamp
    public long SelectLampIDByIDRemote(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("lamp", new String[]{"_id_lamp"}, "_id_lamp_remote=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                if (c.getString(c.getColumnIndex("_id_lamp")) != null) {
                    return Integer.parseInt(c.getString(c.getColumnIndex("_id_lamp")));
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } finally {
            db.close();
        }
    }

    public Cursor Select_ListLampsAutoComplete() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("lamp", new String[]{"_id_lamp as _id, descricao"}, "enable=?", new String[]{"1"}, null, null, "descricao", null);
            c.moveToFirst();
            return c;
        } finally {
            db.close();
        }
    }

    public Cursor getMatchingLamp(String s) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("LAMP", new String[]{"_id_lamp as _id, descricao || ' ' || potencia || 'W' as descricao"}, "descricao || ' ' || potencia || 'W' like ? AND enable = ?", new String[]{"%" + s + "%", "1"}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    /***********************************************************************************************
     * FUNÇÕES RELACIONADAS A SOLUÇÃO LED
     ***********************************************************************************************/

    // Insere Uma Solução LED
    public long Insert_LED(String descricao, String potencia, double valor, double valor_revenda, String id_remoto, String enable) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            if ((id_remoto != null) && (!id_remoto.equals(""))) {
                values.put("_id_led_remote", id_remoto);
            } else {
                values.put("_id_led_remote", (byte[]) null);
            }
            values.put("descricao", descricao);
            values.put("potencia", potencia);
            values.put("valor", valor);
            values.put("valor_revenda", valor_revenda);
            if (enable == null) {
                values.put("enable", "1");
            } else {
                values.put("enable", enable);
            }
            return db.insert("led_solution", "", values);
        } finally {
            db.close();
        }
    }

    //Seleciona o Led pelo ID
    public Cursor SelectLEDByID(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("led_solution", null, "_id_led=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Select Lamp pendentes a inserção
    public Cursor SelectLEDsInsertPending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("led_solution", null, "_id_led_remote is null", null, null, null, "_id_led", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Insert Remote ID from LED SOLUTION
    public int InsertLEDRemoteID(String RemoteID, String id_led) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("_id_led_remote", RemoteID);
            return db.update("led_solution", values, "_id_led=?", new String[]{id_led});
        } finally {
            db.close();
        }
    }

    //Select List of Lamps
    public Cursor Select_ListLEDS() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("led_solution", null, "enable=?", new String[]{"1"}, null, null, "descricao", null);
            c.moveToFirst();
            return c;
        } finally {
            db.close();
        }
    }

    // Atualiza LED
    public long Update_Led(String id, String descricao, String potencia, double valor, double valor_revenda, String edited) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("valor", valor);
            values.put("valor_revenda", valor_revenda);
            values.put("descricao", descricao);
            values.put("potencia", potencia);
            values.put("edited", edited);
            return db.update("led_solution", values, "_id_led=?", new String[]{id});
        } finally {
            db.close();
        }
    }

    //Seleciona o Id remoto da tabela Lamp
    public long SelectLedRemoteIDById(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("led_solution", new String[]{"_id_led_remote"}, "_id_led=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                if (c.getString(c.getColumnIndex("_id_led_remote")) != null) {
                    return Integer.parseInt(c.getString(c.getColumnIndex("_id_led_remote")));
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } finally {
            db.close();
        }
    }

    //Remove o editado da tabela Lamp
    public int RemoveEditedfromLed(String id) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("edited", "");

            return db.update("led_solution", values, "_id_led=?", new String[]{id});
        } finally {
            db.close();
        }
    }

    //Seleciona as Lamps pendentes para inserir no DB remoto
    public Cursor SelectLedEditPending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("led_solution", null, "edited=?", new String[]{"1"}, null, null, "_id_led", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Deleta Lampa do DB
    public int DeleteLed(String id) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("enable", "0");

            return db.update("led_solution", values, "_id_led=?", new String[]{id});
        } finally {
            db.close();
        }
    }

    //Seleciona as Lamps pendentes para Deletar no DB remoto
    public Cursor SelectLedDeletePending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("led_solution", null, "enable=?", new String[]{"0"}, null, null, "_id_led", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Seleciona o ultimo ID Remoto Inserido
    public String SelectLastRemoteIDofLed() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("led_solution", new String[]{"max(_id_led_remote) as _id_led_remote"}, null, null, null, null, null, null);
            c.moveToFirst();
            return c.getString(c.getColumnIndex("_id_led_remote"));
        } finally {
            db.close();
        }
    }

    //Seleciona o Id remoto da tabela Lamp
    public long SelectLedIDByIDRemote(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("led_solution", new String[]{"_id_led"}, "_id_led_remote=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                if (c.getString(c.getColumnIndex("_id_led")) != null) {
                    return Integer.parseInt(c.getString(c.getColumnIndex("_id_led")));
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } finally {
            db.close();
        }
    }

    public Cursor Select_ListLEDSAutoComplete() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("led_solution", new String[]{"_id_led as _id, descricao"}, "enable=?", new String[]{"1"}, null, null, "descricao", null);
            c.moveToFirst();
            return c;
        } finally {
            db.close();
        }
    }

    public Cursor getMatchingLED(String s) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("led_solution", new String[]{"_id_led as _id, descricao || ' ' || potencia || 'W' as descricao"}, "descricao || ' ' || potencia || 'W' like ? AND enable = ?", new String[]{"%" + s + "%", "1"}, null, null, "descricao", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    /***********************************************************************************************
     * FUNÇÕES RELACIONADAS AOS AMBIENTES
     ***********************************************************************************************/

    // Insere Lampada Atual
    public long Insert_Place(String ambiente, String id_remoto, String enable) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            if ((id_remoto != null) && (!id_remoto.equals(""))) {
                values.put("_id_ambiente_remote", id_remoto);
            } else {
                values.put("_id_ambiente_remote", (byte[]) null);
            }
            values.put("descricao", ambiente);
            if (enable == null) {
                values.put("enable", "1");
            } else {
                values.put("enable", enable);
            }
            return db.insert("ambientes", "", values);
        } finally {
            db.close();
        }
    }

    //Seleciona a Lamp pelo ID
    public Cursor SelectAmbienteByID(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("ambientes", null, "_id_ambiente=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Insert Remote ID from Ambiente
    public int InsertAmbienteRemoteID(String RemoteID, String id_ambiente) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("_id_ambiente_remote", RemoteID);

            return db.update("ambientes", values, "_id_ambiente=?", new String[]{id_ambiente});
        } finally {
            db.close();
        }
    }

    // Update Lampada Atual
    public long Update_Ambiente(String id, String descricao, String edited) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("descricao", descricao);
            values.put("edited", edited);
            return db.update("ambientes", values, "_id_ambiente=?", new String[]{id});
        } finally {
            db.close();
        }
    }

    //Seleciona o Id remoto da tabela Ambientes
    public long SelectAmbienteRemoteIDById(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("ambientes", new String[]{"_id_ambiente_remote"}, "_id_ambiente=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                if (c.getString(c.getColumnIndex("_id_ambiente_remote")) != null) {
                    return Integer.parseInt(c.getString(c.getColumnIndex("_id_ambiente_remote")));
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } finally {
            db.close();
        }
    }

    //Remove o editado da tabela Ambientes
    public int RemoveEditedfromAmbientes(String id) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("edited", "");

            return db.update("ambientes", values, "_id_ambiente=?", new String[]{id});
        } finally {
            db.close();
        }
    }

    //Select Ambientes pendentes a inserção
    public Cursor SelectAmbientesInsertPending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("ambientes", null, "_id_ambiente_remote is null", null, null, null, "_id_ambiente", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Seleciona as Lamps pendentes para inserir no DB remoto
    public Cursor SelectAmbientesEditPending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("ambientes", null, "edited=?", new String[]{"1"}, null, null, "_id_ambiente", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Seleciona as Lamps pendentes para Deletar no DB remoto
    public Cursor SelectAmbientesDeletePending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("ambientes", null, "enable=?", new String[]{"0"}, null, null, "_id_ambiente", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Seleciona o ultimo ID Remoto Inserido
    public String SelectLastRemoteIDofPlaces() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("ambientes", new String[]{"max(_id_ambiente_remote) as _id_ambiente_remote"}, null, null, null, null, null, null);
            c.moveToFirst();
            return c.getString(c.getColumnIndex("_id_ambiente_remote"));
        } finally {
            db.close();
        }
    }

    //Deleta Ambiente do DB
    public int DeleteAmbiente(String id) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("enable", "0");

            return db.update("ambientes", values, "_id_ambiente=?", new String[]{id});
        } finally {
            db.close();
        }
    }

    //Select List of Lamps
    public Cursor Select_ListAmbientes() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("ambientes", null, "enable=?", new String[]{"1"}, null, null, "descricao", null);
            c.moveToFirst();
            return c;
        } finally {
            db.close();
        }
    }

    //Seleciona o Id remoto da tabela Ambientes
    public long SelectAmbienteIDByIDRemote(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("ambientes", new String[]{"_id_ambiente"}, "_id_ambiente_remote=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                if (c.getString(c.getColumnIndex("_id_ambiente")) != null) {
                    return Integer.parseInt(c.getString(c.getColumnIndex("_id_ambiente")));
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } finally {
            db.close();
        }
    }

    public Cursor Select_ListAmbientesAutoComplete() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            /*
            Cursor query (boolean distinct,
            String table,
            String[] columns,
            String selection,
            String[] selectionArgs,
            String groupBy,
            String having,
            String orderBy,
            String limit)
            */

            Cursor c = db.query("ambientes", new String[]{"_id_ambiente as _id, descricao"}, "enable = ?", new String[]{"1"}, null, null, "descricao", null);
            c.moveToFirst();
            return c;
        } finally {
            db.close();
        }
    }

    public Cursor getMatchingAmbientes(String s) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("ambientes", new String[]{"_id_ambiente as _id, descricao"}, "descricao like ? AND enable = ?", new String[]{"%" + s + "%", "1"}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    /***********************************************************************************************
     * FUNÇÕES RELACIONADAS AOS USUÁRIOS DO SISTEMA
     ***********************************************************************************************/

    // Insere um novo Usuário
    public long Insert_User(Array_Users users) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            if (users.id != null) {
                values.put("_id_usuario_remote", users.id);
            } else {
                values.put("_id_usuario_remote", (byte[]) null);
            }
            values.put("nome", users.nome);
            values.put("usuario", users.usuario);
            values.put("senha", users.senha);
            values.put("acesso", users.acesso);
            return db.insert("usuario", "", values);
        } finally {
            db.close();
        }
    }

    public void RemoveAllUser() {
        SQLiteDatabase db = getWritableDatabase();

        try {
            Cursor c = db.query("usuario", null, null, null, null, null, null, null);
            c.moveToFirst();

            if (c.getCount() > 0) {
                db.delete("usuario", null, null);
                //  Log.d(TAG, "Usuários Removidos com sucesso !");
            } else {
                //   Log.d(TAG, "Cursor igual a zero");
            }
            c.close();

        } finally {
            db.close();
        }
    }

    public boolean SelectUser(String user, String pass) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("usuario", null, "usuario=? AND senha=? AND enable = ?", new String[]{user, pass, "1"}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                c.close();
                return true;
            } else {
                c.close();
                return false;
            }
        } finally {
            db.close();
        }
    }

    public String SelectAccess(String user, String pass) {
        SQLiteDatabase db = getReadableDatabase();
        String access;
        try {
            Cursor c = db.query("usuario", null, "usuario=? AND senha=?", new String[]{user, pass}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                access = c.getString(c.getColumnIndex("acesso"));
                return access;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Seleciona o ultimo ID Remoto Inserido
    public String SelectLastRemoteIDofUsers() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("usuario", new String[]{"max(_id_usuario_remote) as _id_usuario_remote"}, null, null, null, null, null, null);
            c.moveToFirst();
            return c.getString(c.getColumnIndex("_id_usuario_remote"));
        } finally {
            db.close();
        }
    }

    //Select List of Lamps
    public Cursor Select_ListUsers() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("usuario", null, "(_id_usuario_remote > ? OR _id_usuario_remote is null) AND enable = ?", new String[]{"1", "1"}, null, null, "nome", null);
            c.moveToFirst();
            return c;
        } finally {
            db.close();
        }
    }

    //Seleciona a Lamp pelo ID
    public Cursor SelectUserByID(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("usuario", null, "_id_usuario=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    public boolean SelectUserByRemoteID(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("usuario", null, "_id_usuario_remote = ?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return true;
            } else {
                return false;
            }
        } finally {
            db.close();
        }
    }

    public String SelectNomeOfUserByUserandPass(String user, String pass) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("usuario", new String[]{"nome"}, "usuario = ? AND senha = ?", new String[]{user, pass}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c.getString(c.getColumnIndex("nome"));
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Insert Remote ID for User
    public int InsertUserRemoteID(String RemoteID, String id_user) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("_id_usuario_remote", RemoteID);

            return db.update("usuario", values, "_id_usuario=?", new String[]{id_user});
        } finally {
            db.close();
        }
    }

    //Select Ambientes pendentes a inserção
    public Cursor SelectUsersInsertPending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("usuario", null, "_id_usuario_remote is null", null, null, null, "_id_usuario", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Seleciona os Usuários pendentes para inserir no DB remoto
    public Cursor SelectUsersEditPending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("usuario", null, "edited=?", new String[]{"1"}, null, null, "_id_usuario", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    public long Update_User(String id, String nome, String usuario, String senha, String acesso, String edited) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("nome", nome);
            values.put("usuario", usuario);
            values.put("senha", senha);
            values.put("acesso", acesso);
            values.put("edited", edited);
            return db.update("usuario", values, "_id_usuario=?", new String[]{id});
        } finally {
            db.close();
        }
    }

    //Seleciona o Id remoto da tabela Usuarios
    public long SelectUserRemoteIDById(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("usuario", new String[]{"_id_usuario_remote"}, "_id_usuario=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                if (c.getString(c.getColumnIndex("_id_usuario_remote")) != null) {
                    return Integer.parseInt(c.getString(c.getColumnIndex("_id_usuario_remote")));
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } finally {
            db.close();
        }
    }

    //Remove o editado da tabela Usuarios
    public int RemoveEditedfromUsers(String id) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("edited", "");

            return db.update("usuario", values, "_id_usuario=?", new String[]{id});
        } finally {
            db.close();
        }
    }

    //Deleta Lampa do DB
    public int DeleteUser(String id) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("enable", "0");

            return db.update("usuario", values, "_id_usuario=?", new String[]{id});
        } finally {
            db.close();
        }
    }

    public int DeleteAllUsers() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            return db.delete("usuario", null, null);
        } finally {
            db.close();
        }
    }

    //Seleciona os Usuários pendentes para Deletar no DB remoto
    public Cursor SelectUsersDeletePending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("usuario", null, "enable=?", new String[]{"0"}, null, null, "_id_usuario", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Seleciona o Id remoto da tabela Usuarios
    public long SelectUserIDByIDRemote(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("usuario", new String[]{"_id_usuario"}, "_id_usuario_remote=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                if (c.getString(c.getColumnIndex("_id_usuario")) != null) {
                    return Integer.parseInt(c.getString(c.getColumnIndex("_id_usuario")));
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } finally {
            db.close();
        }
    }

    /***********************************************************************************************
     * FUNÇÕES RELACIONADAS A MÃO DE OBRA
     ***********************************************************************************************/

    // Insere Mão de Obra
    public long Insert_HandsOn(String descricao, double value_handson, String id_remoto, String enable) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            if ((id_remoto != null) && (!id_remoto.equals(""))) {
                values.put("_id_handson_remote", id_remoto);
            } else {
                values.put("_id_handson_remote", (byte[]) null);
            }
            values.put("descricao", descricao);
            values.put("valor", value_handson);
            if (enable == null) {
                values.put("enable", "1");
            } else {
                values.put("enable", enable);
            }
            return db.insert("handson", "", values);
        } finally {
            db.close();
        }
    }

    //Seleciona a Mão de Obra pelo ID
    public Cursor SelectHandsOnByID(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("handson", null, "_id_handson=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Insert Remote ID for HandsOn
    public int InsertHandsOnRemoteID(String RemoteID, String id_handson) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("_id_handson_remote", RemoteID);

            return db.update("handson", values, "_id_handson=?", new String[]{id_handson});
        } finally {
            db.close();
        }
    }

    //Select HandsOn pendentes a inserção
    public Cursor SelectHandsOnInsertPending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("handson", null, "_id_handson_remote is null", null, null, null, "_id_handson", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    // Update HandsOn
    public long Update_HandsOn(String id, String descricao, double value, String edited) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("descricao", descricao);
            values.put("valor", value);
            values.put("edited", edited);
            return db.update("handson", values, "_id_handson=?", new String[]{id});
        } finally {
            db.close();
        }
    }

    //Seleciona o Id remoto da tabela HandsOn
    public long SelectHandsOnRemoteIDById(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("handson", new String[]{"_id_handson_remote"}, "_id_handson=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                if (c.getString(c.getColumnIndex("_id_handson_remote")) != null) {
                    return Integer.parseInt(c.getString(c.getColumnIndex("_id_handson_remote")));
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } finally {
            db.close();
        }
    }

    //Remove o editado da tabela HandsOn
    public int RemoveEditedfromHandsOn(String id) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("edited", "");

            return db.update("handson", values, "_id_handson=?", new String[]{id});
        } finally {
            db.close();
        }
    }

    //Seleciona os HandsOn pendentes para inserir no DB remoto
    public Cursor SelectHandsOnEditPending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("handson", null, "edited=?", new String[]{"1"}, null, null, "_id_handson", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Select List of HandsOn
    public Cursor Select_ListHandsOn() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("handson", new String[]{"handson._id_handson as _id , handson.*"}, "enable = ?", new String[]{"1"}, null, null, "descricao", null);
            c.moveToFirst();
            return c;
        } finally {
            db.close();
        }
    }

    //Deleta HandsOn do DB
    public int DeleteHandsOn(String id) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("enable", "0");

            return db.update("handson", values, "_id_handson=?", new String[]{id});
        } finally {
            db.close();
        }
    }

    //Seleciona od pendentes para Deletar no DB remoto
    public Cursor SelectHandsOnDeletePending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("handson", null, "enable=?", new String[]{"0"}, null, null, "_id_handson", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Seleciona o ultimo ID Remoto Inserido
    public String SelectLastRemoteIDofHandsOn() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("handson", new String[]{"max(_id_handson_remote) as _id_handson_remote"}, null, null, null, null, null, null);
            c.moveToFirst();
            return c.getString(c.getColumnIndex("_id_handson_remote"));
        } finally {
            db.close();
        }
    }

    //Seleciona o Id remoto da tabela HandsOn
    public long SelectHandsOnIDByIDRemote(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("handson", new String[]{"_id_handson"}, "_id_handson_remote=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                if (c.getString(c.getColumnIndex("_id_handson")) != null) {
                    return Integer.parseInt(c.getString(c.getColumnIndex("_id_handson")));
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } finally {
            db.close();
        }
    }

    public Cursor Select_ListHandsOnAutoComplete() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("handson", new String[]{"_id_handson as _id, descricao"}, "enable = ?", new String[]{"1"}, null, null, "descricao", null);
            c.moveToFirst();
            return c;
        } finally {
            db.close();
        }
    }

    public Cursor getMatchingHandsOn(String s) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("handson", new String[]{"_id_handson as _id, descricao"}, "descricao like ? AND enable = ?", new String[]{"%" + s + "%", "1"}, null, null, "descricao", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    /***********************************************************************************************
     * FUNÇÕES RELACIONADAS AO PREÇO KWh
     ***********************************************************************************************/
    // Update Preço KWh
    public long Update_KWh(double value) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            Cursor c = db.query("kwh", null, null, null, null, null, null, null);
            c.moveToFirst();

            if (c.getCount() == 1) {
                ContentValues values = new ContentValues();
                values.put("valor", value);
                values.put("edited", "1");
                return db.update("kwh", values, "_id_kwh=?", new String[]{"1"});
            } else {
                ContentValues values = new ContentValues();
                values.put("valor", value);
                values.put("edited", "1");
                return db.insert("kwh", "", values);
            }

        } finally {
            db.close();
        }
    }

    public Double Select_KWh() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("kwh", null, null, null, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c.getDouble(c.getColumnIndex("valor"));
            } else {
                return 0.0;
            }
        } finally {
            db.close();
        }
    }


    /***********************************************************************************************
     * FUNÇÕES RELACIONADAS AO WATCH
     ***********************************************************************************************/

    //Seleciona o ultimo ID Remoto do Watch
    public String SelectLastRemoteIDofWatch() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("watch", new String[]{"max(_id_watch_remote) as _id_watch_remote"}, null, null, null, null, null, null);
            c.moveToFirst();
            return c.getString(c.getColumnIndex("_id_watch_remote"));
        } finally {
            db.close();
        }
    }

    public long Insert_Watch(Long id_remoto, int action, String table, int table_id, int done) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("_id_watch_remote", id_remoto);
            values.put("action", action);
            values.put("_table", table);
            values.put("table_id", table_id);
            values.put("done", done);
            return db.insert("watch", "", values);
        } finally {
            db.close();
        }
    }

    public int CountWatch() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("watch", null, null, null, null, null, null, null);
            c.moveToFirst();
            return c.getCount();
        } finally {
            db.close();
        }
    }

    //Seleciona od pendentes para Atualizar o DB
    public Cursor SelectWatchPending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("watch", null, "done=?", new String[]{"0"}, null, null, "_id_watch", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Seleciona od pendentes para Atualizar o DB
    public Cursor SelectListWatch() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("watch", null, null, null, null, null, "_id_watch", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    // Update Preço KWh
    public long WatchMarkDone(long id) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("done", 1);
            return db.update("watch", values, "_id_watch=?", new String[]{String.valueOf(id)});
        } finally {
            db.close();
        }
    }

    /***********************************************************************************************
     * FUNÇÕES RELACIONADAS AO ESTUDO
     ***********************************************************************************************/

    // Insere Lampada Atual
    public long Insert_Estudo(String descricao, String id_cliente, String id_client_remote, String id_remoto, String data, String psm, String pcm, String data_pedido, String enable) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        if ((id_remoto != null) && (!id_remoto.equals(""))) {
            values.put("_id_estudo_remote", id_remoto);
        } else {
            values.put("_id_estudo_remote", (byte[]) null);
        }
        values.put("descricao", descricao);
        values.put("_id_cliente", id_cliente);
        values.put("_id_client_remote", id_client_remote);
        values.put("psm", psm);
        values.put("pcm", pcm);
        values.put("data", data);
        values.put("data_pedido", data_pedido);

        if (enable == null) {
            values.put("enable", "1");
        } else {
            values.put("enable", enable);
        }
        return db.insert("estudo", "", values);
    }

    //Seleciona a Lamp pelo ID
    public Cursor SelectEstudoByID(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("estudo", null, "_id_estudo=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Insert Remote ID from Lamp
    public int InsertEstudoRemoteID(String RemoteID, String id_estudo) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("_id_estudo_remote", RemoteID);

            // insert into Clientes values (...)
            //Log.d(TAG, "ID Remoto inserido com sucesso !");
            //return db.insert("clientes", "", values);
            return db.update("estudo", values, "_id_estudo=?", new String[]{id_estudo});
        } finally {
            db.close();
        }
    }

    //Insert Remote ID from Lamp
    public int InsertEstudoClientRemoteID(String RemoteID, String id_estudo) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("_id_client_remote", RemoteID);

            // insert into Clientes values (...)
            //Log.d(TAG, "ID Remoto inserido com sucesso !");
            //return db.insert("clientes", "", values);
            return db.update("estudo", values, "_id_estudo=?", new String[]{id_estudo});
        } finally {
            db.close();
        }
    }

    //Select Lamp pendentes a inserção
    public Cursor SelectEstudoInsertPending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("estudo", null, "_id_estudo_remote is null", null, null, null, "_id_estudo", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Select List of Lamps
    public Cursor Select_ListEstudos() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("estudo, clientes", new String[]{"estudo.*,clientes.nome as cliente"}, "enable=? AND  CASE WHEN estudo._id_client_remote is null THEN estudo._id_cliente = clientes._id_cliente ELSE estudo._id_client_remote = clientes._id_client_remote END", new String[]{"1"}, null, null, "descricao", null);
            c.moveToFirst();
            return c;
        } finally {
            db.close();
        }
    }

    //Select List of Lamps
    public Cursor Select_PedidosOfEstudo() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("estudo, clientes", new String[]{"estudo.*,clientes.nome as cliente"}, "enable = ? AND CASE WHEN estudo._id_client_remote is null THEN estudo._id_cliente = clientes._id_cliente ELSE estudo._id_client_remote = clientes._id_client_remote END AND (psm = ? OR pcm = ?)", new String[]{"1", "1", "1"}, null, null, "descricao", null);
            c.moveToFirst();
            return c;
        } finally {
            db.close();
        }
    }

    //Select Nome do Estudo
    public String Select_NameofEstudo(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("estudo", new String[]{"descricao"}, "enable=? AND _id_estudo=?", new String[]{"1", id}, null, null, null, null);
            c.moveToFirst();
            return c.getString(c.getColumnIndex("descricao"));
        } finally {
            db.close();
        }
    }

    // Insere Lampada Atual
    public long Update_Estudo(String id, String descricao, String edited, String id_client_remote, String data, String data_pedido, String psm, String pcm, String enable) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            if (descricao != null) {
                values.put("descricao", descricao);
            }
            if (edited != null) {
                values.put("edited", edited);
            }
            if (id_client_remote != null) {
                values.put("_id_client_remote", id_client_remote);
            }
            if (data != null) {
                values.put("data", data);
            }
            if (psm != null) {
                values.put("psm", psm);
            }
            if (pcm != null) {
                values.put("pcm", pcm);
            }
            if (data_pedido != null) {
                values.put("data_pedido", data_pedido);
            }
            if (enable != null) {
                values.put("enable", enable);
            }
            // insert into Clientes values (...)
            // Log.d(TAG, "Cadastro Atualizados com sucesso !");
            //return db.insert("clientes", "", values);
            return db.update("estudo", values, "_id_estudo=?", new String[]{id});
        } finally {
            db.close();
        }
    }

    //Seleciona o Id remoto da tabela Lamp
    public long SelectEstudoRemoteIDById(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("estudo", new String[]{"_id_estudo_remote"}, "_id_estudo=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                if (c.getString(c.getColumnIndex("_id_estudo_remote")) != null) {
                    return Integer.parseInt(c.getString(c.getColumnIndex("_id_estudo_remote")));
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } finally {
            db.close();
        }
    }

    //Remove o editado da tabela Lamp
    public int RemoveEditedfromEstudo(String id) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("edited", "");

            return db.update("estudo", values, "_id_estudo=?", new String[]{id});
        } finally {
            db.close();
        }
    }

    //Seleciona os HandsOn pendentes para inserir no DB remoto
    public Cursor SelectEstudoEditPending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("estudo", null, "edited=?", new String[]{"1"}, null, null, "_id_estudo", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Seleciona o ultimo ID Remoto Inserido
    public String SelectLastRemoteIDofEstudo() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("estudo", new String[]{"max(_id_estudo_remote) as _id_estudo_remote"}, null, null, null, null, null, null);
            c.moveToFirst();
            return c.getString(c.getColumnIndex("_id_estudo_remote"));
        } finally {
            db.close();
        }
    }

    //Seleciona o Id remoto da tabela Usuarios
    public long SelectEstudoIDByIDRemote(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("estudo", new String[]{"_id_estudo"}, "_id_estudo_remote=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                if (c.getString(c.getColumnIndex("_id_estudo")) != null) {
                    return Integer.parseInt(c.getString(c.getColumnIndex("_id_estudo")));
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } finally {
            db.close();
        }
    }

    //Deleta Lampa do DB
    public int DeleteEstudo(String id) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("enable", "0");

            return db.update("estudo", values, "_id_estudo=?", new String[]{id});
        } finally {
            db.close();
        }
    }

    //Seleciona od pendentes para Deletar no DB remoto
    public Cursor SelectEstudoDeletePending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("estudo", null, "enable=?", new String[]{"0"}, null, null, "_id_estudo", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    /***********************************************************************************************
     * FUNÇÕES RELACIONADAS AOS AMBIENTES DOS ESTUDOS
     ***********************************************************************************************/

    // Insere Lampada Atual
    public long Insert_AmbienteOfEstudo(String id_remoto, String id_estudo, String id_estudo_remote, String id_ambiente, String id_ambiente_remote, String enable) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        if ((id_remoto != null) && (!id_remoto.equals(""))) {
            values.put("_id_ambiente_estudo_remote", id_remoto);
        } else {
            values.put("_id_ambiente_estudo_remote", (byte[]) null);
        }
        if (id_estudo != null) {
            values.put("_id_estudo", id_estudo);
        }
        if (id_estudo_remote != null) {
            values.put("_id_estudo_remote", id_estudo_remote);
        }
        if (id_ambiente != null) {
            values.put("_id_ambiente", id_ambiente);
        }
        if (id_ambiente_remote != null) {
            values.put("_id_ambiente_remote", id_ambiente_remote);
        }
        if (enable == null) {
            values.put("enable", "1");
        } else {
            values.put("enable", enable);
        }
        return db.insert("ambientes_estudo", "", values);
    }

    //Seleciona o Ambiente de um Estudo pelo ID
    public Cursor SelectAmbienteOfEstudoByID(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("ambientes_estudo", null, "_id_ambiente_estudo=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    public int InsertAmbienteOfEstudoAmbienteRemoteID(String RemoteID, String id_AmbienteOfEstudo) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("_id_ambiente_remote", RemoteID);

            // insert into Clientes values (...)
            //Log.d(TAG, "ID Remoto inserido com sucesso !");
            //return db.insert("clientes", "", values);
            return db.update("ambientes_estudo", values, "_id_ambiente_estudo=?", new String[]{id_AmbienteOfEstudo});
        } finally {
            db.close();
        }
    }

    //Seleciona o Id remoto da tabela Lamp
    public long SelectAmbienteOfEstudoRemoteIDById(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("ambientes_estudo", new String[]{"_id_ambiente_estudo_remote"}, "_id_ambiente_estudo=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                if (c.getString(c.getColumnIndex("_id_ambiente_estudo_remote")) != null) {
                    return Integer.parseInt(c.getString(c.getColumnIndex("_id_ambiente_estudo_remote")));
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } finally {
            db.close();
        }
    }

    public int InsertEstudoOfEstudoAmbienteRemoteID(String RemoteID, String id_AmbienteOfEstudo) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("_id_estudo_remote", RemoteID);

            // insert into Clientes values (...)
            //Log.d(TAG, "ID Remoto inserido com sucesso !");
            //return db.insert("clientes", "", values);
            return db.update("ambientes_estudo", values, "_id_ambiente_estudo=?", new String[]{id_AmbienteOfEstudo});
        } finally {
            db.close();
        }
    }

    //Insert Remote ID from Ambiente of Estudo
    public int InsertAmbienteOfEstudoRemoteID(String RemoteID, String id_AmbienteOfEstudo) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("_id_ambiente_estudo_remote", RemoteID);

            // insert into Clientes values (...)
            //Log.d(TAG, "ID Remoto inserido com sucesso !");
            //return db.insert("clientes", "", values);
            return db.update("ambientes_estudo", values, "_id_ambiente_estudo=?", new String[]{id_AmbienteOfEstudo});
        } finally {
            db.close();
        }
    }

    //Select Lamp pendentes a inserção
    public Cursor SelectAmbienteOfEstudoInsertPending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("ambientes_estudo", null, "_id_ambiente_estudo_remote is null", null, null, null, "_id_ambiente_estudo", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    //Select Lamp pendentes a inserção
    public Cursor SelectListAmbienteOfEstudo() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("ambientes_estudo", null, null, null, null, null, "_id_ambiente_estudo", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }


    //Select List of Lamps
    public Cursor Select_ListAmbientesOfEstudo(String id_estudo, String id_estudo_remoto) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c;
        try {
            c = db.query("ambientes_estudo, ambientes, estudo", new String[]{"ambientes_estudo.*,ambientes.descricao"}, "ambientes_estudo.enable=? AND CASE WHEN ambientes_estudo._id_ambiente_remote is null THEN ambientes_estudo._id_ambiente = ambientes._id_ambiente ELSE ambientes_estudo._id_ambiente_remote = ambientes._id_ambiente_remote END AND CASE WHEN ambientes_estudo._id_estudo_remote is null THEN ambientes_estudo._id_estudo = estudo._id_estudo AND ambientes_estudo._id_estudo=? ELSE ambientes_estudo._id_estudo_remote = estudo._id_estudo_remote AND ambientes_estudo._id_estudo_remote=? END", new String[]{"1", id_estudo, id_estudo_remoto}, null, null, "ambientes.descricao", null);
            c.moveToFirst();
            return c;
        } finally {
            db.close();
        }
    }

    //Seleciona o ultimo ID Remoto Inserido
    public String SelectLastRemoteIDofAmbientesOfEstudo() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("ambientes_estudo", new String[]{"max(_id_ambiente_estudo_remote) as _id_ambiente_estudo_remote"}, null, null, null, null, null, null);
            c.moveToFirst();
            return c.getString(c.getColumnIndex("_id_ambiente_estudo_remote"));
        } finally {
            db.close();
        }
    }

    //Seleciona o Id remoto da tabela Usuarios
    public long SelectAmbienteOfEstudoIDByIDRemote(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("ambientes_estudo", new String[]{"_id_ambiente_estudo"}, "_id_ambiente_estudo_remote=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                if (c.getString(c.getColumnIndex("_id_ambiente_estudo")) != null) {
                    return Integer.parseInt(c.getString(c.getColumnIndex("_id_ambiente_estudo")));
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } finally {
            db.close();
        }
    }

    //Deleta Lampa do DB
    public int DeleteAmbienteOfEstudo(String id) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("enable", "0");

            return db.update("ambientes_estudo", values, "_id_ambiente_estudo=?", new String[]{id});
        } finally {
            db.close();
        }
    }

    public String SelectAmbienteofAmbienteOfEstudo(String id_ambiente_remoto) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c;
        try {
            c = db.query("ambientes_estudo, ambientes", new String[]{"ambientes.descricao as descricao"}, "ambientes_estudo.enable=? AND ambientes_estudo._id_ambiente_estudo = ? AND CASE WHEN ambientes_estudo._id_ambiente_remote is null THEN ambientes_estudo._id_ambiente = ambientes._id_ambiente ELSE ambientes_estudo._id_ambiente_remote = ambientes._id_ambiente_remote END", new String[]{"1", id_ambiente_remoto}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c.getString(c.getColumnIndex("descricao"));
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    public long SelectAmbienteRemoteOfAmbienteOfEstudo(String id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c;
        try {
            c = db.query("ambientes_estudo", new String[]{"_id_ambiente_remote"}, "enable = ? AND _id_ambiente_estudo = ?", new String[]{"1", id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c.getLong(c.getColumnIndex("_id_ambiente_remote"));
            } else {
                return 0;
            }
        } finally {
            db.close();
        }
    }

    //Seleciona as Lamps pendentes para Deletar no DB remoto
    public Cursor SelectAmbienteofEstudoDeletePending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("ambientes_estudo", null, "enable=?", new String[]{"0"}, null, null, "_id_ambiente_estudo", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    /***********************************************************************************************
     * FUNÇÕES RELACIONADAS AOS ITENS DO ESTUDO
     ***********************************************************************************************/

    // Insere Lampada Atual
    public long Insert_Itens_Of_Estudo(String id_remoto, String id_estudo, String id_estudo_remote,
                                       String id_ambiente_estudo, String id_ambiente_estudo_remote,
                                       int key_table, String id_table, String id_table_remote,
                                       String id_lamp, String id_lamp_remote,
                                       String id_led, String id_led_remote,
                                       String quantidade, String horas, String enable) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        if ((id_remoto != null) && (!id_remoto.equals(""))) {
            values.put("_id_itens_remote", id_remoto);
        } else {
            values.put("_id_itens_remote", (byte[]) null);
        }

        if (id_estudo != null) {
            values.put("_id_estudo", id_estudo);
        }

        if (id_estudo_remote != null) {
            values.put("_id_estudo_remote", id_estudo_remote);
        }

        if (id_ambiente_estudo != null) {
            values.put("_id_ambiente_estudo", id_ambiente_estudo);
        }

        if (id_ambiente_estudo_remote != null) {
            values.put("_id_ambiente_estudo_remote", id_ambiente_estudo_remote);
        }
        values.put("key_table", key_table);
        if (id_table != null) {
            values.put("_id_table", id_table);
        }

        if (id_table_remote != null) {
            values.put("_id_table_remote", id_table_remote);
        }

        if (id_lamp != null) {
            values.put("_id_lamp", id_lamp);
        }

        if (id_lamp_remote != null) {
            values.put("_id_lamp_remote", id_lamp_remote);
        }

        if (id_led != null) {
            values.put("_id_led", id_led);
        }

        if (id_led_remote != null) {
            values.put("_id_led_remote", id_led_remote);
        }

        if (quantidade != null) {
            values.put("quantidade", quantidade);
        }

        if (horas != null) {
            values.put("horas", horas);
        }

        if (enable == null) {
            values.put("enable", "1");
        } else {
            values.put("enable", enable);
        }
        return db.insert("itens_of_estudo", "", values);
    }

    public Cursor SelectItemOfEstudoByID(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("itens_of_estudo", null, "_id_itens = ?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    public int InsertEstudoRemoteInItensOfEstudo(String RemoteID, String id_ItensOfEstudo) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("_id_estudo_remote", RemoteID);

            // insert into Clientes values (...)
            //Log.d(TAG, "ID Remoto inserido com sucesso !");
            //return db.insert("clientes", "", values);
            return db.update("itens_of_estudo", values, "_id_itens = ?", new String[]{id_ItensOfEstudo});
        } finally {
            db.close();
        }
    }

    public long SelectItensOfEstudoRemoteIDById(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("itens_of_estudo", new String[]{"_id_itens_remote"}, "_id_itens = ?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                if (c.getString(c.getColumnIndex("_id_itens_remote")) != null) {
                    return Integer.parseInt(c.getString(c.getColumnIndex("_id_itens_remote")));
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } finally {
            db.close();
        }
    }

    public int InsertAmbienteOfEstudoRemoteIDInItensOfEstudo(String RemoteID, String id_ItensOfEstudo) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("_id_ambiente_estudo_remote", RemoteID);

            // insert into Clientes values (...)
            //Log.d(TAG, "ID Remoto inserido com sucesso !");
            //return db.insert("clientes", "", values);
            return db.update("itens_of_estudo", values, "_id_itens = ?", new String[]{id_ItensOfEstudo});
        } finally {
            db.close();
        }
    }

    public int InsertTableRemoteIDInItensOfEstudo(String RemoteID, String id_ItensOfEstudo) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("_id_table_remote", RemoteID);

            // insert into Clientes values (...)
            //Log.d(TAG, "ID Remoto inserido com sucesso !");
            //return db.insert("clientes", "", values);
            return db.update("itens_of_estudo", values, "_id_itens = ?", new String[]{id_ItensOfEstudo});
        } finally {
            db.close();
        }
    }

    public int InsertLampRemoteIDInItensOfEstudo(String RemoteID, String id_ItensOfEstudo) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("_id_lamp_remote", RemoteID);

            // insert into Clientes values (...)
            //Log.d(TAG, "ID Remoto inserido com sucesso !");
            //return db.insert("clientes", "", values);
            return db.update("itens_of_estudo", values, "_id_itens = ?", new String[]{id_ItensOfEstudo});
        } finally {
            db.close();
        }
    }

    public int InsertLedRemoteIDInItensOfEstudo(String RemoteID, String id_ItensOfEstudo) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("_id_led_remote", RemoteID);

            // insert into Clientes values (...)
            //Log.d(TAG, "ID Remoto inserido com sucesso !");
            //return db.insert("clientes", "", values);
            return db.update("itens_of_estudo", values, "_id_itens = ?", new String[]{id_ItensOfEstudo});
        } finally {
            db.close();
        }
    }

    public int InsertItemOfEstudoRemoteID(String RemoteID, String id_ItemOfEstudo) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("_id_itens_remote", RemoteID);

            // insert into Clientes values (...)
            //Log.d(TAG, "ID Remoto inserido com sucesso !");
            //return db.insert("clientes", "", values);
            return db.update("itens_of_estudo", values, "_id_itens = ?", new String[]{id_ItemOfEstudo});
        } finally {
            db.close();
        }
    }

    public Cursor SelectItensOfEstudoInsertPending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("itens_of_estudo", null, "_id_itens_remote is null", null, null, null, "_id_itens", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    public Cursor SelectLampsOfAmbienteOfEstudo(String id_ambiente_estudo, String id_ambiente_estudo_remote) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c;
        try {
            c = db.query("itens_of_estudo, lamp, ambientes_estudo", new String[]{"itens_of_estudo._id_itens as _id, lamp.descricao as descricao,lamp.potencia as potencia, itens_of_estudo.quantidade as quantidade, itens_of_estudo.horas as horas"},
                    "itens_of_estudo.enable = ? AND (itens_of_estudo._id_lamp is not null OR itens_of_estudo._id_lamp_remote is not null) AND " +
                            "CASE WHEN itens_of_estudo._id_lamp_remote is null THEN itens_of_estudo._id_lamp = lamp._id_lamp ELSE itens_of_estudo._id_lamp_remote = lamp._id_lamp_remote END AND " +
                            "CASE WHEN itens_of_estudo._id_ambiente_estudo_remote is null THEN itens_of_estudo._id_ambiente_estudo = ambientes_estudo._id_ambiente_estudo AND ambientes_estudo._id_ambiente_estudo = ? ELSE itens_of_estudo._id_ambiente_estudo_remote = ambientes_estudo._id_ambiente_estudo_remote AND ambientes_estudo._id_ambiente_estudo_remote = ? END"
                    , new String[]{"1", id_ambiente_estudo, id_ambiente_estudo_remote}, null, null, "lamp.descricao", null);
            c.moveToFirst();
            return c;
        } finally {
            db.close();
        }
    }

    public Cursor SelectLedsOfAmbienteOfEstudo(String id_ambiente_estudo, String id_ambiente_estudo_remote) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c;
        try {
            c = db.query("itens_of_estudo, led_solution, ambientes_estudo", new String[]{"itens_of_estudo._id_itens as _id, led_solution.descricao as descricao, led_solution.potencia as potencia, itens_of_estudo.quantidade as quantidade, itens_of_estudo.horas as horas"},
                    "itens_of_estudo.enable = ? AND (itens_of_estudo._id_led is not null OR itens_of_estudo._id_led_remote is not null) AND " +
                            "CASE WHEN itens_of_estudo._id_led_remote is null THEN itens_of_estudo._id_led = led_solution._id_led ELSE itens_of_estudo._id_led_remote = led_solution._id_led_remote END AND " +
                            "CASE WHEN itens_of_estudo._id_ambiente_estudo_remote is null THEN itens_of_estudo._id_ambiente_estudo = ambientes_estudo._id_ambiente_estudo AND ambientes_estudo._id_ambiente_estudo = ? ELSE itens_of_estudo._id_ambiente_estudo_remote = ambientes_estudo._id_ambiente_estudo_remote AND ambientes_estudo._id_ambiente_estudo_remote = ? END"
                    , new String[]{"1", id_ambiente_estudo, id_ambiente_estudo_remote}, null, null, "led_solution.descricao", null);
            c.moveToFirst();
            return c;
        } finally {
            db.close();
        }
    }

    public Cursor SelectHandsOnOfAmbienteOfEstudo(String id_ambiente_estudo, String id_ambiente_estudo_remote) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c;
        try {
            c = db.query("itens_of_estudo, handson, ambientes_estudo", new String[]{"itens_of_estudo._id_itens as _id, handson.descricao as descricao, quantidade"},
                    "itens_of_estudo.enable = ? AND itens_of_estudo.key_table = 2 AND " +
                            "CASE WHEN itens_of_estudo._id_table_remote is null THEN itens_of_estudo._id_table = handson._id_handson ELSE itens_of_estudo._id_table_remote = handson._id_handson_remote END AND " +
                            "CASE WHEN itens_of_estudo._id_ambiente_estudo_remote is null THEN itens_of_estudo._id_ambiente_estudo = ambientes_estudo._id_ambiente_estudo AND ambientes_estudo._id_ambiente_estudo = ? ELSE itens_of_estudo._id_ambiente_estudo_remote = ambientes_estudo._id_ambiente_estudo_remote AND ambientes_estudo._id_ambiente_estudo_remote = ? END"
                    , new String[]{"1", id_ambiente_estudo, id_ambiente_estudo_remote}, null, null, "handson.descricao", null);
            c.moveToFirst();
            return c;
        } finally {
            db.close();
        }
    }

    public String SelectLastRemoteIDofItensOfEstudo() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("itens_of_estudo", new String[]{"max(_id_itens_remote) as _id_itens_remote"}, null, null, null, null, null, null);
            c.moveToFirst();
            return c.getString(c.getColumnIndex("_id_itens_remote"));
        } finally {
            db.close();
        }
    }

    public long SelectItensOfEstudoIDByIDRemote(String id) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("itens_of_estudo", new String[]{"_id_itens"}, "_id_itens_remote=?", new String[]{id}, null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                if (c.getString(c.getColumnIndex("_id_itens")) != null) {
                    return Integer.parseInt(c.getString(c.getColumnIndex("_id_itens")));
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } finally {
            db.close();
        }
    }

    public int DeleteItensOfEstudo(String id) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("enable", "0");

            return db.update("itens_of_estudo", values, "_id_itens = ?", new String[]{id});
        } finally {
            db.close();
        }
    }

    public Cursor SelectItensOfEstudoDeletePending() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("itens_of_estudo", null, "enable = ?", new String[]{"0"}, null, null, "_id_itens", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    /***********************************************************************************************
     * FUNÇÕES RELACIONADAS AS ESTATISTICAS
     ***********************************************************************************************/

    public Cursor SelectEstatiLamps(String id_estudo, String id_estudo_remote) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("itens_of_estudo " +
                            "INNER JOIN ambientes_estudo  ON " +
                            "CASE WHEN (ambientes_estudo._id_ambiente_estudo_remote is null OR itens_of_estudo._id_ambiente_estudo_remote is null) THEN " +
                            "ambientes_estudo._id_ambiente_estudo = itens_of_estudo._id_ambiente_estudo ELSE " +
                            "ambientes_estudo._id_ambiente_estudo_remote = itens_of_estudo._id_ambiente_estudo_remote END " +
                            "INNER JOIN ambientes ON " +
                            "CASE WHEN (ambientes_estudo._id_ambiente_remote is null OR ambientes._id_ambiente_remote is null) THEN " +
                            "ambientes_estudo._id_ambiente = ambientes._id_ambiente ELSE " +
                            "ambientes_estudo._id_ambiente_remote = ambientes._id_ambiente_remote END " +
                            "INNER JOIN lamp ON " +
                            "CASE WHEN (itens_of_estudo._id_lamp_remote is null OR lamp._id_lamp_remote is null) THEN " +
                            "itens_of_estudo._id_lamp = lamp._id_lamp ELSE " +
                            "itens_of_estudo._id_lamp_remote = lamp._id_lamp_remote END " +
                            "INNER JOIN led_solution ON " +
                            "CASE WHEN (itens_of_estudo._id_led_remote is null OR led_solution._id_led_remote is null) THEN " +
                            "itens_of_estudo._id_led = led_solution._id_led ELSE " +
                            "itens_of_estudo._id_led_remote = led_solution._id_led_remote END ",
                    new String[]{"ambientes.descricao as descricao, horas, quantidade, led_solution.potencia as pot_led, lamp.potencia as pot_lamp, valor"},
                    "CASE WHEN itens_of_estudo._id_estudo_remote is null THEN itens_of_estudo._id_estudo = ? ELSE itens_of_estudo._id_estudo_remote = ? END AND itens_of_estudo.enable = ?",
                    new String[]{id_estudo, id_estudo_remote, "1"},
                    null, null, "ambientes.descricao", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    public Cursor SelectEstatiHandsOn(String id_estudo, String id_estudo_remote) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("itens_of_estudo " +
                            "INNER JOIN ambientes_estudo  ON " +
                            "CASE WHEN (ambientes_estudo._id_ambiente_estudo_remote is null OR itens_of_estudo._id_ambiente_estudo_remote is null) THEN " +
                            "ambientes_estudo._id_ambiente_estudo = itens_of_estudo._id_ambiente_estudo ELSE " +
                            "ambientes_estudo._id_ambiente_estudo_remote = itens_of_estudo._id_ambiente_estudo_remote END " +
                            "INNER JOIN ambientes ON " +
                            "CASE WHEN (ambientes_estudo._id_ambiente_remote is null OR ambientes._id_ambiente_remote is null) THEN " +
                            "ambientes_estudo._id_ambiente = ambientes._id_ambiente ELSE " +
                            "ambientes_estudo._id_ambiente_remote = ambientes._id_ambiente_remote END " +
                            "INNER JOIN handson ON " +
                            "CASE WHEN (itens_of_estudo._id_table_remote is null OR handson._id_handson_remote is null) THEN " +
                            "itens_of_estudo._id_table = handson._id_handson ELSE " +
                            "itens_of_estudo._id_table_remote = handson._id_handson_remote END ",
                    new String[]{"ambientes.descricao as descricao, itens_of_estudo.quantidade, valor , sum(valor*quantidade) as valor_mob"},
                    "CASE WHEN itens_of_estudo._id_estudo_remote is null THEN itens_of_estudo._id_estudo = ? ELSE itens_of_estudo._id_estudo_remote = ? END AND itens_of_estudo.enable = ? AND itens_of_estudo.key_table = 2",
                    new String[]{id_estudo, id_estudo_remote, "1"},
                    "ambientes.descricao", null, "ambientes.descricao", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    public Cursor SelectClienteOfEstudo(String id_estudo) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("estudo " +
                            "INNER JOIN clientes  ON " +
                            "CASE WHEN (estudo._id_client_remote is null OR clientes._id_client_remote is null) THEN " +
                            "estudo._id_cliente = clientes._id_cliente ELSE " +
                            "estudo._id_client_remote = clientes._id_client_remote END",
                    new String[]{"nome, endereco, numero,comp, bairro, cidade, email, email2, tel, tel2, contato"},
                    "estudo._id_estudo = ? AND estudo.enable = ?",
                    new String[]{id_estudo, "1"},
                    null, null, null, null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    /***********************************************************************************************
     * FUNÇÕES RELACIONADAS AO RELATÓRIO
     ***********************************************************************************************/

    public Cursor SelectRelatorioLamps(String id_estudo, String id_estudo_remote) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("itens_of_estudo " +
                            "INNER JOIN ambientes_estudo  ON " +
                            "CASE WHEN (ambientes_estudo._id_ambiente_estudo_remote is null OR itens_of_estudo._id_ambiente_estudo_remote is null) THEN " +
                            "ambientes_estudo._id_ambiente_estudo = itens_of_estudo._id_ambiente_estudo ELSE " +
                            "ambientes_estudo._id_ambiente_estudo_remote = itens_of_estudo._id_ambiente_estudo_remote END " +
                            "INNER JOIN ambientes ON " +
                            "CASE WHEN (ambientes_estudo._id_ambiente_remote is null OR ambientes._id_ambiente_remote is null) THEN " +
                            "ambientes_estudo._id_ambiente = ambientes._id_ambiente ELSE " +
                            "ambientes_estudo._id_ambiente_remote = ambientes._id_ambiente_remote END " +
                            "INNER JOIN lamp ON " +
                            "CASE WHEN (itens_of_estudo._id_lamp_remote is null OR lamp._id_lamp_remote is null) THEN " +
                            "itens_of_estudo._id_lamp = lamp._id_lamp ELSE " +
                            "itens_of_estudo._id_lamp_remote = lamp._id_lamp_remote END " +
                            "INNER JOIN led_solution ON " +
                            "CASE WHEN (itens_of_estudo._id_led_remote is null OR led_solution._id_led_remote is null) THEN " +
                            "itens_of_estudo._id_led = led_solution._id_led ELSE " +
                            "itens_of_estudo._id_led_remote = led_solution._id_led_remote END ",
                    new String[]{"ambientes.descricao as descricao, lamp.descricao || ' ' || lamp.potencia || 'W' as lamp, led_solution.descricao || ' ' || led_solution.potencia || 'W' as led, horas, quantidade, valor, led_solution.potencia as pot_led, lamp.potencia as pot_lamp, (valor*quantidade) as investimento"},
                    "CASE WHEN itens_of_estudo._id_estudo_remote is null THEN itens_of_estudo._id_estudo = ? ELSE itens_of_estudo._id_estudo_remote = ? END AND itens_of_estudo.enable = ?",
                    new String[]{id_estudo, id_estudo_remote, "1"},
                    null, null, "ambientes.descricao", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    public Cursor SelectRelatorioHandsOn(String id_estudo, String id_estudo_remote) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("itens_of_estudo " +
                            "INNER JOIN ambientes_estudo  ON " +
                            "CASE WHEN (ambientes_estudo._id_ambiente_estudo_remote is null OR itens_of_estudo._id_ambiente_estudo_remote is null) THEN " +
                            "ambientes_estudo._id_ambiente_estudo = itens_of_estudo._id_ambiente_estudo ELSE " +
                            "ambientes_estudo._id_ambiente_estudo_remote = itens_of_estudo._id_ambiente_estudo_remote END " +
                            "INNER JOIN ambientes ON " +
                            "CASE WHEN (ambientes_estudo._id_ambiente_remote is null OR ambientes._id_ambiente_remote is null) THEN " +
                            "ambientes_estudo._id_ambiente = ambientes._id_ambiente ELSE " +
                            "ambientes_estudo._id_ambiente_remote = ambientes._id_ambiente_remote END " +
                            "INNER JOIN handson ON " +
                            "CASE WHEN (itens_of_estudo._id_table_remote is null OR handson._id_handson_remote is null) THEN " +
                            "itens_of_estudo._id_table = handson._id_handson ELSE " +
                            "itens_of_estudo._id_table_remote = handson._id_handson_remote END ",
                    new String[]{"ambientes.descricao as descricao, itens_of_estudo.quantidade, valor, handson.descricao as maodeobra, (valor*quantidade) as valor_total"},
                    "CASE WHEN itens_of_estudo._id_estudo_remote is null THEN itens_of_estudo._id_estudo = ? ELSE itens_of_estudo._id_estudo_remote = ? END AND itens_of_estudo.enable = ? AND itens_of_estudo.key_table = 2",
                    new String[]{id_estudo, id_estudo_remote, "1"},
                    null, null, "ambientes.descricao", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    /***********************************************************************************************
     * FUNÇÕES RELACIONADAS AS PESQUISAS DAS ACTIVITYS
     ***********************************************************************************************/

    public Cursor getPesquisarClienteActivityClientes(String s) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("clientes", null, "nome like ?", new String[]{"%" + s + "%"}, null, null, "nome", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    public Cursor getPesquisarClienteActivityEstudos(String s) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("estudo " +
                            "INNER JOIN clientes ON " +
                            "CASE WHEN (estudo._id_client_remote is null OR clientes._id_client_remote is null) THEN " +
                            "estudo._id_cliente = clientes._id_cliente ELSE " +
                            "estudo._id_client_remote = clientes._id_client_remote END ",
                    new String[]{"estudo._id_estudo, estudo.descricao, clientes.nome as cliente, estudo.data"}, "nome like ? AND estudo.enable = ?", new String[]{"%" + s + "%", "1"}, null, null, "nome", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }

    public Cursor getPesquisarClienteActivityPedidos(String s) {
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor c = db.query("estudo " +
                            "INNER JOIN clientes ON " +
                            "CASE WHEN (estudo._id_client_remote is null OR clientes._id_client_remote is null) THEN " +
                            "estudo._id_cliente = clientes._id_cliente ELSE " +
                            "estudo._id_client_remote = clientes._id_client_remote END ",
                    new String[]{"estudo._id_estudo, estudo.descricao, clientes.nome as cliente, estudo.data, estudo.data_pedido, estudo.psm, estudo.pcm, estudo._id_estudo_remote"}, "nome like ? AND estudo.enable = ? AND (estudo.psm = ? OR estudo.pcm = ?)", new String[]{"%" + s + "%", "1", "1", "1"}, null, null, "nome", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                return c;
            } else {
                return null;
            }
        } finally {
            db.close();
        }
    }
}
