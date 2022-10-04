package co.edu.unal.reto8_sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class AyudanteBaseDeDatos extends SQLiteOpenHelper {
    private static final String NOMBRE_BASE_DE_DATOS = "Reto8",
            NOMBRE_TABLA_EMPRESA = "tbCompany",
            NOMBRE_TABLA_TIPO_EMPRESA = "tbCompanyType",
            NOMBRE_TABLA_SERVICIO = "tbService";
    private static final int VERSION_BASE_DE_DATOS = 1;

    public AyudanteBaseDeDatos(Context context) {
        super(context, NOMBRE_BASE_DE_DATOS, null, VERSION_BASE_DE_DATOS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s(id integer primary key autoincrement, description text)", NOMBRE_TABLA_TIPO_EMPRESA));
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s(id integer primary key autoincrement, name text, url text, phone text, email text, idType int,  FOREIGN KEY (idType) REFERENCES "+ NOMBRE_TABLA_TIPO_EMPRESA +"(id))", NOMBRE_TABLA_EMPRESA));
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s(id integer primary key autoincrement, description text, idCompany int, FOREIGN KEY (idCompany) REFERENCES "+ NOMBRE_TABLA_EMPRESA +"(id))", NOMBRE_TABLA_SERVICIO));
        db.execSQL(String.format("INSERT INTO "+ NOMBRE_TABLA_TIPO_EMPRESA +" (description) VALUES('Consultoria')"));
        db.execSQL(String.format("INSERT INTO "+ NOMBRE_TABLA_TIPO_EMPRESA +" (description) VALUES('Desarrollo a medida')"));
        db.execSQL(String.format("INSERT INTO "+ NOMBRE_TABLA_TIPO_EMPRESA +" (description) VALUES('Fabrica de software')"));

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<String> getTypes(){
        // Select All Query
        String selectQuery = "SELECT  * FROM " + NOMBRE_TABLA_TIPO_EMPRESA;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);//selectQuery,selectedArguments

        // looping through all rows and adding to list
        List<String> list = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(1));//adding 2nd column data
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();
        // returning lables
        return list;
    }

}