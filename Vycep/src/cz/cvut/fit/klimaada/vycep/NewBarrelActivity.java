package cz.cvut.fit.klimaada.vycep;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.cvut.fit.klimaada.vycep.controller.Controller;
import cz.cvut.fit.klimaada.vycep.entity.Beer;
import cz.cvut.fit.klimaada.vycep.entity.Brewery;

public class NewBarrelActivity extends Activity {
    private Spinner spinnerBeer;
    private Spinner spinnerBrewery;
    private Button submit;
    private Spinner volumeSpinner;
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
        volumeSpinner = (Spinner) findViewById(R.id.volumeSpinner);
        count = (EditText) findViewById(R.id.countEditText);
        price = (EditText) findViewById(R.id.priceEditText);
        Button addBeer = (Button) findViewById(R.id.addBeer);
        Button addBrewery = (Button) findViewById(R.id.addBrewery);

        volumeSpinner.setSelection(2);

        addBeer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showBeerDialog();
            }
        });

        addBrewery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showBreweryDialog();
            }
        });


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
                String volumeInput = volumeSpinner.getSelectedItem().toString().substring(0, volumeSpinner.getSelectedItem().toString().length() - 2);
                String countInput = count.getText().toString();
                String priceInput = price.getText().toString();
                if (!validCount(countInput)) {
                    count.setError("Neplatné množství sudů");
                } else if (!validPrice(priceInput)) {
                    price.setError("Neplatná cena sudu");
                } else if (!validBeer((Beer) spinnerBeer.getSelectedItem())) {
                    spinnerBeer.setBackgroundColor(v.getResources().getColor(R.color.red));
                } else {
                    mBarrelsToAdd += Integer.parseInt(countInput);
                    Controller.getInstanceOf().newBarrels(
                            (Beer) spinnerBeer.getSelectedItem(),
                            Integer.parseInt(volumeInput),
                            Integer.parseInt(countInput),
                            Double.parseDouble(priceInput), mContext);
                }
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

            private boolean validBeer(Beer selectedItem) {
                if (selectedItem != null) return true;
                return false;
            }

        });

        Controller.getInstanceOf().getBreweries(this);
    }


    private void showBeerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nové pivo");
        final Activity activity = this;
// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                final Brewery brewery = (Brewery) spinnerBrewery.getSelectedItem();
                Controller.getInstanceOf().addRequest(new Beer(input.getText().toString(), brewery), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Controller.getInstanceOf().getBeersByBrewery(brewery, activity);
                        dialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        try {
                            String responseBody = new String(volleyError.networkResponse.data, "utf-8");

                            Log.e("NEW_BARREL_ACTIVITY", responseBody);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }, activity);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void showBreweryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nový pivovar");
        final Activity activity = this;
// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                Controller.getInstanceOf().addRequest(new Brewery(input.getText().toString()), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Controller.getInstanceOf().getBreweries(activity);
                        dialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("NEW_BARREL_ACTIVITY", volleyError.toString());
                    }
                }, activity);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }


    public void doAfterBeersReceive(List<Beer> beers) {
        // Create an ArrayAdapter using the string array and a default
        // spinner layout;
        if (beers == null) beers = new ArrayList<>();
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
