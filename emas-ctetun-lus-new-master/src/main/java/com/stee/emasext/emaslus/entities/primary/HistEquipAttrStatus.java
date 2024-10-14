package com.stee.emasext.emaslus.entities.primary;

import com.stee.emasext.emaslus.entities.primary.ids.HistEquipAttrStatusId;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "hist_equip_attr_status")
@IdClass(HistEquipAttrStatusId.class)
public class HistEquipAttrStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "equip_id", nullable = false)
    private String equipId;

    @Id
    @Column(name = "attr_code", nullable = false)
    private String attrCode;

    @Column(name = "attr_value")
    private Integer attrValue;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;

}
