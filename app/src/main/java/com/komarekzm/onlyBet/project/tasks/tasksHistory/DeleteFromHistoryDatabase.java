package com.komarekzm.onlyBet.project.tasks.tasksHistory;

import android.content.Context;
import android.os.AsyncTask;

import com.komarekzm.onlyBet.project.models.database.HistoryDatabase;
import com.komarekzm.onlyBet.project.models.objects.Tip;

public class DeleteFromHistoryDatabase extends AsyncTask<Void, Void, Long> {
    private Tip tip;
    private Context context;

    private OnDeleteComplete onDeleteComplete;

    public interface OnDeleteComplete {
        void setQueryComplete(Long result);
    }

    public DeleteFromHistoryDatabase(Context context, Tip tip) {
        this.tip = tip;
        this.context = context;
    }

    public DeleteFromHistoryDatabase(Context context) {
        this.context = context;
        this.tip = null;
    }

    public void setDeleteCompleteListener(OnDeleteComplete onDeleteComplete) {
        this.onDeleteComplete = onDeleteComplete;
    }

    @Override
    protected Long doInBackground(Void... params) {
        HistoryDatabase database = HistoryDatabase.getInstance(context);
        if (tip != null)
            return database.deleteTip(tip);
        else
            return database.deleteAllData();
    }

    @Override
    protected void onPostExecute(Long result) {
        onDeleteComplete.setQueryComplete(result);
    }
}
