package co.edu.unal.reto8_sqlite;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import co.edu.unal.reto8_sqlite.Empresa;

public class AdaptadorEmpresas extends RecyclerView.Adapter<AdaptadorEmpresas.MyViewHolder>{
    private List<Empresa> listaDeEmpresas;

    public void setListaDeEmpresas(List<Empresa> listaDeEmpresas) {
        this.listaDeEmpresas = listaDeEmpresas;
    }

    public AdaptadorEmpresas(List<Empresa> empresas) {
        this.listaDeEmpresas = empresas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View filaEmpresa = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fila_empresa, viewGroup, false);
        return new MyViewHolder(filaEmpresa);
    }
    // method for filtering our recyclerview items.
    public void filterList(List<Empresa> filterlist) {
        // below line is to add our filtered
        // list in our course array list.
        listaDeEmpresas = filterlist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        // Obtener la mascota de nuestra lista gracias al índice i
        Empresa empresa = listaDeEmpresas.get(i);

        // Obtener los datos de la lista
        String CompanyName = empresa.getName();
        String CompanyURL = empresa.getUrl();
        String CompanyPhone = empresa.getPhone();
        String CompanyEmail = empresa.getEmail();
        long CompanyType = empresa.getIdType();

        String CompanyTypeString = empresa.getType();

        // Y poner a los TextView los datos con setText
        myViewHolder.Name.setText(CompanyName);
        myViewHolder.URL.setText(CompanyURL);
        myViewHolder.Phone.setText(CompanyPhone);
        myViewHolder.Email.setText(CompanyEmail);
        myViewHolder.Type.setText(CompanyTypeString);
    }

    @Override
    public int getItemCount() {
        return listaDeEmpresas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Name, URL, Phone, Email, Type;

        MyViewHolder(View itemView) {
            super(itemView);
            this.Name  = itemView.findViewById(R.id.Name);
            this.URL = itemView.findViewById(R.id.URL);
            this.Phone = itemView.findViewById(R.id.Phone);
            this.Email = itemView.findViewById(R.id.Email);
            this.Type = itemView.findViewById(R.id.Type);

        }
    }
}
