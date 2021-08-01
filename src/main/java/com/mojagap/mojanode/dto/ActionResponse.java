package com.mojagap.mojanode.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
public final class ActionResponse {
    private Integer resourceId;
    private String message;

    public ActionResponse(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public ActionResponse(String message) {
        this.message = message;
    }
}
