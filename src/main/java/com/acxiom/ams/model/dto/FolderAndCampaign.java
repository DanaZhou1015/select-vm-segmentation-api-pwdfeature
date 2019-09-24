package com.acxiom.ams.model.dto;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 18:02 2/5/2018
 */
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Data
public class FolderAndCampaign {
    @NotNull(message = "{temporarySegment.folderId.notNull}")
    private List<Long> folderIdList;
    @NotNull(message = "{message.error.audienceId}")
    private List<Long> campaignIdList;
    private String owner;
}
