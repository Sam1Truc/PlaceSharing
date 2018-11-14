package placesharing.model;

import android.location.Location;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sabussy on 07/03/17.
 */
public class Place implements ClusterItem {
    private String identifiant;
    private String nom;
    private String description;
    private double latitude;
    private double longitude;
    private User auteur;
    private List<Commentaire> commentaireList;
    private Double note;

    public Place(){
    }
    
    public Place(String id, String nom, String desc, Location loc, User auth){
        identifiant = id;
        this.nom = nom;
        description = desc;
        latitude = loc.getLatitude();
        longitude = loc.getLongitude();
        auteur = auth;
        this.note = (double)0;
        commentaireList = new ArrayList<>();
    }

    public Place(String id, String nom, String desc, LatLng latLng, User auth){
        identifiant = id;
        this.nom = nom;
        description = desc;
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        auteur = auth;
        this.note = (double)0;
        commentaireList = new ArrayList<>();
    }
    public Place(String id, String nom, String desc, LatLng latLng, User auth, Double note){
        this(id,nom,desc,latLng,auth);
        this.note = note;
    }

    public Place(String id, String nom, String desc, double altitude, double latitude, double longitude, User auteur){
        identifiant = id;
        this.nom = nom;
        description = desc;
        this.latitude = latitude;
        this.longitude = longitude;
        this.auteur = auteur;
        commentaireList = new ArrayList<>();
    }
    public Place(String id, String nom, String desc, double altitude, double latitude, double longitude, User auteur, Double note){
        this(id,nom,desc,altitude,latitude,longitude,auteur);
        this.note = note;
    }


    public String getNom(){
        return nom;
    }

    public User getAuteur(){
        return auteur;
    }

    public String getDescription(){ return description; }

    public List<Commentaire> getCommentaireList(){
        return commentaireList;
    }

    public void ajouterCommentaire(Commentaire com){
        if(commentaireList == null){
            commentaireList = new ArrayList<>();
        }
        commentaireList.add(com);
    }

    public void supprimerCommentaire(Commentaire com){
        getCommentaireList().remove(com);
    }

    public double getNote(){
        if(commentaireList == null) { return note; }
        else if (commentaireList.isEmpty()) { return note; }
        double noteMoyenne = note;
        for (Commentaire com : getCommentaireList()) {
            noteMoyenne = (noteMoyenne+com.getNote())/2;
        }
        return Math.round(noteMoyenne * 2) / 2.0;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setNote(Double note){
        this.note = note;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuteur(User auteur) {
        this.auteur = auteur;
    }

    public void setCommentaireList(List<Commentaire> commentaireList) {
        this.commentaireList = commentaireList;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String toString(){
        return String.format("Lieu %s : /nDescription : %s",nom,description);
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(latitude,longitude);
    }
}
