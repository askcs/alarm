package nl.askcs.alarm.ui.view;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import nl.askcs.alarm.R;
import nl.askcs.alarm.ui.adapters.SettingsAdapter;

public class HeaderListItem implements IItem {

    private final String title, summary;

    public HeaderListItem(String title, String summary) {
        this.title = title;
        this.summary = summary;
    }

    public HeaderListItem(String title) {
        this(title, null);
    }

    @Override
    public int getViewType() {
        return SettingsAdapter.RowType.HEADER_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {

        ViewHolder holder;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.settings_header, null);
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