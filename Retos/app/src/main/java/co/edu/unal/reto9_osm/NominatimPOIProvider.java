package co.edu.unal.reto9_osm;

import android.os.AsyncTask;

import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class POIsActivity extends AsyncTask<GeoPoint,Void,Integer>{
    @Override
    protected Integer doInBackground(GeoPoint... geoPoints) {
        int count = geoPoints.length;
        ArrayList<POI> pois = new ArrayList<>();
        long totalSize = 0;
        for (int i = 0; i < count; i++) {
            NominatimPOIProvider poiProvider = new NominatimPOIProvider("OSMBonusPackTutoUserAgent");
            pois = poiProvider.getPOICloseTo(new GeoPoint(48.8583, 2.2944), "cinema", 50, 0.1);
            // Escape early if cancel() is called
            if (isCancelled()) break;
        }
        return null;
    }
}
