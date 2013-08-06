package nl.askcs.alarm.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * Adapter for easily displaying fragments in combination with a ViewPager. Every Fragment is expected have at least
 * one argument with its title, using the key {@link #ARG_TAB_TITLE}. If the tab title is not available, an empty string
 * is returned.
 */
public class TabFragmentAdapter extends FragmentStatePagerAdapter {

    // The log tag.
    private final String TAG = getClass().getName();

    public static final String ARG_TAB_TITLE = "arg_tab_title";

    // A list of wrappers containing a Fragment and the tab-title
    // (as a String) corresponding to this fragment.
    private final List<Fragment> tabFragments;

    public TabFragmentAdapter(FragmentManager fragmentManager, Fragment... fragments) {

        super(fragmentManager);
        tabFragments = Arrays.asList(fragments);
    }

    @Override
    public Fragment getItem(int position) {

        int index = position % tabFragments.size();
        return tabFragments.get(index);
    }

    /**
     * Every Fragment is expected have at least
     * one argument with its title, using the key {@link #ARG_TAB_TITLE}. If the tab title is not available, an empty string
     * is returned.
     * @param position Position of the Fragment in the adapter
     * @return The title of this fragment
     */
    @Override
    public CharSequence getPageTitle(int position) {

        int index = position % tabFragments.size();
        Fragment fragment = tabFragments.get(index);

        return fragment.getArguments().getString(ARG_TAB_TITLE, "").toUpperCase();
    }

    @Override
    public int getCount() {
        return tabFragments.size();
    }
}