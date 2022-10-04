package co.edu.unal.reto8_sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import co.edu.unal.reto8_sqlite.AyudanteBaseDeDatos;
import co.edu.unal.reto8_sqlite.Empresa;

public class EmpresaController {
    private AyudanteBaseDeDatos ayudanteBaseDeDatos;
    private String NOMBRE_TABLA = "tbCompany";

    public EmpresaController(Context contexto) {
        ayudanteBaseDeDatos = new AyudanteBaseDeDatos(contexto);
    }


    public int eliminarEmpresa(Empresa empresa) {

        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        String[] argumentos = {String.valueOf(empresa.getId())};
        return baseDeDatos.delete(NOMBRE_TABLA, "id = ?", argumentos);
    }

    public long nuevaEmpresa(Empresa empresa) {
        // writable porque vamos a insertar
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        ContentValues valoresParaInsertar = new ContentValues();
        valoresParaInsertar.put("name", empresa.getName());
        valoresParaInsertar.put("url", empresa.getUrl());
        valoresParaInsertar.put("phone", empresa.getPhone());
        valoresParaInsertar.put("email", empresa.getEmail());
        valoresParaInsertar.put("idType", empresa.getIdType());

        return baseDeDatos.insert(NOMBRE_TABLA, null, valoresParaInsertar);
    }

    public int guardarCambios(Empresa empresa) {
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        ContentValues valoresParaActualizar = new ContentValues();
        valoresParaActualizar.put("name", empresa.getName());
        valoresParaActualizar.put("url", empresa.getUrl());
        valoresParaActualizar.put("phone", empresa.getPhone());
        valoresParaActualizar.put("email", empresa.getEmail());
        valoresParaActualizar.put("idType", empresa.getIdType());
        // where id...
        String campoParaActualizar = "id = ?";
        // ... = idMascota
        String[] argumentosParaActualizar = {String.valueOf(empresa.getId())};
        return baseDeDatos.update(NOMBRE_TABLA, valoresParaActualizar, campoParaActualizar, argumentosParaActualizar);
    }

    public ArrayList<Empresa> obtenerEmpresas() {
        ArrayList<Empresa> empresas = new ArrayList<>();
        // readable porque no vamos a modificar, solamente leer
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getReadableDatabase();
        // SELECT * FROM tbCompany
        String[] columnasAConsultar = {"name", "url","phone","email","idtype", "id"};
        Cursor cursor = baseDeDatos.query(
                NOMBRE_TABLA,
                columnasAConsultar,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor == null) {
            /*
                Salimos aquí porque hubo un error, regresar
                lista vacía
             */
            return empresas;

        }
        // Si no hay datos, igualmente regresamos la lista vacía
        if (!cursor.moveToFirst()) return empresas;

        // En caso de que sí haya, iteramos y vamos agregando los
        // datos a la lista de mascotas
        do {

            String nameFromDB = cursor.getString(0);
            String urlFromDB = cursor.getString(1);
            String phoneFromDB = cursor.getString(2);
            String emailFromDB = cursor.getString(3);
            long idtypeFromDB = cursor.getLong(4);
            long idFromDB = cursor.getLong(5);

            Empresa companyFromDB = new Empresa(nameFromDB,urlFromDB, phoneFromDB,emailFromDB,idtypeFromDB,idFromDB);
            empresas.add(companyFromDB);
        } while (cursor.moveToNext());

        // Fin del ciclo. Cerramos cursor y regresamos la lista de mascotas :)
        cursor.close();
        return empresas;
    }

    public Empresa obtenerEmpresa(long idSelected) {
        // readable porque no vamos a modificar, solamente leer
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getReadableDatabase();
        // SELECT * FROM tbCompany
        String[] columnasAConsultar = {"name", "url","phone","email","idtype", "id"};
        Cursor cursor = baseDeDatos.query(
                NOMBRE_TABLA,
                columnasAConsultar,
                "id = " + idSelected,
                null,
                null,
                null,
                null
        );

        if (cursor == null) {
            /*
                Salimos aquí porque hubo un error, regresar
                lista vacía
             */
            return null;

        }
        // Si no hay datos, igualmente regresamos la lista vacía
        if (!cursor.moveToFirst()) return null;

        String nameFromDB = cursor.getString(0);
        String urlFromDB = cursor.getString(1);
        String phoneFromDB = cursor.getString(2);
        String emailFromDB = cursor.getString(3);
        long idtypeFromDB = cursor.getLong(4);
        long idFromDB = cursor.getLong(5);

        Empresa companyFromDB = new Empresa(nameFromDB, urlFromDB, phoneFromDB, emailFromDB, idtypeFromDB, idFromDB);
        cursor.close();

        return companyFromDB;
    }
}
