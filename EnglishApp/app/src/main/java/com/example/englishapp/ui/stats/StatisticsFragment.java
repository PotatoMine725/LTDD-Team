package com.example.englishapp.ui.stats;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.englishapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private LineChart lineChart;
    private DatabaseReference statisticsRef;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.statistics, container, false);

        lineChart = view.findViewById(R.id.lineChart);

        setupChart();
        loadStatisticsHistory();

        return view;
    }

    /**
     * Setup biểu đồ cho đẹp (CHỈ JAVA)
     */
    private void setupChart() {
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);

        // Description
        Description description = new Description();
        description.setText("Words learned per day");
        description.setTextColor(Color.GRAY);
        description.setTextSize(12f);
        lineChart.setDescription(description);

        // Trục X
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextColor(Color.GRAY);

        // không chia số thập phân
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        //ép hiển thị số nguyên
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value + 1);
            }
        });

        // Trục Y trái
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setTextColor(Color.GRAY);

        leftAxis.setGranularity(1f);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        // Tắt trục Y phải
        lineChart.getAxisRight().setEnabled(false);

        // Legend không cần
        lineChart.getLegend().setEnabled(false);
    }

    /**
     * Load data statistics_history từ Firebase
     */
    private void loadStatisticsHistory() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        statisticsRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("statistics")
                .child("statistics_history");

        statisticsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<Entry> entries = new ArrayList<>();
                int index = 0;

                for (DataSnapshot dateSnap : snapshot.getChildren()) {
                    Long words = dateSnap.child("words_learned").getValue(Long.class);
                    if (words != null) {
                        entries.add(new Entry(index++, words));
                    }
                }

                renderBeautifulChart(entries);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    /**
     * Vẽ biểu đồ MIỀN
     */
    private void renderBeautifulChart(List<Entry> entries) {

        LineDataSet dataSet = new LineDataSet(entries, "Words learned");

        // Đường cong mượt
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.25f);

        // Màu sắc
        dataSet.setColor(Color.parseColor("#3F51B5"));
        dataSet.setLineWidth(2.5f);

        // Điểm tròn nhỏ
        dataSet.setCircleColor(Color.parseColor("#3F51B5"));
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);

        // Fill (area)
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#C5CAE9"));
        dataSet.setFillAlpha(180);

        // Ẩn value
        dataSet.setDrawValues(false);

        LineData data = new LineData(dataSet);
        lineChart.setData(data);

        // Animation mượt
        lineChart.animateX(700);
        lineChart.invalidate();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tìm button notification trong statistics layout
        ImageView btnNotification = view.findViewById(R.id.btn_notification);

        // Thiết lập click listener
        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> showNotificationFragment());
        }
    }

    // Mở notification (DialogFragment)
    private void showNotificationFragment() {
        NotificationFragment fragment = new NotificationFragment();

        // Lấy FragmentManager từ Activity chứa Fragment này
        FragmentManager fragmentManager = getParentFragmentManager();

        // Hiển thị DialogFragment
        fragment.show(fragmentManager, "notification_dialog");
    }
}