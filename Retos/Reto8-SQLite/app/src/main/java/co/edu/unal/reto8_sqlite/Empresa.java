package co.edu.unal.reto8_sqlite;

public class Empresa {
    private String name;
    private String url;
    private String phone;
    private String email;
    private long idType;
    private long id; // El ID de la BD

    public Empresa(String name, String url, String phone, String email, long idType, long id) {
        this.name = name;
        this.url = url;
        this.phone = phone;
        this.email = email;
        this.idType = idType;
        this.id = id;
    }

    public Empresa(String name, String url, String phone, String email, long idType) {
        this.name = name;
        this.url = url;
        this.phone = phone;
        this.email = email;
        this.idType = idType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getIdType() {
        return idType;
    }

    public void setIdType(long idType) {
        this.idType = idType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Empresa{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", idType=" + idType +
                ", id=" + id +
                '}';
    }

    public String getType() {
        if(idType == 1){
            return "Consultoria";
        }else if(idType == 2){
            return "Desarrollo a medida";
        }else{
            return "Fabrica de software";
        }
    }
}
