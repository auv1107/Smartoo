package com.sctdroid.autosigner.presentation.ui.items;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sctdroid.autosigner.R;
import com.sctdroid.autosigner.presentation.ui.base.BaseItemView;
import com.sctdroid.autosigner.utils.GlideCircleTransform;
import com.sina.weibo.sdk.openapi.models.User;

/**
 * Created by lixindong on 6/22/16.
 */
public class FriendItemView extends BaseItemView {
    public FriendItemView(Context context) {
        super(context);
    }

    @Override
    public void init() {
        super.init();
        inflate(getContext(), R.layout.item_friend, this);
        avatar = findViewByID(R.id.avatar);
        name = findViewByID(R.id.name);
        latest_msg = findViewByID(R.id.latest_msg);
        source = findViewByID(R.id.source);
        status = findViewByID(R.id.status);
    }

    ImageView avatar;
    TextView name;
    TextView latest_msg;
    TextView source;
    TextView status;

    @Override
    public void bind(Object object) {
        if (object instanceof User) {
            bind((User) object);
        }
    }

    public void bind(User user) {
        if (user == null) return;

        Glide.with(getContext())
                .load(user.avatar_large)
                .transform(new GlideCircleTransform(getContext()))
                .into(avatar);
        name.setText(user.name);
        if (user.status != null) {
            latest_msg.setText(user.status.text);
        }
        if (!TextUtils.isEmpty(user.remark)) {
            source.setVisibility(VISIBLE);
            source.setText(user.remark);
        } else {
            source.setVisibility(GONE);
        }

        status.setVisibility(GONE);
    }
}
