package com.nibm.pizzamaniamobileapp.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.android.PolyUtil;
import com.nibm.pizzamaniamobileapp.R;
import com.nibm.pizzamaniamobileapp.model.Order;
import com.nibm.pizzamaniamobileapp.utils.LocationService;
import com.nibm.pizzamaniamobileapp.viewmodel.DeliveryViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DeliveryFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private MapView mapView;
    private DeliveryViewModel viewModel;
    private LocationService locationService;

    // UI Elements
    private LottieAnimationView animationStatus;
    private LinearLayout animationContainer;
    private TextView tvOrderStatus, tvProgressText, tvDeliveryAddress, tvCustomerName, tvTotalPrice;
    private ProgressBar progressDelivery;
    private Button btnContact;

    // Map elements
    private Marker userMarker, branchMarker;
    private Polyline routePolyline;
    private LatLng branchLatLng;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery, container, false);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(DeliveryViewModel.class);

        // Initialize UI elements
        initializeUI(view);

        // MapView lifecycle
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        // Location service
        locationService = new LocationService(requireContext());

        // Load current order
        findCurrentOrder();
        setupObservers();

        return view;
    }

    private void initializeUI(View view) {
        mapView = view.findViewById(R.id.mapView);
        animationContainer = view.findViewById(R.id.animationContainer);
        animationStatus = view.findViewById(R.id.animationStatus);
        tvOrderStatus = view.findViewById(R.id.tvOrderStatus);
        tvProgressText = view.findViewById(R.id.tvProgressText);
        progressDelivery = view.findViewById(R.id.progressDelivery);
        tvCustomerName = view.findViewById(R.id.tvCustomerName);
        tvDeliveryAddress = view.findViewById(R.id.tvDeliveryAddress);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        btnContact = view.findViewById(R.id.btnContact);

        btnContact.setOnClickListener(v -> contactRestaurant());
    }

    public static DeliveryFragment newInstance(String orderId) {
        DeliveryFragment fragment = new DeliveryFragment();
        Bundle args = new Bundle();
        args.putString("orderId", orderId);
        fragment.setArguments(args);
        return fragment;
    }

    private void findCurrentOrder() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("orders")
                .whereEqualTo("userId", userId)
                .whereIn("status", Arrays.asList("pending", "preparing", "out_for_delivery"))
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String orderId = document.getId();
                        viewModel.startListeningToOrder(orderId);
                        fetchOrderDetails(orderId);
                    } else {
                        showNoActiveOrders();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Error finding orders: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void fetchOrderDetails(String orderId) {
        FirebaseFirestore.getInstance().collection("orders").document(orderId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Order order = documentSnapshot.toObject(Order.class);
                        if (order != null) {
                            fetchBranchLocation(order.getBranchId());
                            tvDeliveryAddress.setText(order.getDeliveryAddress());
                            tvCustomerName.setText(order.getCustomerName());
                            tvTotalPrice.setText(String.format("$%.2f", order.getTotalPrice()));
                        }
                    }
                });
    }

    private void fetchBranchLocation(String branchId) {
        FirebaseFirestore.getInstance().collection("branches").document(branchId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        GeoPoint geoPoint = documentSnapshot.getGeoPoint("location");
                        if (geoPoint != null) {
                            branchLatLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                            addBranchMarker();
                        }
                    }
                });
    }

    private void setupObservers() {
        viewModel.getOrderLiveData().observe(getViewLifecycleOwner(), this::updateOrderUI);
        viewModel.getUserLocationLiveData().observe(getViewLifecycleOwner(), this::updateUserLocation);
        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
        });
    }

    private void updateOrderUI(Order order) {
        if (order == null) return;

        updateStatusUI(order.getStatus());
        updateProgress(order.getStatus());

        switch (order.getStatus()) {
            case "pending":
            case "preparing":
            case "delivered":
                mapView.setVisibility(View.GONE);
                animationContainer.setVisibility(View.VISIBLE);
                break;

            case "out_for_delivery":
                mapView.setVisibility(View.VISIBLE);
                animationContainer.setVisibility(View.GONE);
                break;
        }

        if ("delivered".equals(order.getStatus())) {
            locationService.stopLocationUpdates();
            Toast.makeText(requireContext(), "Order delivered successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateStatusUI(String status) {
        int animationRes = R.raw.processing;
        String statusText = "Order Received";

        switch (status) {
            case "pending":
                animationRes = R.raw.processing;
                statusText = "Order Received";
                break;
            case "preparing":
                animationRes = R.raw.cooking;
                statusText = "Preparing Your Order";
                break;
            case "out_for_delivery":
                animationRes = R.raw.processing;
                statusText = "Out for Delivery";
                break;
            case "delivered":
                animationRes = R.raw.check;
                statusText = "Delivered";
                break;
        }

        tvOrderStatus.setText(statusText);
        animationStatus.setAnimation(animationRes);
        animationStatus.playAnimation();
    }

    private void updateProgress(String status) {
        int progress = 0;
        String progressText = "";

        switch (status) {
            case "pending":
                progress = 25;
                progressText = "Order Received";
                break;
            case "preparing":
                progress = 50;
                progressText = "Preparing Food";
                break;
            case "out_for_delivery":
                progress = 75;
                progressText = "On the Way";
                break;
            case "delivered":
                progress = 100;
                progressText = "Delivered";
                break;
        }

        progressDelivery.setProgress(progress);
        tvProgressText.setText(progressText);
    }

    private void showNoActiveOrders() {
        tvOrderStatus.setText("No active deliveries");
        animationStatus.setAnimation(R.raw.empty_state);
        animationStatus.playAnimation();
        mapView.setVisibility(View.GONE);
        animationContainer.setVisibility(View.VISIBLE);
    }

    private void contactRestaurant() {
        Toast.makeText(requireContext(), "Contacting restaurant...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        setupMap();

        if (checkLocationPermission()) startLocationTracking();
        else requestLocationPermission();
    }

    private void setupMap() {
        if (googleMap == null) return;

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationTracking() {
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    viewModel.updateUserLocation(latLng);
                }
            }
        };

        locationService.startLocationUpdates(locationCallback);
    }

    private void updateUserLocation(LatLng location) {
        updateUserMarker(location);
        updateCameraPosition(location);
    }

    private void updateUserMarker(LatLng position) {
        if (googleMap != null) {
            if (userMarker != null) userMarker.setPosition(position);
            else userMarker = googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title("Your Location")
                    .icon(bitmapDescriptorFromVector(R.drawable.ic_user_location)));
        }
    }

    private void addBranchMarker() {
        if (googleMap != null && branchLatLng != null) {
            if (branchMarker != null) branchMarker.remove();
            branchMarker = googleMap.addMarker(new MarkerOptions()
                    .position(branchLatLng)
                    .title("Restaurant")
                    .icon(bitmapDescriptorFromVector(R.drawable.ic_restaurant)));
            if (userMarker == null) googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(branchLatLng, 15));
        }
    }

    private void updateCameraPosition(LatLng userLocation) {
        if (userLocation != null && branchLatLng != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(userLocation);
            builder.include(branchLatLng);

            try {
                LatLngBounds bounds = builder.build();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            } catch (IllegalStateException e) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
            }
        } else if (userLocation != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(requireContext(), vectorResId);
        if (vectorDrawable == null) return BitmapDescriptorFactory.defaultMarker();
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationTracking();
            if (googleMap != null && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
        } else {
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void drawRoute(LatLng origin, LatLng destination) {
        String apiKey = getString(R.string.google_maps_key);
        String url = "https://maps.googleapis.com/maps/api/directions/json" +
                "?origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&mode=driving" +
                "&key=" + apiKey;

        new FetchRouteTask().execute(url);
    }

    private class FetchRouteTask extends AsyncTask<String, Void, List<LatLng>> {
        @Override
        protected List<LatLng> doInBackground(String... strings) {
            String url = strings[0];
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();

            try (Response response = client.newCall(request).execute()) {
                if (response.body() == null) return null;

                String json = response.body().string();
                JSONObject obj = new JSONObject(json);
                JSONArray steps = obj.getJSONArray("routes").getJSONObject(0)
                        .getJSONArray("legs").getJSONObject(0)
                        .getJSONArray("steps");

                List<LatLng> poly = new ArrayList<>();
                for (int i = 0; i < steps.length(); i++) {
                    String polyline = steps.getJSONObject(i).getJSONObject("polyline").getString("points");
                    poly.addAll(PolyUtil.decode(polyline));
                }
                return poly;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<LatLng> points) {
            if (points != null && googleMap != null) {
                if (routePolyline != null) routePolyline.remove();
                routePolyline = googleMap.addPolyline(new PolylineOptions().addAll(points).color(0xFF2196F3).width(10));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        locationService.stopLocationUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        locationService.stopLocationUpdates();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
