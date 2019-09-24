package com.acxiom.ams.model.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: Gavin
 * @Description:
 * @Date: 12/11/2017 4:27 PM
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class ProfilingDTO {
    private long id;
    private String profilingDescription;
    @NotNull(message = "{profiling.request.param.name.notNull}")
    private String profilingName;
    @NotNull(message = "{profiling.request.param.json.notNull}")
    private String profilingJson;
    private String createBy;
}
