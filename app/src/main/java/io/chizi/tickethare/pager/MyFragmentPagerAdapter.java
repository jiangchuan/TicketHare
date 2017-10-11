package io.chizi.tickethare.pager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import io.chizi.tickethare.acquire.AcquireFragment;
import io.chizi.tickethare.database.DatabaseFragment;
import io.chizi.tickethare.loadmap.LoadmapFragment;

/**
 * Created by Jiangchuan on 5/21/17.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    // Holds tab titles
    private String tabTitles[] = new String[]{"生成罚单", "查看罚单", "地图下载"};
    private Context context;

    public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    // Return the correct Fragment based on index
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new AcquireFragment();
        } else if (position == 1) {
            return new DatabaseFragment();
        } else if (position == 2) {
            return new LoadmapFragment();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Return the tab title to SlidingTabLayout
        return tabTitles[position];
    }
}
