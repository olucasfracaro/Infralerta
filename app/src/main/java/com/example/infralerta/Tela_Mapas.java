package com.example.infralerta;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.BuildConfig;
import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Tela_Mapas extends AppCompatActivity {
    Button btMais;
    FloatingActionButton btMapaMapa, btMapaDenuncia;
    MapView map;
    EditText txtPesquisa;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    static final String userAgent = BuildConfig.LIBRARY_PACKAGE_NAME+"/"+BuildConfig.VERSION_NAME;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_mapas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        txtPesquisa = findViewById(R.id.txtPesquisa);
        txtPesquisa.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    pesquisarEndereco();
                    return true;
                }
                return false;
            }
        });

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        IMapController controlador = map.getController();
        controlador.setZoom(20.0);

        GeoPoint pontoInicio = new GeoPoint(-23.4667301, -46.5403522,15);
        controlador.setCenter(pontoInicio);

        btMais = findViewById(R.id.btMais);
        btMapaMapa = findViewById(R.id.btmapamapa);
        btMapaDenuncia = findViewById(R.id.btdenunciasmapa);
        btMais.setOnClickListener(view -> {
            Intent Problema = new Intent(Tela_Mapas.this, Tela_Problemas.class);
            startActivity(Problema);
        });
        btMapaMapa.setOnClickListener(v -> {

        });

        btMapaDenuncia.setOnClickListener((v -> {
            Intent Denuncia = new Intent(Tela_Mapas.this, Tela_Denuncias.class);
            startActivity(Denuncia);
        }));
    }

    public void pesquisarEndereco() {
        String endereco = txtPesquisa.getText().toString();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                GeocoderNominatim geocoder = new GeocoderNominatim(userAgent);
                try {
                    List<Address> addressList = geocoder.getFromLocationName(endereco,1);
                    if (addressList != null && !addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                IMapController controlador = map.getController();
                                GeoPoint locEndereco = new GeoPoint(address.getLatitude(),address.getLongitude());
                                controlador.setCenter(locEndereco);
                                controlador.setZoom(20.0);

                                Marker marcadorInicio = new Marker(map);
                                marcadorInicio.setTitle("Você pesquisou este local!");
                                marcadorInicio.setSubDescription("Clique para dispensar essa mensagem");
                                marcadorInicio.setPosition(locEndereco);
                                marcadorInicio.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                map.getOverlays().add(marcadorInicio);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}