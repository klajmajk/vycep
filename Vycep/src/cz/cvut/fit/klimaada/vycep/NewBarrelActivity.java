package cz.cvut.fit.klimaada.vycep;

import java.util.List;

import cz.cvut.fit.klimaada.vycep.entity.BarrelKind;
import cz.cvut.fit.klimaada.vycep.rest.ICallback;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;

public class NewBarrelActivity extends Activity implements ICallback {
	private Spinner spinner;
	private Button submit;
	private EditText volume;
	private EditText count;
	private EditText price;
	private Context mContext;
	private int mBarrelsToAdd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mBarrelsToAdd = 0;
		mContext = this;
		super.onCreate(savedInstanceState);

		setContentView(R.layout.new_barrel);
		spinner = (Spinner) findViewById(R.id.spinner);
		submit = (Button) findViewById(R.id.submit);
		volume = (EditText) findViewById(R.id.volumeEditText);
		count = (EditText) findViewById(R.id.countEditText);
		price = (EditText) findViewById(R.id.priceEditText);

		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String volumeInput = volume.getText().toString();
				String countInput = count.getText().toString();
				String priceInput = price.getText().toString();
				if (!validCount(countInput)) {
					count.setError("Neplatné množství sudù");
				} else if (!validVolume(volumeInput)) {
					volume.setError("Neplatná velikost sudù");
				} else if (!validPrice(priceInput)) {
					price.setError("Neplatná cena sudu");
				} else
					mBarrelsToAdd += Integer.parseInt(countInput);
					Controller.getInstanceOf().newBarrels(
							(BarrelKind) spinner.getSelectedItem(),
							Integer.parseInt(volumeInput),
							Integer.parseInt(countInput),
							Double.parseDouble(priceInput), mContext);
			}

			private boolean validPrice(String priceInput) {
				try {
					double price = Double.parseDouble(priceInput);
					if (price > 0)
						return true;
					else
						return false;
				} catch (NumberFormatException e) {
					return false;
				}

			}

			private boolean validCount(String countInput) {
				// TODO Auto-generated method stub
				try {
					int number = Integer.parseInt(countInput);
					if (number > 0)
						return true;
					else
						return false;
				} catch (NumberFormatException e) {
					return false;
				}

			}

			private boolean validVolume(String volumeInput) {
				try {
					int number = Integer.parseInt(volumeInput);
					if ((number > 0) && (number <= 60))
						return true;
					else
						return false;
				} catch (NumberFormatException e) {
					return false;
				}
			}

		});

		Controller.getInstanceOf().getBarrelKinds(this);
	}

	@Override
	public void doAfterReceive(Object object) {
		//TODO pøedìlat znamená to že sudy byly pøidány
		if(object == null){
			mBarrelsToAdd--;
			if(mBarrelsToAdd==0) {
				finish();
			}
		}
		else if (object instanceof List<?>) {
			List<BarrelKind> kinds = (List<BarrelKind>) object;

			// Create an ArrayAdapter using the string array and a default
			// spinner layout;
			ArrayAdapter<BarrelKind> adapter = new ArrayAdapter<>(this,
					android.R.layout.simple_spinner_dropdown_item, kinds);
			// Specify the layout to use when the list of choices appears
			// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			spinner.setAdapter(adapter);
		}

	}
}
