package com.acxiom.ams.mapper.util;

import com.acxiom.ams.model.po.FolderPo;
import org.springframework.stereotype.Component;

/**
 * Created by cldong on 12/14/2017.
 */
@Component
public class FolderPoToFolderId {
    public Long toFolderId(FolderPo folderPo) {
        return folderPo.getId();
    }
}
