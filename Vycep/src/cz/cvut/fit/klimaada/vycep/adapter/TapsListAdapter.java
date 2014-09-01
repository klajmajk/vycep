package cz.cvut.fit.klimaada.vycep.adapter;

import java.text.DecimalFormat;
import java.util.List;

import cz.cvut.fit.klimaada.vycep.Controller;
import cz.cvut.fit.klimaada.vycep.R;
import cz.cvut.fit.klimaada.vycep.entity.Barrel;
import cz.cvut.fit.klimaada.vycep.entity.BarrelState;
import cz.cvut.fit.klimaada.vycep.entity.Tap;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

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
		Log.d("TAPS_LIST_ADATER", "barrel:"+barrel+" poured: "+poured);
		

		if(getItem(position).getBarrel()==null) barrel.setText("Nenaraženno");
		else barrel.setText(getItem(position).getBarrel().toString());
		DecimalFormat df = new DecimalFormat("####0.000 L");
		poured.setText(df.format(getItem(position).getPoured()));
	

		return convertView;
	}

}
