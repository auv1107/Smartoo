package com.antiless.support.fragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.antiless.support.BaseFragment;
import com.antiless.support.R;
import com.antiless.support.widget.tablayout.TabLayout;

import java.util.List;

/**
 * Created by lixindong on 2018/8/11.
 */

public abstract class TabFragment extends BaseFragment {

    protected TabLayout mTabLayout;
    protected ViewPager mViewPager;
    protected TextView mEmptyView;
    protected FragmentStatePagerAdapter mAdapter;

    protected int mLastSelectedItem = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container) {
        View root = inflater.inflate(getLayoutId(), container, false);
        return root;
    }

    protected int getLayoutId() {
        return R.layout.layout_tabbed;
    }

    @Override
    public void onViewCreated(View view) {
        findViews(view);
        initViews(view);
    }

    protected void findViews(View root) {
        mTabLayout = root.findViewById(R.id.tabLayout);
        mViewPager = root.findViewById(R.id.viewPager);
        mEmptyView = root.findViewById(R.id.emptyView);
    }

    protected void initViews(View root) {
        initViewPager();
        initTabLayout();
    }

    private SparseArray<Fragment> mCacheFragment = new SparseArray<>();

    protected void initViewPager() {
        mAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public int getCount() {
                return getFragmentCount();
            }

            @Override
            public Fragment getItem(int position) {
                int uniqueId = getUniqueId(position);
                if (mCacheFragment.get(uniqueId) == null) {
                    mCacheFragment.append(uniqueId, getFragmentList(position));
                }
                return mCacheFragment.get(uniqueId);
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return getFragmentTitle(position);
            }
        };
        mViewPager.setAdapter(mAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                TabFragment.this.onPageSelected(position);
            }
        });
        mLastSelectedItem = mViewPager.getCurrentItem();
    }

    public void clearCache() {
        mCacheFragment.clear();
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        List<Fragment> fragments = fm.getFragments();
        if(fragments != null && fragments.size() > 0){
            for (int i = 0; i < fragments.size(); i++) {
                ft.remove(fragments.get(i));
            }
        }
        ft.commit();
    }

    protected void onPageSelected(int position) {}

    protected void initTabLayout() {
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public Fragment getCurrentFragment() {
        return mCacheFragment.get(mViewPager.getCurrentItem());
    }

    public abstract Fragment getFragmentList(int position);
    public abstract String getFragmentTitle(int position);
    public abstract int getUniqueId(int position);
    public abstract int getFragmentCount();
}
