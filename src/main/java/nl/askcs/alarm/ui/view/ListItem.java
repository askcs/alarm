package nl.askcs.alarm.ui.view;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import nl.askcs.alarm.R;
import nl.askcs.alarm.ui.adapters.SettingsAdapter;

public class ListItem implements IItem {

    private final String title, summary;

    public ListItem(String title) {
        this(title, null);
    }

    public ListItem(String title, String summary) {
        this.title = title;
        this.summary = summary;
    }

    @Override
    public int getViewType() {
        return SettingsAdapter.RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {

        ViewHolder holder;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.settings_list_item, null);
            holder = new ViewHolder();

            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.summary = (TextView) convertView.findViewById(R.id.summary);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(title);

        if(summary != null)
            holder.summary.setText(summary);

        return convertView;
    }

    static class ViewHolder {
        public TextView title, summary;
    }
}