package com.komarekzm.onlyBet.project.tasks.tasksTip;

import android.content.Context;
import android.os.AsyncTask;

import com.komarekzm.onlyBet.project.models.database.TipsDatabase;
import com.komarekzm.onlyBet.project.models.objects.Tip;


public class DeleteFromTipDatabase extends AsyncTask<Void, Void, Long> {
    private Tip tip;
    private Context context;

    private OnDeleteComplete onDeleteComplete;

    public interface OnDeleteComplete {
        void setQueryComplete(Long result);
    }

    public DeleteFromTipDatabase(Context context, Tip tip) {
        this.tip = tip;
        this.context = context;
    }

    public DeleteFromTipDatabase(Context context) {
        this.context = context;
        this.tip = null;
    }

    public void setDeleteCompleteListener(OnDeleteComplete onDeleteComplete) {
        this.onDeleteComplete = onDeleteComplete;
    }

    @Override
    protected Long doInBackground(Void... params) {
        TipsDatabase database = TipsDatabase.getInstance(context);
        if (tip != null)
            return database.deleteTip(tip);
        else
            return database.deleteAllData();
    }

    //Value returned from doInBackground is passed to onPostExecute
    @Override
    protected void onPostExecute(Long result) {
        onDeleteComplete.setQueryComplete(result);
    }
}
