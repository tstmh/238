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
@Table(name = "hist_ctrl_record")
public class HistCtrlRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "cmd_id", nullable = false)
    private String cmdId;

    @Id
    @Column(name = "equip_id", nullable = false)
    private String equipId;

    @Column(name = "received_date")
    private LocalDateTime receivedDate;

    @Column(name = "sender")
    private String sender;

    @Column(name = "cmd_content")
    private String cmdContent;

    @Column(name = "ack_date")
    private String ackDate;

    @Column(name = "ack_content")
    private String ackContent;

    @Column(name = "exec_id", nullable = false)
    private String execId;

}
