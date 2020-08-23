package gonext.smsapp.fragments.covid;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import gonext.smsapp.R;

public class StatisticsFragment extends Fragment implements View.OnClickListener {

    Context context;
    LinearLayout llTapOne, llTapTwo, llTapThree;
    TextView tvTapOne, tvTapTwo, tvTapThree;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.colorPrimary));

        initViews(view);

        return view;
    }

    private void initViews(View view) {

        tvTapOne = view.findViewById(R.id.id_tap_one_tv);
        tvTapTwo = view.findViewById(R.id.id_tap_two_tv);
        tvTapThree = view.findViewById(R.id.id_tap_three_tv);
        llTapOne = view.findViewById(R.id.id_tap_one_ll);
        llTapTwo = view.findViewById(R.id.id_tap_two_ll);
        llTapThree = view.findViewById(R.id.id_tap_three_ll);

        /*init_onClick*/
        tvTapOne.setOnClickListener(this);
        tvTapTwo.setOnClickListener(this);
        tvTapThree.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_tap_one_tv:
                setView(tvTapOne,"ONE");
                break;
            case R.id.id_tap_two_tv:
                setView(tvTapTwo, "TWO");
                break;
            case R.id.id_tap_three_tv:
                setView(tvTapThree, "THREE");
                break;
        }
    }

    private void setView(TextView textView, String check) {
        tvTapOne.setBackground(getActivity().getResources().getDrawable(R.drawable.edittext_outline_style_white));
        tvTapOne.setTextColor(getActivity().getResources().getColor(R.color.colorWhite));
        tvTapTwo.setBackground(getActivity().getResources().getDrawable(R.drawable.edittext_outline_style_white));
        tvTapTwo.setTextColor(getActivity().getResources().getColor(R.color.colorWhite));
        tvTapThree.setBackground(getActivity().getResources().getDrawable(R.drawable.edittext_outline_style_white));
        tvTapThree.setTextColor(getActivity().getResources().getColor(R.color.colorWhite));
        textView.setBackground(getActivity().getResources().getDrawable(R.drawable.button_white_fill_style_curve));
        textView.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));

        switch (check){
            case "ONE":
                llTapOne.setVisibility(View.VISIBLE);
                llTapTwo.setVisibility(View.GONE);
                llTapThree.setVisibility(View.GONE);
                break;
            case "TWO":
                llTapTwo.setVisibility(View.VISIBLE);
                llTapThree.setVisibility(View.GONE);
                llTapOne.setVisibility(View.GONE);
                break;
            case "THREE":
                llTapThree.setVisibility(View.VISIBLE);
                llTapOne.setVisibility(View.GONE);
                llTapTwo.setVisibility(View.GONE);
                break;
        }
    }
}
