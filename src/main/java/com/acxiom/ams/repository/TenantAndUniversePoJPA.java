package com.acxiom.ams.repository;

import com.acxiom.ams.model.po.TenantAndUniverseKey;
import com.acxiom.ams.model.po.TenantAndUniversePo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantAndUniversePoJPA extends JpaRepository<TenantAndUniversePo, TenantAndUniverseKey> {
}
