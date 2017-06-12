package com.chilindo.weathermap.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ChilindoSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static ChilindoSyncAdapter chilindoSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("ChilindoSyncService", "onCreate - ChilindoSyncService");
        synchronized (sSyncAdapterLock) {
            if (chilindoSyncAdapter == null) {
                chilindoSyncAdapter = new ChilindoSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return chilindoSyncAdapter.getSyncAdapterBinder();
    }
}