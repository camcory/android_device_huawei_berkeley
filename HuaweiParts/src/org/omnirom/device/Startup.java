/*
* Copyright (C) 2018 The OmniROM Project
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;

public class Startup extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent bootintent) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SystemProperties.set(DeviceSettings.FPNAV_ENABLED_PROP, sharedPrefs.getBoolean(
                DeviceSettings.KEY_FP_GESTURES, false) ? "1" : "0");
        Utils.writeValue(DeviceSettings.HIGH_TOUCH_MODE, sharedPrefs.getBoolean(
                DeviceSettings.KEY_HIGH_TOUCH, false) ? "1" : "0");
    }
}
