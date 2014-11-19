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
import android.widget.TextView;
import android.widget.SeekBar;

import org.telegram.android.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;

public class SettingsUltraFeaturesActivity extends BaseFragment {
    private ListView listView;

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
                    } else if (i == showAndroidEmojiRow) {
                        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Ultra", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        enabled = preferences.getBoolean("showAndroidEmoji", false);
                        editor.putBoolean("showAndroidEmoji", !enabled);
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
                    }
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!enabled);
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
                    view = new TextCheckCell(mContext);
                }
                TextCheckCell checkCell = (TextCheckCell) view;
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("Ultra", Activity.MODE_PRIVATE);
                if (i == enableMarkdownRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("EnableMarkdown", R.string.EnableMarkdown), preferences.getBoolean("view_markdown", false), true);
                } else if (i == showAndroidEmojiRow) {
                    checkCell.setTextAndCheck(LocaleController.getString("showAndroidEmoji", R.string.showAndroidEmoji), preferences.getBoolean("showAndroidEmoji", false), true);
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
