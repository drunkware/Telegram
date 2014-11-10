/*
 * This is the source code of Telegram Ultra for Android.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Fahad Alduraibi, 2014.
 */

package org.telegram.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import org.telegram.ui.Views.ColorPickerView;

public class SettingsUltraFeaturesActivity extends BaseFragment {
    private ListView listView;
    private SeekBar seekBar;

    private int enableMarkdownRow;
    private int PhotoQualityRow;
    private int showAndroidEmojiRow;

    private int rowCount = 0;

    @Override
    public boolean onFragmentCreate() {
        enableMarkdownRow = rowCount++;
        PhotoQualityRow = rowCount++;
        showAndroidEmojiRow = rowCount++;

        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();

    }

    @Override
    public View createView(final LayoutInflater inflater, ViewGroup container) {
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
            listView = (ListView) fragmentView.findViewById(R.id.listView);
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
                    } else if (i == PhotoQualityRow) {
                        final View layout = inflater.inflate(R.layout.settings_seekbar_dialog_layout, (ViewGroup) fragmentView.findViewById(R.id.seekBar_Dialog_Layout));
                        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                        builder.setView(layout);
                        builder.setTitle(LocaleController.getString("PhotoQuality", R.string.PhotoQuality));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (listView != null) {
                                    listView.invalidateViews();
                                }
                                dialog.dismiss();
                            }
                        });
                        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                if (listView != null) {
                                    listView.invalidateViews();
                                }
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        SeekBar sb = (SeekBar) layout.findViewById(R.id.seekBar1);
                        final TextView txtSeekPercent = (TextView) layout.findViewById(R.id.txtSeekPercent);
                        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Ultra", Activity.MODE_PRIVATE);
                        int percent = preferences.getInt("PhotoQuality", 80);
                        txtSeekPercent.setText(Integer.toString(percent) + "%");
                        sb.setProgress(percent);
                        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                txtSeekPercent.setText(Integer.toString(progress) + "%");
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Ultra", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putInt("PhotoQuality", seekBar.getProgress());
                                editor.commit();
//                                if (listView != null) {
//                                    listView.invalidateViews();
//                                }
                            }
                        });

                    }
                }
            });
        } else {
            ViewGroup parent = (ViewGroup) fragmentView.getParent();
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
            return (i == enableMarkdownRow || i == PhotoQualityRow || i == showAndroidEmojiRow);
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
                    LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = li.inflate(R.layout.settings_row_check_layout, viewGroup, false);
                }
                TextView textView = (TextView) view.findViewById(R.id.settings_row_text);
                View divider = view.findViewById(R.id.settings_row_divider);

                ImageView checkButton = (ImageView) view.findViewById(R.id.settings_row_check_button);
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
                    LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = li.inflate(R.layout.settings_row_ultra_slider_layout, viewGroup, false);
                }
                TextView textView = (TextView) view.findViewById(R.id.settings_row_text);
                View divider = view.findViewById(R.id.settings_row_divider);
                TextView seekBarPercent = (TextView) view.findViewById(R.id.settings_seekBar_percent);
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Ultra", Activity.MODE_PRIVATE);
                if (i == PhotoQualityRow) {
                    textView.setText(LocaleController.getString("PhotoQuality", R.string.PhotoQuality));
                    seekBarPercent.setText(Integer.toString(preferences.getInt("PhotoQuality", 80)) + "%");
                    divider.setVisibility(View.VISIBLE);
                }
            }
            return view;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == enableMarkdownRow || i == showAndroidEmojiRow) {
                return 0;
            } else if (i == PhotoQualityRow) {
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
