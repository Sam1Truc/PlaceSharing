package placesharing.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import placesharing.R;
import placesharing.model.Place;
import placesharing.model.User;

public class AddPlaceFragment extends LocalisationHandlingFragment {

    private EditText placeName;
    private EditText placeDescription;
    private RatingBar note;
    private View addPlaceView;
    private Button validButton;
    private MainActivity ourActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ourActivity = (MainActivity)getActivity();
        addPlaceView = inflater.inflate(R.layout.activity_add_place,container,false);
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        placeDescription = (EditText) addPlaceView.findViewById(R.id.place_description_editText);
        placeName = (EditText) addPlaceView.findViewById(R.id.place_name_editText);
        note = (RatingBar) addPlaceView.findViewById(R.id.ratingBar);
        note.setStepSize(0.5f);

        if(savedInstanceState != null){
            placeName.setText(savedInstanceState.getString("placeName"));
            placeDescription.setText(savedInstanceState.getString("placeDescription"));
            note.setRating(savedInstanceState.getFloat("note"));
        }

        validButton = (Button) addPlaceView.findViewById(R.id.add_button);
        validButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCurrentLocation == null){
                    Toast.makeText(getActivity(),getString(R.string.error_location),Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    Toast.makeText(getActivity(),getString(R.string.no_user),Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(placeName.getText().toString().trim().isEmpty()){
                    Toast.makeText(getActivity(),getString(R.string.no_place_name),Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(placeDescription.getText().toString().trim().isEmpty()){
                    Toast.makeText(getActivity(),getString(R.string.no_place_desc),Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (((MainActivity) getActivity()).currentMarkerOptions == null){
                    Toast.makeText(getActivity(),getString(R.string.no_new_marker),Toast.LENGTH_SHORT).show();
                    return;
                }
                User user = new User(FirebaseAuth.getInstance().getCurrentUser());
                Place p = new Place(database.push().getKey(),placeName.getText().toString(),placeDescription.getText().toString(),ourActivity.currentMarkerOptions.getPosition(),user, ((double) note.getRating()));
                database.child("lieux").child(p.getIdentifiant()).setValue(p);

                Toast.makeText(getActivity(), getString(R.string.lieu_ajouté), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(),MainActivity.class));
            }
        });

        return addPlaceView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("placeName",placeName.getText().toString());
        outState.putString("placeDescription",placeDescription.getText().toString());
        outState.putFloat("note",note.getRating());
        super.onSaveInstanceState(outState);
    }

}
