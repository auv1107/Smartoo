package com.sctdroid.autosigner.views;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sctdroid.autosigner.R;
import com.sctdroid.autosigner.presentation.ui.activities.StatusActivity;
import com.sctdroid.autosigner.utils.FilenameUtils;
import com.sctdroid.autosigner.utils.GlideCircleTransform;
import com.sina.weibo.sdk.openapi.models.Status;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixindong on 1/26/16.
 */
@EViewGroup(R.layout.item_post)
public class StatusItem extends RelativeLayout implements View.OnClickListener {
    private static final String TAG = StatusItem.class.getSimpleName();
    @ViewById(R.id.avatar)
    ImageView avatar;

    @ViewById(R.id.name)
    TextView name;

    @ViewById(R.id.timestamp)
    TextView timestamp;

    @ViewById(R.id.content)
    TextView content;

    @ViewById(R.id.reposts)
    TextView reposts;

    @ViewById(R.id.comments)
    TextView comments;

    @ViewById(R.id.attitudes)
    TextView attitudes;

    @ViewsById({R.id.pic0, R.id.pic1, R.id.pic2, R.id.pic3, R.id.pic4, R.id.pic5, R.id.pic6, R.id.pic7, R.id.pic8})
    List<ImageView> pics;

    public StatusItem(Context context) {
        super(context);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
//        getContext().startActivity(new Intent(getContext(), StatusActivity.class));
    }

    public interface OnImageClickListener {
        void onImageClicked(int position, ArrayList<String> urls);
    }

    private OnImageClickListener mListener;

    public void setOnImageClickListener(OnImageClickListener listener) {
        mListener = listener;
    }

    @UiThread
    public void bind(Status status) {
        if (status == null) return;
        Log.d(TAG, "user " + status.user.name);
        if (!TextUtils.isEmpty(status.user.avatar_large)) {
            Log.d(TAG, "avatar " + status.user.avatar_large);
            Glide.with(getContext())
                    .load(status.user.avatar_large)
                    .transform(new GlideCircleTransform(getContext()))
                    .into(avatar);
        }
        name.setText(status.user.name);
        timestamp.setText(DateUtils.getRelativeTimeSpanString(Date.parse(status.created_at)));
        content.setText(status.text);

        // counts
        reposts.setText(getContext().getResources().getString(R.string.reposts) + " " + status.reposts_count);
        comments.setText(getContext().getResources().getString(R.string.comments) + " " + status.comments_count);
//        attitudes.setText(getContext().getResources().getString(R.string.attitudes) + " " + status.attitudes_count);

        String middle_prefix = FilenameUtils.getPath(status.bmiddle_pic);
        String origin_prefix = FilenameUtils.getPath(status.original_pic);
        final ArrayList<String> origin_image_urls = getOriginImageUrls(origin_prefix, status.pic_urls);
        int i = 0;
        for (; i < 9; i++) {
            if (status.pic_urls == null || i >= status.pic_urls.size()) {
                pics.get(i).setVisibility(GONE);
                pics.get(i).setOnClickListener(null);
            } else {
                Glide.with(getContext())
                        .load(middle_prefix + "/" + FilenameUtils.getBasename(status.pic_urls.get(i)))
                        .centerCrop()
                        .into(pics.get(i));
                pics.get(i).setVisibility(VISIBLE);
                final int position = i;
                pics.get(i).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mListener != null) {
                            mListener.onImageClicked(position, origin_image_urls);
                        }
                    }
                });
                Log.d(TAG, "pic " + i + " " + middle_prefix + "/" + FilenameUtils.getBasename(status.pic_urls.get(i)));
            }
        }
    }

    private ArrayList<String> getOriginImageUrls(String origin_prefix, ArrayList<String> pic_urls) {
        ArrayList<String> urls = new ArrayList<>();
        if (pic_urls == null) return urls;

        for (String url : pic_urls) {
            urls.add(origin_prefix + "/" + FilenameUtils.getBasename(url));
        }
        return urls;
    }
}
