package com.example.salvatucomida;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity_Login extends AppCompatActivity {

    private EditText edtCorreo, edtPassword;
    private Button btnLogin, btnIrRegistro;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // ⚠️ Si ya hay sesión activa, saltar directo a MainActivity_Inicio
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            irAlInicio();
            return;
        }

        edtCorreo    = findViewById(R.id.edtCorreo);
        edtPassword  = findViewById(R.id.edtPassword);
        btnLogin     = findViewById(R.id.btnLogin);
        btnIrRegistro= findViewById(R.id.btnIrRegistro);

        btnLogin.setOnClickListener(v -> {
            String correo = edtCorreo.getText().toString().trim();
            String pass   = edtPassword.getText().toString().trim();

            if (TextUtils.isEmpty(correo) || TextUtils.isEmpty(pass)) {
                Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(correo, pass)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String nombre = (user != null && user.getDisplayName() != null)
                                    ? user.getDisplayName() : "Usuario";
                            Toast.makeText(this, "✅ Bienvenido, " + nombre, Toast.LENGTH_SHORT).show();
                            irAlInicio();   // ← AHORA SÍ NAVEGA
                        } else {
                            Toast.makeText(this,
                                    "❌ Error: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        btnIrRegistro.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity_Registro.class)));
    }

    private void irAlInicio() {
        startActivity(new Intent(this, MainActivity_Inicio.class));
        finish();
    }
}