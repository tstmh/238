package com.stee.emasext.emaslus.entities.primary.ids;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Wang Yu
 * Created at 2023/4/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TunnelEquipStatusId implements Serializable {
    private String equipId;
    private String attrCode;
}
