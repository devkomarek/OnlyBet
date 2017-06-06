package com.komarekzm.onlyBet.project.tasks.tasksTip;

import android.content.Context;
import android.os.AsyncTask;

import com.komarekzm.onlyBet.project.R;
import com.komarekzm.onlyBet.project.models.database.TipsDatabase;
import com.komarekzm.onlyBet.project.models.objects.ListOfTips;
import com.komarekzm.onlyBet.project.models.objects.Tip;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class LoadInitialTipsToTipDatabase extends AsyncTask<Void, Void, Void> {
    private Context context;
    private OnDatabaseBuilt onDatabaseBuilt;

    public interface OnDatabaseBuilt {
        void buildComplete();
    }

    public LoadInitialTipsToTipDatabase(Context context) {
        this.context = context;
    }

    public void setOnDatabaseBuilt(OnDatabaseBuilt onDatabaseBuilt) {
        this.onDatabaseBuilt = onDatabaseBuilt;
    }

    @Override
    protected Void doInBackground(Void... params) {
        TipsDatabase database = TipsDatabase.getInstance(context);

        InputStream raw = context.getResources().openRawResource(R.raw.tips);
        Reader reader = new BufferedReader(new InputStreamReader(raw));

        ListOfTips listOfTips = new Gson().fromJson(reader, ListOfTips.class);
        List<Tip> tipsList = listOfTips.getTodoArrayList();

        for (Tip item : tipsList) {
            database.insertData(item);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void param) {
        onDatabaseBuilt.buildComplete();
    }
}
