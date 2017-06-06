package com.komarekzm.onlyBet.project.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.komarekzm.onlyBet.project.R;
import com.komarekzm.onlyBet.project.models.objects.History;

import java.util.List;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.CustomViewHolder> {
    private LayoutInflater inflater;
    private List<History> listData;
    private HistoryAdapter.ItemClickCallback itemClickCallback;

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView historyDate;

        CustomViewHolder(View itemView) {
            super(itemView);
            historyDate = (TextView) itemView.findViewById(R.id.text_history_date);
        }

        @Override
        public void onClick(View v) {
        }
    }

    public HistoryAdapter(List<History> listData, Context c) {
        inflater = LayoutInflater.from(c);
        this.listData = listData;
    }

    @Override
    public HistoryAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.history_card, parent, false);
        return new HistoryAdapter.CustomViewHolder(view);
    }

    public interface ItemClickCallback {
        void onItemClick(View v);
    }

    public void setItemClickCallback(final HistoryAdapter.ItemClickCallback itemClickCallback) {
        this.itemClickCallback = itemClickCallback;
    }

    @Override
    public void onBindViewHolder(HistoryAdapter.CustomViewHolder holder, int position) {
        History history = listData.get(position);
        holder.historyDate.setText(history.getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickCallback.onItemClick(v);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}


