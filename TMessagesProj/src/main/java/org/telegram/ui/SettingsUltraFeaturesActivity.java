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
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.telegram.android.LocaleController;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextUltraInfoCheckCell;

public class SettingsUltraFeaturesActivity extends BaseFragment {
    private ListView listView;

    private int enableMarkdownRow;
    private int PhotoQualityRow;
    private int showAndroidEmojiRow;
    private int disableTabletModeRow;
    private int highlighWordsRow;

    private int rowCount = 0;

    @Override
    public boolean onFragmentCreate() {
        enableMarkdownRow = rowCount++;
        PhotoQualityRow = rowCount++;
        if (android.os.Build.VERSION.SDK_INT >= 19) { // Only enable this option for Kitkat and newer android versions
            showAndroidEmojiRow = rowCount++;
        } else {
            showAndroidEmojiRow = -1;
        }
        if ( ApplicationLoader.applicationContext.getResources().getBoolean(R.bool.isTablet) ){ // Only enable this option if it is a tablet
            disableTabletModeRow = rowCount++;
        } else {
            disableTabletModeRow = -1;
        }
        highlighWordsRow = rowCount++;

        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();

    }

    @Override
    public View createView(final LayoutInflater inflater, ViewGroup container) {
        if (fragmentView == null) {
            actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            actionBar.setAllowOverlayTitle(true);
            actionBar.setTitle(LocaleController.getString("UltraSettings", R.string.UltraSettings));
            actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
                @Override
                public void onItemClick(int id) {
                    if (id == -1) {
                        finishFragment();
                    }
                }
            });

            fragmentView = new FrameLayout(getParentActivity());
            FrameLayout frameLayout = (FrameLayout) fragmentView;

            listView = new ListView(getParentActivity());
            listView.setDivider(null);
            listView.setDividerHeight(0);
            listView.setVerticalScrollBarEnabled(false);
            frameLayout.addView(listView);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) listView.getLayoutParams();
            layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
            listView.setLayoutParams(layoutParams);
            listView.setAdapter(new ListAdapter(getParentActivity()));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                    boolean enabled = false;
                    if (i == enableMarkdownRow) {
                        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Ultra", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        enabled = preferences.getBoolean("view_markdown", false);
                        editor.putBoolean("view_markdown", !enabled);
                        editor.commit();
                        ApplicationLoader.MARK_DOWN = !enabled;
                    } else if (i == showAndroidEmojiRow) {
                        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Ultra", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        enabled = preferences.getBoolean("showAndroidEmoji", false);
                        editor.putBoolean("showAndroidEmoji", !enabled);
                        editor.commit();
                        ApplicationLoader.SHOW_ANDROID_EMOJI = !enabled;
                    } else if (i == disableTabletModeRow) {
                        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Ultra", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        enabled = preferences.getBoolean("disableTabletMode", false);
                        editor.putBoolean("disableTabletMode", !enabled);
                        editor.commit();
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
                            }
                        });
                    } else if ( i == highlighWordsRow ) {
                        presentFragment(new UltraHighlightWordsActivity());
                    }
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!enabled);
                    } else if (view instanceof TextUltraInfoCheckCell) {
                        ((TextUltraInfoCheckCell) view).setChecked(!enabled);
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
            return (i == enableMarkdownRow || i == PhotoQualityRow || i == showAndroidEmojiRow || i == disableTabletModeRow || i == highlighWordsRow);
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
                    view = new TextCheckCell(mContext);
                }
                TextCheckCell checkCell = (TextCheckCell) view;
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Ultra", Activity.MODE_PRIVATE);
                if (i == enableMarkdownRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("EnableMarkdown", R.string.EnableMarkdown), ApplicationLoader.MARK_DOWN, true);
                } else if (i == showAndroidEmojiRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("showAndroidEmoji", R.string.showAndroidEmoji), ApplicationLoader.SHOW_ANDROID_EMOJI, true);
                }
            } else if (type == 1) {
                if (view == null) {
                    view = new TextDetailSettingsCell(mContext);
                }
                TextDetailSettingsCell textCell = (TextDetailSettingsCell) view;
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Ultra", Activity.MODE_PRIVATE);
                if (i == PhotoQualityRow) {
                    textCell.setMultilineDetail(true);
                    String value = Integer.toString(preferences.getInt("PhotoQuality", 80)) + "%";
                    textCell.setTextAndValue(LocaleController.getString("PhotoQuality", R.string.PhotoQuality), value, true);
                }
            } else if (type == 2) {
                if (view == null) {
                    view = new TextUltraInfoCheckCell(mContext);
                }
                TextUltraInfoCheckCell checkCell = (TextUltraInfoCheckCell) view;
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Ultra", Activity.MODE_PRIVATE);
                if (i == disableTabletModeRow) {
                    checkCell.setTextInfoAndCheck(LocaleController.getString("disableTabletMode", R.string.disableTabletMode), LocaleController.getString("disableTabletModeInfo", R.string.disableTabletModeInfo), preferences.getBoolean("disableTabletMode", false), true);
                }
            } else if (type == 3) {
                if (view == null) {
                    view = new TextDetailSettingsCell(mContext);
                }
                TextDetailSettingsCell textCell = (TextDetailSettingsCell) view;

                if (i == highlighWordsRow) {
                    String value;
                    if (ApplicationLoader.WORDS_HIGHLIGHT.length() > 0) {
                        value = ApplicationLoader.WORDS_HIGHLIGHT;
                    } else {
                        value  = LocaleController.getString("HighlightWordsEmpty", R.string.HighlightWordsEmpty);
                    }
                    textCell.setTextAndValue(LocaleController.getString("HighlightWords", R.string.HighlightWords), value, true);
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
            } else if ( i == disableTabletModeRow) {
                return 2;
            } else if ( i == highlighWordsRow) {
                return 3;
            }

            return -1;
        }

        @Override
        public int getViewTypeCount() {
            return 4;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}
