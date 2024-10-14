package com.stee.emasext.emaslus.entities.primary;

import com.stee.emasext.emaslus.entities.primary.ids.TunnelEquipStatusId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "equip_attr_status")
@IdClass(TunnelEquipStatusId.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TunnelEquipStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "equip_id", nullable = false)
    private String equipId;

    @Id
    @Column(name = "attr_code", nullable = false)
    private String attrCode;

    @Column(name = "attr_value")
    private Integer attrValue;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;

}
