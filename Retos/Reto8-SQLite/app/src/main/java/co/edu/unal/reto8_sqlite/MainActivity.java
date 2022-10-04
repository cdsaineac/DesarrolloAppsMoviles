package co.edu.unal.reto8_sqlite;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import co.edu.unal.reto8_sqlite.AdaptadorEmpresas;
import co.edu.unal.reto8_sqlite.AgregarEmpresaActivity;
import co.edu.unal.reto8_sqlite.EditarEmpresaActivity;
import co.edu.unal.reto8_sqlite.EmpresaController;
import co.edu.unal.reto8_sqlite.Empresa;
import co.edu.unal.reto8_sqlite.R;
import co.edu.unal.reto8_sqlite.RecyclerTouchListener;

public class MainActivity extends AppCompatActivity {
    private List<Empresa> listaDeEmpresas;
    private RecyclerView recyclerView;
    private AdaptadorEmpresas adaptadorEmpresas;
    private EmpresaController empresasController;
    private FloatingActionButton fabAgregarEmpresa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Ojo: este código es generado automáticamente, pone la vista y ya, pero
        // no tiene nada que ver con el código que vamos a escribir
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Lo siguiente sí es nuestro ;)
        // Definir nuestro controlador
        empresasController = new EmpresaController(MainActivity.this);

        // Instanciar vistas
        recyclerView = findViewById(R.id.recyclerViewMascotas);
        fabAgregarEmpresa = findViewById(R.id.fabAgregarMascota);


        // Por defecto es una lista vacía,
        // se la ponemos al adaptador y configuramos el recyclerView
        listaDeEmpresas = new ArrayList<>();
        adaptadorEmpresas = new AdaptadorEmpresas(listaDeEmpresas);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adaptadorEmpresas);

        // Una vez que ya configuramos el RecyclerView le ponemos los datos de la BD
        refrescarListaDeEmpresas();

        // Listener de los clicks en la lista, o sea el RecyclerView
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override // Un toque sencillo
            public void onClick(View view, int position) {
                // Pasar a la actividad EditarMascotaActivity.java
                Empresa empresaSeleccionada = listaDeEmpresas.get(position);
                Intent intent = new Intent(MainActivity.this, EditarEmpresaActivity.class);
                intent.putExtra("CompanyId", empresaSeleccionada.getId());
                intent.putExtra("CompanyName", empresaSeleccionada.getName());
                intent.putExtra("CompanyURL", empresaSeleccionada.getUrl());
                intent.putExtra("CompanyPhone", empresaSeleccionada.getPhone());
                intent.putExtra("CompanyEmail", empresaSeleccionada.getEmail());
                intent.putExtra("CompanyType", empresaSeleccionada.getType());

                startActivity(intent);
            }

            @Override // Un toque largo
            public void onLongClick(View view, int position) {
                final Empresa empresaParaEliminar = listaDeEmpresas.get(position);
                AlertDialog dialog = new AlertDialog
                        .Builder(MainActivity.this)
                        .setPositiveButton("Sí, eliminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                empresasController.eliminarEmpresa(empresaParaEliminar);
                                refrescarListaDeEmpresas();
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setTitle("Confirmar")
                        .setMessage("¿Eliminar a la empresa " + empresaParaEliminar.getName() + "?")
                        .create();
                dialog.show();

            }
        }));

        // Listener del FAB
        fabAgregarEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simplemente cambiamos de actividad
                Intent intent = new Intent(MainActivity.this, AgregarEmpresaActivity.class);
                startActivity(intent);
            }
        });

        // Créditos
        fabAgregarEmpresa.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Acerca de")
                        .setMessage("CRUD de Android con SQLite")
                        .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogo, int which) {
                                dialogo.dismiss();
                            }
                        })
                        .setPositiveButton("Sitio web", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intentNavegador = new Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com"));
                                startActivity(intentNavegador);
                            }
                        })
                        .create()
                        .show();
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // below line is to get our inflater
        MenuInflater inflater = getMenuInflater();

        // inside inflater we are inflating our menu file.
        inflater.inflate(R.menu.search_menu, menu);

        super.onCreateOptionsMenu(menu);

        // below line is to get our menu item.
        MenuItem searchItem = menu.findItem(R.id.actionSearch);

        // getting search view of our item.
        SearchView searchView = (SearchView) searchItem.getActionView();

        // below line is to get our menu item.
        MenuItem searchItem2 = menu.findItem(R.id.actionSearch2);

        // getting search view of our item.
        SearchView searchView2 = (SearchView) searchItem2.getActionView();

        // below line is to call set on query text listener method.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter(newText);
                return false;
            }
        });
        // below line is to call set on query text listener method.
        searchView2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter2(newText);
                return false;
            }
        });
        return true;


    }

    private void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<Empresa> filteredlist = new ArrayList<Empresa>();

        // running a for loop to compare elements.
        for (Empresa item : listaDeEmpresas) {
            // checking if the entered string matched with any item of our recycler view.
            System.out.println(item.getName());
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adaptadorEmpresas.filterList(filteredlist);
        }
    }
    private void filter2(String text) {
        // creating a new array list to filter our data.
        ArrayList<Empresa> filteredlist = new ArrayList<>();

        // running a for loop to compare elements.
        for (Empresa item : listaDeEmpresas) {
            // checking if the entered string matched with any item of our recycler view.
            System.out.println(item.getType());
            if (item.getType().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adaptadorEmpresas.filterList(filteredlist);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        refrescarListaDeEmpresas();
    }

    public void refrescarListaDeEmpresas() {
        /*
         * ==========
         * Justo aquí obtenemos la lista de la BD
         * y se la ponemos al RecyclerView
         * ============
         *
         * */
        if (adaptadorEmpresas == null) return;
        listaDeEmpresas = empresasController.obtenerEmpresas();
        adaptadorEmpresas.setListaDeEmpresas(listaDeEmpresas);
        adaptadorEmpresas.notifyDataSetChanged();
    }
}