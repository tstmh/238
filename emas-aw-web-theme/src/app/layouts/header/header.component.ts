import { PublicComModule } from './../../public/public-com.module';
import { Component, OnInit, OnDestroy, AfterViewInit, Input, Output, EventEmitter } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { Subscription, from, Subject } from 'rxjs';
import { SystemService } from 'src/app/service/system.service';
import { NzMessageService } from 'ng-zorro-antd/message';
import { LoginService } from '../../auth/login.service';
import { DialogService } from 'src/app/share/dialog';
import { TabRouterService } from '../tab-router/tab-router.service';
import { SYSTEM_EVENT } from '../../app.constants';
import { BullySubjectService } from '../../share/service/bully-subject.service';
import { EventsService } from '../../pages/events/events.service';
import { takeUntil, windowWhen } from 'rxjs/operators';
import { WebSocketService } from 'src/app/service/websocket.service';
import { EquipmentService } from 'src/app/pages/equipment-control/equipment-control.service';
import { ConstantsService } from '../../service/constants.service';
import { CommonService, EquipType } from '../../service/common.service';
import { NzModalService } from 'ng-zorro-antd';
import * as moment from 'moment';

@Component({
    // tslint:disable-next-line:component-selector
    selector: 'sj-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.css'],
    // animations: [fadeIn, fadeOut]
})
export class HeaderComponent implements OnInit, AfterViewInit, OnDestroy {
    abortWebsocket$ = new Subject<void>();
    isVisible = false;
    username: any = '';
    showSider = true;
    gisAccess = false;
    openGisLoading = false;
    showMsgBox = false;
    statusBarText = '';
    unSubscription = new Subject;
    userName = '';
    roleName = '';
    flag = true;
    theme = 'light';
    command = '';
    commandStr = '';
    equipId = '';
    alertId = '';
    alertCode = '';
    isConfigAlertCode: boolean = false;
    listAlertType = [];
    alertCodeList = [];
    isMeasure = false;
    timerArr = [];
    status = '';
    isSpinning = false;
    measureMin = '00';
    measureSec = '00';
    displayListOfData = [];
    constructor(
        private translate: TranslateService,
        private router: Router,
        private dialog: DialogService,
        private equipmentService: EquipmentService,
        private modal: NzModalService,
        public systemService: SystemService,
        private nzMessageService: NzMessageService,
        private loginService: LoginService,
        private eventsService: EventsService,
        private commonService: CommonService,
        private constantsService: ConstantsService,
        private webSocketService: WebSocketService
    ) {
        // 语言服务
        // this.systemService.langSet();

        // 查看gis权限
        // this.checkGisAccess();
    }

    // checkGisAccess() {
    //     const roleId = localStorage.getItem('role_id');
    //     this.auth.getAccessRightByRoleId(roleId).subscribe(res => {
    //         if (res['code'] === '000000') {
    //             this.gisAccess = res['data'].some(role => role.rightName === 'GIS');
    //             if (this.gisAccess) {
    //                 this.openGIS();
    //             }
    //         }
    //     });
    // }
    openGIS() {
        this.openGisLoading = true;
        const url = `${location.origin}/#/gis`;
        window.open(url);

        setTimeout(() => (this.openGisLoading = false), 5000);
    }

