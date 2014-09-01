package cz.cvut.fit.klimaada.vycep.adapter;

import java.util.List;

import cz.cvut.fit.klimaada.vycep.Controller;
import cz.cvut.fit.klimaada.vycep.R;
import cz.cvut.fit.klimaada.vycep.entity.Barrel;
import cz.cvut.fit.klimaada.vycep.entity.BarrelState;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class BarrelsListAdapter extends ArrayAdapter<Barrel> {
	private LayoutInflater mInflater;
	private Context mContext;

	public BarrelsListAdapter(Context context, int resource,
			List<Barrel> objects) {
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
		TextView bougth = (TextView) convertView.findViewById(R.id.bought);
		Button tap = (Button) convertView.findViewById(R.id.tap);
		Button untap = (Button) convertView.findViewById(R.id.untap);
		Button finish = (Button) convertView.findViewById(R.id.finish);

		name.setText(getItem(position).getKind().getName());
		volume.setText(getItem(position).getKind().getVolume() + "L");
		state.setText(getItem(position).getBarrelState().toString());
		bougth.setText(getItem(position).getBought().toLocaleString());

		if (getItem(position).getBarrelState() == BarrelState.TAPED) {
			untap.setEnabled(true);
			finish.setEnabled(true);
			tap.setEnabled(false);
		} else if (getItem(position).getBarrelState() == BarrelState.FINISHED) {
			untap.setEnabled(false);
			finish.setEnabled(false);
			tap.setEnabled(false);

		} else if (getItem(position).getBarrelState() == BarrelState.STOCK) {
			untap.setEnabled(false);
			finish.setEnabled(false);
			tap.setEnabled(true);
		}
		tap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Controller.getInstanceOf().tapBarrel(getItem(position),
						mContext);

			}
		});

		untap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Controller.getInstanceOf().untapBarrel(getItem(position),
						mContext);

			}
		});

		finish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Controller.getInstanceOf().finishBarrel(getItem(position),
						mContext);

			}
		});
		return convertView;
	}

}
