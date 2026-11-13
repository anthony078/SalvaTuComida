package com.example.salvatucomida;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity_Registro extends AppCompatActivity {

    private EditText edtNombre, edtApellido, edtCorreo, edtPassword, edtConfirmar;
    private Button btnRegistrar, btnVolver;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();

        edtNombre    = findViewById(R.id.edtNombre);
        edtApellido  = findViewById(R.id.edtApellido);
        edtCorreo    = findViewById(R.id.edtCorreoReg);
        edtPassword  = findViewById(R.id.edtPasswordReg);
        edtConfirmar = findViewById(R.id.edtConfirmarPassword);
        btnRegistrar = findViewById(R.id.btnRegistrarFinal);
        btnVolver    = findViewById(R.id.btnVolverLogin);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
        btnVolver.setOnClickListener(v -> finish());
    }

    private void registrarUsuario() {
        String nombre    = edtNombre.getText().toString().trim();
        String apellido  = edtApellido.getText().toString().trim();
        String correo    = edtCorreo.getText().toString().trim();
        String pass      = edtPassword.getText().toString().trim();
        String confirmar = edtConfirmar.getText().toString().trim();

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(apellido) ||
                TextUtils.isEmpty(correo) || TextUtils.isEmpty(pass) ||
                TextUtils.isEmpty(confirmar)) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!correo.toLowerCase().endsWith("@gmail.com")) {
            edtCorreo.setError("Solo se aceptan correos @gmail.com");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            edtCorreo.setError("Formato de correo inválido");
            return;
        }

        if (!esPasswordSegura(pass)) {
            edtPassword.setError("Mín. 8 caracteres, 1 mayúscula, 1 minúscula y 1 número");
            return;
        }

        if (!pass.equals(confirmar)) {
            edtConfirmar.setError("Las contraseñas no coinciden");
            return;
        }

        mAuth.createUserWithEmailAndPassword(correo, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates =
                                    new UserProfileChangeRequest.Builder()
                                            .setDisplayName(nombre + " " + apellido)
                                            .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        Toast.makeText(this,
                                                "✅ Usuario registrado con éxito",
                                                Toast.LENGTH_LONG).show();
                                        // Va directo al inicio (ya está logueado)
                                        startActivity(new Intent(this, MainActivity_Inicio.class));
                                        finish();
                                    });
                        }
                    } else {
                        Toast.makeText(this,
                                "❌ Error: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean esPasswordSegura(String password) {
        if (password.length() < 8) return false;
        boolean tieneMayuscula = false;
        boolean tieneMinuscula = false;
        boolean tieneNumero = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) tieneMayuscula = true;
            else if (Character.isLowerCase(c)) tieneMinuscula = true;
            else if (Character.isDigit(c)) tieneNumero = true;
        }
        return tieneMayuscula && tieneMinuscula && tieneNumero;
    }
}