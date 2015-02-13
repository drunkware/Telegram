/*
 * This is the source code of Telegram for Android v. 2.0.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2014.
 */

package org.telegram.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import org.telegram.android.AndroidUtilities;
import org.telegram.android.LocaleController;
import org.telegram.android.MessagesController;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;
import org.telegram.messenger.TLRPC;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextFieldCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.ColorPickerView;

public class UltraHighlightWordsActivity extends BaseFragment {
    private ListView listView;
    private TextFieldCell highlightWordField;
    private int colorValue;

    private int highlightWordRow;
    private int dividerRow;
    private int colorRow;
    private int helpRow;
    private int rowCount = 0;

    private final static int done_button = 1;

    @Override
    public boolean onFragmentCreate() {
        highlightWordRow = rowCount++;
        dividerRow = rowCount++;
        colorRow = rowCount++;
        helpRow = rowCount++;

        return super.onFragmentCreate();
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        if (fragmentView == null) {
            colorValue = ApplicationLoader.WORDS_HIGHLIGHT_COLOR;
            actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            actionBar.setAllowOverlayTitle(true);
            actionBar.setTitle(LocaleController.getString("HighlightWords", R.string.HighlightWords));
            actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
                @Override
                public void onItemClick(int id) {
                    if (id == -1) {
                        finishFragment();
                    } else if (id == done_button) {
                        saveHighlightWords();
                    }
                }
            });

            ActionBarMenu menu = actionBar.createMenu();
            menu.addItemWithWidth(done_button, R.drawable.ic_done, AndroidUtilities.dp(56));

            highlightWordField = new TextFieldCell(getParentActivity());
            highlightWordField.setFieldTitleAndHint(null, LocaleController.getString("WordsToHighlight", R.string.WordsToHighlight), AndroidUtilities.dp(5), false);
            highlightWordField.setFieldText(ApplicationLoader.WORDS_HIGHLIGHT);
            highlightWordField.setBackgroundColor(0xffffffff);

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
            layoutParams.gravity = Gravity.TOP;
            listView.setLayoutParams(layoutParams);
            listView.setAdapter(new ListAdapter(getParentActivity()));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                    if (i == colorRow) {
                        if (getParentActivity() == null) {
                            return;
                        }

                        LayoutInflater li = (LayoutInflater)getParentActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = li.inflate(R.layout.settings_color_dialog_layout, null, false);
                        final ColorPickerView colorPickerView = (ColorPickerView)view.findViewById(R.id.color_picker);

                        colorPickerView.setOldCenterColor(colorValue);
                        //colorPickerView.setColor(colorValue);

                        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                        builder.setTitle(LocaleController.getString("HighlightColor", R.string.HighlightColor));
                        builder.setView(view);
                        builder.setPositiveButton(LocaleController.getString("Set", R.string.Set), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                colorValue = colorPickerView.getColor();
                                listView.invalidateViews();
                            }
                        });
                        builder.setNeutralButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        showAlertDialog(builder);
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
            return (i == colorRow);
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
            int viewType = getItemViewType(i);
            if (viewType == 0) {
                if (view == null) {
                    view = new TextInfoPrivacyCell(mContext);
                }
                if (i == helpRow) {
                    TLRPC.User user = MessagesController.getInstance().getUser(UserConfig.getClientUserId());
                    if (user == null) {
                        user = UserConfig.getCurrentUser();
                    }
                    ((TextInfoPrivacyCell) view).setText(Html.fromHtml(LocaleController.formatString("HighlightWordsHelp", R.string.HighlightWordsHelp, user.first_name, colorValue)));
                    ((TextInfoPrivacyCell) view).setTextColor(0xff797979);
                    view.setBackgroundResource(R.drawable.greydivider);
                }
            } else if (viewType == 1) {
                return highlightWordField;
            } else if (viewType == 2) {
                if (view == null) {
                    view = new TextColorCell(mContext);
                }
                TextColorCell textcolorCell = (TextColorCell) view;
                textcolorCell.setTextAndColor(LocaleController.getString("HighlightColor", R.string.HighlightColor), colorValue, true);
            } else if (viewType == 3) {
                if (view == null) {
                    view = new DividerCell(mContext);
                }
            }
            return view;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == helpRow) {
                return 0;
            } else if (i == highlightWordRow) {
                return 1;
            } else if (i == colorRow) {
                return 2;
            } else if (i == dividerRow) {
                return 3;
            }
            return 3;
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

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        boolean animations = preferences.getBoolean("view_animations", true);
        if (!animations) {
            highlightWordField.requestFocus();
            AndroidUtilities.showKeyboard(highlightWordField);
        }
    }

    @Override
    public void onOpenAnimationEnd() {
        highlightWordField.requestFocus();
        AndroidUtilities.showKeyboard(highlightWordField);
    }

    private void saveHighlightWords() {
        String newWords = highlightWordField.getFieldText().trim();

        // If the newWords is NOT equal to what we have already then save it
        if (!ApplicationLoader.WORDS_HIGHLIGHT.equals(newWords)) {
            // Change the global variable to the new value
            ApplicationLoader.WORDS_HIGHLIGHT = newWords;

            // Save the new value
            SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Ultra", Activity.MODE_PRIVATE).edit();
            editor.putString("HighlightWords", newWords);
            editor.commit();
        }

        if (ApplicationLoader.WORDS_HIGHLIGHT_COLOR != colorValue) {
            ApplicationLoader.WORDS_HIGHLIGHT_COLOR = colorValue;

            SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Ultra", Activity.MODE_PRIVATE).edit();
            editor.putInt("HighlightColor", colorValue);
            editor.commit();
        }

        finishFragment();
    }
}
