import { Component, OnInit, AfterViewInit } from '@angular/core';
import { SystemService } from 'src/app/service/system.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { AuthService } from './auth.service';
import { LoginService } from './login.service';
// import { MessagesService } from 'app/shared/service';
import { Router } from '@angular/router';
import { NzMessageService } from 'ng-zorro-antd';
import { NzModalService } from 'ng-zorro-antd/modal';
// import { sha256 } from 'js-sha256';
import * as moment from 'moment';

@Component({
    selector: 'sj-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, AfterViewInit {
    loginForm: FormGroup;
    clickLoding = false;
    username: '';
    password: '';
    alertMsg = '';
    isVisible = false;
    userInfo;
    passData = '';
    pListData = '';
    loginMsgMap = {
        '68': 'Please change the Password and Re-login the application. ',
        '66': 'Wrong Username/Password! ',
        '64': 'You are already Login, please logout the application and try again! ',
        '19': 'Your account is disabled, please approach the administrator to unlock the account! ',
        '69': 'Your account is locked as you have exceed the maximum retries,please approach the administrator to unlock the account! ',
        '6a': 'Please change the Password and try again! ',
        '6b': 'You have exceeded the maximum limit of Retries! ',
        '76': 'Your password is expired, please approach the administrator to reset the password! '
    };
    constructor(
        public systemService: SystemService,
        private fb: FormBuilder,
        private authService: AuthService,
        private modal: NzModalService,
        // private msg: MessagesService,
        private router: Router,
        private loginService: LoginService,
        private message: NzMessageService
    ) {
        // 语言服务
        this.systemService.langSet();
    }

    ngOnInit() {
        this.loginForm = this.fb.group({
            username: ['', Validators.required],
            password: ['', Validators.required],
            remember: ['']
        });
    }

    ngAfterViewInit(): void {
    }
    // getRoleName(value, url) {
    //     const time = new Date().getTime();
    //     const param2 = {
    //         userName: value.username,
    //         token: sha256(value.username + value.password + time).toUpperCase(),
    //         time
    //     };
    //     this.authService.getRoleName(param2).subscribe(res => {
    //         console.log(res);
    //         if (res.code === '900002') {
    //             this.authService.getRoleNameLogOut(param2).subscribe(data => {
    //                 this.getRoleName(value, url);
    //             });
    //         } else {
    //             this._setStorage('roleName', res.data.userRole.roleName);
    //             this.router.navigate(url);
    //         }
    //     });
    // }
    login() {
        // console.log(this.loginForm)
        // console.log('not here');
        for (const i in this.loginForm.controls) {
            if (this.loginForm.controls.hasOwnProperty(i)) {
                this.loginForm.controls[i].markAsDirty();
                this.loginForm.controls[i].updateValueAndValidity();
            }
        }

        if (this.loginForm.invalid) {
            return;
        }

        this.clickLoding = true;
        const value = this.loginForm.value;
        const param = {
            username: value.username.toLowerCase(),
            password: value.password
        };
        // console.log(param)
        this.username = value.username;
        this.password = value.password;
        this.passWordEncrypt();
        this.loginRequest();
        //this.passWordDecrypt();
    }

    passWordEncrypt() {
        const crypto = require('crypto-js');        
        const key = crypto.enc.Utf8.parse("qP2$bG9;vA0^uW0:");
        const iv = crypto.enc.Utf8.parse("qP2$bG9;vA0^uW0:");
        // console.log('login:', this.username + this.password)
        const str = crypto.enc.Utf8.parse(this.username + this.password);
		//mode: AES encrypt mode, padding: filling method
		const encryptedData = crypto.AES.encrypt(str, key, {
			iv:iv,
			mode: crypto.mode.CBC,
			padding: crypto.pad.Pkcs7
		});
        //return base64 format ciphertext
		this.passData = crypto.enc.Base64.stringify(encryptedData.ciphertext);
        //console.log('passData:',this.passData);

        //masp using crypto function
        // var sha512 = require('js-sha512');
        // var hash = sha512(this.password + '{89}');
        // console.log('Password:', hash);
    }
    pListEncrypt() {
        const crypto = require('crypto-js');
        const tempStr = localStorage.getItem('role_id').substring(0,7)
        const key = crypto.enc.Utf8.parse(tempStr+";vA0^uW0:");
	    const iv = crypto.enc.Utf8.parse(tempStr+";vA0^uW0:");
        const str = crypto.enc.Utf8.parse(this.pListData);
        const encryptedData = crypto.AES.encrypt(str, key, {
			iv:iv,
			mode: crypto.mode.CBC,
			padding: crypto.pad.Pkcs7
		});
        //return base64 format ciphertext
		let pList = crypto.enc.Base64.stringify(encryptedData.ciphertext);
        this._setStorage('p_list', pList);
        //console.log('Encrypt pList:',pList);
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

    loginRequest() {
        this.loginService.logIn(this.username, this.passData, false).subscribe((r) => {
            // console.log(r);
            const data: any = r;
            let temp: any = '';
            if (!data.Body.Fault) {
                this.loginSuccess(data.Body.loginResponse.userDto);
            } else {
                temp = data.Body.Fault;
                // console.log(temp[0].faultcode)
                let msg = this.loginMsgMap[temp.faultcode] || temp.faultstring;
                let msgType = 'error';
                if (temp.faultcode === '68') {
                    msgType = 'warning';
                    this.toPassword();
                }
                if (temp.faultcode === '70') {
                    this.loginService.logIn(this.username, this.passData, true).subscribe((user: any) => {
                        if (!user.Body.Fault) {
                            this.userInfo = user.Body.loginResponse.userDto;
                            const { passwordLastModDate } = this.userInfo;
                            const graceperiodDays = 90 - moment().diff(passwordLastModDate, 'days');
                            this.alertMsg = 'Your password will be expire. ';
                            if (graceperiodDays > 0) {
                                this.alertMsg = 'Your password will expire in ' + graceperiodDays + ' day(s),';
                            }
                            this.alertMsg += 'Do you want to change password now?';
                            this.isVisible = true;
                        }
                    });
                }
                if (temp.faultcode === 'ns0:Server') {
                    msg = this.loginMsgMap['66'];
                }
                if (msgType === 'error') {
                    this.modal.error({
                        nzTitle: 'Warning',
                        nzContent: msg
                      });
                } else {
                    this.message.create(msgType, msg);
                }
            }
        });
    }
    handleCancel() {
        this.loginSuccess(this.userInfo);
        this.isVisible = false;

    }
    handleOk() {
        this.toPassword();
        this.isVisible = false;
    }
    loginSuccess(userDto) {
        const { userId, username, roleId, roleList } = userDto;
        const loginInfo = {
            Authorization: userId,
            name: username,
            roleId,
            roleList
        };
        this.pListData = JSON.stringify(loginInfo.roleList[0].permmissionDtos);
        this._setUserInfo(loginInfo);
        this.router.navigate(['/emas/home']);
    }
    toPassword() {
        this.router.navigate(['/emas/password-management/change-password']);
        this._setStorage('user_name', this.username);
        this._setStorage('faultcode', 68);
        this._setStorage('timeOut', '0');
        this.loginService.updateRunTime();
    }
    logOut() {
        // console.log('here');
        this.username = this.loginForm.value.userId;
        // console.log(this.username);
        this.loginService.logOut(this.username).subscribe((r) => {
            localStorage.removeItem('access_token');
            localStorage.removeItem('role_id');
            localStorage.removeItem('role_list');
            localStorage.removeItem('p_list');
            // console.log(r);
        });
    }

    private _setStorage(key, value): void {
        localStorage.setItem(key, value);
    }

    private _setUserInfo(userInfo: any): void {
        this._setStorage('access_token', userInfo.Authorization);
        this._setStorage('user_name', userInfo.Authorization);
        this._setStorage('role_id', userInfo.roleId);
        this._setStorage('timeOut', '0');
        localStorage.removeItem('faultcode');
        //this.store.dispatch(SetStore({ pListStr: JSON.stringify(userInfo.roleList[0].permmissionDtos) }));
        this.pListEncrypt();
        this.loginService.updateRunTime();
    }


}
