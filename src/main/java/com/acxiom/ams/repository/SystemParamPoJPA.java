package com.acxiom.ams.repository;

import com.acxiom.ams.model.po.SystemParamPo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 3:59 PM 10/9/2018
 */
public interface SystemParamPoJPA extends JpaRepository<SystemParamPo, Long> {
    SystemParamPo findByJobKey(String jobKey);
}
