package com.sctdroid.autosigner.models;

import com.sctdroid.autosigner.domain.model.Model;
import com.sctdroid.autosigner.domain.model.UserModel;
import com.sina.weibo.sdk.openapi.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixindong on 6/22/16.
 */
public class FirendList {
    public List<Model> models;
    public int next_cursor;
    public int previous_cursor;
    public int total_number;

    public static FirendList parse(JSONObject jsonObject) {
        if (null == jsonObject) {
            return null;
        }

        FirendList list = new FirendList();

        list.next_cursor = jsonObject.optInt("next_cursor", 0);
        list.previous_cursor = jsonObject.optInt("previous_cursor", 0);
        list.total_number = jsonObject.optInt("total_number", 0);
        list.models = parseModel(jsonObject.optJSONArray("users"));

        return list;
    }

    public static FirendList parse(String s) {
        try {
            JSONObject object = new JSONObject(s);
            return parse(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Model> parseModel(JSONArray array) {
        List<Model> models = null;
        if (array != null && array.length() > 0) {
            models = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.optJSONObject(i);
                User user = User.parse(object);
                models.add(new UserModel(user));
            }
        }
        return models;
    }

    public static String optString(JSONObject object, String name, String defValue) {
        if (object.has(name)) {
            return object.optString(name);
        }
        return defValue;
    }
    public static int optInt(JSONObject object, String name, int defValue) {
        if (object.has(name)) {
            return object.optInt(name);
        }
        return defValue;
    }
}
