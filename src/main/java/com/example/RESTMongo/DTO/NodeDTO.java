package com.example.RESTMongo.DTO;

import com.example.RESTMongo.Validation.OnCreate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;

public record NodeDTO(String id,
                      @NotNull(groups = OnCreate.class, message = "Node can't be created with null value")
                      @Min(value = 0, message = "Value can't be less than 0")
                      Double value,
                      @NotNull(groups = OnCreate.class ,message = "Parents cannot be null")
                      ArrayList<String> parents) {
}
