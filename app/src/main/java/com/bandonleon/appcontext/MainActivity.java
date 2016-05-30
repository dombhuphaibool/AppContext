package com.bandonleon.appcontext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bandonleon.appcontext.app.CustomApplication;
import com.bandonleon.appcontext.app.NetworkUtil;
import com.bandonleon.appcontext.app.exchangeable.ExchangeableApiApplication;
import com.bandonleon.appcontext.context.CustomContext;
import com.bandonleon.appcontext.context.ResourceType;
import com.bandonleon.appcontext.context.ResourceTypes;
import com.bandonleon.appcontext.network.api.Api;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements CustomContext.ResourcesListener {
    TextView mOutputTxt;
    ImageView mImageView;
    Button mApiCallBtn;
    Button mImageCallBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupApiInfoUi();
        setupImageInfoUi();

        // @TODO: Maybe show a loading spinner as feedback also?

        @ResourceTypes int resources = ResourceType.API | ResourceType.IMAGE_LOADER;
        CustomApplication.getCustomContext().waitForResources(resources, this, true);
    }

    @Override
    public void onResourcesReady(@ResourceTypes int resources) {
        if (mApiCallBtn != null) {
            mApiCallBtn.setEnabled(true);
        }
        if (mImageCallBtn != null) {
            mImageCallBtn.setEnabled(true);
        }
    }

    @Override
    public boolean onResourcesError(String error) {
        if (mOutputTxt != null) {
            mOutputTxt.setText("Error initializing resources: " +
                    error + " - Please restart the app...");
        }
        return true;
    }

    private void setupApiInfoUi() {
        mOutputTxt = (TextView) findViewById(R.id.output_txt);

        mApiCallBtn = (Button) findViewById(R.id.btn_api_call);
        if (mApiCallBtn != null) {
            mApiCallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callApi();
                }
            });
            mApiCallBtn.setEnabled(false);
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

    private void setupImageInfoUi() {
        mImageView = (ImageView) findViewById(R.id.image_view);

        mImageCallBtn = (Button) findViewById(R.id.btn_image_call);
        if (mImageCallBtn != null) {
            mImageCallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadImage();
                }
            });
            mImageCallBtn.setEnabled(false);
        }

        Spinner imageSpinner = (Spinner) findViewById(R.id.image_spinner);
        if (imageSpinner != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.image_loader, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            imageSpinner.setAdapter(adapter);
            imageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (mImageView != null) {
                        mImageView.setImageResource(0);
                    }
                    String imageLoaderStr = parent.getItemAtPosition(position).toString();
                    ExchangeableApiApplication.switchImageLoader(imageLoaderStr);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void callApi() {
        NetworkUtil.getApi().getInfo(4, "yo", 5, new Api.ResponseListener<JSONObject>() {
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

    private void loadImage() {
        if (mImageView != null) {
            String url = "http://r.ddmcdn.com/s_f/o_1/cx_633/cy_0/cw_1725/ch_1725/w_720/APL/uploads/2014/11/too-cute-doggone-it-video-playlist.jpg";
            NetworkUtil.getImageLoader().load(url, mImageView);
        }
    }
}
