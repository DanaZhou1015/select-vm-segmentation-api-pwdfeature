package com.acxiom.ams.model.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 16:35 3/28/2018
 */

@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class OwnerAndNodeDTO {
   private List<String> ownerList;
   private List<NodeDTO> nodeDTOList;
   private List<String> dataTypeList;
   private List<OwnerAndDataType> ownerAndDataTypeList;
}
