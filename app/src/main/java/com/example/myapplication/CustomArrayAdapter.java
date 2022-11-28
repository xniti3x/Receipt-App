package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<Transaction> {

    private List<Transaction> objects;
    private Context context;
    private Filter filter;

    public CustomArrayAdapter(Context context, int resourceId, List<Transaction> objects) {
        super(context, resourceId, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Transaction getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
// convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        Transaction currentNumberPosition = getItem(position);

        // then according to the position of the view assign the desired TextView 1 for the same
        TextView title = currentItemView.findViewById(R.id.tran_title);
        title.setText(currentNumberPosition.toString());

        TextView desc = currentItemView.findViewById(R.id.tran_desc);
        desc.setText(currentNumberPosition.getBookingDate()+" - "+currentNumberPosition.getRemittanceInformationStructured());

        // then return the recyclable view
        return currentItemView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new AppFilter<Transaction>(objects);
        return filter;
    }

    private class AppFilter<Transaction> extends Filter {

        private ArrayList<Transaction> sourceObjects;

        public AppFilter(List<Transaction> objects) {
            sourceObjects = new ArrayList<Transaction>();
            synchronized (this) {
                sourceObjects.addAll(objects);
            }
        }

        @Override
        protected FilterResults performFiltering(CharSequence chars) {
            java.lang.String filterSeq = chars.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (filterSeq != null && filterSeq.length() > 0) {
                ArrayList<Transaction> filter = new ArrayList<Transaction>();

                for (Transaction transaction : sourceObjects) {
                    // the filtering itself:
                    if (transaction.toString().toLowerCase().contains(filterSeq))
                        filter.add(transaction);
                }
                result.count = filter.size();
                result.values = filter;
            } else {
                // add all objects
                synchronized (this) {
                    result.values = sourceObjects;
                    result.count = sourceObjects.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            // NOTE: this function is *always* called from the UI thread.
            ArrayList<Transaction> filtered = (ArrayList<Transaction>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = filtered.size(); i < l; i++){

                //add( filtered.get(i) );
                add( (com.example.myapplication.Transaction) filtered.get(i));
            }
            notifyDataSetInvalidated();
        }
    }

}