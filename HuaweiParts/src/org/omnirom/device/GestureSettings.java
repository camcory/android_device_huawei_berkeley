/*
* Copyright (C) 2017 The OmniROM Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package org.omnirom.device;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.Preference;
import android.provider.Settings;

public class GestureSettings extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static final String KEY_FP_GESTURE_DEFAULT_CATEGORY = "gesture_settings";

    public static final String FP_GESTURE_LONG_PRESS_APP = "fp_long_press_gesture_app";
    public static final String FP_GESTURE_SWIPE_LEFT_APP = "fp_left_swipe_gesture_app";
    public static final String FP_GESTURE_SWIPE_RIGHT_APP = "fp_right_swipe_gesture_app";
    public static final String FP_GESTURE_TAP_APP = "fp_tap_gesture_app";

    public static final String DEVICE_GESTURE_MAPPING_0 = "device_gesture_mapping_0_0";
    public static final String DEVICE_GESTURE_MAPPING_1 = "device_gesture_mapping_1_0";
    public static final String DEVICE_GESTURE_MAPPING_2 = "device_gesture_mapping_2_0";
    public static final String DEVICE_GESTURE_MAPPING_3 = "device_gesture_mapping_3_0";

    private AppSelectListPreference mFPLongPressApp;
    private AppSelectListPreference mFPLeftSwipeApp;
    private AppSelectListPreference mFPRightSwipeApp;
    private AppSelectListPreference mFPTapApp;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.gesture_settings, rootKey);

            mFPLongPressApp = (AppSelectListPreference) findPreference(FP_GESTURE_LONG_PRESS_APP);
            mFPLongPressApp.setEnabled(true);
            String value = Settings.System.getString(getContext().getContentResolver(),
                    DEVICE_GESTURE_MAPPING_0);
            mFPLongPressApp.setValue(value);
            mFPLongPressApp.setOnPreferenceChangeListener(this);

            mFPLeftSwipeApp = (AppSelectListPreference) findPreference(FP_GESTURE_SWIPE_LEFT_APP);
            mFPLeftSwipeApp.setEnabled(true);
            value = Settings.System.getString(getContext().getContentResolver(),
                    DEVICE_GESTURE_MAPPING_1);
            mFPLeftSwipeApp.setValue(value);
            mFPLeftSwipeApp.setOnPreferenceChangeListener(this);

            mFPRightSwipeApp = (AppSelectListPreference) findPreference(
                    FP_GESTURE_SWIPE_RIGHT_APP);
            mFPRightSwipeApp.setEnabled(true);
            value = Settings.System.getString(getContext().getContentResolver(),
                    DEVICE_GESTURE_MAPPING_2);
            mFPRightSwipeApp.setValue(value);
            mFPRightSwipeApp.setOnPreferenceChangeListener(this);

            mFPTapApp = (AppSelectListPreference) findPreference(FP_GESTURE_TAP_APP);
            mFPTapApp.setEnabled(true);
            value = Settings.System.getString(getContext().getContentResolver(),
                    DEVICE_GESTURE_MAPPING_3);
            mFPTapApp.setValue(value);
            mFPTapApp.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mFPLongPressApp) {
            String value = (String) newValue;
            Settings.System.putString(getContext().getContentResolver(),
                    DEVICE_GESTURE_MAPPING_0, value);
        } else if (preference == mFPLeftSwipeApp) {
            String value = (String) newValue;
            Settings.System.putString(getContext().getContentResolver(),
                    DEVICE_GESTURE_MAPPING_1, value);
        } else if (preference == mFPRightSwipeApp) {
            String value = (String) newValue;
            Settings.System.putString(getContext().getContentResolver(),
                    DEVICE_GESTURE_MAPPING_2, value);
        } else if (preference == mFPTapApp) {
            String value = (String) newValue;
            Settings.System.putString(getContext().getContentResolver(),
                    DEVICE_GESTURE_MAPPING_3, value);
        }
        return true;
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (!(preference instanceof AppSelectListPreference)) {
            super.onDisplayPreferenceDialog(preference);
            return;
        }
        DialogFragment fragment =
                AppSelectListPreference.AppSelectListPreferenceDialogFragment
                        .newInstance(preference.getKey());
        fragment.setTargetFragment(this, 0);
        fragment.show(getFragmentManager(), "dialog_preference");
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
