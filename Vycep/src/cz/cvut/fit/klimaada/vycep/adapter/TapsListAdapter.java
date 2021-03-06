package cz.cvut.fit.klimaada.vycep.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import cz.cvut.fit.klimaada.vycep.R;
import cz.cvut.fit.klimaada.vycep.controller.Controller;
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

        TextView note = (TextView) convertView.findViewById(R.id.note);
        TextView newKegNotification = (TextView) convertView.findViewById(R.id.new_barrel_notification);
        Button newKegButton = (Button) convertView.findViewById(R.id.button);
        newKegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controller.getInstanceOf().getBarrelsFromREST(mContext);
            }
        });


        TextView activePoured = (TextView) convertView.findViewById(R.id.activePoured);
        ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);


        DecimalFormat df = new DecimalFormat("####0.00 L");

        if (getItem(position).getNote() != null) note.setText(getItem(position).getNote());

        if (getItem(position).getKeg() == null) {
            barrel.setText("Nenaraženo");
            activePoured.setText("-");
        } else {
            barrel.setText(getItem(position).getKeg().getKind().getBrewery().getName() + " " + getItem(position).getKeg().getKind().getName() + " " + getItem(position).getKeg().getVolume() / 1000 + " L");
            activePoured.setText(df.format(((double) getItem(position).getActivePoured()) / 1000));
            progressBar.setProgress(getProgress(position));

        }

        if (Controller.getInstanceOf().getTapController().showNewKegNotification()) {
            newKegNotification.setVisibility(View.VISIBLE);
            newKegButton.setVisibility(View.VISIBLE);
            newKegButton.setEnabled(true);
        } else {
            newKegNotification.setVisibility(View.INVISIBLE);
            newKegButton.setVisibility(View.INVISIBLE);
            newKegButton.setEnabled(false);
        }
        //Log.d("Tap_adapter: ", getItem(position).toString());
        poured.setText("Vyčepováno: " + df.format(((double) getItem(position).getPoured()) / 1000));


        return convertView;
    }

    private int getProgress(final int position) {
        return Controller.getInstanceOf().getTapController().getProgress(getItem(position));
    }

}
