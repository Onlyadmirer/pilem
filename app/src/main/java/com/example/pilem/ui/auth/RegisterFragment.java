package com.example.pilem.ui.auth;

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

import com.example.pilem.R;
import com.example.pilem.data.local.AppDatabase;
import com.example.pilem.data.local.UserEntity;

public class RegisterFragment extends Fragment {

    private EditText etUsername, etPassword, etConfirmPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etUsername = view.findViewById(R.id.et_username);
        etPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        Button btnRegister = view.findViewById(R.id.btn_register);
        TextView tvGoToLogin = view.findViewById(R.id.tv_go_to_login);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            AppDatabase.databaseWriteExecutor.execute(() -> {
                UserEntity existingUser = AppDatabase.getDatabase(requireContext()).userDao().getUserByUsername(username);
                if (existingUser != null) {
                    requireActivity().runOnUiThread(() -> 
                        Toast.makeText(requireContext(), "Username already exists", Toast.LENGTH_SHORT).show()
                    );
                } else {
                    UserEntity newUser = new UserEntity(username, password);
                    AppDatabase.getDatabase(requireContext()).userDao().registerUser(newUser);
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(view).popBackStack();
                    });
                }
            });
        });

        tvGoToLogin.setOnClickListener(v -> Navigation.findNavController(view).popBackStack());
    }
}
