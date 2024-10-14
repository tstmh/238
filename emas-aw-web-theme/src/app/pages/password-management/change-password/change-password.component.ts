import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { passwordService } from '../password.service';
import { NzMessageService } from 'ng-zorro-antd';

@Component({
	selector: 'emas-change-password',
	templateUrl: './change-password.component.html',
	styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit {
	validateForm: FormGroup;
	controlArray: any[] = [];
	isCollapse = true;
	listOfData = [];
	updatePassword = true;
	oldPassword = false;
	newPassword = false;
	confirmPassword = false;
	Password = '';
	userID = localStorage.getItem('user_name');
	constructor(
		private fb: FormBuilder,
		private passwordService: passwordService,
		private message: NzMessageService) { }

	ngOnInit() {
		this.initForm();
		// console.log(this.validateForm);
		this.validateForm.get('oldPassword').valueChanges.subscribe(value => {
			if (value !== '') {
				// console.log('oldPassword')
				this.oldPassword = true;
			} else {
				// console.log('oldPassword no')
				// this.adduser = true
				this.oldPassword = false;
			}
		});
		this.validateForm.get('newPassword').valueChanges.subscribe(value => {
			if (value !== '') {
				// console.log('newPassword');
				this.newPassword = true;
				this.Password = value;
			} else {
				// console.log('newPassword no')
				this.newPassword = false;
			}
		});
		this.validateForm.get('confirmPassword').valueChanges.subscribe(value => {
			// console.log(value)
			// console.log(this.Password)
			// console.log(this.oldPassword)
			if (value === this.Password && this.oldPassword) {
				// console.log('confirmPassword')
				this.updatePassword = false;
			} else {
				// console.log('confirmPassword no')
				this.updatePassword = true;
			}
		});
	}
	changePassword(): void {
		if (this.validateForm.value.oldPassword === null || this.validateForm.value.newPassword == null || this.validateForm.value.confirmPassword == null) {
			// console.log('有数据为空')
		} else {
			if (this.validateForm.value.newPassword === this.validateForm.value.confirmPassword) {
				// console.log('新旧密码相等');
				// console.log('userid' + this.userID)
				// console.log('老密码' + this.validateForm.value.oldPassword)
				// console.log('新密码' + this.validateForm.value.newPassword)
				// console.log('确认密码' + this.validateForm.value.confirmPassword)
				this.passwordService.changePassword(this.userID, this.validateForm.value.oldPassword, this.validateForm.value.newPassword).subscribe((r) => {
					const temp: any = r;
					let tempA: any = '';
					// console.log(r);
					tempA = temp.Body.Fault;
					// console.log(temp.Body.Fault);
					if (tempA) {
						let err = tempA['faultstring'];
						switch (tempA['faultcode']) {
							case '72':
								err = `Password must be minimum 12 characters and all of the following categories :
								 Uppercase English Letters(A to Z), Lowercase English Letters(a to z),
								Numbers 0 to 9 Non-Alphanumeric characters(!,@,#,$,%,&,*)!`;
								break;
							case '73':
								err = 'Old password should be different from new password!';
								break;

							case '74':
								err = 'Passwords should not be same as previous 5 passwords';
								break;
						}
						this.message.create('error', err);
					} else {
						this.message.create('success', `Password is Changed Successfully,Please Re-Login the application with new password!`);
					}
				});
				// alert('UpdatePassword Success!');
			} else {
				// console.log('两次密码不一致')
			}
		}
	}
	initForm() {
		this.validateForm = this.fb.group({
			oldPassword: [{ value: null, disabled: false }, [Validators.required]],
			newPassword: [{ value: null, disabled: false }, [Validators.required]],
			confirmPassword: [{ value: null, disabled: false }, [Validators.required]],
		});
	}
	submitForm(): void {
		// console.log(this.validateForm);
		// console.log(this.validateForm.value)
		// console.log(this.userID)
		this.changePassword();
		// alert('UpdatePassword Success!');
	}
	resetForm(e: MouseEvent): void {
		e.preventDefault();
		this.validateForm.reset();
	}
}
