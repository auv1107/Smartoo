package com.sctdroid.autosigner.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.sctdroid.autosigner.R;
import com.sctdroid.autosigner.activities.MainActivity_;
import com.sctdroid.autosigner.databases.RecordHelper;
import com.sctdroid.autosigner.models.Record;
import com.sctdroid.autosigner.utils.Constants;

/**
 * Created by lixindong on 1/19/16.
 */
public class GeoFenceReceiver extends BroadcastReceiver {
    public static final String TAG = GeoFenceReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences preferences = context.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        boolean checked = preferences.getBoolean(Constants.PREF_KEY_AUTO_RECORD_CHECKED, false);
        if (checked) {
            if (Constants.GEOFENCE_BROADCAST_ACTION.equals(intent.getAction())) {
                Log.d(TAG, "geofence broadcast received");
                // 接收广播内容，处理进出的具体操作。
                Bundle bundle = intent.getExtras();
                if (bundle.containsKey("status")) {
                    Log.d(TAG, "status " + bundle.getString("status"));
                    String status = bundle.getString("status");
                    String type = "1".equals(status) ? Record.TYPE_ENTER : Record.TYPE_EXIT;
                    if ("1".equals(status)) {
                        showNotification(context, R.string.nitification_content_enter, R.string.nitification_tricker_enter);
                    } else {
                        showNotification(context, R.string.nitification_content_exit, R.string.nitification_tricker_exit);
                    }
                    RecordHelper.insert(context, new Record("" + System.currentTimeMillis(), type));
                } else {
                    Log.d(TAG, "not found status");
                }
            } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                Log.d(TAG, "connectivity changed");
            }
        } else {
            Log.d(TAG, "no need to deal with this action");
        }
    }

    public void showNotification(Context context, int type, int tricker) {
        showNotification(context, context.getString(type), context.getString(tricker));
    }

    public void showNotification(Context context, String type, String tricker) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("位置更新")//设置通知栏标题
                .setContentText(type)
                .setContentIntent(getDefalutIntent(context, Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
//	.setNumber(number) //设置通知集合的数量
                .setTicker(tricker) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
//	.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                        //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON
        mNotificationManager.notify(1, mBuilder.build());
    }

    public PendingIntent getDefalutIntent(Context context, int flags){
        PendingIntent pendingIntent= PendingIntent.getActivity(context, 1, new Intent(context, MainActivity_.class), flags);
        return pendingIntent;
    }
}
