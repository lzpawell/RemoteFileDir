package xin.lzp.remotefiledir.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import xin.lzp.remotefiledir.R;
import xin.lzp.remotefiledir.util.FTPClientConfig;
import xin.lzp.remotefiledir.util.User;


public class LoginFragment extends Fragment {

    private Button btnLogin;
    private EditText edtUserId;
    private EditText edtPassword;


    public LoginFragment(){

    }



    public static LoginFragment newInstance(OnRequestLoginCallback callback) {
        LoginFragment fragment = new LoginFragment();
        fragment.callback = callback;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        btnLogin = (Button) rootView.findViewById(R.id.btn_login);
        edtUserId = (EditText) rootView.findViewById(R.id.edt_user_id);
        edtPassword = (EditText) rootView.findViewById(R.id.edt_password);


        edtUserId.setText(FTPClientConfig.getUsername());
        edtPassword.setText(FTPClientConfig.getPassword());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //网络IO
                new Thread(){
                    @Override
                    public void run() {

                        final String username = edtUserId.getText().toString().trim();
                        final String password = edtPassword.getText().toString().trim();
                        final User.Result result = User.login(username,password);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (result){
                                    case NO_ERROR:
                                        if(!username.equals(FTPClientConfig.getUsername()))
                                            FTPClientConfig.setUsername(username);

                                        if(!password.equals(FTPClientConfig.getPassword()))
                                            FTPClientConfig.setPassword(password);

                                        callback.loginResult(true);
                                        return;

                                    case USER_NOT_FOUND:
                                        Toast.makeText(getActivity(), "user not found!", Toast.LENGTH_SHORT).show();
                                        break;

                                    case PASSWORD_ERROR:
                                        Toast.makeText(getActivity(), "password error!", Toast.LENGTH_SHORT).show();
                                        break;

                                    default:
                                        break;
                                }

                                callback.loginResult(false);
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



    private OnRequestLoginCallback callback;

    public interface OnRequestLoginCallback{
        void loginResult(boolean result);
    }
}
