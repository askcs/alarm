package nl.askcs.alarm.ui.fragments;

import com.actionbarsherlock.app.SherlockFragment;
import nl.askcs.alarm.ui.ITabFragment;

/**
 * A fragment extending from SherlockFragment that can return a
 * String denoting the title of the tab it is associated to.
 */
public abstract class BaseTabFragment extends SherlockFragment implements ITabFragment {

    /**
     * Returns the title of the tab it is associated to.
     *
     * @return the title of the tab it is associated to.
     */
    public final String getTabTitle() {
        return getArguments().getString(ARG_TAB_TITLE).toUpperCase();
    }
}