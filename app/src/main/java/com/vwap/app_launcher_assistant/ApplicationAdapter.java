package com.vwap.app_launcher_assistant;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {
    private final int mBackground;
    private final PackageManager packageManager;
    private final Context mContext;
    private final int mScreenWidth;
    private List<ResolveInfo> mValues;
    private List<ResolveInfo> filteredData;
    private OnAppSelectedListener mAppSelectedListener;

    ApplicationAdapter(final Context context, final List<ResolveInfo> items,
            PackageManager packageManagerObject, int screenWidth) {
        TypedValue mTypedValue = new TypedValue();
        context.getTheme()
                .resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        mValues = items;
        filteredData = mValues;
        packageManager = packageManagerObject;
        mContext = context;
        mAppSelectedListener = (OnAppSelectedListener) context;
        mScreenWidth = screenWidth;
    }

    Filter getFilter() {
        return new ItemFilter();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mView.setBackgroundResource(mBackground);
        viewHolder.mView.getLayoutParams().width = mScreenWidth / 2;
        viewHolder.mView.getLayoutParams().height = mScreenWidth / 2;
        viewHolder.selectedIcon.getLayoutParams().height = mScreenWidth / 3;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String appId = SharedPreferencesUtils.getSharedPreference("app_id", mContext);
        final ResolveInfo data = filteredData.get(position);
        holder.appName.setText(data.loadLabel(packageManager));
        if (!TextUtils.isEmpty(appId) && appId.equalsIgnoreCase(data.activityInfo.packageName)) {
            holder.selectedIcon.setVisibility(View.VISIBLE);
        } else {
            holder.selectedIcon.setVisibility(View.GONE);
        }
        holder.iconview.setImageDrawable(data.loadIcon(packageManager));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mAppSelectedListener.onAppSelected(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    public interface OnAppSelectedListener {
        void onAppSelected(ResolveInfo app);
    }

    /**
     * View holder that represents an item of the grid.
     */
    static final class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        TextView appName;
        ImageView iconview;
        View selectedIcon;

        ViewHolder(final View view) {
            super(view);
            mView = view;
            appName = view.findViewById(R.id.title);
            iconview = view.findViewById(R.id.icon);
            selectedIcon = view.findViewById(R.id.selected);
        }
    }

    /**
     * Filter to assist with the search functionality.
     */
    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(final CharSequence constraint) {
            String filterString = constraint.toString()
                    .toLowerCase();
            FilterResults results = new FilterResults();
            if (TextUtils.isEmpty(filterString)) {
                results.values = mValues;
                results.count = mValues.size();
            } else {
                final List<ResolveInfo> list = mValues;
                int count = list.size();
                final ArrayList<ResolveInfo> searchResults = new ArrayList<>(count);
                ResolveInfo filterablePerson;
                for (int i = 0; i < count; i++) {
                    filterablePerson = list.get(i);
                    String appName = (String) filterablePerson.loadLabel(packageManager);
                    if (appName.toLowerCase()
                            .contains(filterString)) {
                        searchResults.add(filterablePerson);
                    }
                }
                results.values = searchResults;
                results.count = searchResults.size();
            }
            return results;
        }

        @SuppressWarnings ("unchecked")
        @Override
        protected void publishResults(final CharSequence constraint,
                final Filter.FilterResults results) {
            filteredData = (ArrayList<ResolveInfo>) results.values;
            notifyDataSetChanged();
        }
    }
}