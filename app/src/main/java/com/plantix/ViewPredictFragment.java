package com.plantix;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.noties.markwon.Markwon;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewPredictFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewPredictFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ViewPredictFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewPredictFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static final String TAG = ViewPredictFragment.class.getName();
    private PredictionViewModel viewModel;
    public static ViewPredictFragment newInstance(String param1, String param2) {
        ViewPredictFragment fragment = new ViewPredictFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        viewModel = new ViewModelProvider(requireActivity()).get(PredictionViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_predict, container, false);

//        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
//
//        List<String> imageUrls = new ArrayList<>();
//        imageUrls.add("https://plantix-image-pool.s3.ap-southeast-1.amazonaws.com/uploads/1/1716868299437");
//        imageUrls.add("https://plantix-image-pool.s3.ap-southeast-1.amazonaws.com/uploads/1/1716868299444");
//        imageUrls.add("https://plantix-image-pool.s3.ap-southeast-1.amazonaws.com/uploads/1/1716868299452");
//        imageUrls.add("https://plantix-image-pool.s3.ap-southeast-1.amazonaws.com/uploads/1/1716868299459");
//
//        SlideshowAdapter adapter = new SlideshowAdapter(getActivity(), imageUrls);
//        viewPager.setAdapter(adapter);

        ImageButton btnReturnHome = view.findViewById(R.id.return_button);
        btnReturnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack(ViewPredictFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        TableLayout tableLayout = view.findViewById(R.id.tableLayout);

//        Toast.makeText(getActivity(), "Khong co du lieu", Toast.LENGTH_LONG).show();
        tableLayout.removeAllViews();


        TableRow headerRow = createHeaderRow();
        setTextViewColor(headerRow, Color.BLACK);
        // Thêm hàng tiêu đề vào bảng
        tableLayout.addView(headerRow);

        TableRow row = new TableRow(requireContext());
        TextView notiTextView = createTextView(String.valueOf("Không có dữ liệu"));
        row.addView(notiTextView);
        tableLayout.addView(row);


        // Quan sát LiveData từ ViewModel
        viewModel.getPredictions().observe(getViewLifecycleOwner(), new Observer<List<Prediction>>() {
            @Override
            public void onChanged(List<Prediction> predictions) {
//                Toast.makeText(getActivity(), "predictions", Toast.LENGTH_LONG).show();
                if (predictions != null && !predictions.isEmpty()) { // && !predictions.isEmpty()
                    // Xóa tất cả các hàng cũ trong bảng (nếu có)
                    tableLayout.removeAllViews();

                    TableRow headerRow = createHeaderRow();
                    setTextViewColor(headerRow, Color.BLACK);
                    // Thêm hàng tiêu đề vào bảng
                    tableLayout.addView(headerRow);


                    // Tạo hàng và cột cho mỗi mục dự đoán
                    int i = 1;
                    for (Prediction prediction : predictions) {
                        TableRow row = new TableRow(requireContext());

                        TextView noTextView = createTextView(String.valueOf(i));
                        row.addView(noTextView);

//                        TextView idTextView = createTextView(String.valueOf(prediction.getId()));
//                        row.addView(idTextView);
                        String newName = transformName(prediction.getName());
                        TextView nameTextView = createTextView(newName);
                        row.addView(nameTextView);

                        // Làm tròn prop đến 3 chữ số thập phân
                        DecimalFormat df = new DecimalFormat("#.###");
                        String probRounded = df.format(prediction.getProb() * 100);
                        TextView probTextView = createTextView(probRounded + "%");
                        row.addView(probTextView);

                        setTextViewColor(row, Color.BLACK);
                        // Thêm hàng vào bảng
                        tableLayout.addView(row);

                        i++;
                    }
                } else {
                    // Nếu không có dữ liệu dự đoán, có thể hiển thị một thông báo hoặc làm gì đó khác
                }
            }
        });

        viewModel.getHighestProbDisease().observe(getViewLifecycleOwner(), new Observer<DiseaseData>() {
            @Override
            public void onChanged(DiseaseData highestProbDisease) {
//                Toast.makeText(getActivity(), "highestProbDisease", Toast.LENGTH_LONG).show();
                // Update UI with highestProbDisease
                if (highestProbDisease != null) {
                    // Example: Display the highest probability disease details
                    TextView symtomTextView = view.findViewById(R.id.textViewSymtom);
                    String symtomMarkdown = highestProbDisease.getSymtomMarkdown();
                    Markwon markdown1 = Markwon.create(requireActivity());
                    markdown1.setMarkdown(symtomTextView, symtomMarkdown);

                    TextView descriptionTextView = view.findViewById(R.id.textViewMoreInformation);
                    String descriptionMarkdown = highestProbDisease.getDescriptionMarkdown();
                    Markwon markdown2 = Markwon.create(requireActivity());
                    markdown2.setMarkdown(descriptionTextView, descriptionMarkdown);



                }
                if (highestProbDisease.getImageUrls() != null) {
                    ViewPager2 viewPager = view.findViewById(R.id.viewPager);

                    List<String> imageUrls = highestProbDisease.getImageUrls();

                    SlideshowAdapter adapter = new SlideshowAdapter(getActivity(), imageUrls);
                    viewPager.setAdapter(adapter);
                }
            }
        });
//        TextView textView = view.findViewById(R.id.textViewSymtom);
//
//        String markdown = "- Chelsea\n" +
//                "- The Blue";
//
//        Markwon markwon = Markwon.create(requireActivity());
//        markwon.setMarkdown(textView, markdown);


        return view;
    }
    private TableRow createHeaderRow() {
        TableRow row = new TableRow(requireContext());

        TextView noHeader = createTextViewForHeader("STT");
        row.addView(noHeader);

//        TextView idHeader = createTextView("ID");
//        row.addView(idHeader);

        TextView nameHeader = createTextViewForHeader("Tên bệnh");
        row.addView(nameHeader);

        TextView probHeader = createTextViewForHeader("Xác suất dự đoán");
        row.addView(probHeader);

        return row;
    }
    private TextView createTextView(String text) {
        TextView textView = new TextView(requireContext());
        textView.setText(text);
        textView.setPadding(10, 10, 10, 10);
        return textView;
    }
    private TextView createTextViewForHeader(String text) {
        TextView textView = new TextView(requireContext());
        textView.setText(text);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(10, 10, 10, 10);
        return textView;
    }
    private void setTextViewColor(ViewGroup viewGroup, int color) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(color);
            } else if (view instanceof ViewGroup) {
                setTextViewColor((ViewGroup) view, color);
            }
        }
    }
    private String transformName(String originalName) {
        if (originalName.equals("leaf-mold")) {
            return "Leaf Mold";
        }
        if (originalName.equals("late-blight")) {
            return "Late Blight";
        }
        if (originalName.equals("early-blight")) {
            return "Early Blight";
        }
        if (originalName.equals("baterial-spot")) {
            return "Baterial Spot";
        }
        if (originalName.equals("yellow-leaf-curl-virus")) {
            return "Yellow Leaf Curl Virus";
        }
        if (originalName.equals("healthy")) {
            return "Healthy";
        }
        if (originalName.equals("mosaic-virus")) {
            return "Mosaic Virus";
        }
        return originalName;
    }
}

