import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {Bully, BullySubjectService} from 'src/app/share/service/bully-subject.service';
import {wsSend} from 'src/app/public/utils/webservices';
import { Subject } from 'rxjs';

@Injectable({
	providedIn: 'root'
})
export class UserService {
	// public data: any;
	constructor(
		private router: Router,
		private bully: BullySubjectService
	) {
	}
	$addData: Subject<any> = new Subject();
	$totalPage: Subject<any> = new Subject();
	/**
	 * User Role
	 */
	FindAllRoles() {
		return wsSend('findAllRoles', {}, 'RoleWebService');
	}
	findAllPermissions() {
		return wsSend('findAllPermissions', {}, 'PermissionWebService');
	}
	addPermissionsToRole(data) {
		// console.log(data);
		return wsSend('addPermissionsToRole', data, 'PermissionWebService');
	}
	/**
	 * User
	 */
	findAllUsers() {
		return wsSend('findAllUsers', {}, 'UserWebService');
	}
	/**
	 * addUser
	 */
	addUser(data, arg) {
		return wsSend('addUserForWeb', {'arg0': data, 'arg1': arg}, 'UserWebService');
	}
	updateUser(data, arg) {
		return wsSend('updateUserForWeb', {'arg0': data, 'arg1': arg}, 'UserWebService');
	}
	/**
	 * deleteUser
	 */
	removeUser(data, arg) {
		// console.log(data)
		 return wsSend('removeUserForWeb', {'arg0': data, 'arg1': arg}, 'UserWebService');
	}
	// getSelectList(arr,key,listName) {
	// 	let map = new Map();
	// 	arr.forEach((item)=>{
	// 	  if (!map.has(item[key])&&item[key]){
	// 	  map.set(item[key],item);
	// 	  }
	// 	});
	// 	let uniqueList = [...map.values()];
	// 	console.log(uniqueList);
	// 	uniqueList.forEach((data) => {
	// 		listName.push({label: data[key],value: data[key]});
	// 	  });
	// 	}

}
