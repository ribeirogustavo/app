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
 * Created by Gustavo on 21/10/2016.
 */

public class OrcamentosAdapter extends android.support.v7.widget.RecyclerView.Adapter<OrcamentosAdapter.OrcamentosViewHolder> {
    private final Context context;
    private Cursor c;
    private OrcamentosAdapter.OrcamentosOnClickListener orcamentosOnClickListener;
    private String FormatNumber;

    public OrcamentosAdapter(Context context, Cursor c, OrcamentosAdapter.OrcamentosOnClickListener orcamentosOnClickListener) {
        this.context = context;
        this.c = c;
        this.orcamentosOnClickListener = orcamentosOnClickListener;
    }

    @Override
    public OrcamentosAdapter.OrcamentosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_orcamentos, parent, false);
        OrcamentosAdapter.OrcamentosViewHolder holder = new OrcamentosAdapter.OrcamentosViewHolder(view);
        return holder;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onBindViewHolder(final OrcamentosAdapter.OrcamentosViewHolder holder, int position) {

        String orcamento = null;
        String cliente = null;
        String data = null;
        String id_remote;

        //Atualiza a View
        c.moveToPosition(position);

        cliente = c.getString(c.getColumnIndex("cliente"));
        data = c.getString(c.getColumnIndex("data"));
        id_remote = c.getString(c.getColumnIndex("id_remote"));

        if (id_remote == null) {
            holder.orcamento.setText(context.getResources().getString(R.string.numpendente));
        } else {
            FormatNumber = data.replace("/", "");
            char num[] = FormatNumber.toCharArray();
            holder.orcamento.setText(""+ num[6] + num[7] + num[2] + num[3] + num[0] + num[1] + id_remote);
        }

        holder.cliente.setText(cliente);
        holder.data.setText(data);

        //Click
        if (orcamentosOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    c.moveToPosition(pos);
                    //A variavel position é final
                    orcamentosOnClickListener.onClickOrcamento(holder.itemView, c.getLong(c.getColumnIndex("_id_orcamento")));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return c.getCount();
    }

    public interface OrcamentosOnClickListener {
        void onClickOrcamento(View view, Long idx);
    }

    //ViewHolder com as views
    public static class OrcamentosViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {

        public TextView orcamento;
        public TextView cliente;
        public TextView data;
        CardView carView;


        public OrcamentosViewHolder(View view) {
            super(view);
            //Cria as views para salvar no ViewHolder

            orcamento = (TextView) view.findViewById(R.id.orcamento);
            cliente = (TextView) view.findViewById(R.id.cliente_orcamento);
            data = (TextView) view.findViewById(R.id.data_orcamento);
            carView = (CardView) view.findViewById(R.id.card_view);
        }
    }
}
