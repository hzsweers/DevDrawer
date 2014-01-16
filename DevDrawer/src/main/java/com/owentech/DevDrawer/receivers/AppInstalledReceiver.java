package com.owentech.DevDrawer.receivers;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.owentech.DevDrawer.R;
import com.owentech.DevDrawer.utils.AppWidgetUtil;
import com.owentech.DevDrawer.utils.Database;

/**
 * Created with IntelliJ IDEA.
 * User: owent
 * Date: 25/01/2013
 * Time: 19:33
 * To change this template use File | Settings | File Templates.
 */

public class AppInstalledReceiver extends BroadcastReceiver {

    Database database;
    public static String TAG = "DevDrawer-AppInstalledReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // New app has been installed, check and add to the database / widget
        String newPackage = intent.getData().getSchemeSpecificPart();

        database = new Database(context);
        database.createTables();

        if (database.getFiltersCount() != 0) {
            int[] appWidgetIds = AppWidgetUtil.findAppWidgetIds(context);
            for (int appWidgetId : appWidgetIds) {
                int match = database.parseAndMatch(newPackage, appWidgetId);
                if (match != Database.NOT_FOUND) {
                    Log.d(TAG, "Matches Filter");
                    database.addAppToDatabase(intent.getData().getSchemeSpecificPart(), Integer.toString(match), appWidgetId);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listView);
                    }
                } else {
                    Log.d(TAG, "Doesn't Match Filter");
                }
            }
        }
    }
}