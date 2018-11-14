package placesharing.activities;

import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

import placesharing.R;
import placesharing.model.Place;

public class HomePageFragment extends LocalisationHandlingFragment implements OnMapReadyCallback, OnMyLocationButtonClickListener {

    private static final String TAG = "MAPFRAGMENT";
    private static final String NEW_POS_TITLE = "Nouvelle position";
    private static final String NEW_POS_SNIPPET = "Appuyez ici pour ajouter ce lieux.";
    private static final int ZOOM = 15;
    private DatabaseReference mPlacesRef;
    private Marker mMarker;
    private LatLng lastPositionMarker;
    private String lastTitleMarker;
    private CameraPosition mCameraPositionSaved;
    private GoogleMap googleMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPlacesRef = FirebaseDatabase.getInstance().getReference().child("lieux");
        if(savedInstanceState != null){
            mCameraPositionSaved = savedInstanceState.getParcelable("camera");
            if(savedInstanceState.getParcelable("mMarkerPosition") != null){
                lastPositionMarker = savedInstanceState.getParcelable("mMarkerPosition");
                lastTitleMarker = savedInstanceState.getString("mMarkerTitle");
            }
        }
        else{
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, SupportMapFragment.newInstance()).commit();
        return super.onCreateView(inflater, container, savedInstanceState);
    }



    @Override
    public void onStart() {
        super.onStart();
        final SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
        mapFragment.getMapAsync(this);
    }

    private void loadPlaces(Place place) {
        if(place == null) return;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(place.getLatitude(),place.getLongitude()));
        markerOptions.title(place.getNom());
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        markerOptions.snippet(place.getDescription() + " Cliquez pour plus d'informations.");
        googleMap.addMarker(markerOptions);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.main_menu_bar, menu);
        MenuItem searchItem = menu.findItem(R.id.action_recherche);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onMapSearch(searchView);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onMapSearch(SearchView searchView) {
        String location = searchView.getQuery().toString();
        List<Address> addressList = null;

        if (!location.isEmpty()) {
            Geocoder geocoder = new Geocoder(getActivity());
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(!geocoder.isPresent() || addressList == null){
                Toast.makeText(getActivity(),getString(R.string.error_device_might_reboot),Toast.LENGTH_SHORT).show();
                return;
            }
            if (addressList.size() > 0) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                nouveauMarker(latLng,address.getAddressLine(0));
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_result), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(googleMap!=null){
            outState.putParcelable("camera", googleMap.getCameraPosition());
        }
        if (mMarker != null){
            outState.putParcelable("mMarkerPosition",mMarker.getPosition());
            outState.putString("mMarkerTitle",mMarker.getTitle());
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setPadding(0, 0, 0, 0);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        if (checkPermission()) {
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    return maPositionClick();
                }
            });
        }

        if(mCameraPositionSaved != null){
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPositionSaved));
        }
        if(lastPositionMarker!=null && lastTitleMarker != null){
            nouveauMarker(lastPositionMarker,lastTitleMarker);
        }

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                nouveauMarker(latLng,null);
            }
        });

        ValueEventListener placesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    loadPlaces(postSnapshot.getValue(Place.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(getActivity(),"Erreur lors du chargement des lieux.",Toast.LENGTH_SHORT).show();
                return;
            }
        };
        mPlacesRef.addValueEventListener(placesListener);

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                ((MainActivity) getActivity()).currentMarkerOptions = new MarkerOptions().position(marker.getPosition()).title(marker.getTitle()).snippet(marker.getSnippet());
                if(marker.getTitle().equals(NEW_POS_TITLE) || marker.getSnippet().equals(NEW_POS_SNIPPET)){
                    ((MainActivity) getActivity()).displayView(R.id.ajout_lieu);
                }
                else{
                        ((MainActivity) getActivity()).displayView(R.id.place_info);
                    }
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),ZOOM));
                marker.showInfoWindow();
                return true;
            }
        });

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return maPositionClick();
    }

    private boolean maPositionClick(){
        if(mCurrentLocation == null){
            Toast.makeText(getContext(),getString(R.string.no_location_try_again),Toast.LENGTH_SHORT).show();
            return false;
        }
        return nouveauMarker(mCurrentLocation,null);
    }

    private boolean nouveauMarker(LatLng position, @Nullable String titre){
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, ZOOM));

        MarkerOptions markerOptions = new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        if(titre == null || titre.trim().isEmpty() ){
            markerOptions.title(NEW_POS_TITLE);
        }
        else{
            markerOptions.title(titre);
        }
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        markerOptions.snippet(NEW_POS_SNIPPET);

        if(markerOptions != ((MainActivity) getActivity()).currentMarkerOptions) {
            ((MainActivity) getActivity()).currentMarkerOptions = markerOptions;
            if (mMarker != null) {
                mMarker.remove();
            }
            mMarker = googleMap.addMarker(((MainActivity) getActivity()).currentMarkerOptions);
            mMarker.showInfoWindow();
            return true;
        }
        return false;
    }
    private boolean nouveauMarker(Location position, @Nullable String titre){
        if(position == null){
            Toast.makeText(getContext(),getString(R.string.no_location_try_again),Toast.LENGTH_SHORT).show();
            return false;
        }
        return nouveauMarker(new LatLng(position.getLatitude(), position.getLongitude()), titre);
    }
}
