package gonext.smsapp.fragments.covid;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import gonext.smsapp.R;

public class CheckInFragment extends Fragment {

    Context context;

    public CheckInFragment() {
        // Required empty public constructor
    }

    public static CheckInFragment newInstance() {
        return new CheckInFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        View view = inflater.inflate(R.layout.fragment_checkin, container, false);
        getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.colorPrimary));

        initViews(view);

        return view;
    }

    private void initViews(View view) {

    }


}
