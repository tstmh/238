import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Bully, BullySubjectService } from 'src/app/share/service/bully-subject.service';
import { wsSend } from 'src/app/public/utils/webservices';
import { TabRouterService } from '../layouts/tab-router/tab-router.service'
// import { SYSTEM_EVENT, MESSAGE_CHANNEL } from 'app/app.constants';

@Injectable({
	providedIn: 'root'
})
export class LoginService {
	constructor(
		private router: Router,
		private bully: BullySubjectService,
		private tabRouter: TabRouterService,
	) { }

    /**
     * check token
     */
	autoAccessToken() {
		// 还要判断这个token 过期没有
		if (!localStorage.getItem('access_token')) {
			this.logInCheck();
			return false;
		}
		return true;
	}

    /**
     * return access_token
     */
	getAccessToken() {
		this.autoAccessToken();
		return localStorage.getItem('access_token');
	}

    /**
     * login
     */
	logIn(username, password, isForceLogin) {
		// 还有登录时间戳
		return wsSend('login', { 'arg0': username, 'arg1': password, 'arg2': isForceLogin }, 'UserWebService');
		// return wsSend('login', { 'arg0': 'testuser9', 'arg1': 'Ttee7890*', 'arg2': true }, 'UserWebService');
	}
	logInCheck() {
		this.router.navigate(['/login']);
	}
    /**
     * 登出
     * userId
     */
	logOut(username) {
		return wsSend('logout', { 'arg0': username }, 'UserWebService');
	}

	logOutAuto() {
		const username = localStorage.getItem('user_name');
		if (username != null){
			this.logOut(username).subscribe((r) => {
				localStorage.clear();
				localStorage.removeItem('access_token');
				localStorage.removeItem('role_id');
				localStorage.removeItem('role_list');
				localStorage.removeItem('timeOut');
			});
		}		
		this.tabRouter.closeAll();
		this.router.navigate(['/login']);
	}

	updateRunTime() {
		localStorage.setItem('runTime', new Date().getTime() + '');
	}

}
