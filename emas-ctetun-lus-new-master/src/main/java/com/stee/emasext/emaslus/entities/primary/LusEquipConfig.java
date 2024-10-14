package com.stee.emasext.emaslus.entities.primary;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "lus_equip_config")
public class LusEquipConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "Equip_id", nullable = false)
    private String equipId;

    @Column(name = "Equip_type")
    private String equipType;

    @Column(name = "System_id")
    private String systemId;

    @Column(name = "Subsystem_id")
    private String subsystemId;

    @Column(name = "Fels_code")
    private String felsCode;

    @Column(name = "Description")
    private String description;

    @Column(name = "Enable")
    private String enable;

    @Column(name = "Ip_address")
    private String ipAddress;

    @Column(name = "Controller_id")
    private String controllerId;

    @Column(name = "Assign_position")
    private Integer assignPosition;

    @Column(name = "Firmware_version")
    private String firmwareVersion;

    @Column(name = "Update_time")
    private LocalDateTime updateTime;

    @Column(name = "Location_description")
    private String locationDescription;

    @Column(name = "Ccs_id")
    private String ccsId;

    @Column(name = "Ccs_enable")
    private String ccsEnable;

    @Column(name = "Created_by")
    private String createdBy;

    @Column(name = "Created_date")
    private LocalDateTime createdDate;

    @Column(name = "Updated_by")
    private String updatedBy;

    @Column(name = "Updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "port")
    private Integer port;

}
