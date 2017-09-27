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
 * Created by Gustavo on 28/09/2016.
 */

public class UsersAdapter extends android.support.v7.widget.RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {
    private final Context context;
    private Cursor c;
    private UsersAdapter.UsersOnClickListener usersOnClickListener;

    public UsersAdapter(Context context, Cursor c, UsersAdapter.UsersOnClickListener usersOnClickListener) {
        this.context = context;
        this.c = c;
        this.usersOnClickListener = usersOnClickListener;
    }

    @Override
    public UsersAdapter.UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_users, parent, false);
        UsersAdapter.UsersViewHolder holder = new UsersAdapter.UsersViewHolder(view);
        return holder;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onBindViewHolder(final UsersAdapter.UsersViewHolder holder, int position) {
        String nome = null;

        //Atualiza a View
        c.moveToPosition(position);

        nome = c.getString(c.getColumnIndex("nome"));
        //potencia = c.getString(c.getColumnIndex("potencia")) + "W";

        holder.nome.setText(nome);
        //holder.potencia.setText(potencia);

        //Click
        if (usersOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    c.moveToPosition(pos);
                    //A variavel position é final
                    usersOnClickListener.onClickUser(holder.itemView, c.getInt(c.getColumnIndex("_id_usuario")));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return c.getCount();
    }

    public interface UsersOnClickListener {
        void onClickUser(View view, int idx);
    }

    //ViewHolder com as views
    public static class UsersViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public TextView nome;
        //public TextView potencia;

        public UsersViewHolder(View view) {
            super(view);
            //Cria as views para salvar no ViewHolder
            nome = (TextView) view.findViewById(R.id.usuario);
        }
    }


}
