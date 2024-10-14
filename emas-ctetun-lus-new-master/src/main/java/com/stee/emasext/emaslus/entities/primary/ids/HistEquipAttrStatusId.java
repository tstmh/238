package com.stee.emasext.emaslus.entities.primary.ids;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Wang Yu
 * Created at 2023/6/9
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistEquipAttrStatusId implements Serializable {
    private String equipId;
    private String attrCode;
}
