package placesharing.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import placesharing.R;
import placesharing.model.User;

public class UserInfoFragment extends Fragment {

    private TextView email;
    private View userInfo;
    private User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        userInfo = inflater.inflate(R.layout.activity_user_info,container,false);

        email = (TextView) userInfo.findViewById(R.id.email_text);

        user = new User(FirebaseAuth.getInstance().getCurrentUser());
        email.setText(user.getEmail());

        return userInfo;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("email",email.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null){
            email.setText(savedInstanceState.getString("email"));
        }
        super.onViewStateRestored(savedInstanceState);
    }
}
