package nl.askcs.alarm.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import nl.askcs.alarm.R;

import java.io.IOException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: Leon
 * Date: 3-6-13
 * Time: 13:26
 * To change this template use File | Settings | File Templates.
 */
public class MapFragment extends Fragment {

    private ImageView map;
    private LinearLayout ll;
    private int height = 0, width = 0;

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
                URL url = new URL("http://maps.google.com/maps/api/staticmap?center=Coolhaven%20236,%20Rotterdam&zoom=16&size=" + Integer.toString(width) + "x" + Integer.toString(height) + "&maptype=roadmap&sensor=false&markers=Coolhaven%20236,%20Rotterdam");
                return BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if(bitmap == null) {
                Toast.makeText(MapFragment.this.getActivity(), "Could not reach Google Maps", Toast.LENGTH_SHORT).show();
            } else {
                map.setImageBitmap(bitmap);
            }

        }
    }
}
