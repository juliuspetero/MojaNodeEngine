package com.mojagap.mojanode.dto.branch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mojagap.mojanode.dto.company.CompanyDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class BranchDto {
    private Integer id;
    private String name;
    private String openingDate;
    private String status;
    private CompanyDto company;
}
