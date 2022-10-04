package co.edu.unal.reto8_sqlite;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import co.edu.unal.reto8_sqlite.EmpresaController;
import co.edu.unal.reto8_sqlite.Empresa;

public class EditarEmpresaActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText etEditName, etEditUrl, etEditPhone, etEditEmail;
    private Spinner etEditType;
    private Button btnGuardarCambios, btnCancelarEdicion;
    private Empresa empresa;
    private EmpresaController empresasController;
    private long newTypeValidated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_empresa);

        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        // Si no hay datos (cosa rara) salimos
        if (extras == null) {
            finish();
            return;
        }

        empresasController = new EmpresaController(EditarEmpresaActivity.this);

        empresa = empresasController.obtenerEmpresa(extras.getLong("CompanyId"));
        if (extras != null) {
            for (String key : extras.keySet()) {
                System.out.println(key + " : " + (extras.get(key) != null ? extras.get(key) : "NULL"));
            }
        }

        // Ahora declaramos las vistas
        etEditName = findViewById(R.id.etEditName);
        etEditUrl = findViewById(R.id.etEditUrl);
        etEditPhone = findViewById(R.id.etEditPhone);
        etEditEmail = findViewById(R.id.etEditEmail);
        etEditType = findViewById(R.id.etEditType);

        etEditType.setOnItemSelectedListener(this);

        // Loading spinner data from database
        loadTypeData();

        btnCancelarEdicion = findViewById(R.id.btnCancelarEdicion);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);

        // Rellenar los EditText con los datos de la mascota
        etEditName.setText(empresa.getName());
        etEditUrl.setText(empresa.getUrl());
        etEditPhone.setText(empresa.getPhone());
        etEditEmail.setText(empresa.getEmail());

        // Listener del click del botón para salir, simplemente cierra la actividad
        btnCancelarEdicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Listener del click del botón que guarda cambios
        btnGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remover previos errores si existen
                etEditName.setError(null);
                etEditUrl.setError(null);
                etEditPhone.setError(null);
                etEditEmail.setError(null);

                String newName = etEditName.getText().toString();
                String newURL = etEditUrl.getText().toString();
                String newPhone = etEditPhone.getText().toString();
                String newEmail = etEditEmail.getText().toString();
                if (newName.isEmpty()) {
                    etEditName.setError("Insert Name");
                    etEditName.requestFocus();
                    return;
                }
                if (newURL.isEmpty()) {
                    etEditUrl.setError("Insert URL");
                    etEditUrl.requestFocus();
                    return;
                }
                if (newPhone.isEmpty()) {
                    etEditPhone.setError("Insert URL");
                    etEditPhone.requestFocus();
                    return;
                }
                if (newEmail.isEmpty()) {
                    etEditEmail.setError("Insert URL");
                    etEditEmail.requestFocus();
                    return;
                }

                // Si llegamos hasta aquí es porque los datos ya están validados
                Empresa empresaConNuevosCambios = new Empresa(newName, newURL,newPhone,newEmail,newTypeValidated, empresa.getId());
                int filasModificadas = empresasController.guardarCambios(empresaConNuevosCambios);
                if (filasModificadas != 1) {
                    // De alguna forma ocurrió un error porque se debió modificar únicamente una fila
                    Toast.makeText(EditarEmpresaActivity.this, "Error guardando cambios. Intente de nuevo.", Toast.LENGTH_SHORT).show();
                } else {
                    // Si las cosas van bien, volvemos a la principal
                    // cerrando esta actividad
                    finish();
                }
            }
        });
    }

    private void loadTypeData() {
        AyudanteBaseDeDatos db = new AyudanteBaseDeDatos(getApplicationContext());
        List<String> labels = db.getTypes();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labels);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        etEditType.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String label = parent.getItemAtPosition(position).toString();

        if(label.equals("Consultoria")){
            newTypeValidated = 1;
        }else if (label.equals("Desarrollo a medida")){
            newTypeValidated = 2;
        }else{
            newTypeValidated = 3;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        newTypeValidated =1;
    }
}




