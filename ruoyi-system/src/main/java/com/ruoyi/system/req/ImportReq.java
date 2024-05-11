package com.ruoyi.system.req;

import com.ruoyi.system.domain.KgEdgeClass;
import com.ruoyi.system.domain.KgNodeClass;
import lombok.Data;

@Data
public class ImportReq {
    private KgNodeClass fromNodeClass;

    private KgNodeClass toNodeClass;

    private KgEdgeClass edgeClass;

    private String jsonData;
}
