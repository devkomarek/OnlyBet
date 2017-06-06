package com.komarekzm.onlyBet.project.tasks.tasksTip;

import android.content.Context;
import android.os.AsyncTask;

import com.komarekzm.onlyBet.project.models.database.TipsDatabase;

import java.util.ArrayList;

public class ReadFromTipDatabase extends AsyncTask<Void, Void, ArrayList> {
    private Context context;

    private OnQueryComplete onQueryComplete;

    public interface OnQueryComplete {
        void setQueryComplete(ArrayList result);
    }

    public void setQueryCompleteListener(OnQueryComplete onQueryComplete) {
        this.onQueryComplete = onQueryComplete;
    }

    public ReadFromTipDatabase(Context context) {
        this.context = context;
    }

    @Override
    protected ArrayList doInBackground(Void... params) {
        TipsDatabase database = TipsDatabase.getInstance(context);
        return database.getAllData();
    }

    @Override
    protected void onPostExecute(ArrayList result) {
        onQueryComplete.setQueryComplete(result);
    }
}

