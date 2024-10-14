package com.stee.pasystem.entities;

import javax.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "equip_config")
@Data
public class TunnelEquipConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "equip_id")
    private String equipId;
    @Basic
    @Column(name = "equip_type")
    private String equipType;
    @Basic
    @Column(name = "plc_host")
    private String plcHost;
    @Basic
    @Column(name = "modbus_status_address")
    private Integer modbusStatusAddress;
    @Basic
    @Column(name = "status_address")
    private Integer statusAddress;
    @Basic
    @Column(name = "created_date")
    private Date createdDate;
    @Basic
    @Column(name = "created_by")
    private String createdBy;
    @Basic
    @Column(name = "updated_date")
    private Date updatedDate;
    @Basic
    @Column(name = "updated_by")
    private String updatedBy;
    @Basic
    @Column(name = "fels_code")
    private String felsCode;
    @Basic
    @Column(name = "status_address_length")
    private Integer statusAddressLength;
    @Basic
    @Column(name = "expway_code")
    private String expwayCode;
    @Basic
    @Column(name = "dir")
    private Integer dir;
    @Basic
    @Column(name = "km_marking")
    private BigDecimal kmMarking;
    @Basic
    @Column(name = "location_description")
    private String locationDescription;
}
