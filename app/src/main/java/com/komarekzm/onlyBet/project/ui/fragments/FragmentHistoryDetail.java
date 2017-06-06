package com.komarekzm.onlyBet.project.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.komarekzm.onlyBet.project.R;
import com.komarekzm.onlyBet.project.adapters.TipAdapter;
import com.komarekzm.onlyBet.project.models.objects.Tip;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.komarekzm.onlyBet.project.ui.activities.Main2Activity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FragmentHistoryDetail extends Fragment {
    private static final String LIST_DATA = "LIST_DATA_DETAIL";

    private ArrayList<Tip> listData;
    private RecyclerView tipList;
    private Button button;
    private AdView mAdView;
    private TextView textView;

    public FragmentHistoryDetail() {
    }

    public static FragmentHistoryDetail newInstance(ArrayList<Tip> listData) {
        FragmentHistoryDetail fragment = new FragmentHistoryDetail();
        Bundle args = new Bundle();
        args.putParcelableArrayList(LIST_DATA, listData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.listData = getArguments().getParcelableArrayList(LIST_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history_detail, container, false);
        tipList = (RecyclerView) v.findViewById(R.id.list_history_detail);
        return v;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        TipAdapter adapter = new TipAdapter(listData, getActivity());
        Collections.sort(listData, new Comparator<Tip>() {
            DateFormat f = new SimpleDateFormat("hh:mm");

            @Override
            public int compare(Tip o2, Tip o1) {
                try {
                    return f.parse(o2.getTime()).compareTo(f.parse(o1.getTime()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
        tipList.setAdapter(adapter);
        tipList.setLayoutManager(new LinearLayoutManager(getActivity()));
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
