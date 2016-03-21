package com.freedom.augmentedreality;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.freedom.augmentedreality.adapters.MarkersAdapter;
import com.freedom.augmentedreality.app.AppConfig;
import com.freedom.augmentedreality.helper.SQLiteHandler;
import com.freedom.augmentedreality.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarkerActivity extends AppCompatActivity {
    private ProgressDialog pDialog;
    private RecyclerView recyclerView;
    private MarkersAdapter mAdapter;
    private List<Marker> markerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        recyclerView = (RecyclerView) findViewById(R.id.markers_recycler_view);


        getAllMarker();
//        Button b = (Button) findViewById(R.id.button);
//        b.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getAllMarker();
//            }
//        });
    }

    public void getAllMarker() {

        String tag_string_req = "get_all_marker";

        pDialog.setMessage("Sync marker ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_ALLMARKER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray markers = jObj.getJSONArray("markers");
                    for(int i = 0; i < markers.length(); i++) {
                        JSONObject marker = (JSONObject) markers.get(i);
                        Integer id = marker.getInt("id");
                        String name = marker.getString("name");
                        String image = marker.getString("image");
                        String iset = marker.getString("iset");
                        String fset = marker.getString("fset");
                        String fset3 = marker.getString("fset3");
                        Marker temp = new Marker(id, name, image, iset, fset, fset3);
                        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
                        db.addMarker(temp);
                        db.close();
                    }
                    Log.e("VVV", response);
                    Log.d("Reading: ", "Reading all contacts..");

                    SQLiteHandler db = new SQLiteHandler(getApplicationContext());
                    markerList = db.getAllMarkers();
                    db.close();

                    mAdapter = new MarkersAdapter(markerList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();

                    for (Marker cn : markerList) {
                        String log = "Id: " + cn.get_id() + " ,Name: " + cn.get_name() + " ,Image: " + cn.get_image();
                        // Writing Contacts to log
                        Log.d("Name: ", log);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });

        ArApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
