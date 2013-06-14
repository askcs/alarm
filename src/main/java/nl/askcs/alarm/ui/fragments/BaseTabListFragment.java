package nl.askcs.alarm.ui.fragments;

import com.actionbarsherlock.app.SherlockListFragment;
import nl.askcs.alarm.ui.ITabFragment;

public class BaseTabListFragment extends SherlockListFragment implements ITabFragment {
    /**

     * Returns the title of the tab it is associated to.
     *
     * @return the title of the tab it is associated to.
     */
    public final String getTabTitle() {
        return getArguments().getString(ARG_TAB_TITLE).toUpperCase();
    }
}
