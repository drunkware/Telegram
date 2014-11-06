/*
 * This is the source code of Telegram for Android v. 1.3.2.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013.
 */

package org.telegram.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.telegram.android.AndroidUtilities;
import org.telegram.android.LocaleController;
import org.telegram.android.NotificationsController;
import org.telegram.android.NotificationCenter;
import org.telegram.messenger.TLObject;
import org.telegram.messenger.TLRPC;
import org.telegram.messenger.ConnectionsManager;
import org.telegram.messenger.FileLog;
import org.telegram.android.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.RPCRequest;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Views.ActionBar.ActionBarLayer;
import org.telegram.ui.Views.ActionBar.BaseFragment;
import org.telegram.ui.Views.ColorPickerView;
import org.telegram.ui.Views.SettingsSectionLayout;

public class SettingsUltraFeaturesActivity extends BaseFragment {
    private ListView listView;

    private int enableMarkdownRow;
    private int photoResolutionRow;
    private int useNativeEmojiRow;

    private int rowCount = 0;

    @Override
    public boolean onFragmentCreate() {
        enableMarkdownRow = rowCount++;
        photoResolutionRow = rowCount++;
        useNativeEmojiRow = rowCount++;

//        NotificationCenter.getInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);

        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
//        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
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
                    } else if (i == photoResolutionRow) {
                        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Ultra", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        boolean enabled = preferences.getBoolean("photoResolution", false);
                        editor.putBoolean("photoResolution", !enabled);
                        editor.commit();
                        listView.invalidateViews();
                    } else if (i == useNativeEmojiRow) {
                        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Ultra", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        boolean enabled = preferences.getBoolean("useNativeEmoji", false);
                        editor.putBoolean("useNativeEmoji", !enabled);
                        editor.commit();
                        listView.invalidateViews();
                    }
                }
            });
        } else {
            ViewGroup parent = (ViewGroup)fragmentView.getParent();
            if (parent != null) {
                parent.removeView(fragmentView);
            }
        }
        return fragmentView;
    }

//    @Override
//    public void didReceivedNotification(int id, Object... args) {
//        if (id == NotificationCenter.notificationsSettingsUpdated) {
//            listView.invalidateViews();
//        }
//    }

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
            return (i == enableMarkdownRow || i == photoResolutionRow || i == useNativeEmojiRow);
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
                    view = new SettingsSectionLayout(mContext);
                }
                if (i == enableMarkdownRow) {
                    ((SettingsSectionLayout) view).setText(LocaleController.getString("EnableMarkdown", R.string.EnableMarkdown));
                } else if (i == photoResolutionRow) {
                    ((SettingsSectionLayout) view).setText(LocaleController.getString("PhotoResolution", R.string.PhotoResolution));
                } else if (i == useNativeEmojiRow) {
                    ((SettingsSectionLayout) view).setText(LocaleController.getString("useAndroidEmoji", R.string.useAndroidEmoji));
                }
            } if (type == 1) {
                if (view == null) {
                    LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = li.inflate(R.layout.settings_row_check_notify_layout, viewGroup, false);
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
                } else if (i == photoResolutionRow) {
                    textView.setText(LocaleController.getString("PhotoResolution", R.string.PhotoResolution));
                    divider.setVisibility(View.VISIBLE);
                    enabled = preferences.getBoolean("photoResolution", false);
                } else if (i == useNativeEmojiRow) {
                    textView.setText(LocaleController.getString("useAndroidEmoji", R.string.useAndroidEmoji));
                    divider.setVisibility(View.VISIBLE);
                    enabled = preferences.getBoolean("useNativeEmoji", false);
                }
                if (enabled) {
                    checkButton.setImageResource(R.drawable.btn_check_on);
                } else {
                    checkButton.setImageResource(R.drawable.btn_check_off);
                }
            }
            return view;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == 99999999) { //not sure which one
                return 0;
            } else if (i == enableMarkdownRow || i == photoResolutionRow || i == useNativeEmojiRow ) {
                return 1;
            } else {
                return 2;
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
