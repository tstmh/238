import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {Bully, BullySubjectService} from 'src/app/share/service/bully-subject.service';
import {wsSend} from 'src/app/public/utils/webservices';

@Injectable({
	providedIn: 'root'
})
export class AuditService {
	public data: any;
	constructor(
		private router: Router,
		private bully: BullySubjectService
	) {
	}
	/**
	 * User Role
	 */
	findAllUsers() {
		return wsSend('findAllUsers', {}, 'UserWebService');
	}
	// 根据ID和日期查找
	findByActorAndDate(arg0, arg1, arg2) {
		return wsSend('findByActorAndDate', {'arg0': arg0, 'arg1': arg1, 'arg2': arg2}, 'AuditLogWebService');
	}
	// 根据iD查所有
	findByActor(arg0) {
		// console.log(arg0)
		return wsSend('findByActor', {'arg0': arg0}, 'AuditLogWebService');
	}

}
