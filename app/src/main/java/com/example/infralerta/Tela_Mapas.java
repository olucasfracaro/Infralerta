package com.example.infralerta;

import android.Manifest;
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
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Tela_Mapas extends AppCompatActivity {
    FloatingActionButton btMais, btMapaMapa, btMapaDenuncia;
    MapView map;
    IMapController controlador;
    EditText txtPesquisa;
    MyLocationNewOverlay mLocationOverlay;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    // obtendo o nome do pacote; é necessário para identificar o client para acessar o mapa;
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
        // obtendo o endereço e pesquisando quando o botão de pesquisa (ou enter) é clicado
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
        controlador = map.getController();
        controlador.setZoom(20.0);

        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), map);

        // checando se as permissões de localização foram fornecidas, se não, pedimos ela
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissoes = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            pedirPermissoesSeNecessario(permissoes);
        } else {
            obterLocalizacao();
        }



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
                                controlador = map.getController();
                                controlador.animateTo(locEndereco);
                                controlador.setZoom(20.0);

                                Marker marcadorPesquisa = new Marker(map);
                                marcadorPesquisa.setTitle("Você pesquisou este local!");
                                marcadorPesquisa.setSubDescription("Clique para dispensar essa mensagem");
                                marcadorPesquisa.setPosition(locEndereco);
                                marcadorPesquisa.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                map.getOverlays().add(marcadorPesquisa);
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

    private void obterLocalizacao() {
        // aqui obtemos a localização do usuário e em seguida paramos de detectar a localização
        // em caso de erro ao puxar a localização, define-se o centro para o Centro de Guarulhos
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                GeoPoint locUsuario = mLocationOverlay.getMyLocation();

                map.post(() -> {
                    if (locUsuario != null) {
                        controlador.setZoom(20.0);
                        controlador.setCenter(locUsuario);
                        Marker marcadorUsuario = new Marker(map);
                        marcadorUsuario.setTitle("Você está aqui!");
                        marcadorUsuario.setSubDescription("Clique para dispensar essa mensagem");
                        marcadorUsuario.setPosition(locUsuario);
                        marcadorUsuario.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        map.getOverlays().add(marcadorUsuario);
                    } else {
                        GeoPoint pontoInicio = new GeoPoint(-23.4667301, -46.5403522,15);
                        controlador.setZoom(15.0);
                        controlador.setCenter(pontoInicio);
                    }
                });

                mLocationOverlay.disableMyLocation();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                // se a permissão é cancelada, os arrays de resultado estarão vazios
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    obterLocalizacao();
                } else {
                    GeoPoint pontoInicio = new GeoPoint(-23.4667301, -46.5403522,15);
                    controlador.setCenter(pontoInicio);
                }
        }
    }

    private void pedirPermissoesSeNecessario(String[] permissoes) {
        ArrayList<String> permissoesParaPedir = new ArrayList<>();
        for (String permissao : permissoes) {
            if (ContextCompat.checkSelfPermission(this, permissao)
                    != PackageManager.PERMISSION_GRANTED) {
                permissoesParaPedir.add(permissao);
            }
        }
        if (!permissoesParaPedir.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissoesParaPedir.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}