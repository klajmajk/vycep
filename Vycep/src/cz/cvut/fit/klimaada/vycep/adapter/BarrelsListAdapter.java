package cz.cvut.fit.klimaada.vycep.adapter;

import java.text.DecimalFormat;
import java.util.List;

import cz.cvut.fit.klimaada.vycep.Controller;
import cz.cvut.fit.klimaada.vycep.R;
import cz.cvut.fit.klimaada.vycep.entity.Barrel;
import cz.cvut.fit.klimaada.vycep.entity.BarrelState;
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

public class BarrelsListAdapter extends ArrayAdapter<Barrel> {
	protected static final String LOG_TAG = "BarrelsListAdapter";
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
		ImageButton delete = (ImageButton) convertView.findViewById(R.id.delete);
		

        DecimalFormat df = new DecimalFormat("#");

		name.setText(getItem(position).getKind().getBreweryName()+" "+getItem(position).getKind().getBeerName() );
		volume.setText(df.format((double)getItem(position).getVolume()/1000)+ "L");
		state.setText(getItem(position).getBarrelState().toString());
		bougth.setText("Naskladnìno: "+getItem(position).getBought().toLocaleString());

		if (getItem(position).getBarrelState() == BarrelState.TAPED) {
			untap.setEnabled(true);
			finish.setEnabled(true);
			tap.setEnabled(false);
			delete.setEnabled(false);
		} else if (getItem(position).getBarrelState() == BarrelState.FINISHED) {
			untap.setEnabled(false);
			finish.setEnabled(false);
			tap.setEnabled(false);
			delete.setEnabled(false);

		} else if (getItem(position).getBarrelState() == BarrelState.STOCK) {
			untap.setEnabled(false);
			finish.setEnabled(false);
			tap.setEnabled(true);
			if(getItem(position).getTaped() != null) delete.setEnabled(false);
			else delete.setEnabled(true);
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
				Log.d(LOG_TAG, "odrazime");
				Controller.getInstanceOf().untapBarrel(getItem(position),
						mContext);

			}
		});

		finish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d(LOG_TAG, "dopito");
				Controller.getInstanceOf().finishBarrel(getItem(position),
						mContext);

			}
		});
		
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d(LOG_TAG, "dopito");
				Controller.getInstanceOf().deleteBarrel(getItem(position),
						mContext);

			}
		});
		
		Log.d(LOG_TAG, "test");
		return convertView;
	}
	
	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}

}
