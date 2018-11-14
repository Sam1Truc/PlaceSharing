package placesharing.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import placesharing.R;
import placesharing.model.Commentaire;
import placesharing.model.MonAdaptateurCommentaires;
import placesharing.model.Place;
import placesharing.model.User;

public class PlaceInfoFragment extends Fragment {

    private TextView nomPlace;
    private TextView nomAuteur;
    private TextView notePlace;
    private TextView descPlace;
    private View placeInfoView;
    private RecyclerView listeCommentaires;
    private RatingBar nouvNote;
    private EditText nouvCommentaire;
    private Button buttonNouvCommentaire;

    private DatabaseReference mLieuxRef;
    private DataSnapshot mPlace;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        placeInfoView = inflater.inflate(R.layout.activity_place_info,container,false);

        mLieuxRef = FirebaseDatabase.getInstance().getReference().child("lieux");

        nomPlace = (TextView) placeInfoView.findViewById(R.id.place_name);
        nomAuteur = (TextView) placeInfoView.findViewById(R.id.author_name);
        notePlace = (TextView) placeInfoView.findViewById(R.id.note);
        descPlace = (TextView) placeInfoView.findViewById(R.id.description_place);
        listeCommentaires = (RecyclerView) placeInfoView.findViewById(R.id.list_commentaires);
        nouvNote = (RatingBar) placeInfoView.findViewById(R.id.ratingBar);
        nouvCommentaire = (EditText) placeInfoView.findViewById(R.id.editTextCommentaire);
        buttonNouvCommentaire = (Button) placeInfoView.findViewById(R.id.buttonSendCommentaire);

        nouvNote.setStepSize(0.5f);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        listeCommentaires.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listeCommentaires.getContext(),layoutManager.getOrientation());
        listeCommentaires.addItemDecoration(dividerItemDecoration);

        if(savedInstanceState != null){
            nomPlace.setText(savedInstanceState.getString("nomPlace"));
            nomAuteur.setText(savedInstanceState.getString("nomAuteur"));
            notePlace.setText(savedInstanceState.getString("notePlace"));
            descPlace.setText(savedInstanceState.getString("descPlace"));
            nouvNote.setRating(savedInstanceState.getFloat("nouvNote"));
            nouvCommentaire.setText(savedInstanceState.getString("nouvCommentaire"));
        }

        ValueEventListener placeValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(((MainActivity) getActivity()).currentMarkerOptions == null || dataSnapshot == null){
                    Toast.makeText(getActivity(),getString(R.string.no_place_found),Toast.LENGTH_SHORT).show();
                    return;
                }
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    if(postSnapshot.getValue(Place.class).getNom().equals(((MainActivity) getActivity()).currentMarkerOptions.getTitle())){
                        mPlace = postSnapshot;
                        loadPlace(postSnapshot.getValue(Place.class));
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(),getString(R.string.error_location_loading),Toast.LENGTH_SHORT).show();
            }
        };
        mLieuxRef.addValueEventListener(placeValueEventListener);


        buttonNouvCommentaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nouvCommentaire.getText().toString().trim().isEmpty()){
                    Toast.makeText(getActivity(),getString(R.string.no_comm),Toast.LENGTH_SHORT).show();
                    return;
                }
                Commentaire com = new Commentaire(new User(FirebaseAuth.getInstance().getCurrentUser()),nouvCommentaire.getText().toString(),nouvNote.getRating());
                if(mPlace != null){
                    Place place = mPlace.getValue(Place.class);
                    place.ajouterCommentaire(com);
                    mLieuxRef.child(mPlace.getKey()).setValue(place);
                    Toast.makeText(getActivity(),getString(R.string.comm_added),Toast.LENGTH_SHORT).show();
                    nouvNote.setRating(0);
                    nouvCommentaire.setText("");
                }
            }
        });

        return placeInfoView;
    }

    private void loadPlace(Place mPlace) {
        nomPlace.setText(mPlace.getNom());
        nomAuteur.setText(mPlace.getAuteur().getEmail());
        notePlace.setText(String.valueOf(mPlace.getNote()));
        descPlace.setText(mPlace.getDescription());
        listeCommentaires.setAdapter(new MonAdaptateurCommentaires(mPlace.getCommentaireList()));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("nomPlace",nomPlace.getText().toString());
        outState.putString("nomAuteur",nomAuteur.getText().toString());
        outState.putString("notePlace",notePlace.getText().toString());
        outState.putString("descPlace",descPlace.getText().toString());
        outState.putFloat("nouvNote",nouvNote.getRating());
        outState.putString("nouvCommentaire",nouvCommentaire.getText().toString());
        super.onSaveInstanceState(outState);
    }
}
