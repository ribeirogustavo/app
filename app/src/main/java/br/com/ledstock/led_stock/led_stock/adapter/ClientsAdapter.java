package br.com.ledstock.led_stock.led_stock.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.ledstock.led_stock.R;

/**
 * Created by Gustavo on 16/08/2016.
 */

public class ClientsAdapter extends android.support.v7.widget.RecyclerView.Adapter<ClientsAdapter.ClientesViewHolder> {
    private final Context context;
    private Cursor c;
    private ClienteOnClickListener clienteOnClickListener;

    public ClientsAdapter(Context context, Cursor c, ClienteOnClickListener clientsOnClickListener) {
        this.context = context;
        this.c = c;
        this.clienteOnClickListener = clientsOnClickListener;
    }

    public ClientesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_cliente, viewGroup, false);
        ClientesViewHolder holder = new ClientesViewHolder(view);
        return holder;
    }

    /*
    @Override
    public void onBindViewHolder(ClientesViewHolder holder, int position) {
    }*/

    @TargetApi(Build.VERSION_CODES.M)
    public void onBindViewHolder(final ClientesViewHolder holder, int position) {
        String nome = null;
        String email = null;
        String contato = null;
        String tel = null;

        //position = holder.getAdapterPosition();
        //Atualiza a View
        c.moveToPosition(position);

        nome = c.getString(c.getColumnIndex("nome"));
        email = c.getString(c.getColumnIndex("email"));
        contato = c.getString(c.getColumnIndex("contato"));
        tel = c.getString(c.getColumnIndex("tel"));

        holder.nome.setText(nome);
        holder.email.setText(email);
        holder.contato.setText(contato);
        holder.tel.setText(tel);

        //Click
        if (clienteOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    c.moveToPosition(pos);
                    //A variavel position é final
                    clienteOnClickListener.onClickCliente(holder.itemView, c.getInt(c.getColumnIndex("_id_cliente")));
                }
            });

            /*
            //Click Longo
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    clienteOnClickListener.onLongClickCarro(holder.itemView, position);
                    return true;
                }
            });*/
        }

    }

    @Override
    public int getItemCount() {
        return c.getCount();
    }

    public interface ClienteOnClickListener {
        void onClickCliente(View view, int idx);
    }

    //ViewHolder com as views
    public static class ClientesViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public TextView nome;
        public TextView contato;
        public TextView email;
        public TextView tel;

        public ClientesViewHolder(View view) {
            super(view);
            //Cria as views para salvar no ViewHolder
            nome = (TextView) view.findViewById(R.id.Nome_Cliente);
            contato = (TextView) view.findViewById(R.id.Contato);
            email = (TextView) view.findViewById(R.id.Email);
            tel = (TextView) view.findViewById(R.id.Telefone);
        }
    }
}

/*
public class ClientsAdapter extends BaseAdapter{

    private final Context context;
    private final Cursor c;

    public ClientsAdapter(Context context, Cursor c) {
        this.context = context;
        this.c = c;
    }

    @Override
    public int getCount() {

        return c.getCount();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        c.moveToPosition(i);
        return c.getLong(c.getColumnIndex("_id_cliente"));
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.adapter_cliente, viewGroup, false);
        TextView nome = (TextView) view.findViewById(R.id.Nome_Cliente);
        TextView email = (TextView) view.findViewById(R.id.Email);
        TextView contato = (TextView) view.findViewById(R.id.Contato);
        TextView telefone = (TextView) view.findViewById(R.id.Telefone);
        c.moveToPosition(i);
        nome.setText(c.getString(c.getColumnIndex("nome")));
        email.setText(c.getString(c.getColumnIndex("email")));
        contato.setText(c.getString(c.getColumnIndex("contato")));
        telefone.setText(c.getString(c.getColumnIndex("tel")));

        return view;
    }
}*/

