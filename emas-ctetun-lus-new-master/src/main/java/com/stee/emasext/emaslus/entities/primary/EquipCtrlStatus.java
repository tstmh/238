package com.stee.emasext.emaslus.entities.primary;

import com.stee.emasext.emaslus.entities.primary.ids.EquipCtrlStatusId;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "equip_ctrl_status")
@IdClass(EquipCtrlStatusId.class)
public class EquipCtrlStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "equip_id", nullable = false)
    private String equipId;

    @Id
    @Column(name = "ctrl_code", nullable = false)
    private String ctrlCode;

    @Column(name = "ctrl_value")
    private Integer ctrlValue;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;

}
