package com.acxiom.ams.model.dto.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import java.util.List;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 3:37 PM 9/10/2018
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class ExportInsightDTO {
   @NotBlank(message = "{message.error.audienceId}")
   private String audienceId;
   @NotBlank(message = "{message.error.audienceName}")
   private String audienceName;
   @NotEmpty(message = "{message.error.segmentInfo}")
   private List<SegmentInfo> segmentInfoList;
   @NotEmpty(message = "{message.error.insightRule}")
   private List<InsightRule> insightRuleList;
}
