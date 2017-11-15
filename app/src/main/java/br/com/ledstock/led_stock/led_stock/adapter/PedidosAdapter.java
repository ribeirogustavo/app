package br.com.ledstock.led_stock.led_stock.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.ledstock.led_stock.R;

/**
 * Created by Gustavo on 25/11/2016.
 */

public class PedidosAdapter extends android.support.v7.widget.RecyclerView.Adapter<PedidosAdapter.PedidosViewHolder> {
    private final Context context;
    private Cursor c;
    private PedidosAdapter.PedidosOnClickListener pedidosOnClickListener;

    public PedidosAdapter(Context context, Cursor c, PedidosAdapter.PedidosOnClickListener pedidosOnClickListener) {
        this.context = context;
        this.c = c;
        this.pedidosOnClickListener = pedidosOnClickListener;
    }

    @Override
    public PedidosAdapter.PedidosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_pedidos, parent, false);
        PedidosAdapter.PedidosViewHolder holder = new PedidosAdapter.PedidosViewHolder(view);
        return holder;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onBindViewHolder(final PedidosAdapter.PedidosViewHolder holder, int position) {

        String id_remote;
        String cliente;
        String data;
        String psm;
        String pcm;
        String FormatNumber;
        final int table;

        //Atualiza a View
        c.moveToPosition(position);

        table = c.getInt(c.getColumnIndex("table"));

        id_remote = c.getString(c.getColumnIndex("id_remote"));
        cliente = c.getString(c.getColumnIndex("cliente"));
        data = c.getString(c.getColumnIndex("data_pedido"));
        psm = c.getString(c.getColumnIndex("psm"));
        pcm = c.getString(c.getColumnIndex("pcm"));

        if (id_remote == null) {
            holder.numPedido.setText(context.getResources().getString(R.string.pedido_pendente));
        } else {
            FormatNumber = data.replace("/", "");
            char num[] = FormatNumber.toCharArray();
            String concat;
            if (table == 1){
                concat = "#" + "1" + num[6] + num[7] + num[3] + num[2] + num[0] + num[1] + id_remote;
            }else {
                concat = "#" + "2" + num[6] + num[7] + num[3] + num[2] + num[0] + num[1] + id_remote;
            }
            holder.numPedido.setText(concat);
        }
        holder.cliente.setText(cliente);
        if (psm.equals("1")) {
            holder.mdo.setText(context.getResources().getString(R.string.psm));
        } else if (pcm.equals("1")) {
            holder.mdo.setText(context.getResources().getString(R.string.pcm));
        }
        holder.data_pedido.setText(data);

        //Click
        if (pedidosOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    c.moveToPosition(pos);
                    //A variavel position é final
                    pedidosOnClickListener.onClickPedido(holder.itemView, c.getLong(c.getColumnIndex("_id_table")), table);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return c.getCount();
    }

    public interface PedidosOnClickListener {
        void onClickPedido(View view, Long idx, int table);
    }

    //ViewHolder com as views
    public static class PedidosViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {

        public TextView numPedido;
        public TextView cliente;
        public TextView mdo;
        public TextView data_pedido;
        CardView carView;


        public PedidosViewHolder(View view) {
            super(view);
            //Cria as views para salvar no ViewHolder

            numPedido = (TextView) view.findViewById(R.id.numPedido);
            cliente = (TextView) view.findViewById(R.id.cliente_estudo);
            mdo = (TextView) view.findViewById(R.id.mdo);
            data_pedido = (TextView) view.findViewById(R.id.data_pedido);
            carView = (CardView) view.findViewById(R.id.card_view);
        }
    }
}
