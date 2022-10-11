package co.edu.unal.reto9_osm;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.SearchView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.slider.Slider;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.utils.BonusPackHelper;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static MapView map = null;
    private static ArrayList<POI> mPOIs; //made static to pass between activities
    private static RadiusMarkerClusterer mPoiMarkers;
    GeoPoint startPoint;
    double distance;
    int results = 10;
    String query ;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_main);

        map = (MapView) findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(9.5);

        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                            } else {
                                // No location access granted.
                            }
                        }
                );
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        startPoint = new GeoPoint(bestLocation.getLatitude(), bestLocation.getLongitude());


        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), map);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mLocationOverlay.setDrawAccuracyEnabled(true);
        mapController.animateTo(startPoint);
        map.getOverlays().add(mLocationOverlay);

        mPoiMarkers = new RadiusMarkerClusterer(this);
        Bitmap clusterIcon = BonusPackHelper.getBitmapFromVectorDrawable(this, R.drawable.marker_poi);
        mPoiMarkers.setIcon(clusterIcon);
        mPoiMarkers.mAnchorV = Marker.ANCHOR_BOTTOM;
        mPoiMarkers.mTextAnchorU = 0.70f;
        mPoiMarkers.mTextAnchorV = 0.27f;
        mPoiMarkers.getTextPaint().setTextSize(12 * getResources().getDisplayMetrics().density);
        map.getOverlays().add(mPoiMarkers);

        SearchView simpleSearchView = (SearchView) findViewById(R.id.simpleSearchView); // inititate a search view
        // perform set on query text listener event
        simpleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String a) {
                query = a;
                getPOIAsync(ctx);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                query = newText;
                getPOIAsync(ctx);
                return false;
            }
        });
        Slider slider = (Slider) findViewById(R.id.slider);
        slider.addOnChangeListener((slider1, value, fromUser) -> {
            distance = value/1000;
            getPOIAsync(ctx);
        });

    }

    private void getPOIAsync(Context ctx) {
        for (Overlay a :
                map.getOverlays()) {
            if(!a.getClass().toString().contains("location")){
                map.getOverlays().remove(a);
            }
        }
        mPoiMarkers = new RadiusMarkerClusterer(ctx);
        Bitmap clusterIcon = BonusPackHelper.getBitmapFromVectorDrawable(this, R.drawable.marker_poi);
        mPoiMarkers.setIcon(clusterIcon);
        mPoiMarkers.mAnchorV = Marker.ANCHOR_BOTTOM;
        mPoiMarkers.mTextAnchorU = 0.70f;
        mPoiMarkers.mTextAnchorV = 0.27f;
        mPoiMarkers.getTextPaint().setTextSize(12 * getResources().getDisplayMetrics().density);
        map.getOverlays().add(mPoiMarkers);
        new POILoadingTask().execute();
    }


    private class POILoadingTask extends AsyncTask<GeoPoint, Void, ArrayList<POI>> {
        NominatimPOIProvider poiProvider = new NominatimPOIProvider("OSMBonusPackTutoUserAgent");

        protected ArrayList<POI> doInBackground(GeoPoint... params) {

            ArrayList<POI> pois = poiProvider.getPOICloseTo(startPoint, query, results, distance);
            return pois;
        }

        protected void onPostExecute(ArrayList<POI> pois) {
            mPOIs = pois;
            updateUIWithPOI(mPOIs);
        }

        void updateUIWithPOI(ArrayList<POI> pois) {
            Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.marker_poi, null);
            if (pois != null) {
                for (POI poi : mPOIs) {
                    Marker poiMarker = new Marker(map);
                    poiMarker.setTitle(poi.mType);
                    poiMarker.setId("Marker");
                    poiMarker.setSnippet(poi.mDescription);
                    poiMarker.setPosition(poi.mLocation);
                    poiMarker.setIcon(icon);
                    if (poi.mThumbnail != null) {
                        poiMarker.setImage(new BitmapDrawable(poi.mThumbnail));
                    }
                    mPoiMarkers.add(poiMarker);
                }
            }else{
                System.out.println("pois is null");
            }
            map.getOverlays().add(mPoiMarkers);
            mPoiMarkers.invalidate();
            map.invalidate();
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }
}