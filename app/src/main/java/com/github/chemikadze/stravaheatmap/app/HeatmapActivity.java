package com.github.chemikadze.stravaheatmap.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

import static com.github.chemikadze.stravaheatmap.app.StravaOverlayProvider.ActivityType;
import static com.github.chemikadze.stravaheatmap.app.StravaOverlayProvider.Color;


public class HeatmapActivity extends Activity implements OnMapReadyCallback {

    private TileOverlay overlay;
    private Color currentColor = Color.RED;
    private ActivityType currentType = ActivityType.BOTH;
    private GoogleMap map;
    private int currentMapType = GoogleMap.MAP_TYPE_TERRAIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heatmap);
        setTitle(R.string.title_heatmap);
        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("activity_type", currentType.name());
        outState.putString("map_color", currentColor.name());
        if (map != null) {
            outState.putInt("map_type", map.getMapType());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("map_color")) {
            currentColor = Color.valueOf(savedInstanceState.getString("map_color"));
        }
        if (savedInstanceState.containsKey("activity_type")) {
            currentType = ActivityType.valueOf(savedInstanceState.getString("activity_type"));
        }
        if (savedInstanceState.containsKey("map_type")) {
            currentMapType = savedInstanceState.getInt("map_type");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        UiSettings settings = map.getUiSettings();
        settings.setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);
        settings.setZoomControlsEnabled(true);
        recreateTileOverlay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_heatmap, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.color_blue:
                currentColor = Color.BLUE;
                break;
            case R.id.color_red:
                currentColor = Color.RED;
                break;
            case R.id.color_yellow:
                currentColor = Color.YELLOW;
                break;
            case R.id.type_cycling:
                currentType = ActivityType.CYCLING;
                break;
            case R.id.type_running:
                currentType = ActivityType.RUNNING;
                break;
            case R.id.type_both:
                currentType = ActivityType.BOTH;
                break;
            case R.id.map_normal:
                currentMapType = GoogleMap.MAP_TYPE_NORMAL;
                break;
            case R.id.map_satellite:
                currentMapType = GoogleMap.MAP_TYPE_SATELLITE;
                break;
            case R.id.map_terrain:
                currentMapType = GoogleMap.MAP_TYPE_TERRAIN;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        recreateTileOverlay();
        return true;
    }

    private void recreateTileOverlay() {
        if (overlay != null) {
            overlay.remove();
        }
        TileProvider myTileProvider = new StravaOverlayProvider(currentType, currentColor);
        overlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(myTileProvider));
        map.setMapType(currentMapType);
    }
}
