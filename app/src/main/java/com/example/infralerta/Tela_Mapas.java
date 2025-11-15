package com.example.infralerta;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
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
    FloatingActionButton btLogout, btMais, btMapaMapa, btMapaDenuncia;
    MapView map;
    IMapController controlador;
    EditText txtPesquisa;
    String localselecionado;
    Drawable drawMarcador;
    MyLocationNewOverlay mLocationOverlay;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    private Address address;

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
        controlador.setZoom(19.0);

        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx), map);

        // checando se as permissões de localização foram fornecidas, se não, pedimos ela
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissoes = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            pedirPermissoesSeNecessario(permissoes);
        } else {
            obterLocalizacao();
        }

        drawMarcador = ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_location_on_48, null);

        btLogout = findViewById(R.id.btLogout);
        btMais = findViewById(R.id.btMais);
        btMapaMapa = findViewById(R.id.btmapamapa);
        btMapaDenuncia = findViewById(R.id.btdenunciasmapa);

        btLogout.setOnClickListener(v -> logout());

        btMais.setOnClickListener(view -> {
            if (txtPesquisa.length()!=0) {
                Intent Problema = new Intent(Tela_Mapas.this, Tela_Problemas.class);
                localselecionado = txtPesquisa.getText().toString();

                String localSelecionado = localselecionado.substring(0, 1).toUpperCase() + localselecionado.substring(1);
                String coordenadas = String.format("%f,%f", address.getLatitude(), address.getLongitude());

                Problema.putExtra("local", localSelecionado);
                Problema.putExtra("coordenadas", coordenadas);
                startActivity(Problema);
            } else {
                Toast.makeText(this, "Pesquise uma localização antes de criar uma denúncia", Toast.LENGTH_SHORT).show();
            }

        });

        btMapaMapa.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] permissoes = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                pedirPermissoesSeNecessario(permissoes);
            } else {
                obterLocalizacao();
            }
        });

        btMapaDenuncia.setOnClickListener((v -> {
            Intent Denuncia = new Intent(Tela_Mapas.this, Tela_Denuncias.class);
            startActivity(Denuncia);
        }));
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.clear();
        editor.apply();
        finish();
    }

    public void pesquisarEndereco() {
        String endereco = txtPesquisa.getText().toString() + ", Guarulhos";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                GeocoderNominatim geocoder = new GeocoderNominatim(userAgent);
                try {
                    List<Address> addressList = geocoder.getFromLocationName(endereco,1);
                    if (addressList != null && !addressList.isEmpty()) {
                        address = addressList.get(0);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                IMapController controlador = map.getController();
                                GeoPoint locEndereco = new GeoPoint(address.getLatitude(),address.getLongitude());
                                controlador = map.getController();
                                controlador.animateTo(locEndereco);
                                controlador.setZoom(19.0);

                                Marker marcadorPesquisa = new Marker(map);
                                marcadorPesquisa.setTitle("Você pesquisou este endereco!");
                                marcadorPesquisa.setSubDescription("Clique para dispensar essa mensagem");
                                marcadorPesquisa.setPosition(locEndereco);
                                marcadorPesquisa.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                marcadorPesquisa.setIcon(drawMarcador);
                                map.getOverlays().add(marcadorPesquisa);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Tela_Mapas.this, "Local não encontrado. Pesquise uma localização de Guarulhos válida", Toast.LENGTH_SHORT).show();
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
                        controlador.setZoom(19.0);
                        controlador.setCenter(locUsuario);
                        Marker marcadorUsuario = new Marker(map);
                        marcadorUsuario.setTitle("Você está aqui!");
                        marcadorUsuario.setSubDescription("Clique para dispensar essa mensagem");
                        marcadorUsuario.setPosition(locUsuario);
                        marcadorUsuario.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        marcadorUsuario.setIcon(drawMarcador);
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