package com.komarekzm.onlyBet.project.tasks.tasksHistory;

import android.content.Context;
import android.os.AsyncTask;

import com.komarekzm.onlyBet.project.models.database.HistoryDatabase;

import java.util.ArrayList;

public class ReadFromHistoryDatabase extends AsyncTask<Void, Void, ArrayList> {
    private Context context;

    private OnQueryComplete onQueryComplete;

    public interface OnQueryComplete {
        void setQueryComplete(ArrayList result);
    }

    public void setQueryCompleteListener(OnQueryComplete onQueryComplete) {
        this.onQueryComplete = onQueryComplete;
    }

    public ReadFromHistoryDatabase(Context context) {
        this.context = context;
    }

    @Override
    protected ArrayList doInBackground(Void... params) {
        HistoryDatabase database = HistoryDatabase.getInstance(context);
        return database.getAllData();
    }

    @Override
    protected void onPostExecute(ArrayList result) {
        onQueryComplete.setQueryComplete(result);
    }
}