    login() {
        this.router.navigate(['/login']);
    }
    // sider的显示与隐藏
    onShowSider() {
        this.showSider = !this.showSider;
    }
    // 发送CCM登出状态的消息
    ccmlogOut() {
        // this.ls.logOut();
    }
    cancel(): void {
        this.nzMessageService.info('cancel');
    }
    // 打印
    print() {
        // const printhtml = bodyhtml.substring(bodyhtml.indexOf(startFlag),
        //     bodyhtml.indexOf(endFlag));
        // 生成并打印ifrme
        // const printHtml = document.getElementById('tableContent').innerHTML;
        // const f = document.getElementById('printf');
        // f.contentDocument.write(printHtml);
        // f.contentDocument.close();
        // f.contentWindow.print();

        // 有打印样式，但取消页面不重载，使用reload重载
        const printHtml = document.getElementById('tableContent').innerHTML;
        //  const preHtml = window.document.body.innerHTML;
        window.document.body.innerHTML = printHtml;
        window.print();
        //  window.document.body.innerHTML = preHtml;
        window.location.reload();

        // 无打印样式
        // const printHtml = document.getElementById('tableContent').innerHTML;
        // const newWindow = window.open('page.html');
        // newWindow.document.body.innerHTML = printHtml;
        // newWindow.print();
        // newWindow.close();
    }
    // 帮助
    help() {
        this.flag = true;
        this.isVisible = true;
    }

    // 关于
    about() {
        this.flag = false;
        this.isVisible = true;
    }

    // logout
    confirm(): void {
        this.nzMessageService.info('Logout Successful.');
        this.loginService.logOutAuto();

    }
    ngOnInit() {
        const tempRole = localStorage.getItem('role_id').slice(5).toLowerCase();
        this.userName = localStorage.getItem('user_name');
        this.roleName = tempRole.substring(0, 1).toUpperCase() + tempRole.substring(1);
        this.webSocketService.getMessage().pipe(takeUntil(this.unSubscription)).subscribe((msg: any) => {
            let count = 0;
            // 心跳通信 type === 'HeartBeat'
            try {
                const subStr = msg.data.substring(0, 11);
                // console.log('subStr:', subStr);
                if (subStr == 'AlertNotify') {
                    this.alertId = msg.data.split(',')[1];
                    this.alertCode = msg.data.split(',')[2];
                    this.getTrafficAlert();
                }
                switch (msg.data) {
                    case 'EquipStatus':
                        this.statusBarText = 'Equipment(s) Status Updated!';
                        this.statusBarTextClear();
                        break;
                    case 'TechnicalAlarm':
                        this.statusBarText = 'Technical Alarm(s) Updated!';
                        localStorage.setItem('alarmUpdate', '1');
                        this.getFireAlarm();
                        this.statusBarTextClear();
                        break;
                    case 'TrafficAlert':
                        this.statusBarText = 'TrafficAlert(s) Updated!';
                        this.statusBarTextClear();
                        // this.showNotification();
                        break;
                    case 'CteTunStatus':
                        this.statusBarText = 'CTE Tunnel Status Updated!';
                        localStorage.setItem('cteStatusUpdate', '1');
                        this.statusBarTextClear();
                        break;
                    case 'CteTunMeasure':
                        this.getMeasure();
                        break;
                    default:
                        break;
                }
            } catch (e) {
                console.warn('websocket process warn:', e);
            }
        });
        this.getFireAlarm();                //check have fire alarm or not, need show the lingdang
        this.getMeasure();                  //check have traffic measure or not
        this.getTheme();
        this.getTrafficAlertType();
    }
    sBTextClear;
    statusBarTextClear() {
        if (this.sBTextClear) {
            clearTimeout(this.sBTextClear);
        }
        this.sBTextClear = setTimeout(() => { this.statusBarText = '' }, 20000);
    }
    ngOnDestroy(): void {
        if (this.unSubscription) {
            this.unSubscription.next();
            this.unSubscription.complete();
        }
    }

    ngAfterViewInit() {
        setTimeout(() => this.systemService.timeSet());
    }

    //加载Technical Alarm
    getFireAlarm() {
        const subSystemId = 'group1';
        const equipType = 'fir';
        const expwayCode = 'af';
        const queryData = {
            propertyCode: [0]
        };
        queryData['subsystemId'] = subSystemId;
        queryData['expwayCode'] = expwayCode;
        queryData['equipType'] = equipType;

        this.eventsService.GetTechnicalAlarmByEquipTypeAndExpwayCode(queryData).subscribe((res: any) => {
            const tempData: any = res.Body.getTechnicalAlarmByEquipTypeAndExpwayCodeResponse.technicalAlarmList;
            this.mergeData(tempData);
        });
    }
    mergeData(displayList = []): void {
        for (const display of displayList) {
            this.displayListOfData.push(display['equipId']);
        }
        // this.getMeasure();
    }

