package com.example.myapplication.new_user;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class NewUserActivity extends AppCompatActivity implements View.OnClickListener{

    EditText newName, newEmail, newPassword;
    Button newButton;

    FirebaseAuth mAuthCria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        newName  = findViewById(R.id.new_name);
        newEmail  = findViewById(R.id.new_email);
        newPassword  = findViewById(R.id.new_password);
        newButton  = findViewById(R.id.new_button);

        mAuthCria = FirebaseAuth.getInstance();

        newButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String name = newName.getText().toString();
        String email = newEmail.getText().toString();
        String senha = newPassword.getText().toString();

        if(name.equals("")){
            newName.setError("Preencha o Nome");
            newName.requestFocus();
            return;
        }

        if(newEmail.equals("") || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            newEmail.setError("Preencha corretamente");
            newEmail.requestFocus();
            return;
        }

        if(senha.equals("")){
            newPassword.setError("Preencha a Senha");
            newPassword.requestFocus();
            return;
        }

        mAuthCria.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Usuário criado com sucesso", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                }
                else
                    Toast.makeText(getApplicationContext(), "Erro ao criar usuário", Toast.LENGTH_LONG).show();
            }
        });

    }
}