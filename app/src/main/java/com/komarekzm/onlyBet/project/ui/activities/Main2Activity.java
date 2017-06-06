package com.komarekzm.onlyBet.project.ui.activities;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.komarekzm.onlyBet.project.R;
import com.komarekzm.onlyBet.project.adapters.PagerAdapter;
import com.komarekzm.onlyBet.project.models.objects.ListOfHistoryAndTips;
import com.komarekzm.onlyBet.project.models.objects.Tip;
import com.komarekzm.onlyBet.project.Toasts.Toasts;
import com.komarekzm.onlyBet.project.tasks.tasksHistory.DeleteFromHistoryDatabase;
import com.komarekzm.onlyBet.project.tasks.tasksHistory.LoadInitialTipsToHistoryDatabase;
import com.komarekzm.onlyBet.project.tasks.tasksHistory.ReadFromHistoryDatabase;
import com.komarekzm.onlyBet.project.tasks.tasksHistory.WriteToHistoryDatabase;
import com.komarekzm.onlyBet.project.tasks.tasksTip.DeleteFromTipDatabase;
import com.komarekzm.onlyBet.project.tasks.tasksTip.LoadInitialTipsToTipDatabase;
import com.komarekzm.onlyBet.project.tasks.tasksTip.ReadFromTipDatabase;
import com.komarekzm.onlyBet.project.tasks.tasksTip.WriteToTipDatabase;
import com.komarekzm.onlyBet.project.ui.fragments.FragmentHistoryDay;
import com.komarekzm.onlyBet.project.ui.fragments.FragmentHistoryMonth;
import com.komarekzm.onlyBet.project.ui.fragments.FragmentTipsList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements FragmentTipsList.FragmentItemClickCallback, FragmentHistoryDay.FragmentItemClickCallback, FragmentHistoryMonth.FragmentItemClickCallback {

    private String DB_TIP_PATH = "http://194.135.88.176:8080/bestTips/tips";
    private String DB_HISTORY_PATH = "http://194.135.88.176:8080/bestTips/history";
    private ArrayList listDataFromTipDatabase;
    private ArrayList listDataFromHistoryDatabase;
    private static String version;
    public static Boolean loading = false;
    TabLayout tabLayout;
    PagerAdapter pagerAdapter;
    ViewPager viewPager;
    String myLog = "myLog";

    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    FrameLayout progressBarHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activateDevelopMode();
        setOtherContent();
        getApkVersion();
        configureMobileAds();
        configureFirebase();
        tryReconnect();
    }

    private void setOtherContent() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void configureMobileAds() {
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-7555876579173279~2252647540");
    }

    private void configureFirebase() {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //FirebaseCrash.report(new Exception("My first Android non-fatal error"));
    }

    private void activateDevelopMode() {
        DB_TIP_PATH += "_beta";
        DB_HISTORY_PATH += "_beta";
    }

    private void tryReconnect() {
        ConnectivityManager cm = (ConnectivityManager) Main2Activity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            loadListIfDatabaseExist();
            new JSONTask().execute(DB_TIP_PATH, DB_HISTORY_PATH);
        } else {
            Toasts.displayWhileStart(Main2Activity.this, Toasts.State.noConnections);
            loadListIfDatabaseExist();
        }
    }

    private void rateUs() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.komarekzm.onlyBet.project")));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.komarekzm.onlyBet.project")));
        }
    }

    private void loadListIfDatabaseExist() {
        File tipsDatabase = getApplicationContext().getDatabasePath("tips.db");
        File historyDatabase = getApplicationContext().getDatabasePath("history.db");
        if (!tipsDatabase.exists() && !historyDatabase.exists()) {
            initialDatabases();
        } else if (!tipsDatabase.exists()) {
            initialTipsDatabase();
        } else if (!historyDatabase.exists()) {
            initialHistoryDatabase();
        } else {
            loadList();
        }
    }

    private void initialDatabases() {
        LoadInitialTipsToTipDatabase loader = new LoadInitialTipsToTipDatabase(getApplicationContext());
        loader.setOnDatabaseBuilt(new LoadInitialTipsToTipDatabase.OnDatabaseBuilt() {
            @Override
            public void buildComplete() {
                LoadInitialTipsToHistoryDatabase loader = new LoadInitialTipsToHistoryDatabase(getApplicationContext());
                loader.setOnDatabaseBuilt(new LoadInitialTipsToHistoryDatabase.OnDatabaseBuilt() {
                    @Override
                    public void buildComplete() {
                        loadList();
                    }
                });
                loader.execute();
            }
        });
        loader.execute();
    }

    private void initialTipsDatabase() {
        LoadInitialTipsToTipDatabase loader = new LoadInitialTipsToTipDatabase(getApplicationContext());
        loader.setOnDatabaseBuilt(new LoadInitialTipsToTipDatabase.OnDatabaseBuilt() {
            @Override
            public void buildComplete() {
                loadList();
            }
        });
        loader.execute();
    }

    private void initialHistoryDatabase() {
        LoadInitialTipsToHistoryDatabase loader = new LoadInitialTipsToHistoryDatabase(getApplicationContext());
        loader.setOnDatabaseBuilt(new LoadInitialTipsToHistoryDatabase.OnDatabaseBuilt() {
            @Override
            public void buildComplete() {
                loadList();
            }
        });
        loader.execute();
    }

    private void getApkVersion() {
        try {
            version = "v." + getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (pagerAdapter != null)
            if (getSupportFragmentManager().getBackStackEntryCount() > 0 && pagerAdapter.getFragment(pagerAdapter.getCount() - 1).isMenuVisible()) {
                getSupportFragmentManager().popBackStack();
            } else {
                if (doubleBackToExitPressedOnce) {
                    getSupportFragmentManager().popBackStack();
                    super.onBackPressed();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
    }

    private void loadList() {
        ReadFromTipDatabase reader = new ReadFromTipDatabase(getApplicationContext());
        reader.setQueryCompleteListener(new ReadFromTipDatabase.OnQueryComplete() {
            @Override
            public void setQueryComplete(ArrayList result) {
                listDataFromTipDatabase = result;

                ReadFromHistoryDatabase reader = new ReadFromHistoryDatabase(getApplicationContext());
                reader.setQueryCompleteListener(new ReadFromHistoryDatabase.OnQueryComplete() {
                    @Override
                    public void setQueryComplete(ArrayList result) {
                        listDataFromHistoryDatabase = result;
                        loadListFragment();
                    }
                });

                reader.execute();
            }
        });
        reader.execute();
    }

    private void loadListFragment() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), Main2Activity.this, listDataFromTipDatabase, listDataFromHistoryDatabase);
        viewPager.refreshDrawableState();
        viewPager.setAdapter(pagerAdapter);
        setTabLayout();
    }

    private void setTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setBackgroundColor(321);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            assert tab != null;
            tab.setCustomView(pagerAdapter.getTabView(i));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    Boolean isRefresh = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh_settings) {
            //progressBarAnimationIn();
            isRefresh = true;
            new JSONTask().execute(DB_TIP_PATH, DB_HISTORY_PATH);
            return true;
        }
        if (id == R.id.rate_us) {
            rateUs();
        }
        return super.onOptionsItemSelected(item);
    }

    public void progressBarAnimationIn() {
        ConnectivityManager cm = (ConnectivityManager) Main2Activity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
            progressBarHolder.bringToFront();
            loading = true;
        }
    }


    public void progressBarAnimationOut() {
        ConnectivityManager cm = (ConnectivityManager) Main2Activity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
            outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);
            loading = false;
        }
    }

    @Override
    public void onListItemSwiped(int position) {

    }

    @Override
    public void onListItemClicked(int position) {

    }

    public class JSONTask extends AsyncTask<String, String, ListOfHistoryAndTips> {
        @Override
        protected ListOfHistoryAndTips doInBackground(String... params) {
            try {
                ListOfHistoryAndTips list = new ListOfHistoryAndTips();
                for (String param : params) {
                    URL url = new URL(param);
                    String JSON = getJson(url);
                    JSONObject parentObject = new JSONObject(JSON);
                    JSONArray parentArray = parentObject.getJSONArray("_embedded");
                    if (parentArray.length() == 1)
                        list.setListTip(completeEmbeddedTip(parentArray));
                    else
                        list.setListHistory(completeEmbeddedHistory(parentArray));
                }
                return list;

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        private String getJson(URL url) throws IOException {
            HttpURLConnection connection;
            BufferedReader reader;
            connection = (HttpURLConnection) url.openConnection();
            String header = "Basic " + new String(android.util.Base64.encode("a:a".getBytes(), android.util.Base64.NO_WRAP));
            connection.setRequestProperty("Authorization", header);
            connection.connect();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            reader.close();
            connection.disconnect();
            return buffer.toString();
        }

        List<Tip> completeEmbeddedHistory(JSONArray parentArray) throws JSONException {
            ArrayList<Tip> finalTipsList = new ArrayList<>();
            for (int j = 0; j < parentArray.length(); j++) {
                JSONObject finalObject = parentArray.getJSONObject(j);
                String date = finalObject.getString("Date");
                JSONArray parentBetList = finalObject.getJSONArray("BetList");
                completeBetList(finalTipsList, parentBetList, date);
            }

            return finalTipsList;
        }

        List<Tip> completeEmbeddedTip(JSONArray parentArray) throws JSONException {
            ArrayList<Tip> finalTipsList = new ArrayList<>();
            JSONObject finalObject = parentArray.getJSONObject(0);
            String date = finalObject.getString("Date");
            JSONArray parentBetList = finalObject.getJSONArray("BetList");
            completeBetList(finalTipsList, parentBetList, date);
            return finalTipsList;
        }

        private List<Tip> completeBetList(ArrayList<Tip> finalTipsList, JSONArray parentBetList, String date) throws JSONException {
            for (int i = 0; i < parentBetList.length(); i++) {
                Tip tip = new Tip("", "", "", "", "", "");

                if (parentBetList.getJSONObject(i).isNull("Name"))
                    tip.setName("Undefined");
                else
                    tip.setName(parentBetList.getJSONObject(i).getString("Name"));

                if (parentBetList.getJSONObject(i).isNull("Win"))
                    tip.setWin("Undefined");
                else
                    tip.setWin(parentBetList.getJSONObject(i).getString("Win"));

                if (parentBetList.getJSONObject(i).isNull("Time"))
                    tip.setTime("Undefined");
                else
                    tip.setTime(parentBetList.getJSONObject(i).getString("Time"));

                if (parentBetList.getJSONObject(i).isNull("Course"))
                    tip.setCourse("Undefined");
                else
                    tip.setCourse(parentBetList.getJSONObject(i).getString("Course"));

                if (parentBetList.getJSONObject(i).isNull("League"))
                    tip.setLeague("--Undefined--");
                else
                    tip.setLeague(parentBetList.getJSONObject(i).getString("League"));

                tip.setDate(date);
                finalTipsList.add(tip);
            }
            return finalTipsList;
        }

        @Override
        protected void onPreExecute() {
            progressBarAnimationIn();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(final ListOfHistoryAndTips result) {
            if (result == null) {
                progressBarAnimationOut();
                Toasts.displayWhileStart(Main2Activity.this, Toasts.State.noConnections);
                return;
            }

            List<Tip> listTip = result.getListTip();

            String[] dateSplit = listTip.get(0).getDate().split("/");

            if (!dateSplit[1].equals(version)) {
                Toasts.displayWhileStart(Main2Activity.this, Toasts.State.newVersion);
                listTip = new ArrayList<>();
                Tip tip = new Tip("Released the new version", "Get the latest versionB", "", "", "", "OnlyBet");
                listTip.add(tip);
                result.setListTip(listTip);
                DeleteData(result);
                return;
            }


            DeleteData(result);


        }

        private void toastDisplay(List<Tip> list1, List<Tip> list2) {
            if (isRefresh) {
                if (equateList(list1, list2))
                    Toasts.displayWhileRefresh(Main2Activity.this, Toasts.State.actualTips);
                else
                    Toasts.displayWhileRefresh(Main2Activity.this, Toasts.State.newTips);

                isRefresh = false;
            } else {
                if (!equateList(list1, list2)) {
                    Toasts.displayWhileStart(Main2Activity.this, Toasts.State.newTips);


                }
            }
        }

        private void DeleteData(final ListOfHistoryAndTips r) {
            ReadFromTipDatabase reader = new ReadFromTipDatabase(getApplicationContext());
            reader.setQueryCompleteListener(new ReadFromTipDatabase.OnQueryComplete() {
                @Override
                public void setQueryComplete(final ArrayList readResult) {
                    DeleteFromHistoryDatabase delete = new DeleteFromHistoryDatabase(getApplicationContext());
                    delete.setDeleteCompleteListener(new DeleteFromHistoryDatabase.OnDeleteComplete() {
                        @Override
                        public void setQueryComplete(Long res) {
                            WriteToHistoryDatabase writer = new WriteToHistoryDatabase(getApplicationContext(), r.getListHistory());
                            writer.setWriteCompleteListener(new WriteToHistoryDatabase.OnWriteComplete() {
                                @Override
                                public void setWriteComplete(long result) {
                                    DeleteFromTipDatabase delete = new DeleteFromTipDatabase(getApplicationContext());
                                    delete.setDeleteCompleteListener(new DeleteFromTipDatabase.OnDeleteComplete() {
                                        @Override
                                        public void setQueryComplete(Long res) {
                                            WriteToTipDatabase writer = new WriteToTipDatabase(getApplicationContext(), r.getListTip());
                                            writer.setWriteCompleteListener(new WriteToTipDatabase.OnWriteComplete() {
                                                @Override
                                                public void setWriteComplete(long result) {
                                                    toastDisplay(readResult, r.getListTip());
                                                    progressBarAnimationOut();
                                                    viewPager.getAdapter().notifyDataSetChanged();
                                                }
                                            });
                                            writer.execute();
                                        }

                                    });
                                    delete.execute();
                                }
                            });
                            writer.execute();
                        }

                    });
                    delete.execute();
                }
            });
            reader.execute();
        }

        private boolean equateList(List<Tip> list1, List<Tip> list2) {
            try {
                return list1.size() != 0 && list2.size() != 0 && list1.get(0).getDate().equals(list2.get(0).getDate());
            } catch (Exception ex) {
                FirebaseCrash.report(new Exception("Error: equateList not working"));
            }
            return false;
        }
    }
}
