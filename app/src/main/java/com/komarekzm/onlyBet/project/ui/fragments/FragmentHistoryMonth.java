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

public class FragmentHistoryMonth extends Fragment implements PagerAdapter.UpdatableFragment {
    private static final String LIST_DATA = "LIST_DATA_HISTORY";

    private ArrayList<History> dateList;
    private FragmentHistoryMonth.FragmentItemClickCallback callback;
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;

    public FragmentHistoryMonth() {
    }

    public static FragmentHistoryMonth newInstance(ArrayList<History> listData) {
        FragmentHistoryMonth fragment = new FragmentHistoryMonth();
        Bundle args = new Bundle();
        args.putParcelableArrayList(LIST_DATA, listData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ArrayList<Tip> historyList = getArguments().getParcelableArrayList(LIST_DATA);
            this.dateList = convertHistoryListToDateList(historyList);
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
                        ArrayList<History> list = new ArrayList<>();
                        Set<String> hs = new HashSet<>();
                        for (Tip tip : (ArrayList<Tip>) result) {
                            String[] tempSplitDate = tip.getDate().split("\\.");
                            String tempMonthAndYearDate = tempSplitDate[1] + "." + tempSplitDate[2];
                            if (tempMonthAndYearDate.equals(date)) {
                                hs.add(tip.getDate());
                            }
                        }

                        for (String st : hs) {
                            list.add(new History(st));
                        }

                        final FragmentManager fm = getFragmentManager();
                        final FragmentTransaction transaction = fm.beginTransaction();
                        Fragment fragment = FragmentHistoryDay.newInstance(list);
                        transaction.add(R.id.cont_history_list_list, fragment, "day");
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });
                reader.execute();
            }

        });
    }

    public ArrayList<History> convertHistoryListToDateList(ArrayList<Tip> historyList) {
        ArrayList<History> listHistory = new ArrayList<>();
        ArrayList<String> listDay = new ArrayList<>();
        ArrayList<String> listMonth = new ArrayList<>();
        for (int i = 0; i < historyList.size(); i++) {
            Tip tip = historyList.get(i);
            listDay.add(tip.getDate());
        }
        Set<String> hs = new HashSet<>();
        hs.addAll(listDay);
        listDay.clear();
        listDay.addAll(hs);

        for (String st : listDay) {
            String[] temp = st.split("\\.");
            String monthAndYear = temp[1] + "." + temp[2];
            if (!listMonth.contains(monthAndYear))
                listMonth.add(monthAndYear);
        }

        Collections.sort(listMonth, new Comparator<String>() {
            DateFormat f = new SimpleDateFormat("MM.yyyy");

            @Override
            public int compare(String o1, String o2) {
                try {
                    return f.parse(o2).compareTo(f.parse(o1));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
        for (String st : listMonth) {
            listHistory.add(new History(st));
        }
        return listHistory;
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
        if (context instanceof FragmentHistoryMonth.FragmentItemClickCallback) {
            callback = (FragmentHistoryMonth.FragmentItemClickCallback) context;
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

    @Override
    public void update() {
        Log.i("sprawa", "onCreateView: hello");
        ReadFromHistoryDatabase reader = new ReadFromHistoryDatabase(getContext());
        reader.setQueryCompleteListener(new ReadFromHistoryDatabase.OnQueryComplete() {
            @Override
            public void setQueryComplete(ArrayList result) {
                dateList = convertHistoryListToDateList(result);
                itemListen();
                activityCreated();
            }
        });
        reader.execute();
    }


    public interface FragmentItemClickCallback {
        void onListItemSwiped(int position);

        void onListItemClicked(int position);
    }

}