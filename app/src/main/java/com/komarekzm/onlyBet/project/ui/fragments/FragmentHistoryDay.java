package com.komarekzm.onlyBet.project.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.komarekzm.onlyBet.project.R;
import com.komarekzm.onlyBet.project.adapters.HistoryAdapter;
import com.komarekzm.onlyBet.project.adapters.PagerAdapter;
import com.komarekzm.onlyBet.project.models.objects.History;
import com.komarekzm.onlyBet.project.models.objects.Tip;
import com.komarekzm.onlyBet.project.tasks.tasksHistory.ReadFromHistoryDatabase;
import com.komarekzm.onlyBet.project.ui.activities.Main2Activity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class FragmentHistoryDay extends Fragment {
    private static final String LIST_DATA = "LIST_DATA_HISTORY";

    private ArrayList<History> dateList;
    private FragmentHistoryDay.FragmentItemClickCallback callback;
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;

    public FragmentHistoryDay() {
    }

    public static FragmentHistoryDay newInstance(ArrayList<History> listData) {
        FragmentHistoryDay fragment = new FragmentHistoryDay();
        Bundle args = new Bundle();
        args.putParcelableArrayList(LIST_DATA, listData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ArrayList<History> historyList = getArguments().getParcelableArrayList(LIST_DATA);
            this.dateList = sortHistoryDayList(historyList);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history_list, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.list_history);
        itemListen();
        return v;
    }

    private void itemListen() {
        historyAdapter = new HistoryAdapter(dateList, getActivity());
        historyAdapter.setItemClickCallback(new HistoryAdapter.ItemClickCallback() {
            @Override
            public void onItemClick(View p) {
                if (Main2Activity.loading == true) return;
                TextView textView = (TextView) p.findViewById(R.id.text_history_date);
                final String date = textView.getText().toString();
                ReadFromHistoryDatabase reader = new ReadFromHistoryDatabase(getContext());
                reader.setQueryCompleteListener(new ReadFromHistoryDatabase.OnQueryComplete() {
                    @Override
                    public void setQueryComplete(ArrayList result) {
                        ArrayList<Tip> list = new ArrayList<>();
                        for (Tip tip : (ArrayList<Tip>) result) {
                            if (tip.getDate().equals(date)) {
                                list.add(tip);
                            }
                        }
                        final FragmentManager fm = getFragmentManager();
                        final FragmentTransaction transaction = fm.beginTransaction();
                        Fragment fragment = FragmentHistoryDetail.newInstance(list);
                        transaction.replace(R.id.cont_history_list_list, fragment, "detail");
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });
                reader.execute();
            }

        });
    }

    public ArrayList<History> sortHistoryDayList(ArrayList<History> historyList) {
        ArrayList<String> listDate = new ArrayList<>();
        for (int i = 0; i < historyList.size(); i++) {
            listDate.add(historyList.get(i).getDate());
        }

        Collections.sort(listDate, new Comparator<String>() {
            DateFormat f = new SimpleDateFormat("dd.MM.yyyy");

            @Override
            public int compare(String o1, String o2) {
                try {
                    return f.parse(o2).compareTo(f.parse(o1));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
        historyList.clear();
        for (String st : listDate) {
            historyList.add(new History(st));
        }
        return historyList;
    }

    private void activityCreated() {
        recyclerView.setAdapter(historyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activityCreated();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentHistoryDay.FragmentItemClickCallback) {
            callback = (FragmentHistoryDay.FragmentItemClickCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }


    public interface FragmentItemClickCallback {
        void onListItemSwiped(int position);

        void onListItemClicked(int position);
    }

}