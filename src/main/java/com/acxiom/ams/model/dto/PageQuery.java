package com.acxiom.ams.model.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: Gavin
 * @Description: 
 * @Date: 12/6/2017 5:47 PM
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class PageQuery {
    @Min(value = 0, message = "{page.query.min.size}")
    private Integer page;
    @Min(value = 5, message = "{page.query.page.min.size}")
    @Max(value = 100, message = "{page.query.page.max.size}")
    private Integer pageSize;
}
