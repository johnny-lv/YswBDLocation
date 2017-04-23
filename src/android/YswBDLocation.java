package com.yaoshangwang.yswapp.plugins.locaiton;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;


public class YswBDLocation extends CordovaPlugin {

    public static CallbackContext cbCtx = null;

    public LocationClient dbLocationClient = null;

    public BDLocationListener dbListener = new BDLocationListener() {

        @Override
        public void onConnectHotSpotMessage(String var1, int var2) {

        }

        @Override
        public void onReceiveLocation(BDLocation location) {
            try {
                JSONObject json = new JSONObject();

                json.put("time", location.getTime());
                json.put("locType", location.getLocType());
                json.put("latitude", location.getLatitude());
                json.put("longitude", location.getLongitude());
                json.put("radius", location.getRadius());

                if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
                    json.put("speed", location.getSpeed());
                    json.put("satellite", location.getSatelliteNumber());
                    json.put("height", location.getAltitude());
                    json.put("direction", location.getDirection());
                    json.put("addr", location.getAddrStr());
                    json.put("describe", "gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
                    json.put("addr", location.getAddrStr());
                    json.put("operationers", location.getOperators());
                    json.put("describe", "网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    json.put("describe", "离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    json.put("describe", "服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    json.put("describe", "网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    json.put("describe", "无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, json);
                pluginResult.setKeepCallback(true);
                cbCtx.sendPluginResult(pluginResult);
            } catch (JSONException e) {
                String errMsg = e.getMessage();
                PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, errMsg);
                pluginResult.setKeepCallback(true);
                cbCtx.sendPluginResult(pluginResult);
            } finally {
                dbLocationClient.stop();
            }
        }
    };

    @Override
    public boolean execute(String action, final JSONArray args, CallbackContext callbackContext) throws JSONException {
        boolean ret = false;

        if ("getCurrentPosition".equalsIgnoreCase(action)) {
            cbCtx = callbackContext;

            PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
            pluginResult.setKeepCallback(true);
            cbCtx.sendPluginResult(pluginResult);

            if (dbLocationClient == null) {
                dbLocationClient = new LocationClient(this.webView.getContext());
                dbLocationClient.registerLocationListener(dbListener);

                LocationClientOption option = new LocationClientOption();
                option.setLocationMode(LocationMode.Hight_Accuracy);
                // default: gcj02
                option.setCoorType("bd09ll");
                option.setIsNeedAddress(true);
                option.setOpenGps(true);
                option.setLocationNotify(false);
                option.setIsNeedLocationDescribe(true);
                option.setIsNeedLocationPoiList(true);
                dbLocationClient.setLocOption(option);
            }

            dbLocationClient.start();
            ret = true;
        }

        return ret;
    }
}
