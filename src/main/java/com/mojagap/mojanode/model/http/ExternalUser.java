package com.mojagap.mojanode.model.http;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalUser {
    private Integer id;
    private String name;
    private String username;
    private String phone;
    private String website;
}
