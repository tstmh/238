import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {Bully, BullySubjectService} from 'src/app/share/service/bully-subject.service';
import {wsSend} from 'src/app/public/utils/webservices';

@Injectable({
	providedIn: 'root'
})
export class passwordService {
	// public data: any;

	constructor(
		private router: Router,
		private bully: BullySubjectService
	) {
	}
	/**
	 * change-pasword
	 */
	// 修改密码
	changePassword(arg0, arg1, arg2) {
		// console.log(arg0)
		// console.log(arg1)
		// console.log(arg2)
		arg1 = this.passWordEncrypt(arg0, arg1);
		arg2 = this.passWordEncrypt(arg0, arg2);
		return wsSend('changePassword', {'arg0': arg0, 'arg1': arg1, 'arg2': arg2}, 'UserWebService');
	}
	// 重置密码
	resetPassword(arg0, arg1, arg2) {
		// console.log(arg0)
		// console.log(arg1)
		arg1 = this.passWordEncrypt(arg0, arg1);
		return wsSend('resetPasswordForWeb', {'arg0': arg0, 'arg1': arg1, 'arg2': arg2}, 'UserWebService');
	}
	/**
	 * User Role
	 */
	FindAllRoles() {
		return wsSend('findAllRoles', {}, 'RoleWebService');
	}
	/**
	 * User
	 */
	findAllUsers() {
		return wsSend('findAllUsers', {}, 'UserWebService');
	}

	passWordEncrypt(uID, ele) {
        const crypto = require('crypto-js')
        const key = crypto.enc.Utf8.parse("qP2$bG9;vA0^uW0:");
	    const iv = crypto.enc.Utf8.parse("qP2$bG9;vA0^uW0:");
		// console.log('update:', uID + ele)
        const str = crypto.enc.Utf8.parse(uID + ele);
		//mode: AES encrypt mode, padding: filling method
		const encryptedData = crypto.AES.encrypt(str, key, {
			iv:iv,
			mode: crypto.mode.CBC,
			padding: crypto.pad.Pkcs7
		});
        //return base64 format ciphertext
		const passData = crypto.enc.Base64.stringify(encryptedData.ciphertext);
        return passData;
    }

}
