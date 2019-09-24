package com.acxiom.ams.repository;

import com.acxiom.ams.model.po.TenantAndChannelPo;
import com.acxiom.ams.model.po.TenantPo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 11:46 12/19/2017
 */
public interface TenantAndChannelPoJPA extends JpaRepository<TenantAndChannelPo, Long> {

    List<TenantAndChannelPo> findByTenantPo(TenantPo tenantPo);

    TenantAndChannelPo findByTenantPoAndChannelName(TenantPo tenantPo,
        String channelName);

    List<TenantAndChannelPo> findByIdIn(List<Long> ids);
}
