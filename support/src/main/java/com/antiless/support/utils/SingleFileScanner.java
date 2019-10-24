
package com.antiless.support.utils;

import java.io.File;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

public class SingleFileScanner implements MediaScannerConnectionClient {

    private MediaScannerConnection mConn;
    private String mScanFile;

    public SingleFileScanner(Context context) {
        mConn = new MediaScannerConnection(context.getApplicationContext(), this);
    }

    @Override
    public void onMediaScannerConnected() {
        mConn.scanFile(this.mScanFile, null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        mConn.disconnect();
    }

    public void scan(String path) {
        mScanFile = path;
        mConn.connect();
    }

    public void scan(File file) {
        if (file == null || !file.exists()) {
            throw new RuntimeException("SingleFileScanner Error: file is null or file NOT exists");
        }
        this.scan(file.getAbsolutePath());
    }
}