package com.sctdroid.autosigner.fragments;

import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.sctdroid.autosigner.R;
import com.sctdroid.autosigner.databases.RecordHelper;
import com.sctdroid.autosigner.models.Record;
import com.sctdroid.autosigner.utils.Constants;
import com.sctdroid.autosigner.views.adapter.StaticAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.IgnoreWhen;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by lixindong on 1/19/16.
 */
@EFragment(R.layout.fragment_static)
public class StaticFragment extends Fragment {
    private static final String TAG = StaticFragment.class.getSimpleName();

    @ViewById(R.id.listview)
    ListView listView;

    @Bean(StaticAdapter.class)
    StaticAdapter adapter;

    @ViewById(R.id.switcher)
    CheckBox switcher;

    @AfterViews
    void init() {
        readDatabase();
        listView.setAdapter(adapter);
        boolean checked = getActivity().getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE).getBoolean(Constants.PREF_KEY_AUTO_RECORD_CHECKED, false);
        locate();
        if (checked && !getLocationClient().isStarted()) {
            openGeoFenceAlert("0");
        } else if (!checked && getLocationClient().isStarted()) {
            removeGeoFenceAlert("0");
        }
        Toast.makeText(getActivity(), getLocationClient().isStarted() ? "started" : "not started", Toast.LENGTH_LONG).show();
        switcher.setChecked(checked);
        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                SharedPreferences preferences = getActivity().getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
                preferences.edit().putBoolean(Constants.PREF_KEY_AUTO_RECORD_CHECKED, checked).apply();
                if (checked) {
                    openGeoFenceAlert("0");
                } else {
                    removeGeoFenceAlert("0");
                }
            }
        });
    }

    private void removeGeoFenceAlert(String s) {
        getLocationClient().removeGeoFenceAlert(getPendingIntent(), s);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (mlocationClient != null) {
//            mlocationClient.onDestroy();
//        }
    }

    @Background
    @IgnoreWhen(IgnoreWhen.State.DETACHED)
    void addSimulateData() {
        int count = 11;
        for (int i = 0; i < count; i++) {
            RecordHelper.insert(getActivity(), new Record(""+(System.currentTimeMillis()-5*60*60*1000+i*23*60*1000), i % 2 == 0 ? Record.TYPE_ENTER : Record.TYPE_EXIT));
        }
    }

    @Click(R.id.title)
    void titleClicked() {
        addSimulateData();
        readDatabase();
    }

    @Background
    void readDatabase() {
        List<Record> list = RecordHelper.query(getActivity());
        if (list == null || list.size() == 0) {
            Log.d(TAG, "nothing in databases");
            return;
        }
        adapter.update(list);
        for (int i = 0; i < list.size(); i++) {
            Log.d(TAG, list.get(i).getId() + " " + list.get(i).getBehavior_type() + " " + list.get(i).getTimestamp());
        }
    }

    static AMapLocationClient mlocationClient = null;

    static PendingIntent mPendingIntent = null;

    private PendingIntent getPendingIntent() {
        if (mPendingIntent == null) {
            //声明对应的intent对象
            Intent intent = new Intent(Constants.GEOFENCE_BROADCAST_ACTION);
            //创建PendingIntent对象
            mPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
        }
        return mPendingIntent;
    }

    public AMapLocationClient getLocationClient() {
        //实例化定位客户端
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(getActivity());
        }
        return mlocationClient;
    }

    void openGeoFenceAlert(String fenceId) {
        //添加地理围栏
        getLocationClient().addGeoFenceAlert(fenceId, 40.007021,116.483853, 50, -1, getPendingIntent());

        //启动定位
        getLocationClient().startLocation();
    }

    void locate() {
        //声明mLocationOption对象
        AMapLocationClientOption mLocationOption = null;
//初始化定位参数
        mLocationOption = new AMapLocationClientOption();
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
//设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
//设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
//设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
//设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
//给定位客户端对象设置定位参数
        getLocationClient().setLocationOption(mLocationOption);
        //设置定位回调监听
        getLocationClient().setLocationListener(listener);
//启动定位
        getLocationClient().startLocation();
        Log.d(TAG, "start location");
    }

    AMapLocationListener listener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    StringBuilder builder = new StringBuilder();
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    builder.append(amapLocation.getLatitude());//获取纬度
                    builder.append(" ");
                    builder.append(amapLocation.getLongitude());//获取经度
                    builder.append(" ");
                    amapLocation.getAccuracy();//获取精度信息
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    df.format(date);//定位时间
                    amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    amapLocation.getCountry();//国家信息
                    amapLocation.getProvince();//省信息
                    amapLocation.getCity();//城市信息
                    amapLocation.getDistrict();//城区信息
                    amapLocation.getStreet();//街道信息
                    amapLocation.getStreetNum();//街道门牌号信息
                    amapLocation.getCityCode();//城市编码
                    amapLocation.getAdCode();//地区编码
                    Log.d(TAG, builder.toString());
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError","location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };
}
