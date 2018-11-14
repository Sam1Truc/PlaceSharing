package placesharing.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sabussy on 07/03/17.
 */
public class Commentaire {

    private User auteur;
    private String avis;
    private double note; //entre 0 et 5

    public Commentaire(){

    }

    public Commentaire(User auteur){
        this.auteur = auteur;
        avis = "";
        note = 0;
    }

    public Commentaire(User auteur, String commentaire, float note)
    {
        this(auteur);
        if(!commentaire.isEmpty())
            avis = commentaire;
        this.note = note;
    }

    public String getAvis(){
        return avis;
    }

    public User getAuteur(){
        return auteur;
    }

    public double getNote(){
        return note;
    }

    public void setAuteur(User auteur) { this.auteur = auteur; }

    public void setAvis(String avis) { this.avis = avis; }

    public void setNote(double note) { this.note = note; }

}
