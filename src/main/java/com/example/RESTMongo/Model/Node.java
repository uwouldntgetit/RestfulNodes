package com.example.RESTMongo.Model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

// @Document marks the class as a MongoDB document, basically a table
@Document(collection = "Nodes")
public class Node {
    @Id
    private final String id;
    private double value;
    // list of parents' id
    @NotNull
    ArrayList<String> parents;

    public Node(String id, double value, ArrayList<String> parents){
        this.id = id;
        this.value = value;
        this.parents = parents;
    }

    public String getId() {
        return id;
    }
    public double getValue(){
        return value;
    }
    public ArrayList<String> getParents(){
        return parents;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setParents(ArrayList<String> parents) {
        this.parents = parents;
    }
}
