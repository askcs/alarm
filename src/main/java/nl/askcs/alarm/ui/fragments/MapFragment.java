package nl.askcs.alarm.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import nl.askcs.alarm.R;

import java.io.IOException;
import java.net.URL;

import static nl.askcs.alarm.ui.adapters.TabFragmentAdapter.ARG_TAB_TITLE;

/**
 * Displays a map with the current location of the user. The map should be able to show positions of Helpers in the
 * Alarm mode. Currently, a map is shown using the Google Maps Static API, using the address of the ASK HQ.
 */
public class MapFragment extends BaseTabFragment {

    private ImageView map;
    private LinearLayout ll;
    private int height = 0, width = 0;

    public static BaseTabFragment getInstance(Context context) {
        BaseTabFragment fragment = new MapFragment();

        // Set the tab title
        Bundle args = new Bundle(1);
        args.putString(ARG_TAB_TITLE, context.getString(R.string.frag_alarm_maps_title));
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_map, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        map = (ImageView) view.findViewById(R.id.map);
        ll = (LinearLayout) view.findViewById(R.id.container);
        height = 1024;
        width = 1024;
        new DownloadTask().execute();
    }

    class DownloadTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {

            try {
                URL url = new URL("http://maps.google.com/maps/api/staticmap?center=Coolhaven%20236,%20Rotterdam&zoom=16&size=640x640&maptype=roadmap&sensor=false&markers=Coolhaven%20236,%20Rotterdam");
                return BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if (bitmap == null) {
                Toast.makeText(MapFragment.this.getActivity(), "Could not reach Google Maps", Toast.LENGTH_SHORT).show();
            } else {
                map.setImageBitmap(bitmap);
            }

        }
    }
}
