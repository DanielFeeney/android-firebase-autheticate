package com.example.myapplication.forgot_password;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    //Elementos da tela
    private EditText rec_password;
    private Button recPassBtn;

    //Autenticacao Firebase
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //Instancia Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //Click do botao de recuperar senha
        rec_password  = findViewById(R.id.rec_email);
        recPassBtn  = findViewById(R.id.rec_pass_btn);
        recPassBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        //Pega o texto do e-mail e verifica se esta vazio ou no padrao de e-mail
        //caso der falha ele retorna uma msg padrao
        String email = rec_password.getText().toString();
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.equals("")){
            rec_password.setError("Preencha o e-mail corretamente");
            return;
        }

        //Passsada a verificacao o e-mail eh enviado ao firebase
        //em caso de sucesso ou falha aparece uma msg
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "E-mail enviado.", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                }
                else
                    Toast.makeText(getApplicationContext(), "Erro.", Toast.LENGTH_LONG).show();
            }
        });
    }
}