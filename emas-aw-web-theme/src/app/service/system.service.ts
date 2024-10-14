import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import * as moment from 'moment';
import { NzMessageService } from 'ng-zorro-antd';
import {ConstantsService} from '../service/constants.service';
import {LoginService} from '../auth/login.service';
@Injectable({
	providedIn: 'root'
})
export class SystemService {
	time = {
		date: '',
		time: ''
	};
	timeSetTimer: any = null;
	lang = 'en';
	langLabel = 'EN';
	langs = [{ label: '简体中文', value: 'zh' }, { label: 'EN', value: 'en' }, { label: 'Việt nam', value: 'vi' }];
	menuList = [
		{
			label: 'Audit Trail',
			value: 'audit-trail'
		},{
			label: 'Password Management',
			value: 'password-management'
		},{
			label: 'ResetPassword',
			value: 'reset-password'
		},{
			label: 'Config',
			value: 'config'
		},{
			label: 'Equipment Control',
			value: 'equipment-control'
		},{
			label: 'Reports',
			value: 'reports'
		},{
			label: 'Events',
			value: 'events'
		},{
			label: 'User Management',
			value: 'user-management'
		}		
	];
	cancelled = false;

	constructor(
		private translate: TranslateService,
		private constantsService: ConstantsService,
		private loginService: LoginService,
		private message: NzMessageService
	) { }

	updateTime() {
		// 设置moment 语言
		// const lang = this.lang === 'zh' ? this.lang + '-cn' : this.lang;
		// moment.locale(this.lang);
		moment.locale();
		this.time.date = moment().format('dddd D MMMM YYYY');
		this.time.time = moment().format('k:mm:ss');
	}

	pListDecrypt() {
        const crypto = require('crypto-js')
        let tempStr = localStorage.getItem('role_id').substring(0,7)
        const key = crypto.enc.Utf8.parse(tempStr+";vA0^uW0:");
        const iv = crypto.enc.Utf8.parse(tempStr+";vA0^uW0:");
        const pListTemp = localStorage.getItem('p_list');
        const base64 = crypto.enc.Base64.parse(pListTemp);
		const str = crypto.enc.Base64.stringify(base64);
		const decryptedData = crypto.AES.decrypt(str, key, {
			iv:iv,
			mode: crypto.mode.CBC, 
			padding: crypto.pad.Pkcs7
		});
		let pList = crypto.enc.Utf8.stringify(decryptedData).toString();
        //console.log('Decrypt pList:',pList);
        return pList;
    }
	
	checkLogin() {
		const timeOutValue = localStorage.getItem("timeOut");
		localStorage.setItem('IdleTime', this.constantsService.IdleTime.toString());
		const logoutTime = this.constantsService.IdleTime*1000 + Number.parseInt(localStorage.getItem("runTime"));
		const curTime = new Date().getTime();
		this.cancelled = logoutTime ? false : true;
		if(logoutTime < curTime || timeOutValue == "ye4") {
           this.loginService.logOutAuto();
		   location.replace('/#/login');
		};
		const nowCode = localStorage.getItem('faultcode');
		if (nowCode != '68'){
			let pList = [];
			try {
				pList = JSON.parse(this.pListDecrypt());
			} catch (e) {
				this.loginService.logOutAuto();
			}		
			pList = pList ? pList : [];
			//console.log(pList)
			if (pList.length < 1) {
				this.loginService.logOutAuto();
				location.replace('/#/login');
			} else {
				let tempList = [];
				pList.forEach(e =>{
					this.menuList.forEach(ele =>{
						if (e.name == ele.label) {
							tempList.push(ele.value);
						}
					});
				});
				const nowPath = location.href;
				let pathList = nowPath.split("/");
				let tempPath = pathList[5];
				if (tempPath != "home") {
					let isCur = false;
					let isOpe = false;
					tempList.forEach(ele => {
						tempPath = pathList[5];
						if (ele == "password-management") {
							if (localStorage.getItem('role_id') != "ROLE_ADMIN") {
								ele = "password-management/change-password";
								tempPath = tempPath + "/" + pathList[6];
								isOpe = true;
							}
						}
						//console.log('ele:'+ ele + ', tempPath:' + tempPath)
						if (ele == tempPath){
							isCur = true;
						}
					});
					if (!isCur) {
						//this.loginService.logOutAuto();
						if (isOpe && pathList[6] == 'reset-password') {
							location.replace('/#/emas/password-management/change-password');
							this.message.create('error','You do not have permission to access this function, but you can use "Change Password" function to change your password.');
							return;
						}
						location.replace('/#/login');
					}
				}
			}
		}
	}
    timeSet(){
		if(!this.timeSetTimer){
			this.timeSetTimer =true ;
			this.runtime();
		}
	}

	private runtime(){
		if(this.cancelled){
			this.timeSetTimer = false;
			this.cancelled = false;
			return;
		}
		setTimeout(() =>{
			this.updateTime();
			this.checkLogin() 
			this.runtime();
		},1000);
	}

	langSet() {
        // 语言服务
        const lang = localStorage.getItem('itmp_lang');

        // 1.默认采用 '英语'
        this.lang = lang ? lang : 'en';

        // 2. 默认采用 '浏览器设置的第一语言'
        /* if (lang) {
            this.lang = lang;
        } else {
            const browserLang = this.translate.getBrowserLang();
            console.log(browserLang);
            const initLang = browserLang.match(/en|zh|vi/) ? browserLang : 'en';
            this.lang = initLang;
        } */
        this.translate.use(this.lang);
        this.langLabel = this.langs.find(l => l.value === this.lang).label;
    }

}
