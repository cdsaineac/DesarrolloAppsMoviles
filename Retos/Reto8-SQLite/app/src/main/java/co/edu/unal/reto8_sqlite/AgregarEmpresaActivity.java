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


public class AgregarEmpresaActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Button btnAgregarEmpresa, btnCancelarNuevaEmpresa;
    private EditText etName, etUrl, etPhone, etEmail;
    private Spinner etType;
    private EmpresaController empresasController;

    private long typeSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_empresa);

        // Instanciar vistas
        etName = findViewById(R.id.etName);
        etUrl = findViewById(R.id.etUrl);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etType = findViewById(R.id.etType);

        etType.setOnItemSelectedListener(this);

        // Loading spinner data from database  
        loadTypeData();

        btnAgregarEmpresa = findViewById(R.id.btnAgregarEmpresa);
        btnCancelarNuevaEmpresa = findViewById(R.id.btnCancelarNuevaEmpresa);
        // Crear el controlador
        empresasController = new EmpresaController(AgregarEmpresaActivity.this);

        // Agregar listener del botón de guardar
        btnAgregarEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Resetear errores a ambos
                etName.setError(null);
                etUrl.setError(null);
                etPhone.setError(null);
                etEmail.setError(null);
                String Name = etName.getText().toString();
                String URL = etUrl.getText().toString();
                String Phone = etPhone.getText().toString();
                String Email = etEmail.getText().toString();
                if ("".equals(Name)) {
                    etName.setError("Insert Name");
                    etName.requestFocus();
                    return;
                }
                if ("".equals(URL)) {
                    etUrl.setError("Insert URL");
                    etUrl.requestFocus();
                    return;
                }
                if ("".equals(Email)) {
                    etEmail.setError("Insert Email");
                    etEmail.requestFocus();
                    return;
                }
                if ("".equals(Phone)) {
                    etPhone.setError("Insert Phone");
                    etPhone.requestFocus();
                    return;
                }

                // Ya pasó la validación
                Empresa nuevaEmpresa = new Empresa(Name,URL,Phone,Email, typeSelected);
                long id = empresasController.nuevaEmpresa(nuevaEmpresa);
                if (id == -1) {
                    // De alguna manera ocurrió un error
                    Toast.makeText(AgregarEmpresaActivity.this, "Error Saving. Please try again", Toast.LENGTH_SHORT).show();
                } else {
                    // Terminar
                    finish();
                }
            }
        });

        // El de cancelar simplemente cierra la actividad
        btnCancelarNuevaEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        etType.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String label = parent.getItemAtPosition(position).toString();

        if(label.equals("Consultoria")){
            typeSelected = 1;
        }else if (label.equals("Desarrollo a medida")){
            typeSelected = 2;
        }else{
            typeSelected = 3;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        typeSelected =1;
    }
}