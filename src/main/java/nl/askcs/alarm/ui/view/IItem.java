package nl.askcs.alarm.ui.view;

import android.view.LayoutInflater;
import android.view.View;

public interface IItem {
    public int getViewType();
    public View getView(LayoutInflater inflater, View convertView);
}