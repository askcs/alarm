package nl.askcs.alarm.ui.fragments;

import android.os.Bundle;
import nl.askcs.alarm.models.Alarm;
import nl.askcs.alarm.ui.adapters.SettingsAdapter;
import nl.askcs.alarm.ui.view.HeaderListItem;
import nl.askcs.alarm.ui.view.IItem;
import nl.askcs.alarm.ui.view.ListItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends BaseTabListFragment {

    private SettingsAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayList<IItem> list = new ArrayList<IItem>();

        list.add(new HeaderListItem("Gebruikergegevens"));
        list.add(new ListItem("Ingelogd als Leon Joosse"));
        list.add(new ListItem("Klik om uit te loggen"));
        list.add(new ListItem("PIN code"));

        list.add(new HeaderListItem("Alarmen"));

        List<Alarm> alarms = null;
        try {
            alarms = getSherlockActivity().getDao(Alarm.class, Integer.class).queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(alarms != null) {
            for(Alarm alarm : alarms) {
                list.add(new ListItem(alarm.getTitle(), "Klik om te wijzigen"));
            }
        }

        list.add(new ListItem("Alarm toevoegen..."));


        adapter = new SettingsAdapter(getActivity(), list);
        setListAdapter(adapter);
    }
}
