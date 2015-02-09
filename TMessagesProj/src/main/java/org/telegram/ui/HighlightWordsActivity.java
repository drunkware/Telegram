/*
 * This is the source code of Telegram for Android v. 1.7.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2014.
 */

package org.telegram.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.Html;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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


public class HighlightWordsActivity extends BaseFragment {

    private EditText highlightWordField;
    private View doneButton;

    private final static int done_button = 1;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        if (fragmentView == null) {
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
            doneButton = menu.addItemWithWidth(done_button, R.drawable.ic_done, AndroidUtilities.dp(56));

            fragmentView = new LinearLayout(getParentActivity());
            fragmentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ((LinearLayout) fragmentView).setOrientation(LinearLayout.VERTICAL);
            fragmentView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });

            highlightWordField = new EditText(getParentActivity());
            highlightWordField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            highlightWordField.setHintTextColor(0xff979797);
            highlightWordField.setTextColor(0xff212121);
            highlightWordField.setMaxLines(1);
            highlightWordField.setLines(1);
            highlightWordField.setPadding(0, 0, 0, 0);
            highlightWordField.setSingleLine(true);
            highlightWordField.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
            highlightWordField.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
            highlightWordField.setImeOptions(EditorInfo.IME_ACTION_DONE);
            highlightWordField.setHint(LocaleController.getString("WordsToHighlight", R.string.WordsToHighlight));
            AndroidUtilities.clearCursorDrawable(highlightWordField);
            highlightWordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_DONE && doneButton != null) {
                        doneButton.performClick();
                        return true;
                    }
                    return false;
                }
            });

            ((LinearLayout) fragmentView).addView(highlightWordField);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)highlightWordField.getLayoutParams();
            layoutParams.topMargin = AndroidUtilities.dp(24);
            layoutParams.height = AndroidUtilities.dp(36);
            layoutParams.leftMargin = AndroidUtilities.dp(24);
            layoutParams.rightMargin = AndroidUtilities.dp(24);
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            highlightWordField.setLayoutParams(layoutParams);

            highlightWordField.setText(ApplicationLoader.WORDS_HIGHLIGHT);
            highlightWordField.setSelection(highlightWordField.length());

            TextView helpTextView = new TextView(getParentActivity());
            helpTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            helpTextView.setTextColor(0xff6d6d72);
            helpTextView.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);

            TLRPC.User user = MessagesController.getInstance().getUser(UserConfig.getClientUserId());
            if (user == null) {
                user = UserConfig.getCurrentUser();
            }
            helpTextView.setText(Html.fromHtml(LocaleController.formatString("HighlightWordsHelp", R.string.HighlightWordsHelp, user.first_name, ApplicationLoader.WORDS_HIGHLIGHT_COLOR)));
            ((LinearLayout) fragmentView).addView(helpTextView);
            layoutParams = (LinearLayout.LayoutParams)helpTextView.getLayoutParams();
            layoutParams.topMargin = AndroidUtilities.dp(10);
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT;
            layoutParams.leftMargin = AndroidUtilities.dp(24);
            layoutParams.rightMargin = AndroidUtilities.dp(24);
            helpTextView.setLayoutParams(layoutParams);

        } else {
            ViewGroup parent = (ViewGroup)fragmentView.getParent();
            if (parent != null) {
                parent.removeView(fragmentView);
            }
        }
        return fragmentView;
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

    private void saveHighlightWords() {
        String newWords = highlightWordField.getText().toString().trim();

        // If the newWords is NOT equal to what we have already then save it
        if (!ApplicationLoader.WORDS_HIGHLIGHT.equals(newWords)) {
            // Change the global variable to the new value
            ApplicationLoader.WORDS_HIGHLIGHT = newWords;

            // Save the new value
            SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("Ultra", Activity.MODE_PRIVATE).edit();
            editor.putString("HighLightWords", ApplicationLoader.WORDS_HIGHLIGHT);
            editor.commit();
        }

        finishFragment();
        return;
    }

    @Override
    public void onOpenAnimationEnd() {
        highlightWordField.requestFocus();
        AndroidUtilities.showKeyboard(highlightWordField);
    }
}
