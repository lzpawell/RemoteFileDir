package xin.lzp.remotefiledir.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import xin.lzp.remotefiledir.R;
import xin.lzp.remotefiledir.ui.FTPFileListActivity;
import xin.lzp.remotefiledir.util.FTPClientConfig;
import xin.lzp.remotefiledir.util.User;

public class RegisterFragment extends Fragment {


    public RegisterFragment() {
        // Required empty public constructor
    }


    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    private Button btnRegister;
    private EditText edtUserId;
    private EditText edtPassword;
    private EditText edtConfirmPasswd;
    private EditText edtEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        initView(rootView);
        
        return rootView;
    }

    private void initView(View rootView) {
        btnRegister = (Button) rootView.findViewById(R.id.btn_register);
        edtUserId = (EditText) rootView.findViewById(R.id.edt_user_id);
        edtPassword = (EditText) rootView.findViewById(R.id.edt_password);
        edtConfirmPasswd = (EditText) rootView.findViewById(R.id.edt_confirm_password);
        edtEmail = (EditText) rootView.findViewById(R.id.edt_email);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //网络IO
                new Thread(){
                    @Override
                    public void run() {
                        final String username = edtUserId.getText().toString().trim();
                        final String password = edtPassword.getText().toString().trim();
                        final User.Result result = User.register(username,
                                password,
                                edtConfirmPasswd.getText().toString().trim(),
                                edtEmail.getText().toString().trim());

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (result){
                                    case NO_ERROR:
                                        if(!username.equals(FTPClientConfig.getUsername()))
                                            FTPClientConfig.setUsername(username);

                                        if(!password.equals(FTPClientConfig.getPassword()))
                                            FTPClientConfig.setPassword(password);

                                        Intent intent = new Intent(getActivity(), FTPFileListActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                        break;

                                    case USER_IS_EXISTED:
                                        Toast.makeText(getActivity(), "user is existed!", Toast.LENGTH_SHORT).show();
                                        break;

                                    case PASSWORD_ERROR:
                                        Toast.makeText(getActivity(), "password error!", Toast.LENGTH_SHORT).show();
                                        break;

                                    default:
                                        break;
                                }
                            }
                        });
                    }
                }.start();

            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
