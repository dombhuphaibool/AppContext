package com.bandonleon.appcontext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.bandonleon.appcontext.app.NetworkApi;
import com.bandonleon.appcontext.app.exchangeable.ExchangeableApiApplication;
import com.bandonleon.appcontext.network.Api;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    TextView mOutputTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOutputTxt = (TextView) findViewById(R.id.output_txt);

        Button callButton = (Button) findViewById(R.id.btn_call);
        if (callButton != null) {
            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callApi();
                }
            });
        }

        Spinner networkSpinner = (Spinner) findViewById(R.id.network_spinner);
        if (networkSpinner != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.network_api, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            networkSpinner.setAdapter(adapter);
            networkSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String networkApiStr = parent.getItemAtPosition(position).toString();
                    ExchangeableApiApplication.switchNetworkApi(networkApiStr);
                    if (mOutputTxt != null) {
                        mOutputTxt.setText("Switched to " + networkApiStr);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Nothing to do
                }
            });
        }
    }

    private void callApi() {
        NetworkApi.get().getInfo(4, "yo", 5, new Api.ResponseListener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                if (mOutputTxt != null) {
                    mOutputTxt.setText(response.toString());
                }
            }

            @Override
            public void onError(String error) {
                if (mOutputTxt != null) {
                    mOutputTxt.setText(error);
                }
            }
        });
    }
}
