package lu.btsin.bobtis;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PopupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PopupFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    private ArrayList<String> data = new ArrayList<>();

    public PopupFragment() {
        // Required empty public constructor
        for (int i = 0; i < 40; i++) {
            data.add(String.valueOf(i));
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PopupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PopupFragment newInstance(String param1, String param2) {
        PopupFragment fragment = new PopupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(getString(R.string.app_name));
        View view = inflater.inflate(R.layout.fragment_popup,container);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        TextView tw = new TextView(getContext());
        tw.setGravity(Gravity.CENTER);
        alertDialogBuilder.setCustomTitle(tw);

//        alertDialogBuilder.setIcon(R.drawable.ic_bobtis);
        view = getLayoutInflater().inflate(R.layout.fragment_popup, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setPositiveButton("Ok",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // on success
                ListView list = (ListView)(getView().findViewById(R.id.studentList));
                ListAdapter ba = (ListAdapter)list.getAdapter();
                Log.i("Popup", String.valueOf(ba.getCount()));
                //dialog.dismiss();
            }
        });
        return alertDialogBuilder.create();
//        Dialog dialog = super.onCreateDialog(savedInstanceState);
//        dialog.setTitle(R.string.app_name);
////        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setFlags(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//        //dialog.setContentView(dialog.getLayoutInflater().inflate(R.layout.fragment_popup,null));
//        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    //    @Override
//    public void onResume() {
//        super.onResume();
//        Window window = getDialog().getWindow();
//        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
//        layoutParams.dimAmount = 0;
//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        window.setGravity(Gravity.CENTER);
//        getDialog().getWindow().setAttributes(layoutParams);
//    }

    @Override
    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.85), (int) (size.y * 0.88));

        // Call super onResume after sizing
        super.onResume();

    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
////        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
////        view.setLayoutParams(lp);
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(getDialog().getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        getDialog().show();
//        getDialog().getWindow().setAttributes(lp);
//        super.onViewCreated(view, savedInstanceState);
//    }

    public void init(){
        ListView list = getView().findViewById(R.id.studentList);
        ListAdapter adapter = new ListAdapter() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tw = new TextView(getContext());
                LinearLayout.LayoutParams layoutParamsText = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                layoutParamsText.setMargins(1,1,1,1);
                layoutParamsText.weight = 1;

                tw.setPadding(1,0,1,0);
                tw.setLayoutParams(layoutParamsText);
                tw.setText(data.get(position));

                LinearLayout ll = new LinearLayout(getContext());
                //ll.setOnClickListener(view -> display(data.get(i)));
                ll.setGravity(Gravity.CLIP_HORIZONTAL);
                ll.setBackgroundResource(R.drawable.coursebackground);
                ((GradientDrawable) ll.getBackground()).setColor(Color.parseColor("#FFFF99"));
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                ll.setPadding(10,0,10,0);
                ll.setLayoutParams(layoutParams);

                ll.setOrientation(LinearLayout.HORIZONTAL);
                ll.addView(tw);
                Button button = new Button(getContext());
                button.setText("Add to favorites");
                ll.addView(button);
                return ll;
            }
        };
        list.setAdapter(adapter);
        adapter.setData(data);
        list.setTextFilterEnabled(true);
        adapter.getFilter().filter("");
        view.getRootView().setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200));
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200));
        list.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200));
    }

}