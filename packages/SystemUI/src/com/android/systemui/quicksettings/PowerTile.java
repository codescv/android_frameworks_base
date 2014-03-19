package com.android.systemui.quicksettings;

import android.content.Context;
import android.os.PowerManager;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import com.android.systemui.R;
import com.android.systemui.statusbar.phone.QuickSettingsController;
import com.android.systemui.statusbar.phone.QuickSettingsContainerView;

import java.io.DataOutputStream;

public class PowerTile extends QuickSettingsTile {

    private PowerManager pm;

    public PowerTile(Context context, QuickSettingsController qsc) {
        super(context, qsc);
        pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mOnClick = new OnClickListener() {
            @Override
            public void onClick(View v) {
                pm.goToSleep(SystemClock.uptimeMillis());
            }
        };
        mOnLongClick = new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                new Thread(new Runnable() {
                    public void run() {
                        showPowerMenu();
                    }
                }).start();

                return true;
            }
        };
    }

    // Simulate power button long press
    private void showPowerMenu() {
        try {
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());
            System.out.println("start send!");

            outputStream.writeBytes("sendevent /dev/input/event0 0001 116 1\n");
            outputStream.flush();
            System.out.println("sent!");

            outputStream.writeBytes("sendevent /dev/input/event0 0000 0000 00000000\n");
            outputStream.flush();
            outputStream.writeBytes("sleep 2\n");
            outputStream.flush();
            outputStream.writeBytes("sendevent /dev/input/event0 0001 116 00000000\n");
            outputStream.flush();
            outputStream.writeBytes("sendevent /dev/input/event0 0000 0000 00000000\n");
            outputStream.flush();
            outputStream.writeBytes("exit\n");
            outputStream.flush();

            System.out.println("exit!");

            su.waitFor();
        } catch(Exception e){
            System.out.println("exception!");
            e.printStackTrace();
        }
    }



    @Override
    void onPostCreate() {
        updateTile();
        super.onPostCreate();
    }

    @Override
    public void updateResources() {
        updateTile();
        super.updateResources();
    }

    private synchronized void updateTile() {
        mDrawable = R.drawable.ic_qs_sleep;
        mLabel = mContext.getString(R.string.quick_settings_screen_sleep);
    }

}
