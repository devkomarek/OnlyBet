package com.komarekzm.onlyBet.project.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.komarekzm.onlyBet.project.R;
import com.komarekzm.onlyBet.project.models.objects.History;
import com.komarekzm.onlyBet.project.models.objects.Tip;
import com.komarekzm.onlyBet.project.ui.fragments.FragmentHistoryMonth;
import com.komarekzm.onlyBet.project.ui.fragments.FragmentTipsList;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class PagerAdapter extends FragmentPagerAdapter {
    private final SparseArray<WeakReference<Fragment>> instantiatedFragments = new SparseArray<>();
    private Context context;
    private String[] tabHeader = new String[]{"Today", "History"};
    private ArrayList<Tip> tipList;
    private ArrayList<History> historyData;
    private Fragment mCurrentFragment;

    public PagerAdapter(FragmentManager fm, Context context, ArrayList<Tip> tipList, ArrayList<History> historyData) {
        super(fm);
        this.context = context;
        this.tipList = tipList;
        this.historyData = historyData;
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }


    @Override
    public int getItemPosition(Object object) {
        if (object instanceof UpdatableFragment) {
            ((UpdatableFragment) object).update();
        }
        //don't return POSITION_NONE, avoid fragment recreation.
        return super.getItemPosition(object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            mCurrentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return FragmentTipsList.newInstance(tipList);
            case 1:
                return FragmentHistoryMonth.newInstance(historyData);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabHeader.length;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final Fragment fragment = (Fragment) super.instantiateItem(container, position);
        instantiatedFragments.put(position, new WeakReference<>(fragment));
        return fragment;
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        instantiatedFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @Nullable
    public Fragment getFragment(final int position) {
        final WeakReference<Fragment> wr = instantiatedFragments.get(position);
        if (wr != null) {
            return wr.get();
        } else {
            return null;
        }
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabHeader[position];
    }

    public View getTabView(int position) {
        View tab = LayoutInflater.from(context).inflate(R.layout.tab_custom, null);
        TextView tv = (TextView) tab.findViewById(R.id.custom_text);
        tv.setText(tabHeader[position]);
        return tab;
    }

    public interface UpdatableFragment {
        void update();
    }

}
