package com.coppel.dto.originalOrder;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OriginalOrderDTO {

    public String originalOrderId;
    public String createDateTimestamp;
    public String minimumStatus;
    public String maximumStatus;
    public Integer unitCount;
    public String orderType;
    public Integer destinationBusinessUnitId;
    public Integer sourceBusinessUnitId;
    public Integer typeCode;
    public String asnReference;
    public List<ItemDTO> items;
}
