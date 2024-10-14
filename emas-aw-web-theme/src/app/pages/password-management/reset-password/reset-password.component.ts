import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import {passwordService} from '../password.service';
import { NzMessageService } from 'ng-zorro-antd';
import { UserService } from '../../user-management/user-management.service';

@Component({
  selector: 'sj-emas-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {
	sortName: string | null = null;
	sortValue: string | null = null;
	displayListOfData = [];
	allChecked = false;
	indeterminate = true;
  renderHeader = [
		{
			name: 'User ID',
			key: null,
			value: 'userId',
			isChecked: true
		},
		{
			name: 'User Name',
			key: null,
			value: 'username',
			isChecked: true
		},
		{
			name: 'Division',
			key: null,
			value: 'companyName',
			isChecked: true
		},
		{
			name: 'Role',
			key: null,
			value: 'roleId',
			isChecked: true
		},
		{
			name: 'Status',
			key: null,
			value: 'enabled',
			isChecked: true
    }
	];
	validateForm: FormGroup;
	controlArray: any[] = [];
	isCollapse = true;
	allUserList = [];
	isHidden = true;
	resetPassword = true;
	// 双击后得到的数据
	resetUserId = '';
	resetUserName = '';
	validateResetForm: FormGroup;
	listOfData = [
    {
      id: 0,
			userId: '1231231231'
		},
		{
      id: 1,
			userId: '1231231231'
		},
		{
      id: 2,
			userId: '1231231231'
		},
		{
      id: 3,
			userId: '1231231231'
		}
  ];
  listOfData2 = [];
	listRole: Array<{ label: string; value: number }> = [
		{ label: 'All', value: 1},
		{ label: 'Role', value: 2},
		{ label: 'Role', value: 3},
		{ label: 'Role', value: 4},
	  ];
	listDvision: Array<{ label: string; value: number }> = [
		{ label: 'All', value: 1},
		{ label: 'Dvision', value: 2},
		{ label: 'Dvision', value: 3}
  ];
  // 选择器
  listOfSelection = [
    {
      text: 'Select All Row',
      onSelect: () => {
        this.checkAll(true);
      }
    }
  ];
  isAllDisplayDataChecked = false;
  isIndeterminate = false;
  displayOfDateList = [];
  AllRoleList = [{
	description: 'All',
	value: 'All'
}];
DivisionList = [{
	companyName: 'All',
	value: 'All'
}];
  mapOfCheckedId: { [key: string]: boolean } = {};
	constructor(
		private fb: FormBuilder,
		private passwordService: passwordService,
		private message: NzMessageService,
		private userService: UserService) { }
		searchFrontEnd(item): void {
			// console.log(item)
		   const filterFieldArr = Object.keys(item);
		   this.displayOfDateList = this.allUserList;
		   const newArr = filterFieldArr.reduce((pre, cur, index) => {
			   // console.log(pre)
			   // console.log(item[cur])
			   if (item[cur] === 'All') {
				   return pre;
			   } else {
				   return pre.filter(data =>
					   item[cur] ? item[cur] === data[cur] : true
				   );
			   }
		   }, this.displayOfDateList);
		   // console.log(newArr);
		   // console.log(filterFieldArr)
		   this.displayOfDateList = newArr;
		   const ttt: number = this.displayOfDateList.length / 10;
		   const yu = this.displayOfDateList.length % 10;
			if (yu < 5) {
			   // localStorage.setItem('totalPage', (ttt + 0.5).toFixed(0));
			   this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
			} else {
				// localStorage.setItem('totalPage', ttt.toFixed(0));
				this.userService.$totalPage.next(ttt.toFixed(0));
			}
	   }
		sort(sort: { key: string; value: string }): void {
			// console.log(sort);
			this.sortName = sort.key;
			this.sortValue = sort.value;
			this.search();
		}
		updateAllChecked(): void {
			this.indeterminate = false;
			if (this.allChecked) {
				this.allUserList.map(item => { item.checked = true; });
			} else {
				this.allUserList.map(item => { item.checked = false; });
			}
		}
		updateSingleChecked(): void {
			if (this.allUserList.every(item => item.checked === false)) {
				this.allChecked = false;
				this.indeterminate = false;
			} else if (this.allUserList.every(item => item.checked === true)) {
				this.allChecked = true;
				this.indeterminate = false;
			} else {
				this.indeterminate = true;
			}
		}
		search(): void {
			const data = this.allUserList;
			// console.log(this.displayListOfData)
			this.displayListOfData = [];
			/** sort data **/
			// console.log(this.sortValue)
			if (this.sortName && this.sortValue) {
				const arr  = data.sort((a, b) =>
				  this.sortValue === 'ascend'
					? a[this.sortName!] > b[this.sortName!]
					  ? 1
					  : -1
					: b[this.sortName!] > a[this.sortName!]
					? 1
					: -1
				);
				this.displayOfDateList = [...arr];

				// this.displayListOfData = [...arr];
				// console.log(this.displayListOfData)
			} else {
				// this.displayListOfData = [ ...data];
				this.displayOfDateList = [ ...data];
				// console.log(this.displayListOfData)
			}
		}
	ngOnInit() {
		this.initForm();
		// console.log(this.validateForm);
		this.getAllUser();
		this.getAllRoll();
		this.onFormChanges();
	}
	onFormChanges(): void {
		// watch the specpasswordific form control
		this.validateResetForm.get('password').valueChanges.subscribe(val => {
		//   console.log('onFormChanges', val);
		//   console.log(val);
		  if (val) {
			  this.resetPassword = false;
			//   console.log('12')
		  } else {
			this.resetPassword = true;
		  }
		});

	  }
	// 获取所有用户
	getAllUser() {
		this.passwordService.findAllUsers().subscribe((r) => {
			// console.log(r)
			const tempData: any = r;
			this.allUserList = tempData.Body.findAllUsersResponse.userList;
			this.displayOfDateList = this.allUserList;
			const ttt: number = this.displayOfDateList.length / 10;
			const yu = this.displayOfDateList.length % 10;
				if (yu < 5) {
				// localStorage.setItem('totalPage', (ttt + 0.5).toFixed(0));
				this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
				} else {
					// localStorage.setItem('totalPage', ttt.toFixed(0));
					this.userService.$totalPage.next(ttt.toFixed(0));
				}
			this.allUserList.forEach((item) => {
				// item.description = item.roleList.description;
				// 去重
				let unique = true;
				this.DivisionList.forEach((j) => {
					if (item.companyName === j.companyName) {
						unique = false;
					}
				});
				if ( unique) this.DivisionList.push({
					companyName:item.companyName,
					value:item.companyName})
					// console.log(this.DivisionList)
				});
		});
	}
	getAllRoll(): void {
		this.passwordService.FindAllRoles().subscribe((r) => {
			// console.log(r);
			const tempData: any = r;
			tempData.Body.findAllRolesResponse.roleList.forEach((item) => {
				// console.log(item)
				// console.log(item.description)
				this.AllRoleList.push({
					description: item.description,
					value: item.name
				});
			});
			this.formPatchValue();
			});
		}
	showModal(data) {
		// console.log(data)
		// console.log('双击')
		this.isHidden = false;
		this.resetUserId = data.userId;
		this.resetUserName = data.username;
		this.validateResetForm.value.password = ' ';
		const ttt = 1;
		this.userService.$totalPage.next(ttt.toFixed(0));

		// console.log(this.validateResetForm.value.password)
	}
	cancel(){
		this.isHidden=true;
		const ttt: number = this.displayOfDateList.length / 10;
			const yu = this.displayOfDateList.length % 10;
				if (yu < 5) {
				// localStorage.setItem('totalPage', (ttt + 0.5).toFixed(0));
				this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
				} else {
					// localStorage.setItem('totalPage', ttt.toFixed(0));
					this.userService.$totalPage.next(ttt.toFixed(0));
				}
	}
	initForm() {
		this.validateForm = this.fb.group({
			userId: [{ value: null, disabled: false }],
			username: [{ value: null, disabled: false }],
			roleId: [{ value: null, disabled: false }],
			companyName: [{ value: null, disabled: false }],
		});
		this.validateResetForm = this.fb.group({
			userID: [{ value: null, disabled: false }],
			userName: [{ value: null, disabled: false }],
			password: [{ value: null, disabled: false }],
		});
	}
	formPatchValue(): void {
		// console.log(this.flesSelect.value);
		// console.log(this.tempA);
		// console.log(this.listOpeStatus[0].value);
		// console.log(this.listOpeStatus[0].value === this.flesSelect.value);
		 this.validateForm.patchValue({
			roleId: this.AllRoleList[0].value,
			companyName: this.DivisionList[0].value
		 });
		}
	submitResetForm(): void {
		// console.log('重置密码成功')
		const nowUserId = localStorage.getItem('user_name');
		this.isHidden = true;
		// console.log(this.validateResetForm)
		this.passwordService.resetPassword(this.resetUserId, this.validateResetForm.value.password, nowUserId).subscribe((r) => {
			 // console.log(r)
			 // alert('success');
			 this.message.create('success', `Success!`);
		});
	}
	pageChange(e) {
		this.userService.$addData.next(e);
	}
	submitForm(): void {
		 // console.log(this.validateForm.value);
		 if (this.validateForm.value.userId === null
			&& this.validateForm.value.username === null
			&& this.validateForm.value.companyName === this.DivisionList[0].value
			&& this.validateForm.value.roleId === this.AllRoleList[0].description) {
			// console.log(this.displayListOfData)
			this.displayOfDateList = this.allUserList;
			const ttt: number = this.displayOfDateList.length / 10;
			const yu = this.displayOfDateList.length % 10;
				if (yu < 5) {
				// localStorage.setItem('totalPage', (ttt + 0.5).toFixed(0));
				this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
				} else {
					// localStorage.setItem('totalPage', ttt.toFixed(0));
					this.userService.$totalPage.next(ttt.toFixed(0));
				}
		} else {
			this.searchFrontEnd(this.validateForm.value);
			// console.log(this.validateForm.value);
		}
  }
  refreshStatus(): void {
    this.isAllDisplayDataChecked = this.listOfData.every(item => this.mapOfCheckedId[item.id]);
    this.isIndeterminate =
      this.listOfData.some(item => this.mapOfCheckedId[item.id]) && !this.isAllDisplayDataChecked;
  }
  checkAll(value: boolean): void {
    this.listOfData.forEach(item => (this.mapOfCheckedId[item.id] = value));
    this.refreshStatus();
  }
}
