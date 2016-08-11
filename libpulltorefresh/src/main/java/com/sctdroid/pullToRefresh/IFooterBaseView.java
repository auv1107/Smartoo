package com.sctdroid.pullToRefresh;

/**
 * 创建人： limeng
 * 日期： 8/20/15
 */
public interface IFooterBaseView {
    public void setFooterPadding(int bottom);
    public int getFooterBottom();
    public int getFooterHeight();
    public boolean scrollToBottom();
    public void setRefreshFooterListener(RefreshFooterView.RefreshFooterListener listener);
}
