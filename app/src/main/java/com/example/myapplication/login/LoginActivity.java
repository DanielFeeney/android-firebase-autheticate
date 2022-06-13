package com.example.myapplication.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.forgot_password.ForgotPassword;
import com.example.myapplication.new_user.NewUserActivity;
import com.example.myapplication.principal.PrincipalActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    //Elementos da tela
    private EditText username, password;
    private TextView rec_password, new_user;
    private Button login;
    private ProgressBar load;

    //Autenticacao Firebase
    FirebaseAuth fireBaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username  = findViewById(R.id.log_username);
        password  = findViewById(R.id.log_password);
        new_user  = findViewById(R.id.sign_in);
        rec_password  = findViewById(R.id.forgot_password);
        login  = findViewById(R.id.log_button);
        load  = findViewById(R.id.loading);

        //Instancia Firebase
        fireBaseAuth = FirebaseAuth.getInstance();

        //Acao ao click do botao login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });
        //Acao ao click do botao recuperar senha
        //redireciona a tela para a de recuperar senha
        rec_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchScreen(ForgotPassword.class);
            }
        });
        //Acao ao click do botao cadastrar
        //redireciona a tela de cadastrar
        new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchScreen(NewUserActivity.class);
            }
        });
        //Load fica invisivel ate ser chamado
        load.setVisibility(View.GONE);
    }

    //Metodo que troca as telas
    private void switchScreen(Class nextScreen){
        Intent intent = new Intent(getApplicationContext(), nextScreen);
        startActivity(intent);
    }

    private void logIn() {
        String email = username.getText().toString();
        String senha = password.getText().toString();

        //Verificacao de e-mail e senha
        if(email.equals("") || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            username.setError("Preencha o e-mail corretamente");
            username.requestFocus();
            return;
        }

        if(senha.equals("")){
            password.setError("Preencha a senha");
            password.requestFocus();
            return;
        }

        //Load fica visivel ate terminar a execucao
        load.setVisibility(View.VISIBLE);
        //Firebase autentica os dados e verifica se o e-mail foi confirmado
        //Caso ele nao reconheca os dados como validos ou o e-mail como verificado aparece uma msg padrao
        //Caso ele autentique o usuario redireciona para tela principal
        fireBaseAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user.isEmailVerified()){
                        switchScreen(PrincipalActivity.class);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Verifique conta via email.", Toast.LENGTH_LONG).show();
                        user.sendEmailVerification();
                    }
                }
                else
                    Toast.makeText(getApplicationContext(), "Erro ao logar", Toast.LENGTH_LONG).show();

                //Load fica invisivel
                load.setVisibility(View.GONE);
            }
        });


    }
}
