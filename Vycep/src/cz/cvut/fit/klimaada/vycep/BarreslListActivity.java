package cz.cvut.fit.klimaada.vycep;

import java.util.List;

import cz.cvut.fit.klimaada.vycep.adapter.BarrelsListAdapter;
import cz.cvut.fit.klimaada.vycep.entity.Barrel;
import cz.cvut.fit.klimaada.vycep.rest.ICallback;
import Controllers.Controller;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BarreslListActivity extends Activity implements IMyActivity, ICallback {
	private static final String LOG_TAG = "BarreslListActivity";
	private Context mContext = this;
	private BarrelsListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.barrels_list_activity);
		final ListView listview = (ListView) findViewById(R.id.listView);
		adapter = new BarrelsListAdapter(this, R.layout.barrel_item, Controller
				.getInstanceOf().getBarrels());
		listview.setAdapter(adapter);

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Controller.getInstanceOf().getBarrelsFromREST(this);
		//adapter.notifyDataSetChanged();
	}

	@Override
	public void notifyBarrelsReceived(List<Barrel> barrels) {
		Log.d(LOG_TAG, "change notified");
		adapter.notifyDataSetChanged();
	}

	@Override
	public void notifyTapsChanged() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.barrel, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.action_new_barrel) {
			Intent intent = new Intent(this,
					NewBarrelActivity.class);
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void doAfterReceive(Object object) {
		Controller.getInstanceOf().getBarrelsFromREST(this);
	}
}
