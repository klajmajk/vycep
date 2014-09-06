package cz.cvut.fit.klimaada.vycep;

import cz.cvut.fit.klimaada.vycep.adapter.TapsListAdapter;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class PlaceholderFragment extends Fragment implements IStatusView {
	private View rootView;

	private TapsListAdapter tapAdapter;

	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static PlaceholderFragment newInstance(int sectionNumber) {
		PlaceholderFragment fragment = new PlaceholderFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public PlaceholderFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		Controller.getInstanceOf().setView(this);
		ListView tapListView = (ListView) rootView.findViewById(R.id.tapsListView);
        //Log.d(LOG_TAG, "tapList:" + tapListView);
		tapAdapter = new TapsListAdapter(getActivity(), R.layout.tap_item, Controller.getInstanceOf().getTaps());
		tapListView.setAdapter(tapAdapter);
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}

	@Override
	public void setStatusText(String text) {
		TextView textView = (TextView) rootView.findViewById(R.id.status_label);
		textView.setText(text);
		
	}

	@Override
	public void setVolumeText(String text) {
		
		
	}

	@Override
	public Context getContext() {
		return getActivity();
	}

	public void notifyTapsChanged() {
		tapAdapter.notifyDataSetChanged();
		
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Controller.getInstanceOf().persist();
	}

}
