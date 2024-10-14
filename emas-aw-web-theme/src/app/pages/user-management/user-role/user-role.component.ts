import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { UserService } from '../user-management.service';
import { NzMessageService } from 'ng-zorro-antd';
@Component({
  selector: 'sj-emas-user-role',
  templateUrl: './user-role.component.html',
  styleUrls: ['./user-role.component.css']
})
export class UserRoleComponent implements OnInit {
  tempDateA = [];
  displayDataOfRight = [];
  addStatus = true;
  removeStatus = true;
  assignStatus = true;
  cancelStatus = true;
  disabled = false;
  validateForm: FormGroup;
  controlArray: any[] = [];
  isCollapse = true;
  listOfData = [];
  addDataIdx = null;
  removeDataIdx = null;
  addData: string[] = [];
  removeData: string[] = [];
  availableList: string[] = [
    'Equipment Control',
    'Reset Password',
    'Configuration',
    'Audit Trail',
    'User Management',
    'Reports',
    'Events',
    'Password Settings'

    //  'Reports',
    //  'Events',
    //  'User Management',
    //  'Password Settings',
    //  'Audit Trail',
    //  'ResetPassword',
    //  'Configuration',
    //  'Equipment Control'
  ];
  gratedList: string[] = [

  ];

  // 所有角色
  AllRoleList = [];
  // 所有权限
  AvailableList = [];
  tempAvaliableList = [];
  // 筛选后的权限
  EndVailableList = [];
  // 原有权限
  grantedRoleList = [];
  tempListA = [];
  tempListB = [];
  testA: any = [];
  testB: any = [];
  role = '';
  roleSelect = '';
  listRole: Array<{ label: string; value: number }> = [
    { label: 'role1', value: 1 },
    { label: 'role2', value: 2 },
    { label: 'role3', value: 3 },
    { label: 'role4', value: 4 },
  ];
  constructor(
    private fb: FormBuilder,
    private UserService: UserService,
    private message: NzMessageService,
  ) { }

  ngOnInit(): void {
    this.initForm();
    this.getAllRoll();
  }
  submitCancel() {
    //  console.log(this.grantedRoleList)
    const tempArrA = [];
    const tempArrB = [];
    this.UserService.FindAllRoles().subscribe((r) => {
      // console.log(r);
      const tempData: any = r;
      this.testA = tempData.Body.findAllRolesResponse.roleList;
      this.testB = this.testA[0].permmissionDtos;
      this.testA.forEach((item, idx) => {
        if (item.description === this.role) {
          this.tempDateA = item.permmissionDtos;
        }
      });
      this.grantedRoleList = this.tempDateA;
      this.testB.forEach((item, idx) => {
        const data = this.tempDateA.findIndex((value) => value.description === item.description);
        if (data === -1) {
          tempArrA.push(item);
        }
      });
      //  console.log(tempArrA);
      this.EndVailableList = tempArrA;
      this.displayDataOfRight = tempArrA;
    });
  }
  initForm() {
    this.validateForm = this.fb.group({
      role: [{ value: null, disabled: false }],
    });
  }
  submitForm(): void {
    // console.log(this.validateForm);
  }
  // 穿梭框
  addChoice(i) {
    const tempArrA = [];
    this.addDataIdx = i;
    // console.log(i);
    this.addStatus = false;
    this.removeStatus = true;
    this.removeDataIdx = null;
  }
  removeChoice(i) {
    this.removeDataIdx = i;
    // console.log(i);
    this.addDataIdx = null;
    this.addStatus = true;
    this.removeStatus = false;
  }
  add() {
    const tempArrA = [];
    // console.log(this.addDataIdx);
    if (this.addDataIdx != null) {
      // this.addData = this.EndVailableList.splice(this.addDataIdx, 1);
      this.addData = this.displayDataOfRight.splice(this.addDataIdx, 1);

      // this.addData = this.availableList.splice(this.addDataIdx, 1);
    }
    // console.log(this.addData);
    for (const data of this.addData) {
      this.grantedRoleList.push(data);
      // this.gratedList.push(data);
    }
    this.addData = [];
    this.addDataIdx = null;
    // console.log(this.grantedRoleList);
    // console.log(this.EndVailableList);
  }
  remove() {
    const tempArrA = [];
    if (this.removeDataIdx != null) {
      this.removeData = this.grantedRoleList.splice(this.removeDataIdx, 1);
      // console.log(this.removeData);
      // this.removeData = this.gratedList.splice(this.removeDataIdx, 1);
    }
    for (const data of this.removeData) {
      // console.log(data.description)
      // this.EndVailableList.push(data);
      this.displayDataOfRight.push(data);
      // this.availableList.push(data);
    }
    this.removeData = [];
    this.removeDataIdx = null;
    // console.log(this.EndVailableList);
  }
  // 递归
  recursion(a, b) {
    // console.log(a);
    // console.log(b);
    for (let i = 0; i < a.length; i++) {
      if (a[i].description === b[i].description) {
        // console.log(a[i]);
      } else {
        break;
      }
    }

  }
  submitAssign() {
    // console.log(this.grantedRoleList);
    // console.log(data);
    let roleSelect = '';
    this.UserService.FindAllRoles().subscribe((r) => {
      // console.log(r);
      const tempData: any = r;
      this.testA = tempData.Body.findAllRolesResponse.roleList;
      this.testA.forEach((item) => {
        if (item.description === this.role) {
          roleSelect = item.name;
        }
      });
      const addData = { arg0: [], arg1: roleSelect };
      this.grantedRoleList.forEach((item) => {
        addData.arg0.push(
          { description: item.description, name: item.name }
        );
      });
      // console.log(addData)
      this.UserService.addPermissionsToRole(addData).subscribe((r) => {
        // console.log(r)
        this.message.create('success', `Successful addPermissions to Role`);
        this.UserService.FindAllRoles().subscribe((r) => {
          // console.log(r);
          const tempData: any = r;
          this.AllRoleList = tempData.Body.findAllRolesResponse.roleList;
        });
        this.UserService.findAllPermissions().subscribe((r) => {
          // console.log(r)
          const tempData: any = r;
          this.AvailableList = tempData.Body.findAllPermissionsResponse.permissionList;
          // console.log(this.AvailableList);
          this.EndVailableList = this.AvailableList;
          // console.log(this.EndVailableList)
        });
      });
    });
  }
  addAll() {
    for (const data of this.displayDataOfRight) {
      this.grantedRoleList.push(data);
    }
    this.EndVailableList = [];
    this.displayDataOfRight = this.EndVailableList;
    // console.log(this.EndVailableList);
  }
  compareFn = (o1: any, o2: any) => (o1 && o2 ? o1.value === o2.value : o1 === o2);

