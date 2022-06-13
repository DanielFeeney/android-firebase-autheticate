package com.example.myapplication.principal;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityPrincipalBinding;
import com.example.myapplication.login.LoginActivity;
import com.example.myapplication.model.Carro;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PrincipalActivity extends AppCompatActivity implements CarroAdapter.ItemClickListener {

    //Classes que executam a listagem
    RecyclerView recyclerView;
    CarroAdapter carroAdapter;

    //Camp de busca
    SearchView search;

    //Url da api
    String url = "https://private-anon-6538c58ebe-carsapi1.apiary-mock.com/cars";

    //Listas para receber os valores da api
    public static ArrayList<Carro> carroArrayList = new ArrayList<>();
    public static ArrayList<Carro> carros = new ArrayList<>();

    //Dialog pra abrir a msg de favoritar/excluir
    AlertDialog alerta;

    //Botoes flutuantes da tela
    FloatingActionButton favoritos;
    FloatingActionButton voltar;
    FloatingActionButton sair;

    //Database do firebase
    private DatabaseReference mDatabase;

    //Activity principal
    private ActivityPrincipalBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrincipalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Instancia do firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //recyclerView seta a lista de carros na tela
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        carroAdapter = new CarroAdapter(this, carroArrayList);
        carroAdapter.setClickListener(this);
        recyclerView.setAdapter(carroAdapter);
        //Realiza a busca de carros na api
        getCarros();

        //Setados os botoes flutuantes
        favoritos = findViewById(R.id.favorito);
        voltar = findViewById(R.id.voltar);
        voltar.setVisibility(View.GONE);
        sair = findViewById(R.id.logout);
        sair.setVisibility(View.GONE);

        //Ao clicar no botao favoritos a lista mostra os carros favoritados
        //e mostra o botao de sair
        favoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoritos.setVisibility(View.GONE);
                voltar.setVisibility(View.VISIBLE);
                sair.setVisibility(View.VISIBLE);
                getFavoritos();
            }
        });

        //Ao clicar no botao voltar a lista mostra todos os carros
        //e o botao de sair some
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voltar.setVisibility(View.GONE);
                sair.setVisibility(View.GONE);
                favoritos.setVisibility(View.VISIBLE);
                getCarros();
            }
        });

        //Ao clicar no botao sair a aplicacao desvincula o firebase
        //e passa pra tela de login
        sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_LONG).show();

                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        //Realiza a pesquisa de carros em todos os campos visiveis
        //Em ambas as telas
        //A acao dispara ao digitar uma letra ou quando clica em pesquisar
        search = findViewById(R.id.search_bar);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ArrayList<Carro> carrosFiltrados = filter(carros, query);
                if(!carrosFiltrados.isEmpty()){
                    carroArrayList.clear();
                    carroArrayList.addAll(carrosFiltrados);
                    carroAdapter.notifyDataSetChanged();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Carro> carrosFiltrados = filter(carros, newText);
                if(!carrosFiltrados.isEmpty()){
                    carroArrayList.clear();
                    carroArrayList.addAll(carrosFiltrados);
                    carroAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });

    }

    //Metodo que busca todos os carros salvos
    private void getFavoritos(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(Boolean.FALSE.equals(isFavorites())){
                    carroArrayList.clear();
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        for (DataSnapshot post: postSnapshot.getChildren()) {
                            Carro carro = post.getValue(Carro.class);
                            carroArrayList.add(carro);
                        }
                    }
                    carros.clear();
                    carros.addAll(carroArrayList);
                    carroAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
    }

    //Metodo que busca todos os carros da api
    private void getCarros() {
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                carroArrayList.clear();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if(jsonArray.length() > 0){
                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject obj = jsonArray.getJSONObject(i);
                            carroArrayList.add(new Carro(obj.getInt("year"), obj.getLong("id") , obj.getInt("horsepower") ,
                                                        obj.getString("make") , obj.getString("model") , obj.getDouble("price")));
                            carroAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                carros.clear();
                carros.addAll(carroArrayList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    //Metodo que dispara ao click na lista pegando a posicao do elemento na lista
    @Override
    public void onItemClick(View view, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecionado");
        if(isFavorites()){
            builder.setMessage("Favoritar esse carro?");
        }
        else{
            builder.setMessage("Excluir esse carro dos favoritos?");
        }

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if(isFavorites()){
                    //Ao clicar SIM o carro salva nos favoritos
                    favoritar(position);
                }
                else{
                    //Ao clicar SIM o carro exclui nos favoritos
                    excluir(position);
                }
                alerta.hide();
            }
        });
        //Ao clicar NAO a dialog some
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                alerta.hide();
            }
        });
        alerta = builder.create();
        alerta.show();
    }

    //Verifica em qual tela voce esta atualmente
    private boolean isFavorites() {
        return favoritos.getVisibility() == View.VISIBLE;
    }

    //Metodo que favorita o carro no firebase
    private void favoritar(int position){
        Carro carro = carroAdapter.getItem(position);
        mDatabase.child("carros").child("carro" + carro.getId().toString()).setValue(carro);
    }

    //Metodo que exclui o carro no firebase
    private void excluir(int position){
        Carro carro = carroAdapter.getItem(position);
        mDatabase.child("carros").child("carro" + carro.getId().toString()).removeValue();
    }

    //Metodo de filtragem do searchview
    private static ArrayList<Carro> filter(ArrayList<Carro> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final ArrayList<Carro> filteredModelList = new ArrayList<>();
        for (Carro carro : models) {
            String text = carro.getModel().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(carro);
                continue;
            }
            text = carro.getMake().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(carro);
                continue;
            }
            text = carro.getPrice().toString();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(carro);
                continue;
            }
            text = carro.getHorsepower().toString();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(carro);
                continue;
            }
            text = carro.getYear().toString();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(carro);
                continue;
            }

        }
        return filteredModelList;
    }
}