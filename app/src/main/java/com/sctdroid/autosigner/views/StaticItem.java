package com.sctdroid.autosigner.views;

import android.content.Context;
import android.text.format.DateUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sctdroid.autosigner.R;
import com.sctdroid.autosigner.models.Record;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

/**
 * Created by lixindong on 1/19/16.
 */
@EViewGroup(R.layout.item_static)
public class StaticItem extends RelativeLayout{
    @ViewById(R.id.id)
    TextView id;

    @ViewById(R.id.type)
    TextView type;

    @ViewById(R.id.timestamp)
    TextView timestamp;

    public StaticItem(Context context) {
        super(context);
    }

    @UiThread
    public void bind(Record record) {
        if (record == null)
            return;
        id.setText(record.getId());
        type.setText(record.getBehavior_type());
        timestamp.setText(DateUtils.getRelativeTimeSpanString(Long.valueOf(record.getTimestamp())));
    }

    public void reset() {
        id.setText("0");
        type.setText("0");
        timestamp.setText("0");
    }
}
