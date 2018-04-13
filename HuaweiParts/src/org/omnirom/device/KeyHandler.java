/*
* Copyright (C) 2016 The OmniROM Project
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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.HapticFeedbackConstants;
import android.view.WindowManagerPolicy;

import com.android.internal.os.DeviceKeyHandler;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.omni.OmniUtils;

public class KeyHandler implements DeviceKeyHandler {

    private static final String TAG = "KeyHandler";
    private static final boolean DEBUG = true;

    private static final int GESTURE_WAKELOCK_DURATION = 2000;

    private static final int FP_GESTURE_LONG_PRESS = 28;
    private static final int FP_GESTURE_SWIPE_LEFT = 105;
    private static final int FP_GESTURE_SWIPE_RIGHT = 106;
    private static final int FP_GESTURE_TAP = 174;

    private static final int[] sSupportedGestures = new int[]{
        FP_GESTURE_LONG_PRESS,
        FP_GESTURE_SWIPE_LEFT,
        FP_GESTURE_SWIPE_RIGHT,
        FP_GESTURE_TAP
    };

    protected final Context mContext;
    private EventHandler mEventHandler;
    private Handler mHandler = new Handler();
    private boolean mFPcheck;
    private boolean mDispOn;
    private WindowManagerPolicy mPolicy;
    private boolean isFpgesture;

    private BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() {
         @Override
         public void onReceive(Context context, Intent intent) {
             if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                 mDispOn = true;
                 onDisplayOn();
             } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                 mDispOn = false;
                 onDisplayOff();
             }
         }
    };

    public KeyHandler(Context context) {
        mContext = context;
        mDispOn = true;
        mEventHandler = new EventHandler();
        IntentFilter screenStateFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mContext.registerReceiver(mScreenStateReceiver, screenStateFilter);
    }

    private class EventHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
        }
    }

    @Override
    public boolean handleKeyEvent(KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_UP) {
            return false;
        }
        isFpgesture = false;

        if (DEBUG) Log.i(TAG, "nav_code=" + event.getScanCode());
        int fpcode = event.getScanCode();
        mFPcheck = canHandleKeyEvent(event);
        String value = getGestureValueForFPScanCode(fpcode);
        if (mFPcheck && mDispOn && !TextUtils.isEmpty(value) && !value.equals(
                AppSelectListPreference.DISABLED_ENTRY)){
            isFpgesture = true;
            if (!launchSpecialActions(value)) {
                    vibe();
                    Intent intent = createIntent(value);
                    if (DEBUG) Log.i(TAG, "intent = " + intent);
                    mContext.startActivity(intent);
            }
        }
        return isFpgesture;
    }

    @Override
    public boolean canHandleKeyEvent(KeyEvent event) {
        return ArrayUtils.contains(sSupportedGestures, event.getScanCode());
    }

    @Override
    public boolean isDisabledKeyEvent(KeyEvent event) {
        return false;
    }

    @Override
    public boolean isWakeEvent(KeyEvent event){
        return false;
    }

    @Override
    public Intent isActivityLaunchEvent(KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_UP) {
            return null;
        }
        String value = getGestureValueForFPScanCode(event.getScanCode());
        if (!TextUtils.isEmpty(value) && !value.equals(AppSelectListPreference.DISABLED_ENTRY)) {
            if (DEBUG) Log.i(TAG, "isActivityLaunchEvent " + event.getScanCode() + value);
            if (!launchSpecialActions(value)) {
                vibe();
                Intent intent = createIntent(value);
                return intent;
            }
        }
        return null;
    }

    private void onDisplayOn() {
        if (DEBUG) Log.i(TAG, "Display on");
    }

    private void onDisplayOff() {
        if (DEBUG) Log.i(TAG, "Display off");
    }

    private Intent createIntent(String value) {
        ComponentName componentName = ComponentName.unflattenFromString(value);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.setComponent(componentName);
        return intent;
    }

    private boolean launchSpecialActions(String value) {
        if (value.equals(AppSelectListPreference.NAVIGATE_BACK_ENTRY)) {
            vibe();
            OmniUtils.sendKeycode(KeyEvent.KEYCODE_BACK);
            return true;
        } else if (value.equals(AppSelectListPreference.NAVIGATE_HOME_ENTRY)) {
            vibe();
            OmniUtils.sendKeycode(KeyEvent.KEYCODE_HOME);
            return true;
        } else if (value.equals(AppSelectListPreference.NAVIGATE_RECENT_ENTRY)) {
            vibe();
            OmniUtils.sendKeycode(KeyEvent.KEYCODE_APP_SWITCH);
            return true;
        }
        return false;
    }

    private String getGestureValueForFPScanCode(int scanCode) {
        switch(scanCode) {
            case FP_GESTURE_LONG_PRESS:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_0, UserHandle.USER_CURRENT);
            case FP_GESTURE_SWIPE_LEFT:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_1, UserHandle.USER_CURRENT);
            case FP_GESTURE_SWIPE_RIGHT:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_2, UserHandle.USER_CURRENT);
            case FP_GESTURE_TAP:
                return Settings.System.getStringForUser(mContext.getContentResolver(),
                    GestureSettings.DEVICE_GESTURE_MAPPING_3, UserHandle.USER_CURRENT);
        }
        return null;
    }

    @Override
    public void setWindowManagerPolicy(WindowManagerPolicy policy) {
        mPolicy = policy;
    }

    private void vibe(){
        if (isFpgesture && mPolicy != null) {
            mPolicy.performHapticFeedbackLw(null, HapticFeedbackConstants.LONG_PRESS, false);
        }
    }
}
