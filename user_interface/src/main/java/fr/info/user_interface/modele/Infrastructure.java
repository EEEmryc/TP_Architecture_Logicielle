package fr.info.user_interface.modele;

public class Infrastructure {
    private String localisation;
    private String type;
    private String portee;
    private String nombre;
    private String complement;
    private String mail;
    private double latitude;
    private double longitude;

    // Getters et Setters
    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPortee() { return portee; }
    public void setPortee(String portee) { this.portee = portee; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getComplement() { return complement; }
    public void setComplement(String complement) { this.complement = complement; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}