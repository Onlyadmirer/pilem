package com.example.pilem.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pilem.MainActivity;
import com.example.pilem.R;
import com.example.pilem.data.local.AppDatabase;
import com.example.pilem.data.local.UserEntity;
import com.example.pilem.data.local.UserSession;

public class LoginFragment extends Fragment {

    private EditText etUsername, etPassword;
    private UserSession userSession;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userSession = new UserSession(requireContext());

        etUsername = view.findViewById(R.id.et_username);
        etPassword = view.findViewById(R.id.et_password);
        Button btnLogin = view.findViewById(R.id.btn_login);
        TextView tvGoToRegister = view.findViewById(R.id.tv_go_to_register);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            AppDatabase.databaseWriteExecutor.execute(() -> {
                UserEntity user = AppDatabase.getDatabase(requireContext()).userDao().login(username, password);
                requireActivity().runOnUiThread(() -> {
                    if (user != null) {
                        userSession.createSession(user.getUserId());
                        Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show();
                        
                        // Explicit Intent to MainActivity
                        Intent intent = new Intent(requireActivity(), MainActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    } else {
                        Toast.makeText(requireContext(), "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        tvGoToRegister.setOnClickListener(v -> 
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment)
        );
    }
}