    getMeasure() {
        if (this.displayListOfData.length > 0) {
            this.getEquipId();
            this.isMeasure = true;
        } else {
            this.isMeasure = false;
        }
    }

    getMeasureData() {
        const queryData = {
            equipId: this.equipId
        };
        this.equipmentService.getWMSEquipStatusByEquipId(queryData).subscribe(item => {
            //console.log('getMeasure:', item)
            if (item.code == 0) {
                let measureList = item.data.measureStatusList;
                measureList.forEach((ele) => {
                    let measureCode = ele.measureCode;
                    if (measureCode == 'mem') {
                        this.measureMin = ele.measureValue;
                        if (this.measureMin.toString().length < 2) {
                            this.measureMin = '0' + this.measureMin;
                        }
                    }
                    if (measureCode == 'mes') {
                        this.measureSec = ele.measureValue;
                        if (this.measureSec.toString().length < 2) {
                            this.measureSec = '0' + this.measureSec;
                        }
                    }
                });
            }
        });
        this.isMeasure = false;
    }

    async getTrafficAlertType() {
        const allType = await this.commonService.allType;
        const listAlertTypeCode = allType[EquipType.ALERT_TYPE];
        listAlertTypeCode.forEach((item) => {
            this.listAlertType.push({ code: item.value, description: item.description });
        });
        let aCode = this.constantsService.AlertCode;
        if (aCode) {
            this.alertCodeList = aCode.split(',');
        }
        // console.log('listAlertType:', this.listAlertType, 'alertCodeList:', this.alertCodeList);
    }

    getTrafficAlert() {
        if (this.alertId != '' && this.alertCode != '') {
            let equipmentId = this.alertId.substring(0,10);
            let alertType;
            this.alertCodeList.forEach(element => {
                if (element == this.alertCode) {
                    this.isConfigAlertCode = true;
                }
            });
            if (this.isConfigAlertCode) {
                this.listAlertType.forEach(ele => {
                    if (ele.code == this.alertCode) {
                        alertType = ele.description;
                    }
                });
                // const queryData = {
                //     alertId: this.alertId
                // };
                // this.eventsService.getTrafficAlertByAlertId(queryData).subscribe((res: any) => {
                //     equipmentId = res.Body.getTrafficAlertByAlertIdResponse.TrafficAlertDto.equipConfigDto.equipId;
                //     this.showNotification(equipmentId, alertType);
                // });
                this.showNotification(equipmentId, alertType);
            }
            this.isConfigAlertCode = false;
            this.alertCode = '';
            this.alertId = '';
        }
    }

    getEquipId() {
        const queryData = {
            equipType: 'cdt',
            attrCode: 'sta'
        }
        this.equipmentService.getWMSEquipStatusByEquipType(queryData).subscribe(item => {
            //console.log('getEquipId:', item)
            if (item.code == 0) {
                let dataList = item.data;
                for (let equip of dataList) {
                    this.equipId = equip.equipId;
                }
            } else {
                this.nzMessageService.create('error', `No Data`);
            }
            if (this.isMeasure) {
                this.getMeasureData();
            }
        });
    }

    showNotification(equipmentId, alertType) {
        //console.log(window.Notification.permission);
        if (window.Notification) {
            if (window.Notification.permission == "granted") {
                var notification = new Notification('New TrafficAlert', {
                    body: `Got a new TrafficAlert, EquipId: ${equipmentId}, Alert Type: ${alertType}.`
                });
                notification.addEventListener('click', (event) => {
                    window.focus();
                    event.preventDefault();
                    location.replace('/#/emas/events/traffic-alert');
                })
                //setTimeout(function() { notification.close(); }, 10000);
            } else {
                window.Notification.requestPermission();
            }
        } else {
            console.log("Show MSG failed.");
        }
    }

