package cz.cvut.fit.klimaada.vycep.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.List;

import cz.cvut.fit.klimaada.vycep.R;
import cz.cvut.fit.klimaada.vycep.controller.Controller;
import cz.cvut.fit.klimaada.vycep.entity.Keg;
import cz.cvut.fit.klimaada.vycep.entity.KegState;

public class BarrelsListAdapter extends ArrayAdapter<Keg> {
    protected static final String LOG_TAG = "BarrelsListAdapter";
    private LayoutInflater mInflater;
    private Context mContext;

    public BarrelsListAdapter(Context context, int resource,
                              List<Keg> objects) {
        super(context, resource, objects);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.barrel_item, null);
        }

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView volume = (TextView) convertView.findViewById(R.id.volume);
        TextView state = (TextView) convertView.findViewById(R.id.state);
        TextView bought = (TextView) convertView.findViewById(R.id.dateAdd);
        Button tap = (Button) convertView.findViewById(R.id.tap);
        Button untap = (Button) convertView.findViewById(R.id.untap);
        Button finish = (Button) convertView.findViewById(R.id.finish);
        ImageButton delete = (ImageButton) convertView.findViewById(R.id.delete);


        DecimalFormat df = new DecimalFormat("#");

        name.setText(getItem(position).getKind().getBrewery().getName() + " " + getItem(position).getKind().getName());
        volume.setText(df.format((double) getItem(position).getVolume() / 1000) + "L");
        state.setText(getItem(position).getState().toString());
        bought.setText("Naskladněno: " + getItem(position).getDateAdd().toLocaleString());

        if (getItem(position).getState() == KegState.TAPPED) {
            untap.setEnabled(true);
            finish.setEnabled(true);
            tap.setEnabled(false);
            delete.setEnabled(false);
        } else if (getItem(position).getState() == KegState.FINISHED) {
            untap.setEnabled(false);
            finish.setEnabled(false);
            tap.setEnabled(false);
            delete.setEnabled(false);

        } else if (getItem(position).getState() == KegState.STOCKED) {
            untap.setEnabled(false);
            finish.setEnabled(false);
            tap.setEnabled(true);
            if (getItem(position).getDateTap() != null) delete.setEnabled(false);
            else delete.setEnabled(true);
        }
        tap.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Controller.getInstanceOf().getTapController().tapBarrel(getItem(position),
                        mContext);

            }
        });

        untap.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "odrazime");
                Controller.getInstanceOf().getTapController().untapBarrel(getItem(position),
                        mContext);

            }
        });

        finish.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.d(LOG_TAG, "dopito");
                Controller.getInstanceOf().getTapController().finishBarrel(getItem(position),
                        mContext);

            }
        });

        delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Controller.getInstanceOf().getTapController().deleteKeg(getItem(position), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Controller.getInstanceOf().getBarrelsFromREST(mContext);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Handle error

                                String responseBody = null;
                                try {
                                    responseBody = new String(error.networkResponse.data, "utf-8");
                                    Log.e("NEW_BARREL_ACTIVITY", responseBody);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        mContext);

            }
        });

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        // TODO Auto-generated method stub
        super.notifyDataSetChanged();
    }

}
