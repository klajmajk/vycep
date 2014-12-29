package cz.cvut.fit.klimaada.vycep.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import cz.cvut.fit.klimaada.vycep.R;
import cz.cvut.fit.klimaada.vycep.entity.Tap;

public class TapsListAdapter extends ArrayAdapter<Tap> {
    private LayoutInflater mInflater;
    private Context mContext;

    public TapsListAdapter(Context context, int resource,
                           List<Tap> objects) {
        super(context, resource, objects);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.tap_item, null);
        }
        TextView barrel = (TextView) convertView.findViewById(R.id.barrel);
        TextView poured = (TextView) convertView.findViewById(R.id.poured);

        TextView activePoured = (TextView) convertView.findViewById(R.id.activePoured);
        ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
        Log.d("TAPS_LIST_ADATER", "barrel:" + barrel + " poured: " + poured);


        DecimalFormat df = new DecimalFormat("####0.000 L");

        if (getItem(position).getKeg() == null) {
            barrel.setText("Nenaraženo");
            activePoured.setText("Probíhající čepování: - ");
        } else {
            barrel.setText("Naraženo: " + getItem(position).getKeg().getKind().getBrewery().getName() + " " + getItem(position).getKeg().getKind().getBeerName() + " " + getItem(position).getKeg().getVolume() / 1000 + " L");
            activePoured.setText("Probíhající čepování: " + df.format(((double) getItem(position).getActivePoured()) / 1000));
            progressBar.setProgress(getProgress(position));

        }
        //Log.d("Tap_adapter: ", getItem(position).toString());
        poured.setText("Načepováno celý sud: " + df.format(((double) getItem(position).getPoured()) / 1000));


        return convertView;
    }

    private int getProgress(final int position) {
        int result = (int) Math.round(((getItem(position).getPoured() / getItem(position).getKeg().getVolume()) * 100));
        if (result > 100) return 100;
        return result;
    }

}
