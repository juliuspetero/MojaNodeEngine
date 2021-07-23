package com.mojagap.mojanode.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ActionResponse {
    private final Integer resourceId;

    public ActionResponse(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getResourceId() {
        return resourceId;
    }

}
