package com.example.myapplication.model;

public class Carro {

    Long id;
    Integer year, horsepower;
    String make, model;
    Double price;

    public Carro() {
    }

    public Carro(Integer year, Long id, Integer horsepower, String make, String model, Double price) {
        this.year = year;
        this.id = id;
        this.horsepower = horsepower;
        this.make = make;
        this.model = model;
        this.price = price;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getHorsepower() {
        return horsepower;
    }

    public void setHorsepower(Integer horsepower) {
        this.horsepower = horsepower;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

}
