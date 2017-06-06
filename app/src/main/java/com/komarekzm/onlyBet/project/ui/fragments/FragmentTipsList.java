package com.komarekzm.onlyBet.project.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.komarekzm.onlyBet.project.R;
import com.komarekzm.onlyBet.project.adapters.PagerAdapter;
import com.komarekzm.onlyBet.project.adapters.TipAdapter;
import com.komarekzm.onlyBet.project.models.objects.Tip;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.komarekzm.onlyBet.project.tasks.tasksTip.ReadFromTipDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FragmentTipsList extends Fragment implements PagerAdapter.UpdatableFragment {
    private static final String LIST_DATA = "LIST_DATA";

    private ArrayList<Tip> listData;
    private FragmentItemClickCallback callback;
    private RecyclerView recyclerView;
    private AdView mAdView;

    public FragmentTipsList() {
    }

    public static FragmentTipsList newInstance(ArrayList<Tip> listData) {
        FragmentTipsList fragment = new FragmentTipsList();
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
        View v = inflater.inflate(R.layout.fragment_tips_list, container, false);
        mAdView = (AdView) v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int error) {
                mAdView.setVisibility(View.GONE);
            }

        });
        recyclerView = (RecyclerView) v.findViewById(R.id.list_tips);
        return v;
    }

    @Override
    public void onResume() {
        mAdView.resume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mAdView.pause();
        super.onPause();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        activityCreated();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentItemClickCallback) {
            callback = (FragmentItemClickCallback) context;
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
        ReadFromTipDatabase reader = new ReadFromTipDatabase(getContext());
        reader.setQueryCompleteListener(new ReadFromTipDatabase.OnQueryComplete() {
            @Override
            public void setQueryComplete(ArrayList res) {
                listData = res;
                activityCreated();
            }
        });
        reader.execute();

    }

    private void activityCreated() {
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
        recyclerView.setAdapter(adapter);
        adapter.setItemClickCallback(new TipAdapter.ItemClickCallback() {
            @Override
            public void onItemClick(int p) {
                callback.onListItemClicked(p);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public interface FragmentItemClickCallback {
        void onListItemSwiped(int position);

        void onListItemClicked(int position);
    }

}
