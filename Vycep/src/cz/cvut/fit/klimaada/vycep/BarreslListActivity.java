package cz.cvut.fit.klimaada.vycep;

import java.util.List;

import cz.cvut.fit.klimaada.vycep.adapter.BarrelsListAdapter;
import cz.cvut.fit.klimaada.vycep.entity.Barrel;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BarreslListActivity extends Activity implements IMyActivity {
	private Context mContext = this;
	private BarrelsListAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	    setContentView(R.layout.barrels_list_activity);
	    final ListView listview = (ListView) findViewById(R.id.listView);
	    adapter = new BarrelsListAdapter(this, R.layout.barrel_item, Controller.getInstanceOf().getBarrels());
	    listview.setAdapter(adapter);
	   
	}
	@Override
	public void notifyBarrelsReceived(List<Barrel> barrels) {
		adapter.notifyDataSetChanged();
	}
	@Override
	public void notifyTapsChanged() {
		// TODO Auto-generated method stub
		
	}
}
