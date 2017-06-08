package com.komarekzm.onlyBet.project.models;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.komarekzm.onlyBet.project.models.objects.History;
import com.komarekzm.onlyBet.project.models.objects.ListOfHistoryAndTips;
import com.komarekzm.onlyBet.project.models.objects.Tip;
import com.komarekzm.onlyBet.project.tasks.tasksHistory.DeleteFromHistoryDatabase;
import com.komarekzm.onlyBet.project.tasks.tasksHistory.ReadFromHistoryDatabase;
import com.komarekzm.onlyBet.project.tasks.tasksHistory.WriteToHistoryDatabase;
import com.komarekzm.onlyBet.project.tasks.tasksTip.DeleteFromTipDatabase;
import com.komarekzm.onlyBet.project.tasks.tasksTip.ReadFromTipDatabase;
import com.komarekzm.onlyBet.project.tasks.tasksTip.WriteToTipDatabase;
import com.komarekzm.onlyBet.project.ui.activities.Main2Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by komarekzm on 6/8/2017.
 */

public final class Cache {
    private static ListOfHistoryAndTips listOfHistoryAndTips;

    private Cache() {
    }

    public static ListOfHistoryAndTips getListOfHistoryAndTips(final Context context) throws ExecutionException, InterruptedException {
        if (listOfHistoryAndTips == null || listOfHistoryAndTips.getListHistory().size() == 0 || listOfHistoryAndTips.getListTip().size() == 0) {
            loadListOfHistoryAndTipsFromDataBase(context);
            return listOfHistoryAndTips;
        } else {
            return listOfHistoryAndTips;
        }
    }

    public static List<Tip> getListOfHistory(Context context) throws ExecutionException, InterruptedException {
        if (listOfHistoryAndTips == null || listOfHistoryAndTips.getListHistory().size() == 0) {
            loadListOfHistoryFromDataBase(context);
            return listOfHistoryAndTips.getListHistory();
        } else {
            return listOfHistoryAndTips.getListHistory();
        }
    }

    public static List<Tip> getListOfTips(Context context) throws ExecutionException, InterruptedException {
        if (listOfHistoryAndTips == null || listOfHistoryAndTips.getListTip().size() == 0) {
            loadListOfTipFromDatabase(context);
            return listOfHistoryAndTips.getListTip();
        } else {
            return listOfHistoryAndTips.getListTip();
        }
    }

    private static void loadListOfHistoryAndTipsFromDataBase(Context context) throws ExecutionException, InterruptedException {
        loadListOfHistoryFromDataBase(context);
        loadListOfTipFromDatabase(context);
    }

    private static void loadListOfHistoryFromDataBase(Context context) throws ExecutionException, InterruptedException {
        ReadFromHistoryDatabase reader = new ReadFromHistoryDatabase(context);
        reader.setQueryCompleteListener(new ReadFromHistoryDatabase.OnQueryComplete() {
            @Override
            public void setQueryComplete(ArrayList historyResult) {
                listOfHistoryAndTips.setListHistory(historyResult);
            }
        });

        reader.execute().get();
    }

    private static void loadListOfTipFromDatabase(Context context) throws ExecutionException, InterruptedException {
        ReadFromTipDatabase reader = new ReadFromTipDatabase(context);
        reader.setQueryCompleteListener(new ReadFromTipDatabase.OnQueryComplete() {
            @Override
            public void setQueryComplete(final ArrayList tipResult) {
                listOfHistoryAndTips.setListTip(tipResult);
            }
        });
        reader.execute().get();
    }


    public static void setListOfHistoryAndTips(final ListOfHistoryAndTips listOfHistoryAndTips, final Context context) {
        Cache.listOfHistoryAndTips = listOfHistoryAndTips;
        setHistoryToDataBase(listOfHistoryAndTips.getListHistory(), context);
        setTipToDataBase(listOfHistoryAndTips.getListTip(), context);
    }

    private static void setHistoryToDataBase(final List<Tip> historyList, final Context context) {
        final DeleteFromHistoryDatabase delete = new DeleteFromHistoryDatabase(context);
        delete.setDeleteCompleteListener(new DeleteFromHistoryDatabase.OnDeleteComplete() {
            @Override
            public void setQueryComplete(Long res) {
                WriteToHistoryDatabase writer = new WriteToHistoryDatabase(context, historyList);
                writer.setWriteCompleteListener(new WriteToHistoryDatabase.OnWriteComplete() {
                    @Override
                    public void setWriteComplete(long result) {
                    }
                });
                writer.execute();
            }

        });
        delete.execute();
    }

    private static void setTipToDataBase(final List<Tip> tipList, final Context context) {
        DeleteFromTipDatabase delete = new DeleteFromTipDatabase(context);
        delete.setDeleteCompleteListener(new DeleteFromTipDatabase.OnDeleteComplete() {
            @Override
            public void setQueryComplete(Long res) {
                WriteToTipDatabase writer = new WriteToTipDatabase(context, tipList);
                writer.setWriteCompleteListener(new WriteToTipDatabase.OnWriteComplete() {
                    @Override
                    public void setWriteComplete(long result) {
                    }
                });
                writer.execute();
            }

        });
        delete.execute();
    }

}
