import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})
export class ConstantsService {
    IdleTime : number = 6000;// Configure Idle time in second for auto logout
    MaxRecordsCount: number;
    AlertCode: string;
    FluxUrl: string;
    
    constructor(private http: HttpClient) {
        if (!this.MaxRecordsCount) {
            this.http.get('assets/config/config.json')
                .subscribe(data => {
                    this.MaxRecordsCount = data['MaxRecordsCount'];
                    this.IdleTime =  data['IdleTime'];
                    this.AlertCode = data['AlertCode'];
                    this.FluxUrl = data['Flux'];
                });
        }

    }

    public get CurrentUser(): string {
        return this.CurrentUser;
    }

    public set CurrentUser(v: string) {
        this.CurrentUser = v;
    }


    // readonly CurrentUser: string = '';
    readonly defValueForSelect = 'All';
    readonly baseAppUrl: string = 'http://localhost:3000/';
    readonly distLocation: string = 'MyApplication/';
    readonly RibbonQuickAccessToolBarRegion: string = 'RibbonQuickAccessToolBarRegion';
    readonly WorkspaceRegion: string = 'WorkspaceRegion';
    readonly StatusBarRegion: string = 'StatusBarRegion';
    readonly TitleBarRegion: string = 'TitleBarRegion';
    readonly GridControl: string = 'gridToPrint';
    readonly HostIPAddress: string = 'localhost';
    readonly Port: number = 7001;
    readonly ConnectionFactoryName: string = 'jms/TT185ConnectionFactory';
    readonly TopicName: string = 'jms/cmh_aw_topic';
    readonly UPSDownMessage: string = 'UPS is down, please exit the application';
    readonly UPSAlarmSleepTime: number = 30;
    readonly EquipTypeDisableAllButtons: string = 'tic';
    readonly SystemID = 'emas';
    readonly IsResetPasswordClicked: boolean = false;
    readonly HasPermissionToResetPassword: boolean = false;
    readonly IsFirstTimeLogin: boolean = false;

    readonly CurrentRole: string = 'First Time Login';
    readonly CurrentDivision: string = 'All';
    readonly HasLogout: boolean = false;
    readonly AdminRole: string = 'ROLE_ADMIN'; // Used To check whether Logged in User is admin
    readonly IsIdle: boolean = false;
    readonly ExpWayCode: string = 'expway_code';
    readonly EmasSubSystem: string = 'emas_subsystem';
    readonly EquipType: string = 'equip_type';
    readonly LaneCode: string = 'lane_type';
    readonly Direction: string = 'expway_direction';
    readonly AlertType: string = 'traffic_alert';
    readonly AlarmStatus: string = 'tech_alarm_status';
    readonly AlertStatus: string = 'traffic_alert_status';
    readonly EquipStatus: string = 'equip_ope_status';
    readonly PropertyCode: string = 'property_code';
    readonly KmMarking: string = 'km_marking';
    readonly DetectorEquipType: string = 'dtt'; // For TrafficMeasures and TrafficAlerts(only dtt)
    readonly PhotoSensorReading: string = 'equip_status_code';
    readonly SoftwarePropertyCode: string = '2';
    readonly HardwarePropertyCode: string = '1';
    readonly SitePropertyCode: string = '0';
    readonly CTETunnelSubSystem: string = 'Group1';
    readonly SelectAll: string = 'All';
    readonly Equipment: string = 'xxx_xxxxx';
    readonly CommandResponseTimeOut: number = 30;
    readonly PixelFailureTimeOut: number = 180;
    readonly DefaultToggleTime: number = 2;
    readonly UPSEquipType: string = 'up2';
    readonly UPSCountDownMinutes: number = 2;
    readonly UPSCountDownSeconds: number = 59;
    readonly CommandStatusType = {
        Success: 0,
        Failure: 1,
        Processing: 44,
        TimeOut: 45
    };
}

export enum CommandStatusType {
    Success = 0,
    Processing = 44,

    TimeOut = 45
}
