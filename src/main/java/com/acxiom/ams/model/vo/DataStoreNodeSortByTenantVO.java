package com.acxiom.ams.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class DataStoreNodeSortByTenantVO {
    private String tenantName;
    private List<DataStoreNode> dataStoreNodeList;
}
