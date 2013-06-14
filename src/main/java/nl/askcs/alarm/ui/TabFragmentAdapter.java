package nl.askcs.alarm.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.Arrays;
import java.util.List;

public class TabFragmentAdapter extends FragmentStatePagerAdapter {

    // The log tag.
    private final String TAG = getClass().getName();

    // A list of wrappers containing a Fragment and the tab-title
    // (as a String) corresponding to this fragment.
    private final List<? extends Fragment> tabFragments;

    public TabFragmentAdapter(FragmentManager fragmentManager, Fragment... fragments) {

        super(fragmentManager);

        tabFragments = Arrays.asList(fragments);
    }

    @Override
    public Fragment getItem(int position) {

        int index = position % tabFragments.size();
        return tabFragments.get(index);
    }

    @Override
    public CharSequence getPageTitle(int position) {

        int index = position % tabFragments.size();
        Fragment fragment = tabFragments.get(index);

        return fragment instanceof ITabFragment ? ((ITabFragment) fragment).getTabTitle().toUpperCase() : "";
    }

    @Override
    public int getCount() {
        return tabFragments.size();
    }
}