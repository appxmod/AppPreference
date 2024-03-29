/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.preference;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.appcompat.app.GlobalOptions;

public class EditTextPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    private static final String SAVE_STATE_TEXT = "EditTextPreferenceDialogFragment.text";

    private EditText mEditText;

    private CharSequence mText;

    public static EditTextPreferenceDialogFragmentCompat newInstance(String key) {
        final EditTextPreferenceDialogFragmentCompat
                fragment = new EditTextPreferenceDialogFragmentCompat();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mText = getEditTextPreference().getText();
        } else {
            mText = savedInstanceState.getCharSequence(SAVE_STATE_TEXT);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(SAVE_STATE_TEXT, mText);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mEditText = view.findViewById(android.R.id.edit);
	
		if(GlobalOptions.isDark)
			mEditText.setTextColor(Color.WHITE);

        if (mEditText == null) {
            throw new IllegalStateException("Dialog view must contain an EditText with id" +
                    " @android:id/edit");
        }
	
		if (getEditTextPreference().getIsNumberEdit()) {
			mEditText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
			mEditText.setKeyListener(DigitsKeyListener.getInstance("-0123456789."));
		}
		
        mEditText.requestFocus();
        mEditText.setText(mText);
		if (getEditTextPreference().getAutoSelectAll()) {
			mEditText.setSelection(0, mEditText.getText().length());
		} else {
			// Place cursor at the end
			mEditText.setSelection(mEditText.getText().length());
		}
		if (getEditTextPreference().getOnBindEditTextListener() != null) {
            getEditTextPreference().getOnBindEditTextListener().onBindEditText(mEditText);
        }
    }

    private EditTextPreference getEditTextPreference() {
        return (EditTextPreference) getPreference();
    }

    /** @hide */
    @RestrictTo(LIBRARY)
    @Override
    protected boolean needInputMethod() {
        // We want the input method to show, if possible, when dialog is displayed
        return true;
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            String value = mEditText.getText().toString();
            final EditTextPreference preference = getEditTextPreference();
            if (preference.callChangeListener(value)) {
                preference.setText(value);
            }
        }
    }

}
