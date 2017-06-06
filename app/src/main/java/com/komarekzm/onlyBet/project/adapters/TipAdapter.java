package com.komarekzm.onlyBet.project.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.komarekzm.onlyBet.project.R;
import com.komarekzm.onlyBet.project.models.objects.Tip;

import java.util.List;

public class TipAdapter extends RecyclerView.Adapter<TipAdapter.CustomViewHolder> {
    private LayoutInflater inflater;
    private List<Tip> listData;
    private ItemClickCallback itemClickCallback;

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tipName;
        TextView tipWin;
        TextView tipCourse;
        TextView tipTime;
        TextView tipLeague;

        CustomViewHolder(View itemView) {
            super(itemView);
            tipName = (TextView) itemView.findViewById(R.id.textName);
            tipWin = (TextView) itemView.findViewById(R.id.textWin);
            tipCourse = (TextView) itemView.findViewById(R.id.textCourse);
            tipTime = (TextView) itemView.findViewById(R.id.textTime);
            tipLeague = (TextView) itemView.findViewById(R.id.textLeague);
        }

        @Override
        public void onClick(View v) {
            itemClickCallback.onItemClick(getAdapterPosition());
        }
    }

    public TipAdapter(List<Tip> listData, Context c) {
        inflater = LayoutInflater.from(c);
        this.listData = listData;
    }

    @Override
    public TipAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.tip_card, parent, false);
        return new CustomViewHolder(view);
    }

    public interface ItemClickCallback {
        void onItemClick(int p);
    }

    public void setItemClickCallback(final ItemClickCallback itemClickCallback) {
        this.itemClickCallback = itemClickCallback;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Tip tip = listData.get(position);
        holder.tipName.setText(tip.getName());
        holder.tipCourse.setText(tip.getCourse());
        holder.tipTime.setText(tip.getTime());
        holder.tipLeague.setText(tip.getLeague());
        setBackgroundColorInTipCourse(tip.getWin(), holder.tipCourse);
        setTextInTipWin(tip.getWin(), holder.tipWin);
    }

    private void setTextInTipWin(String win, TextView tipCourse) {
        char mark = Character.toUpperCase(win.charAt(win.length() - 1));
        if (mark == 'R' || mark == 'G' || mark == 'B')
            win = win.substring(0, win.length() - 1);
        tipCourse.setText(win);
    }

    private void setBackgroundColorInTipCourse(String win, TextView textView) {
        char mark = Character.toUpperCase(win.charAt(win.length() - 1));
        if (mark == 'R')
            textView.setBackgroundColor(Color.parseColor("#F7484E"));
        if (mark == 'B')
            textView.setBackgroundColor(Color.parseColor("#33B5E5"));
        if (mark != 'R' && mark != 'B')
            textView.setBackgroundColor(Color.parseColor("#5CBB31"));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


}

