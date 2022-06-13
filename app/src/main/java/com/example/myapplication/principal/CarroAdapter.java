package com.example.myapplication.principal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Carro;

import java.util.List;

public class CarroAdapter extends RecyclerView.Adapter<CarroAdapter.ViewHolder>  {

    List<Carro> carros;
    LayoutInflater inflater;
    ItemClickListener clickListener;

    //Instancia a classe do adapter
    public CarroAdapter(Context context, List<Carro> carros) {
        this.inflater = LayoutInflater.from(context);
        this.carros = carros;
    }

    //Vincula a tela de listagem com a tela da classe principal
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.activity_carro, parent, false);
        return new ViewHolder(view);
    }

    // Vincula os textos com os elementos da lista
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Carro carro = getItem(position);
        holder.year.setText(carro.getYear().toString());
        holder.horsepower.setText(carro.getHorsepower().toString());
        holder.make.setText(carro.getMake());
        holder.model.setText(carro.getModel());
        holder.price.setText(carro.getPrice().toString());
    }

    // Numero total de elementos na lista
    @Override
    public int getItemCount() {
        return carros.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //Elementos da tela
        TextView year;
        TextView horsepower;
        TextView make;
        TextView model;
        TextView price;

        //Vinculo dos elementos da tela
        //Elemento da lista
        ViewHolder(View itemView) {
            super(itemView);
            year = itemView.findViewById(R.id.year);
            horsepower = itemView.findViewById(R.id.horsepower);
            make = itemView.findViewById(R.id.make);
            model = itemView.findViewById(R.id.model);
            price = itemView.findViewById(R.id.price);
            //Acao de click ao selecionar o elemento da lista
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    //Retorna a posicao dentro da lista
    Carro getItem(int index) {
        return carros.get(index);
    }

    //Metodo para que a classe principal possa interagir com essa classe
    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    //Acao de click
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
