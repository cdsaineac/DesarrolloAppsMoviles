package co.edu.unal.reto10_webservices;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SearchView;
import android.widget.Spinner;
import org.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {
    String[] Localidades = {
            "Todas las localidades"
            ,"Soacha"
            ,"Usaquen"
            ,"Chapinero"
            ,"Santa Fe"
            ,"San Cristóbal"
            ,"Usme"
            ,"Tunjuelito"
            ,"Bosa"
            ,"Kennedy"
            ,"Fontibón"
            ,"Engativa"
            ,"Suba"
            ,"Teusaquillo"
            ,"Puente Aranda"
            ,"Candelaria"
            ,"Rafael Uribe Uribe"
            ,"Ciudad Bolivar"
    };

    private GeoPoint startPoint;

    private SearchView barraBusqueda;
    private Button filterButton;
    private LinearLayout searchBiblioteca;
    private LinearLayout listLocalidades;
    private Spinner spinnerLocalidades;
    private MapView mapa;

    private String bibliotecaBuscada = "";
    private String localidadSeleccionada = "";
    private boolean filterHidden = true;
    private JSONArray bibliotecasResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
        initListaDesplegable();
        initBarraBusqueda();
        initDataArray();
        hideFilterLocalidades();
        initMapa();
    }


    private void initDataArray() {
        try {
            bibliotecasResultado = new getDataTask().execute(bibliotecaBuscada,localidadSeleccionada).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initMapa(){
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        mapa = (MapView) findViewById(R.id.mapa);
        mapa.setTileSource(TileSourceFactory.MAPNIK);
        mapa.setBuiltInZoomControls(true);
        mapa.setMultiTouchControls(true);
        IMapController mapController = mapa.getController();
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
        paintMyLocation();
        getData();
    }

    private void paintMyLocation() {
        Context ctx = getApplicationContext();
        IMapController mapController = mapa.getController();

        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), mapa);
        mLocationOverlay.enableMyLocation();
        //mLocationOverlay.enableFollowLocation();
        mLocationOverlay.setDrawAccuracyEnabled(true);
        mapController.animateTo(startPoint);
        mapa.getOverlays().add(mLocationOverlay);
    }

    private void paintMarker(JSONObject object) {
        double latitude = 0;
        double longitud = 0;
        String id = "0";
        String title = "";
        String snippet = "";
        try {
            id = object.getString("objectid");
            String pointY = object.getString("point_y").replace(",",".");
            String pointX = object.getString("point_x").replace(",",".");
            title = object.getString("bconombre");
            snippet = object.getString("bcodirecci");
            latitude = Double.parseDouble(pointY);
            longitud = Double.parseDouble(pointX);

        } catch (JSONException e) {
            System.out.println("ERROR");
        }
        GeoPoint point = new GeoPoint(latitude,longitud);
        Marker marker = new Marker(mapa);
        marker.setId("Bibliotk");
        marker.setTitle(title);
        marker.setSnippet(snippet);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapa.getOverlays().add(marker);
    }

    private void initListaDesplegable(){
        spinnerLocalidades = (Spinner) findViewById(R.id.spinnerLocalidades);
        spinnerLocalidades.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,Localidades);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocalidades.setAdapter(aa);
    }

    private void initBarraBusqueda() {
        barraBusqueda =  (SearchView) findViewById(R.id.barraBusqueda);
        barraBusqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                callSearch(newText);
                return true;
            }

            public void callSearch(String query) {
                bibliotecaBuscada = query.toUpperCase();
                getData();
            }

        });

    }

    private JSONArray getData(){
        JSONArray result = new JSONArray();
        cleanMarkers();
        for (int i = 0; i < bibliotecasResultado.length(); i++) {
            try {
                JSONObject object = (JSONObject) bibliotecasResultado.get(i);
                System.out.println(bibliotecaBuscada);
                System.out.println();
                if(localidadSeleccionada.equals("")){
                    if(object.getString("bconombre").toUpperCase().contains(bibliotecaBuscada.toUpperCase())){
                        result.put(object);
                        paintMarker(object);
                    }
                }else{
                    if(object.getString("bconombre").toUpperCase().contains(bibliotecaBuscada.toUpperCase()) && object.getString("bconomloc").equals(localidadSeleccionada)){
                        result.put(object);
                        paintMarker(object);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        System.out.println(result.length());
        System.out.println(result);
        return result;
    }

    private void cleanMarkers() {
        /*for (int j = 0; j < mapa.getOverlays().size(); j++) {
            Overlay possibleMarker = mapa.getOverlays().get(j);
            if(possibleMarker instanceof Marker){
                if(((Marker) possibleMarker).getId().equals("Bibliotk")){
                mapa.getOverlays().remove(possibleMarker);
                }
            }
        }
        */
        mapa.getOverlays().clear();
        paintMyLocation();
    }

    private void initWidgets() {
        searchBiblioteca = (LinearLayout) findViewById(R.id.searchBiblioteca);
        filterButton =  (Button) findViewById(R.id.filterButton);
        listLocalidades = (LinearLayout) findViewById(R.id.ListLocalidades);
    }

    private void hideFilterLocalidades() {
        listLocalidades.setVisibility(View.GONE);
        spinnerLocalidades.setVisibility(View.GONE);
    }
    private void showFilterLocalidades() {
        listLocalidades.setVisibility(View.VISIBLE);
        listLocalidades.invalidate();
        spinnerLocalidades.setVisibility(View.VISIBLE);
        spinnerLocalidades.invalidate();
    }

    public void handleFilterButtonClick(View view) {
        if (filterHidden){
            filterHidden = false;
            showFilterLocalidades();
        }else{
            filterHidden = true;
            hideFilterLocalidades();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        if(position == 0){
            localidadSeleccionada = "";
        }else{
            localidadSeleccionada = Localidades[position];
        }
        getData();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        localidadSeleccionada = "";
        getData();
    }
}