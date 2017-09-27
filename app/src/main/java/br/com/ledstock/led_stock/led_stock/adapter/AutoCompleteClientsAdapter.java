package br.com.ledstock.led_stock.led_stock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.ledstock.led_stock.R;

/**
 * Created by Gustavo on 17/10/2016.
 */

public class AutoCompleteClientsAdapter extends ArrayAdapter<String>{

    private final List<String> clients;
    private List<String> filteredClients = new ArrayList<>();

    public AutoCompleteClientsAdapter(Context context, List<String> clients) {
        super(context, 0, clients);
        this.clients = clients;
    }

    @Override
    public int getCount() {
        return filteredClients.size();
    }

    @Override
    public Filter getFilter() {
        return new ClientsFilter(this, clients);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item from filtered list.
        String client = filteredClients.get(position);

        // Inflate your custom row layout as usual.
        LayoutInflater inflater = LayoutInflater.from(getContext());
        /*
        convertView = inflater.inflate(R.layout.row_dog, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.row_breed);
        ImageView ivIcon = (ImageView) convertView.findViewById(R.id.row_icon);
        tvName.setText(dog.breed);
        ivIcon.setImageResource(dog.drawable);
        */
        convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        TextView Nameclient = (TextView) convertView.findViewById(android.R.id.text1);
        Nameclient.setText(client);

        return convertView;
    }


    class ClientsFilter extends Filter {

        AutoCompleteClientsAdapter adapter;
        List<String> originalList;
        List<String> filteredList;

        public ClientsFilter(AutoCompleteClientsAdapter adapter, List<String> originalList) {
            super();
            this.adapter = adapter;
            this.originalList = originalList;
            this.filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();

            final FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();

                // Your filtering logic goes in here
                for (final String client : originalList) {
                    if (client.toLowerCase().contains(filterPattern)) {
                       filteredList.add(client);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filteredClients.clear();
            adapter.filteredClients.addAll((List) results.values);
            adapter.notifyDataSetChanged();
        }
    }
}

