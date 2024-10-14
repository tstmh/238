package com.stee.emasext.emaslus.entities.secondary;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "equip_status")
public class EquipStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "equip_id", nullable = false)
    private String equipId;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "status_code", nullable = false)
    private String statusCode;

}
