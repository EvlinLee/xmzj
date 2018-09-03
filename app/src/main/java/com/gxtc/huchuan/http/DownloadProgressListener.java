package com.gxtc.huchuan.http;

import cn.edu.zafu.coreprogress.listener.ProgressListener;

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/2/9.
 */

public abstract class DownloadProgressListener implements ProgressListener {

    private String url;

    public DownloadProgressListener(String url) {
        this.url = url;
    }

    @Override
    public void onProgress(long currentBytes, long contentLength, boolean done) {
        onProgress(url, currentBytes, contentLength, done);
    }

    abstract public void onProgress(String url, long currentBytes, long contentLength, boolean done);

}
