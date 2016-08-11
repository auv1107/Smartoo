package com.sctdroid.pullToRefresh;


public interface IHeaderBaseView {
    public void setHeaderPadding(int top);
    public int getHeaderTop();
    public int getHeaderHeight();
    public int getScrollDistance();
    public boolean scrollToTop();
    public void setRefreshHeaderListener(RefreshHeaderView.RefreshHeaderListener listener);
}