  log(value: { value: string }): void {
    // console.log(value);
  }
  removeAll() {
    for (const data of this.grantedRoleList) {
      this.displayDataOfRight.push(data);
    }
    this.grantedRoleList = [];
  }

  // 获取所有角色方法
  getAllRoll(): void {
    this.UserService.FindAllRoles().subscribe((r) => {
      // console.log(r);
      const tempData: any = r;
      this.AllRoleList = tempData.Body.findAllRolesResponse.roleList;
      // this.AvailableList = this.AllRoleList[0].permmissionDtos;
      // console.log(this.AvailableList);
      // this.roleSelect = this.AllRoleList[0].description
      this.validateForm.patchValue({
        role: this.AllRoleList[0].description
      });
      // console.log(this.AllRoleList[0].description);
      // console.log(this.roleSelect);
    });
    this.UserService.findAllPermissions().subscribe((r) => {
      // console.log(r)
      const tempData: any = r;
      this.AvailableList = tempData.Body.findAllPermissionsResponse.permissionList;
      // console.log(this.AvailableList);
      this.EndVailableList = this.AvailableList;
      // console.log(this.EndVailableList)
    });
  }

  filter(arrayA, arrayB): void {
    const tempArrA = arrayA;
    const tempArrB = arrayB;
    arrayA.forEach((item, idx) => {
      arrayB.forEach((data, index) => {
        if (item.description === data.description) {
          tempArrA.splice(idx, 1);
        }
      });
    });
    // console.log(tempArrA);
    return tempArrA;
  }
  // 获取点击的角色
  changeRole(event) {
    // console.log(event);
    this.role = event;
    // console.log(this.AvailableList);
    const tempArrA = [];
    const tempArrB = [];
    this.UserService.FindAllRoles().subscribe((r) => {
      // console.log(r);
      const tempData: any = r;
      this.testA = tempData.Body.findAllRolesResponse.roleList;
      this.testB = this.EndVailableList;
      this.testA.forEach((item, idx) => {
        if (item.description === this.role) {
          this.tempDateA = item.permmissionDtos;
        }
      });
      //  console.log(this.tempDateA)
      this.grantedRoleList = this.tempDateA;
      this.testB.forEach((item, idx) => {
        const data = this.tempDateA.findIndex((value) => value.description === item.description);
        // console.log(data);
        if (data === -1) {
          tempArrA.push(item);
        }
      });
      this.displayDataOfRight = tempArrA;
    });
  }
}