/*
 * This is the source code of Telegram for Android v. 1.3.2.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013.
 */

package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.SeekBar;

import org.telegram.android.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Views.ActionBar.ActionBarLayer;
import org.telegram.ui.Views.ActionBar.BaseFragment;

public class SettingsUltraFeaturesActivity extends BaseFragment {
    private ListView listView;
    private SeekBar seekBar;

    private int enableMarkdownRow;
    private int photoResolutionRow;
    private int showAndroidEmojiRow;

    private int rowCount = 0;

    @Override
    public boolean onFragmentCreate() {
        enableMarkdownRow = rowCount++;
        photoResolutionRow = rowCount++;
        showAndroidEmojiRow = rowCount++;

        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();

    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        if (fragmentView == null) {
            actionBarLayer.setDisplayHomeAsUpEnabled(true, R.drawable.ic_ab_back);
            actionBarLayer.setBackOverlay(R.layout.updating_state_layout);
            actionBarLayer.setTitle(LocaleController.getString("UltraFeatures", R.string.UltraFeatures));
            actionBarLayer.setActionBarMenuOnItemClick(new ActionBarLayer.ActionBarMenuOnItemClick() {
                @Override
                public void onItemClick(int id) {
                    if (id == -1) {
                        finishFragment();
                    }
                }
            });

            fragmentView = inflater.inflate(R.layout.settings_layout, container, false);
            final ListAdapter listAdapter = new ListAdapter(getParentActivity());
            listView = (ListView)fragmentView.findViewById(R.id.listView);
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                    if (i == enableMarkdownRow) {
                        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Ultra", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        boolean enabled = preferences.getBoolean("view_markdown", false);
                        editor.putBoolean("view_markdown", !enabled);
                        editor.commit();
                        listView.invalidateViews();
                    } else if (i == showAndroidEmojiRow) {
                        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Ultra", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        boolean enabled = preferences.getBoolean("showAndroidEmoji", false);
                        editor.putBoolean("showAndroidEmoji", !enabled);
                        editor.commit();
                        listView.invalidateViews();
                    }
                }
            });
            seekBar = (SeekBar)fragmentView.findViewById(R.id.settings_row_seekBar);

            //fragmentView = inflater.inflate(R.layout.settings_row_ultra_slider_layout, container, false);
            //seekBar = (SeekBar)fragmentView.findViewById(R.id.settings_row_seekBar);
//            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBarl) {
//                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Ultra", Activity.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = preferences.edit();
//                    int seekPercentage = preferences.getInt("photoResolution", 80);
//                    editor.putInt("photoResolution", seekPercentage);
//                    editor.commit();
//                    listView.invalidateViews();
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBarl) {
//                    // TODO Auto-generated method stub
//                }
//
//                @Override
//                public void onProgressChanged(SeekBar seekBarl, int progress, boolean fromUser) {
//
//                }
//            });

        } else {
            ViewGroup parent = (ViewGroup)fragmentView.getParent();
            if (parent != null) {
                parent.removeView(fragmentView);
            }
        }
        return fragmentView;
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int i) {
            return (i == enableMarkdownRow || i == photoResolutionRow || i == showAndroidEmojiRow);
        }

        @Override
        public int getCount() {
            return rowCount;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            int type = getItemViewType(i);
            if (type == 0) {
                if (view == null) {
                    LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = li.inflate(R.layout.settings_row_check_layout, viewGroup, false);
                }
                TextView textView = (TextView)view.findViewById(R.id.settings_row_text);
                View divider = view.findViewById(R.id.settings_row_divider);

                ImageView checkButton = (ImageView)view.findViewById(R.id.settings_row_check_button);
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Ultra", Activity.MODE_PRIVATE);
                boolean enabled = false;

                if (i == enableMarkdownRow) {
                    textView.setText(LocaleController.getString("EnableMarkdown", R.string.EnableMarkdown));
                    divider.setVisibility(View.VISIBLE);
                    enabled = preferences.getBoolean("view_markdown", false);
                } else if (i == showAndroidEmojiRow) {
                    textView.setText(LocaleController.getString("showAndroidEmoji", R.string.showAndroidEmoji));
                    divider.setVisibility(View.VISIBLE);
                    enabled = preferences.getBoolean("showAndroidEmoji", false);
                }
                if (enabled) {
                    checkButton.setImageResource(R.drawable.btn_check_on);
                } else {
                    checkButton.setImageResource(R.drawable.btn_check_off);
                }
            } else if (type == 1) {
                if (view == null) {
                    LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = li.inflate(R.layout.settings_row_ultra_slider_layout, viewGroup, false);
                }
                TextView textView = (TextView)view.findViewById(R.id.settings_row_text);
                View divider = view.findViewById(R.id.settings_row_divider);

                SeekBar seekBar = (SeekBar)view.findViewById(R.id.settings_row_seekBar);
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Ultra", Activity.MODE_PRIVATE);

                if (i == photoResolutionRow) {
                    int seekPercentage;
                    textView.setText(LocaleController.getString("PhotoResolution", R.string.PhotoResolution));
                    divider.setVisibility(View.VISIBLE);
                    seekPercentage = preferences.getInt("photoResolution", 80);
                    seekBar.setProgress(seekPercentage);
                }
            }
            return view;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == enableMarkdownRow || i == showAndroidEmojiRow ) {
                return 0;
            } else if ( i == photoResolutionRow ) {
                return 1;
            } else {
                return 9;
            }

        }

        @Override
        public int getViewTypeCount() {
            return 5;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}
