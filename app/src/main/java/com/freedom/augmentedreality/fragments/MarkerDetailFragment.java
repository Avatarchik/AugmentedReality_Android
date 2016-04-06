package com.freedom.augmentedreality.fragments;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.freedom.augmentedreality.R;
import com.freedom.augmentedreality.app.AppConfig;
import com.freedom.augmentedreality.model.Marker;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;


/**
 * A simple {@link Fragment} subclass.
 */
public class MarkerDetailFragment extends Fragment {

    private Marker marker;
    private ProgressDialog pDialog;
    private TextView txt_test;
    public MarkerDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        marker = (Marker) getArguments().getSerializable("marker");

        View view = inflater.inflate(R.layout.fragment_marker_detail, container, false);

        Button btn_download = (Button) view.findViewById(R.id.btn_download);
        txt_test = (TextView) view.findViewById(R.id.txt_test);
        txt_test.setText(marker.getUserName());

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMarker();
            }

        });

        return view;
    }

    private void setMarker() {
        changeMarkerDat();
        new DownloadFileFromURL().execute();

    }

    private void changeMarkerDat() {
        String url_file = marker.get_iset();
        String namefile = url_file.substring(url_file.lastIndexOf("/") + 1, url_file.lastIndexOf("."));
        StringBuilder text = new StringBuilder();
        try {
            File file = new File(getContext().getCacheDir(), "Data/markers.dat");

            BufferedReader br = new BufferedReader(new FileReader(file));

            int i = 0;
            String line;
            while ((line = br.readLine()) != null) {
                i++;

                if(i == 1) {
                    text.append(Integer.valueOf(line.trim()) + 1);
                    text.append('\n');
                } else {
                    if(i == 3) {
                        text.append("../DataNFT/" + namefile);
                        text.append('\n');
                        text.append("NFT");
                        text.append('\n');
                        text.append("FILTER 15.0");
                        text.append('\n');
                        text.append('\n');
                    }

                    text.append(line);
                    text.append('\n');
                }


            }
            br.close();
        }

        catch (IOException e) {
            e.printStackTrace();

        }
        finally{
        }

        try {
            FileWriter fwriter = new FileWriter(new File(getContext().getCacheDir(), "Data/markers.dat"));
            BufferedWriter bwriter = new BufferedWriter(fwriter);
            bwriter.write(text.toString());
            bwriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        txt_test.setText(text.toString());
    }

    class DownloadFileFromURL extends AsyncTask<Void, Void, Void> {

        private void downloadFile(String url_file) {
            String namefile = url_file.substring(url_file.lastIndexOf("/") + 1);
            int count;
            try {
                URL url = new URL(AppConfig.baseURL + url_file);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                OutputStream output = new FileOutputStream(getContext().getCacheDir() +  "/DataNFT/" + namefile);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            downloadFile(marker.get_iset());
            downloadFile(marker.get_fset());
            downloadFile(marker.get_fset3());
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideDialog();
        }
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

