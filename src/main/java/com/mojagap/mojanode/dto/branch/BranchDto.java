package com.mojagap.mojanode.dto.branch;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mojagap.mojanode.dto.company.CompanyDto;
import com.mojagap.mojanode.infrastructure.utility.DateUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class BranchDto {
    private Integer id;
    private String name;
    @JsonFormat(pattern = DateUtil.DD_MMM_YYY)
    private Date openingDate;
    private String status;
    private CompanyDto company;

    public BranchDto(Integer id, String name, Date openingDate, String status) {
        this.id = id;
        this.name = name;
        this.openingDate = openingDate;
        this.status = status;
    }
}
