package placesharing.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by sabussy on 07/03/17.
 */
public class User {
    private String identifiant;
    private String email;
    public User(){
    }

    public User(FirebaseUser user){
        if(user!=null){
            setIdentifiant(user.getDisplayName());
            setEmail(user.getEmail());
        }
    }

    public User(String id, String email){
        identifiant = id;
        this.email = email;
    }

    public String ToString(){
        return String.format("Utilisateur : %s, email : %s",identifiant,email);
    }

    public void setIdentifiant(String identifiant){
        this.identifiant = identifiant;
    }

    public String getIdentifiant(){
        return identifiant;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

}
