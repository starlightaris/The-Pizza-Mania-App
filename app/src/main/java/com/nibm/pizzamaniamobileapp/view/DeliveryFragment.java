package com.nibm.pizzamaniamobileapp.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.maps.SupportMapFragment;
import com.nibm.pizzamaniamobileapp.R;

public class DeliveryFragment extends Fragment {

    private TextView txtOrderDetails, txtStatus;
    private LottieAnimationView lottieStatus;
    private SupportMapFragment mapFragment;

    private enum OrderStatus { PENDING, PREPARING, OUT_FOR_DELIVERY, DELIVERED }
    private OrderStatus currentStatus = OrderStatus.PENDING;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery, container, false);

        txtOrderDetails = view.findViewById(R.id.txtOrderDetails);
        txtStatus = view.findViewById(R.id.txtStatus);
        lottieStatus = view.findViewById(R.id.lottieStatus);

        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);

        // Example initial
        txtOrderDetails.setText("Order #145 | Bill: LKR 3200");
        updateUI(OrderStatus.PENDING);

        return view;
    }

    private void updateUI(OrderStatus status) {
        currentStatus = status;

        // Hide map initially
        if (mapFragment != null && mapFragment.getView() != null) {
            mapFragment.getView().setVisibility(View.GONE);
        }

        // Update status label
        txtStatus.setText("Status: " + status.name());

        // Handle animations
        lottieStatus.setVisibility(View.GONE);
        switch (status) {
            case PENDING:
                lottieStatus.setAnimation(R.raw.processing);
                lottieStatus.setVisibility(View.VISIBLE);
                lottieStatus.playAnimation();
                break;

            case PREPARING:
                lottieStatus.setAnimation(R.raw.cooking);
                lottieStatus.setVisibility(View.VISIBLE);
                lottieStatus.playAnimation();
                break;

            case OUT_FOR_DELIVERY:
                if (mapFragment != null && mapFragment.getView() != null) {
                    mapFragment.getView().setVisibility(View.VISIBLE);
                }
                break;

            case DELIVERED:
                lottieStatus.setAnimation(R.raw.check);
                lottieStatus.setVisibility(View.VISIBLE);
                lottieStatus.playAnimation();
                break;
        }
    }

    // Example: hook this to Firebase or repo updates
    private void onStatusChangedFromServer(String newStatus) {
        switch (newStatus) {
            case "pending": updateUI(OrderStatus.PENDING); break;
            case "preparing": updateUI(OrderStatus.PREPARING); break;
            case "out": updateUI(OrderStatus.OUT_FOR_DELIVERY); break;
            case "delivered": updateUI(OrderStatus.DELIVERED); break;
        }
    }
}
