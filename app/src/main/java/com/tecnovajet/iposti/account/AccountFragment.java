package com.tecnovajet.iposti.account;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tecnovajet.iposti.MainActivity;
import com.tecnovajet.iposti.R;
import com.tecnovajet.iposti.booking.BookingsHistoryActivity;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ProgressBar loading;
    private TextView nameText;

    private String nome = "", cognome = "", cellulare;

    private String urlGetProfileInfo = "OMITTED/getprofileinfo.php";

    private OnFragmentInteractionListener mListener;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View content = inflater.inflate(R.layout.fragment_account, container, false);

        loading = content.findViewById(R.id.progressBar);

        RelativeLayout editProfile = content.findViewById(R.id.editProfile);
        RelativeLayout logout = content.findViewById(R.id.logout);
        RelativeLayout oldBookings = content.findViewById(R.id.oldBookings);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        nameText = content.findViewById(R.id.nameText);
        TextView emailText = content.findViewById(R.id.emailText);

        emailText.setText(currentUser.getEmail());

//        new getProfileInfo().execute();

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), EditAccountActivity.class);
                i.putExtra("nome", nome);
                i.putExtra("cognome", cognome);
                i.putExtra("cellulare", cellulare);
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null){
                    mAuth.signOut();
                    Intent i = new Intent(getActivity(), SignUpInActivity.class);
                    startActivity(i);
                }
            }
        });

        oldBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), BookingsHistoryActivity.class);
                startActivity(i);
            }
        });

        return content;
    }

    private void getProfileInfo(){
        try {
            loading.setVisibility(View.VISIBLE);

            JSONObject jsonBody = new JSONObject();

            jsonBody.put("email", MainActivity.email);

            Log.d("aaa", MainActivity.token);

            JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, urlGetProfileInfo, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObj) {

                    try {
                        JSONArray info = jsonObj.getJSONArray("items");

                        JSONObject c = info.getJSONObject(0);
                        nome = c.getString("Nome");
                        cognome = c.getString("Cognome");
                        cellulare = c.getString("Cellulare");
                    } catch (final JSONException e) {
                        e.printStackTrace();
                    }

                    loading.setVisibility(View.INVISIBLE);

                    nameText.setText(String.format("%s %s", nome, cognome));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("error", error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("token", MainActivity.token);
                    return headers;
                }
            };
            Volley.newRequestQueue(Objects.requireNonNull(getActivity())).add(jsonOblect);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

//        new getProfileInfo().execute();
        getProfileInfo();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