    remoteButton(e) {
        if (e) {
            this.command = "cdr";
            this.commandStr = "Reset";
            console.log("Reset.");
        } else {
            this.command = "cds"
            this.commandStr = "Stop";
            console.log("Stop.");
        }
        this.getEquipId();
        this.Send('cdt');
    }
    Send(equipType) {
        this.dialog.confirm({
            title: 'Confirmation Window',
            content: `Send ${this.commandStr} Command to Count Down Timer Equipment?`,
            buttonOkTxt: 'Yes(Y)',
            buttonCancelTxt: 'No(N)'
        }).subscribe(res => {
            if (res) {
                this.confirmSend(this.equipId, equipType);
                this.isSpinning = true;
                this.getSocket();
            } else {
                this.cancelSend();
            }
        });
    }
    confirmSend(item, equipType) {
        const queryData = {
            wmcsMsg: {
                equipId: item,
                equipType: equipType,
                attributeName: this.command,
                cmdValue: '1',
                sender: 'AW_user',
                exeId: `AW_${localStorage.getItem('user_name')}_${moment().format('HHmmssSSS')}`,
                cmdId: '0'
            }
        };
        this.equipmentService.AW_CFELS_WMS(queryData);
        this.modalListInt();
    }
    modalListInt(): void {
        const timer = setTimeout(() => {
            this.status = 'Time Out';
            this.timerArr = [];
            this.abortWebsocket$.next();
            this.isSpinning = false;
            if (this.status == 'Success') {
                this.modal.success({ nzTitle: 'Send Success', nzContent: `Send ${this.commandStr} Command ${this.status}.` });
            } else {
                this.modal.error({ nzTitle: 'Send Failed', nzContent: `Send ${this.commandStr} Command ${this.status}.` });
            }

        }, 12000);
        this.timerArr.push(timer);
    }
    getSocket(): void {
        this.webSocketService.getMessage().pipe(takeUntil(this.abortWebsocket$.asObservable())).subscribe((msg: any) => {
            try {
                const response = JSON.parse(msg.data);
                console.log('response:', response)
                if (response.type !== 'HeartBeat') {
                    if (this.equipId === response.EquipId) {
                        if (response.Status == 0) {
                            this.status = 'Command Failed';
                        } else if (response.Status == 1) {
                            this.status = 'Success';
                        } else if (response.Status == 2) {
                            this.status = 'PLC Error';
                        } else {
                            this.status = 'Time Out';
                        }
                        clearTimeout(this.timerArr[0]);
                        this.equipId = '';
                        this.abortWebsocket$.next();
                        this.isSpinning = false;
                    }
                }
            } catch (e) {
                console.warn('websocket process warn:', e);
            }
        });
    }
    cancelSend() {
        console.log("Cancel");
    }

    changeTheme(): void {
        const body = document.getElementsByTagName('body')[0];
        if (body.getAttribute(`data-theme-style`) === 'dark') {
            this.saveTheme('light');
            this.getTheme();
        } else {
            this.saveTheme('dark');
            this.getTheme();
        }
    }

    saveTheme(theme): void {
        localStorage.setItem('theme', theme);
        //const nowP = location.href;
        //let pathL = nowP.split("/#/");
        //location.replace('/#/emas/home');
        //location.replace(`/#/${pathL[1]}`);
    }

    getTheme(): void {
        let theme = localStorage.getItem('theme');
        if (!theme) {
            theme = 'dark';
        }
        const body = document.getElementsByTagName('body')[0];
        body.setAttribute('data-theme-style', theme);
        this.theme = theme;
    }
}
