package gonext.smsapp.fragments.covid;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import gonext.smsapp.R;


public class HomeFragment extends Fragment implements View.OnClickListener {

    Context context;
    RelativeLayout rlKnow, rlDo;
    TextView tvKnow, tvDo;
    View viewKnow, viewDo;
    LinearLayout llKnow;
    CardView cardViewToDo;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.colorPrimary));

        initViews(view);

        return view;
    }

    private void initViews(View view) {
        rlKnow = view.findViewById(R.id.id_know_rl);
        rlDo = view.findViewById(R.id.id_do_rl);
        tvKnow = view.findViewById(R.id.id_know_tv);
        tvDo = view.findViewById(R.id.id_do_tv);
        viewKnow = view.findViewById(R.id.id_know_view);
        viewDo = view.findViewById(R.id.id_do_view);
        llKnow = view.findViewById(R.id.id_know_ll);
        cardViewToDo = view.findViewById(R.id.id_todo_ll);

        rlKnow.setOnClickListener(this);
        rlDo.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_know_rl:
                setTextColor(tvKnow);
                setView(viewKnow,"KNOW");
                break;
            case R.id.id_do_rl:
                setTextColor(tvDo);
                setView(viewDo,"DO");
                break;
        }
    }

    private void setView(View view, String check) {
        viewKnow.setBackgroundColor(getActivity().getResources().getColor(R.color.colorWhite));
        viewDo.setBackgroundColor(getActivity().getResources().getColor(R.color.colorWhite));
        view.setBackgroundColor(getActivity().getResources().getColor(R.color.color_red));
        if(check=="KNOW"){
            llKnow.setVisibility(View.VISIBLE);
            cardViewToDo.setVisibility(View.GONE);
        }else{
            llKnow.setVisibility(View.GONE);
            cardViewToDo.setVisibility(View.VISIBLE);
        }

    }

    private void setTextColor(TextView textView) {
        tvKnow.setTextColor(getActivity().getResources().getColor(R.color.colorGray));
        tvDo.setTextColor(getActivity().getResources().getColor(R.color.colorGray));
        textView.setTextColor(getActivity().getResources().getColor(R.color.colorBlack));
    }
}
