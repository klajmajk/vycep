package cz.cvut.fit.klimaada.vycep;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import cz.cvut.fit.klimaada.vycep.controller.Controller;
import cz.cvut.fit.klimaada.vycep.entity.Beer;
import cz.cvut.fit.klimaada.vycep.entity.Brewery;

public class NewBarrelActivity extends Activity {
    private Spinner spinnerBeer;
    private Spinner spinnerBrewery;
    private Button submit;
    private EditText volume;
    private EditText count;
    private EditText price;
    private Activity mContext;
    private int mBarrelsToAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBarrelsToAdd = 0;
        mContext = this;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_barrel);
        spinnerBeer = (Spinner) findViewById(R.id.spinnerBeer);
        spinnerBrewery = (Spinner) findViewById(R.id.spinnerBrewery);
        submit = (Button) findViewById(R.id.submit);
        volume = (EditText) findViewById(R.id.volumeEditText);
        count = (EditText) findViewById(R.id.countEditText);
        price = (EditText) findViewById(R.id.priceEditText);

        spinnerBrewery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Controller.getInstanceOf().getBeersByBrewery((Brewery) spinnerBrewery.getSelectedItem(), mContext);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String volumeInput = volume.getText().toString();
                String countInput = count.getText().toString();
                String priceInput = price.getText().toString();
                if (!validCount(countInput)) {
                    count.setError("Neplatné množství sudů");
                } else if (!validVolume(volumeInput)) {
                    volume.setError("Neplatná velikost sudů");
                } else if (!validPrice(priceInput)) {
                    price.setError("Neplatná cena sudu");
                } else
                    mBarrelsToAdd += Integer.parseInt(countInput);
                Controller.getInstanceOf().newBarrels(
                        (Beer) spinnerBeer.getSelectedItem(),
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

        Controller.getInstanceOf().getBreweries(this);
    }


    public void doAfterBeersReceive(List<Beer> beers) {
            // Create an ArrayAdapter using the string array and a default
            // spinner layout;
            ArrayAdapter<Beer> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, beers);
            // Specify the layout to use when the list of choices appears
            // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
        spinnerBeer.setAdapter(adapter);
    }

    public void doAfterBreweriesReceive(List<Brewery> breweries) {
        // Create an ArrayAdapter using the string array and a default
        // spinner layout;
        ArrayAdapter<Brewery> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, breweries);
        // Specify the layout to use when the list of choices appears
        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerBrewery.setAdapter(adapter);
    }

    public void kegAdded() {
        if (mBarrelsToAdd > 1) mBarrelsToAdd--;
        else if (mBarrelsToAdd == 1) finish();
    }
}
