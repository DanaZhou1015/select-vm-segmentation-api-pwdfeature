package com.acxiom.ams.model.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 4:00 PM 10/9/2018
 */
@Table(name = "system_param")
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@ToString
@Data
public class SystemParamPo {
    @Id
    @Column(name = "job_key", nullable = false)
    private String jobKey;
    @Column(name = "last_run_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastTaskRunTime;
}
