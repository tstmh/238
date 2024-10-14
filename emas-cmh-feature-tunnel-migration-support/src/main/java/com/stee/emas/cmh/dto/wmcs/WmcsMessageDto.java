package com.stee.emas.cmh.dto.wmcs;

import com.stee.emas.cmh.common.GsonUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Wang Yu
 * crated at 2022/5/26
 */
public class WmcsMessageDto implements Serializable {
    private static final long serialVersionUID = -8888679980246672981L;

    private String felsCode;
    private String equipType;
    private String equipId;
    private String location;
    private String attributeId;
    private String cmdValue;
    private Date dateTime;
    private String execId;
    private String cmdId;
    private String sender;

    @Override
    public String toString() {
        return GsonUtil.toJson(this);
    }

    public String getFelsCode() {
        return felsCode;
    }

    public void setFelsCode(String felsCode) {
        this.felsCode = felsCode;
    }

    public String getEquipType() {
        return equipType;
    }

    public void setEquipType(String equipType) {
        this.equipType = equipType;
    }

    public String getEquipId() {
        return equipId;
    }

    public void setEquipId(String equipId) {
        this.equipId = equipId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getCmdValue() {
        return cmdValue;
    }

    public void setCmdValue(String cmdValue) {
        this.cmdValue = cmdValue;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getExecId() {
        return execId;
    }

    public void setExecId(String execId) {
        this.execId = execId;
    }

    public String getCmdId() {
        return cmdId;
    }

    public void setCmdId(String cmdId) {
        this.cmdId = cmdId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
