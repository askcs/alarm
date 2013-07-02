package nl.askcs.alarm.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import nl.askcs.alarm.ui.view.IItem;

import java.util.List;

public class SettingsAdapter extends ArrayAdapter<IItem> {
    private LayoutInflater layoutInflater;

    public enum RowType {
        LIST_ITEM,
        HEADER_ITEM
    }

    public SettingsAdapter(Context context, List<IItem> items) {
        super(context, 0, items);
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(layoutInflater, convertView);
    }
}
