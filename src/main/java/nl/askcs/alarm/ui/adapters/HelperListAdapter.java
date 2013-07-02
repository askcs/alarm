package nl.askcs.alarm.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import nl.askcs.alarm.R;
import nl.askcs.alarm.models.Helper;

import java.util.ArrayList;

/**
 * Adapter to use when a list of {@link Helper}s is displayed with the helper_list_item layout file, using a
 * {@link android.widget.ListView}. Subscribe to the {@link OnHelperActionButtonClickListener} using
 * {@code HelperListAdapter.setOnHelperActionButtonClickListener } to receive click events.
 * on the call and message buttons.
 * Created with IntelliJ IDEA.
 * @author Leon
 * Date: 11-6-13
 * Time: 16:05
 * To change this template use File | Settings | File Templates.
 */
public class HelperListAdapter extends BaseAdapter {

    public static final String TAG = "HelperListAdapter";

    public interface OnHelperActionButtonClickListener {
        public void onCallButtonClick(Helper helper);
        public void onMessageButtonClick(Helper helper);
    }

    private Context mContext;
    private ArrayList<Helper> mHelpers;
    private OnHelperActionButtonClickListener mListener;

    public HelperListAdapter(Context context) {
        mContext = context;
        mHelpers = new ArrayList<Helper>(0);
    }

    public HelperListAdapter(Context context, ArrayList<Helper> helpers) {
        mContext = context;
        mHelpers = helpers == null ? new ArrayList<Helper>(0) : helpers;
    }

    public void setHelpers(ArrayList<Helper> helpers) {
        this.mHelpers = helpers;
    }

    public void setOnHelperActionButtonClickListener(OnHelperActionButtonClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mHelpers != null ? mHelpers.size() : 0;
    }

    @Override
    public Helper getItem(int position) {
        return mHelpers != null ? mHelpers.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        Helper helper = mHelpers.get(position);

        if(convertView == null) {

            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.helper_list_item, null);

            holder.avatar = (ImageView) convertView.findViewById(R.id.profile_avatar);
            holder.call = (ImageButton) convertView.findViewById(R.id.action_button_call);
            holder.message = (ImageButton) convertView.findViewById(R.id.action_button_message);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.estimated_distance = (TextView) convertView.findViewById(R.id.estimated_distance);
            holder.estimated_time = (TextView) convertView.findViewById(R.id.estimated_time);

            // Sets the clicklistener for the call button.
            // The position of the list is used as a reference.
            holder.call.setOnClickListener(callButtonListener);
            holder.call.setTag(position);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.avatar.setImageResource(android.R.drawable.star_big_on);
        holder.name.setText(helper.getName());
        holder.estimated_distance.setText(Float.toString(helper.getDistanceToUser()) + " km");
        holder.estimated_time.setText(Float.toString(helper.getTravelTimeToUser() / 3600) + " min");

        // update the position in the view. This is used with the callButtonListener to know to which Helper it belongs
        holder.call.setTag(position);

        // update the position in the view. This is used with the callButtonListener to know to which Helper it belongs
        holder.message.setTag(position);

        return convertView;
    }

    static class ViewHolder {
        ImageView avatar;
        ImageButton call, message;
        TextView name, estimated_distance, estimated_time;
    }

    private View.OnClickListener callButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if(v.getTag() instanceof Integer) {
                try {
                    int positionInList = (Integer) v.getTag();

                    if(mListener != null) {
                        mListener.onCallButtonClick(mHelpers.get(positionInList));
                    } else {
                        Log.i(TAG, "OnHelperActionButtonClickListener has no subscriber! Subscribe using HelperListAdapter.setOnHelperActionButtonClickListener");
                    }
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    Log.e(TAG, "messageButtonListener triggered, but view has no integer in its view.getTag()");
                }
            }
        }
    };

    private View.OnClickListener messageButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if(v.getTag() instanceof Integer) {
                try {
                    int positionInList = (Integer) v.getTag();

                    if(mListener != null && mHelpers != null) {
                        mListener.onMessageButtonClick(mHelpers.get(positionInList));
                    } else {
                        Log.i(TAG, "OnHelperActionButtonClickListener has no subscriber! Subscribe using HelperListAdapter.setOnHelperActionButtonClickListener");
                    }
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    Log.e(TAG, "messageButtonListener triggered, but view has no integer in its view.getTag()");
                }
            }
        }
    };
}
