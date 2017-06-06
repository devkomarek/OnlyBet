package com.komarekzm.onlyBet.project.tasks.tasksHistory;

import android.content.Context;
import android.os.AsyncTask;

import com.komarekzm.onlyBet.project.models.database.HistoryDatabase;
import com.komarekzm.onlyBet.project.models.objects.Tip;

import java.util.List;

public class WriteToHistoryDatabase extends AsyncTask<Void, Void, Long> {
    private List<Tip> tipList;
    private Tip tip;
    private Context context;

    private OnWriteComplete onWriteComplete;

    public interface OnWriteComplete {
        void setWriteComplete(long result);
    }

    public void setWriteCompleteListener(OnWriteComplete onWriteComplete) {
        this.onWriteComplete = onWriteComplete;
    }

    public WriteToHistoryDatabase(Context context, Tip tip) {
        this.tipList = null;
        this.tip = tip;
        this.context = context;
    }

    public WriteToHistoryDatabase(Context context, List<Tip> tipList) {
        this.tip = null;
        this.tipList = tipList;
        this.context = context;
    }

    @Override
    protected Long doInBackground(Void... params) {
        HistoryDatabase database = HistoryDatabase.getInstance(context);
        if (tipList == null && tip != null)
            return database.insertData(tip);
        else
            return database.insertAllData(tipList);
    }

    @Override
    protected void onPostExecute(Long param) {
        onWriteComplete.setWriteComplete(param);
    }
}

