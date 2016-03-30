package com.freedom.augmentedreality.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.freedom.augmentedreality.R;
import com.freedom.augmentedreality.helper.SessionManager;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ProfileFragment extends Fragment {

    private SessionManager session;
    private TextView name, email;
    public NetworkImageView img_avatar;

    public ProfileFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        session = new SessionManager(getActivity().getApplicationContext());

        name = (TextView) view.findViewById(R.id.name);
        email = (TextView) view.findViewById(R.id.email);
        img_avatar = (NetworkImageView) view.findViewById(R.id.img_avatar);

        name.setText(session.getValue("name"));
        email.setText(session.getValue("email"));

        img_avatar.setDefaultImageResId(R.mipmap.default_avatar);

//        img_avatar.setImageUrl(image_link, ArApplication.getInstance().getImageLoader());
        return view;
    }

}
