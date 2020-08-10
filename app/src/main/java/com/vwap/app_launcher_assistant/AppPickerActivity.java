package com.vwap.app_launcher_assistant;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AppPickerActivity extends AppCompatActivity
        implements ApplicationAdapter.OnAppSelectedListener {
    private PackageManager packageManager = null;
    private ApplicationAdapter listAdapter = null;
    private AlertDialog settingsDialog;
    private int screenWidth;
    private RecyclerView recyclerView;
    private View fabSearch;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String action = getIntent().getAction();
        packageManager = getPackageManager();
        if ((!TextUtils.isEmpty(action) && action.equalsIgnoreCase("android.intent.action.MAIN"))
                || TextUtils.isEmpty(SharedPreferencesUtils.getSharedPreference("app_id", this))) {
            showAppPicker();
        } else {
            String appId = SharedPreferencesUtils.getSharedPreference("app_id", this);
            String appName = SharedPreferencesUtils.getSharedPreference("app_name", this);
            launchApp(appId, appName);
        }
    }

    private void showAppPicker() {
        initViews();
        checkAndShowSettingsAlert();
        new LoadApplications().execute();
    }

    private void initViews() {
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recyclerview);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay()
                .getMetrics(displaymetrics);
        screenWidth = displaymetrics.widthPixels;
        searchView = findViewById(R.id.searchView);
        fabSearch = findViewById(R.id.fab_search);
        setupRecyclerView();
        setupSearchFab();
    }

    private void launchApp(String packageName, final String appName) {
        View v = new View(this);
        v.setBackground(null);
        setContentView(v);
        try {
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            if (null != intent) {
                startActivity(intent);
                incrementAppLaunchCounterInSharedPreference();
                Analytics.event("app_launched",packageName);
                finish();
            } else {
                Analytics.event("app_launch_failed",packageName);
                Toast.makeText(AppPickerActivity.this,
                        "Couldn't launch '" + appName + "'. Please select another app.",
                        Toast.LENGTH_LONG)
                        .show();
                showAppPicker();
            }
        } catch (Exception e) {
            Analytics.event("app_launch_failed",packageName);
            Toast.makeText(AppPickerActivity.this,
                    "Couldn't launch '" + appName + "'. Please select another app.",
                    Toast.LENGTH_LONG)
                    .show();
            showAppPicker();
        }
    }

    private void incrementAppLaunchCounterInSharedPreference() {
        String countString = SharedPreferencesUtils.getSharedPreference("count", this);
        if (!TextUtils.isEmpty(countString)) {
            countString = "" + (Integer.parseInt(countString) + 1);
        } else {
            countString = "" + 1;
        }
        SharedPreferencesUtils.putSharedPreference("count", countString, this);
        Analytics.event("app_launch_count",countString);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        menu.add(0, 0, 0, "Info")
                .setIcon(R.drawable.ic_info_outline)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, 1, 0, "Share")
                .setIcon(R.drawable.ic_action_share)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                showSettingsAlert(true);
                Analytics.event("menu_button_clicked", "Info");

                break;
            case 1:
                Analytics.event("menu_button_clicked", "Share");
                shareApp();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSettingsAlert(boolean shouldShow) {
        String message =
                "To make this app work, Choose \"" + getResources().getString(R.string.app_name)
                        + "\" as the Assist"
                        + " App in settings.\nThen, select your favorite app to open with "
                        + "your phone's Assistant button/gesture from then on.";
        if (shouldShow) {
            if (settingsDialog == null) {
                settingsDialog = new AlertDialog.Builder(this).setTitle("Setup")
                        .setMessage(message)
                        .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Analytics.event("alert_button_clicked", "Open Settings");
                                Intent intent_as = new Intent(
                                        android.provider.Settings.ACTION_VOICE_INPUT_SETTINGS);
                                startActivity(intent_as);
                            }
                        })
                        .setNeutralButton("Select App", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Analytics.event("alert_button_clicked", "Select App");
                                dialog.dismiss();
                            }
                        })
                        .show();
            } else {
                if (!settingsDialog.isShowing()) {
                    settingsDialog.show();
                }
            }
        } else {
            if (settingsDialog != null && settingsDialog.isShowing()) {
                settingsDialog.dismiss();
            }
        }
    }

    private void shareApp() {
        Analytics.event("button_clicked", "Share");

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                getResources().getString(R.string.share_this_app_text));
        //            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareIntent, "Share via:"));
    }

    @Override
    public void onAppSelected(ResolveInfo app) {
        try {
            String packageName = app.activityInfo.packageName;
            String appName = (String) app.loadLabel(packageManager);
            Analytics.event("app_id_selected", packageName);
            SharedPreferencesUtils.putSharedPreference("app_id", packageName, this);
            SharedPreferencesUtils.putSharedPreference("app_name", appName, this);
            if (recyclerView != null && recyclerView.getAdapter() != null) {
                recyclerView.getAdapter()
                        .notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<ResolveInfo> checkForLaunchIntent(List<ResolveInfo> list) {
        ArrayList<ResolveInfo> applist = new ArrayList<>();
        String selectedAppId = SharedPreferencesUtils.getSharedPreference("app_id", this);
        ResolveInfo selectedItem = null;
        for (ResolveInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.activityInfo.packageName)
                        && !getPackageName().equalsIgnoreCase(info.activityInfo.packageName)) {
                    applist.add(info);
                }
                if (!TextUtils.isEmpty(selectedAppId) && selectedAppId.equalsIgnoreCase(
                        info.activityInfo.packageName)) {
                    selectedItem = info;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            Collections.sort(applist, new Comparator<ResolveInfo>() {
                public int compare(final ResolveInfo p1, final ResolveInfo p2) {
                    return ((String) p1.loadLabel(packageManager)).compareTo(
                            (String) p2.loadLabel(packageManager));
                }
            });
            if (selectedItem != null) {
                applist.remove(selectedItem);
                applist.add(0, selectedItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return applist;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAndShowSettingsAlert();
    }

    private void checkAndShowSettingsAlert() {
        String countString = SharedPreferencesUtils.getSharedPreference("count", this);
        showSettingsAlert(TextUtils.isEmpty(countString));
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 2));
        hideSearchView();
        showSearchFab(true);
    }

    private void setupSearchFab() {
        if (fabSearch != null) {
            fabSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    showSearchFab(false);
                    showSearchView();
                }
            });
        }
    }

    private void showSearchFab(final boolean shouldShow) {
        VisibilityUtils.circularRevealAnimation(fabSearch, shouldShow);
    }

    private void showSearchView() {
        if (searchView != null) {
            searchView.setQueryHint("Search Application");
            searchView.setQuery("", false);
            searchView.setFocusableInTouchMode(true);
            searchView.requestFocus();
            searchView.setIconified(false);
            searchView.setIconifiedByDefault(false);
            SearchView.OnQueryTextListener textChangeListener =
                    new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(final String query) {
                            search(query);
                            return true;
                        }

                        @Override
                        public boolean onQueryTextChange(final String cs) {
                            search(cs);
                            return true;
                        }
                    };
            searchView.setOnQueryTextListener(textChangeListener);
            VisibilityUtils.circularRevealAnimation(searchView, true);
        }
    }

    private void hideSearchView() {
        if (searchView != null) {
            searchView.clearFocus();
            searchView.setFocusableInTouchMode(false);
            VisibilityUtils.circularRevealAnimation(searchView, false);
            searchView.setOnCloseListener(null);
            search("");
            showSearchFab(true);
            UIUtils.hideKeyboard(this, searchView);
        }
    }

    private void search(final String query) {
        if (recyclerView != null && recyclerView.getAdapter() != null) {
            ((ApplicationAdapter) recyclerView.getAdapter()).getFilter()
                    .filter(query);
        }
    }

    public void onBackPressed() {
        if (searchView != null && searchView.getVisibility() == View.VISIBLE) {
            hideSearchView();
        } else {
            super.onBackPressed();
        }
    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            final List pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);
            final List<ResolveInfo> applicationsList = checkForLaunchIntent(pkgAppsList);
            Log.d("list size", String.valueOf(applicationsList.size()));
            listAdapter =
                    new ApplicationAdapter(AppPickerActivity.this, applicationsList, packageManager,
                            screenWidth);
            System.out.println("adapter=" + listAdapter);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            findViewById(R.id.progress).setVisibility(View.GONE);
            recyclerView.setAdapter(listAdapter);
            showSearchView();
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}