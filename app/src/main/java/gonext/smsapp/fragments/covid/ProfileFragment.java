package gonext.smsapp.fragments.covid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import gonext.smsapp.R;
import gonext.smsapp.activity.SettingActivity;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    Context context;
    ImageView imgSetting;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.colorPrimary));

        initViews(view);

        return view;
    }

    private void initViews(View view) {
        imgSetting = view.findViewById(R.id.id_setting);

        /*init_onClick*/
        imgSetting.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_setting:
                Intent intent = new Intent(getContext(), SettingActivity.class);
                getContext().startActivity(intent);
                break;
        }
    }
}
