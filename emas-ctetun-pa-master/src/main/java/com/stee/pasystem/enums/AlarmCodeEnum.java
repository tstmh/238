package com.stee.pasystem.enums;

import lombok.Getter;

@Getter
public enum AlarmCodeEnum {
    SYSTEM_SHUTDOWN_DETECTED(1, "System shutdown detected", "dss"),
    HIGH_TEMPERATURE(2, "High Temperature", "dst"),
    REDUNDANT_POWER_FAILURE(3, "Redundant Power Failure", "dsp"),
    LINK_ACTIVE(4, "Link Active", "dsl"),
    MICROPHONE_SUPERVISION(5, "Microphone Supervision", "NA"),
    CONFIGURATION_FILE(6, "Configuration File", "dsc"),
    CALL_STATION_EXTENSION(7, "Call Station Extension", "dsc"),
    CONFIGURATION_VERSION(8, "Configuration Version", "dsv"),
    ILLEGAL_CONFIGURATION(9, "Illegal Configuration", "dsi"),
    USER_INJECTED_FAULT(10, "User Injected Fault", "dsj"),
    NO_FAULTS(11, "No Faults", "NA"),
    ZONE_LINE_FAULT(12, "Zone Line Fault", "dsz"),
    PRERECORDED_MESSAGE_CORRUPT(13, "Prerecorded Message Corrupt", "NA"),
    FAN1_FAULT(14, "Fan 1 Fault", "dsa"),
    FAN2_FAULT(15, "Fan 2 Fault", "dsn"),
    GROUND_SHORT_FAULT(16, "Ground Short Fault", "dsg"),
    POE_SUPPLY_FAULT(17, "PoE Supply Fault", "dss"),
    POWER_SUPPLY_A_FAULT(18, "Power Supply A Fault", "dsp"),
    POWER_SUPPLY_B_FAULT(19, "Power Supply B Fault", "dss"),
    EXTERNAL_POWER_FAULT(20, "External Power Fault", "dsp"),
    DC_AUX1_FAULT(21, "DC Aux 1 Fault", "dsd"),
    DC_AUX2_FAULT(22, "DC Aux 2 Fault", "dse"),
    BATTERY_SHORT_FAULT(23, "Battery Short Fault", "dsb"),
    BATTERY_RI_FAULT(24, "Battery Ri Fault", "dsr"),
    BATTERY_OVERHEAT_FAULT(25, "Battery Overheat Fault", "dso"),
    BATTERY_FLOAT_CHARGE_FAULT(26, "Battery Float Charge Fault", "dsf"),
    MAINS_ABSENT_CHARGE_FAULT(27, "Mains Absent Charge Fault", "dsm"),
    MAINS_ABSENT_PSU1_FAULT(28, "Mains Absent PSU 1 Fault", "dss"),
    BACKUP_ABSENT_PSU1_FAULT(29, "Backup Absent PSU 1 Fault", "dsh"),
    DC_OUT1_PSU2_FAULT(30, "DC Out 1 PSU 2 Fault", "dsk"),
    DC_OUT2_PSU2_FAULT(31, "DC Out 2 PSU 2 Fault", "dsl"),
    AUDIO_LIFELINE_PSU2_FAULT(32, "Audio Lifeline PSU 2 Fault", "dsq"),
    ACC_SUPPLY_PSU2_FAULT(33, "ACC Supply PSU 2 Fault", "dsr"),
    MAINS_ABSENT_PSU3_FAULT(34, "Mains Absent PSU 3 Fault", "dsm"),
    BACKUP_ABSENT_PSU3_FAULT(35, "Backup Absent PSU 3 Fault", "dsn"),
    DC_OUT1_PSU3_FAULT(36, "DC Out 1 PSU 3 Fault", "dsk"),
    DC_OUT2_PSU3_FAULT(37, "DC Out 2 PSU 3 Fault", "dsl"),
    AUDIO_LIFELINE_PSU3_FAULT(38, "Audio Lifeline PSU 3 Fault", "dsq"),
    ACC_SUPPLY_PSU3_FAULT(39, "ACC Supply PSU 3 Fault", "dsr"),
    POWER_MAINS_SUPPLY_FAULT(40, "Power Mains Supply Fault", "dss"),
    POWER_BACKUP_SUPPLY_FAULT(41, "Power Backup Supply Fault", "dsv"),
    CHARGE_SUPPLY_VOLTAGE_TOO_LOW_FAULT(42, "Charge Supply Voltage Too Low Fault", "dsw"),
    BATTERY_OVERVOLTAGE_FAULT(43, "Battery Overvoltage Fault", "dsx"),
    BATTERY_UNDERVOLTAGE_FAULT(44, "Battery Undervoltage Fault", "dsy"),
    MEDIA_CLOCK_FAULT(45, "Media Clock Fault", "dsk"),
    CHARGE_FAULT(46, "Charge Fault", "dsc"),
    SYNCHRONIZATION_FAULT(47, "Synchronization Fault", "dsy"),
    INTERNAL_POWER_FAULT(48, "Internal Power Fault", "dsi"),
    NETWORK_CHANGE(49, "Network Change", "dsn"),
    AMP_48V_A_FAULT(50, "Amp 48V A Fault", "dsg"),
    AMP_48V_B_FAULT(51, "Amp 48V B Fault", "dsh"),
    EOL_FAILURE_LINE_A_FAULT(52, "EOL Failure Line A Fault", "dse"),
    EOL_FAILURE_LINE_B_FAULT(53, "EOL Failure Line B Fault", "dsf"),
    VOIP_FAULT(54, "VoIP Fault", "NA"),
    OVERHEAT_FAULT(55, "Overheat Fault", "dso"),
    UNIT_RESET_FAULT(56, "Unit Reset Fault", "dsr"),
    INCOMPATIBLE_FIRMWARE(57, "Incompatible Firmware", "dsf"),
    AMP_ACC_18V_FAULT(58, "Amp ACC 18V Fault", "dsa"),
    AMP_SPARE_INTERNAL_FAULT(59, "Amp Spare Internal Fault", "dsi"),
    AMP_CHANNEL_OVERLOAD_FAULT(60, "Amp Channel Overload Fault", "dso"),
    AMP_20V_FAULT(61, "Amp 20V Fault", "dsp"),
    NETWORK_LATENCY_FAULT(62, "Network Latency Fault", "dsl"),
    AUDIO_DELAY_FAULT(63, "Audio Delay Fault", "dsd"),
    INTERNAL_COMMUNICATION_FAULT(64, "Internal Communication Fault", "dsr"),
    PREREORDED_MESSAGES_NAMES(65, "Prereorded Messages Names", "dsp"),
    UNIT_MISSING(66, "Unit Missing", "dsu"),
    UPS_BATTERY_OVERCHARGE(67, "Battery Overcharge", "dso"),
    UPS_BATTERY_LOW(68, "Battery Low", "dsl"),
    AMP_CHANNEL_FAULT(69, "Amp Channel Fault", "dss"),
    AMP_SHORT_CIRCUIT_LINE_A_FAULT(70, "Amplifier Short Circuit Line A Fault", "dss"),
    AMP_SHORT_CIRCUIT_LINE_B_FAULT(71, "Amplifier Short Circuit Line B Fault", "dsl"),
    PRERECORDED_MESSAGES_NAMES(72, "Prerecorded Messages Names", "dse"),
    PRERECORDED_MESSAGES_CORRUPT(73, "Prerecorded Messages Corrupt", "dsm"),
    RACK_PRIMARY_POWER_FAILURE(74, "Rack Primary Power Failure", "dsp"),
    RACK_SECONDARY_POWER_FAILURE(75, "Rack Secondary Power Failure", "dss");
    ;

    private final int code;
    private final String description;
    private final String attrCode;

    AlarmCodeEnum(int code, String description, String attrCode) {
        this.code = code;
        this.description = description;
        this.attrCode = attrCode;
    }

    public static AlarmCodeEnum ofCode(int code) {
        for (AlarmCodeEnum alarm : values()) {
            if (alarm.getCode() == code) {
                return alarm;
            }
        }
        throw new IllegalArgumentException("Invalid alarm code: " + code);
    }

}
