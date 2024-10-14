import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import {AuditService} from '../audit-trail.service';
import {CommonService} from '../../../service/common.service';
import { UserService } from '../../user-management/user-management.service';
@Component({
  selector: 'sj-emas-user-activity',
  templateUrl: './user-activity.component.html',
  styleUrls: ['./user-activity.component.css']
})
export class UserActivityComponent implements OnInit {
  sortName: string | null = null;
  sortValue: string | null = null;
  allList = [];
  renderHeader = [
		{
			name: 'User ID',
			key: null,
			value: 'userId',
			isChecked: true
		},
		{
			name: 'Action',
			key: null,
			value: 'action',
			isChecked: true
		},
		{
			name: 'Action Details',
			key: null,
			value: 'actionDetails',
			isChecked: true
		},
		{
			name: 'Date Time',
			key: null,
			value: 'dateTime',
			isChecked: true
		}
  ];

  // 用户列表
  UserActivityList = [];

  validateForm: FormGroup;
	controlArray: any[] = [];
	isCollapse = true;
  // nz-date-picker
  startValue: Date | null = null;
  endValue: Date | null = null;
  endOpen = false;
  // nz-select
  listOfData = [
    {
      id: 0,
      userId: '123'
    },
    {
      id: 1,
      userId: '234'
    },
    {
      id: 0,
      userId: '456'
    }
  ];
  // 所有用户id
  AllUserId = [];
  listOfDataA = [];
  listOfDataB = [];
	expwayCodeList = [];
  expwayDirection = [];
  listUserId: Array<{ label: string; value: number }> = [
    { label: 'user1', value: 1},
    { label: 'user2', value: 2},
    { label: 'user3', value: 3}
  ];
  constructor(private fb: FormBuilder,
    private AuditService: AuditService,
    private commonService: CommonService,
    private userService: UserService) { }

	ngOnInit() {
      this.initForm();
      // console.log(this.validateForm);
      this.GetAllUsers();
      // console.log(this.AllUserId[0].userId)
      this.AuditService.findAllUsers().subscribe((r) => {
      const tempData: any = r;
      this.AllUserId = tempData.Body.findAllUsersResponse.userList;
      let arg1=this.validateForm.value.arg1.getFullYear()+'-'+ this.checkTime(this.validateForm.value.arg1.getMonth()+1)+'-'+this.checkTime(this.validateForm.value.arg1.getDate())+'T'+this.checkTime(this.validateForm.value.arg1.getHours())+':'+this.checkTime(this.validateForm.value.arg1.getMinutes()) + ':' + this.checkTime(this.validateForm.value.arg1.getSeconds()) + '+08:00'
      let arg2=this.validateForm.value.arg2.getFullYear()+'-'+ this.checkTime(this.validateForm.value.arg2.getMonth()+1)+'-'+this.checkTime(this.validateForm.value.arg2.getDate())+'T'+this.checkTime(this.validateForm.value.arg2.getHours())+':'+this.checkTime(this.validateForm.value.arg2.getMinutes()) + ':' + this.checkTime(this.validateForm.value.arg2.getSeconds()) + '+08:00'
      this.AuditService.findByActorAndDate(this.AllUserId[0].userId, arg1, arg2).subscribe((r)=>{
      const tempDataA: any = r;
      this.UserActivityList = tempDataA.Body.findByActorAndDateResponse.return ? tempDataA.Body.findByActorAndDateResponse.return : [];
      this.allList = tempDataA.Body.findByActorAndDateResponse.return ? tempDataA.Body.findByActorAndDateResponse.return : [];
      const ttt: number = this.UserActivityList.length / 10;
			const yu = this.UserActivityList.length % 10;
			 if (yu < 5) {
				// localStorage.setItem('totalPage', (ttt + 0.5).toFixed(0));
				this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
			 } else {
				 // localStorage.setItem('totalPage', ttt.toFixed(0));
				 this.userService.$totalPage.next(ttt.toFixed(0));
			 }
    });
    });
    // this.mergeData(this.UserActivityList, this.listOfDataA);
	}
  pageChange(e) {
    this.userService.$addData.next(e);
  }
	initForm() {
    const now: any = new Date();
		this.validateForm = this.fb.group({
      arg0: [{ value: null, disabled: false }],
      arg1: [{ value: new Date((now / 1000 - 86400 * 30) * 1000), disabled: false }],
      arg2: [{ value: new Date(), disabled: false }]
			// userId: [{ value: null, disabled: false }],
      // fromDate: [{ value: new Date(), disabled: false }],
      // toDate: [{ value: new Date(), disabled: false }]
		});
  }
  //   获取所有用户
  GetAllUsers(): void {
    // console.log('22222222');
    this.AuditService.findAllUsers().subscribe((r) => {
      // console.log(r);
      const tempData: any = r;
      this.AllUserId = tempData.Body.findAllUsersResponse.userList;
      this.formPatchValue();
    });
    // console.log(this.AllUserId);
  }
  formPatchValue(): void {
   //  console.log(this.AllUserId);
		 this.validateForm.patchValue({
        arg0: this.AllUserId[0] ? this.AllUserId[0].userId : '',
		 });
    }
    sort(sort: { key: string; value: string }): void {
      // console.log(sort.key)
      if(sort.key !== 'userId'){
        // console.log(sort.value)
        this.sortName = sort.key;
        this.sortValue = sort.value;
        this.search();
      }
    }
    search(): void {
      const data = this.allList;
      // console.log(data)
      this.UserActivityList = [];
      if (this.sortName && this.sortValue) {
        const arr = data.sort((a, b) =>
				this.sortValue === 'ascend'
        ? a[this.sortName!] > b[this.sortName!]
          ? 1
          : -1
        : b[this.sortName!] > a[this.sortName!]
          ? 1
          : -1
    );
    this.UserActivityList = [...arr];
    // console.log(this.UserActivityList)
  } else {
    this.UserActivityList = [...data];
  }}
	submitForm(): void {
    // console.log(this.validateForm)
    // console.log(this.validateForm);
    // console.log(this.validateForm.value.arg0)
    let arg1=this.validateForm.value.arg1.getFullYear()+'-'+ this.checkTime(this.validateForm.value.arg1.getMonth()+1)+'-'+this.checkTime(this.validateForm.value.arg1.getDate())+'T'+this.checkTime(this.validateForm.value.arg1.getHours())+':'+this.checkTime(this.validateForm.value.arg1.getMinutes()) + ':' + this.checkTime(this.validateForm.value.arg1.getSeconds()) + '+08:00'
    // console.log(arg1);
    let arg2=this.validateForm.value.arg2.getFullYear()+'-'+ this.checkTime(this.validateForm.value.arg2.getMonth()+1)+'-'+this.checkTime(this.validateForm.value.arg2.getDate())+'T'+this.checkTime(this.validateForm.value.arg2.getHours())+':'+this.checkTime(this.validateForm.value.arg2.getMinutes()) + ':' + this.checkTime(this.validateForm.value.arg2.getSeconds()) + '+08:00'
    // console.log(arg1)
    // console.log(arg2)
    this.AuditService.findByActorAndDate(this.validateForm.value.arg0, arg1, arg2).subscribe((r)=>{
      const tempDataA: any = r;
      this.UserActivityList = tempDataA.Body.findByActorAndDateResponse.return ? tempDataA.Body.findByActorAndDateResponse.return : [];
      // console.log(this.UserActivityList);
      this.allList = tempDataA.Body.findByActorAndDateResponse.return ? tempDataA.Body.findByActorAndDateResponse.return : [];
      // console.log(this.allList)
      const ttt: number = this.UserActivityList.length / 10;
			const yu = this.UserActivityList.length % 10;
			 if (yu < 5) {
				// localStorage.setItem('totalPage', (ttt + 0.5).toFixed(0));
				this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
			 } else {
				 // localStorage.setItem('totalPage', ttt.toFixed(0));
				 this.userService.$totalPage.next(ttt.toFixed(0));
			 }
    });
  }
  // nz-date-picker
  disabledStartDate = (startValue: Date): boolean => {
    if (!startValue || !this.endValue) {
      return false;
    }
    return startValue.getTime() > this.endValue.getTime();
  }
  disabledEndDate = (endValue: Date): boolean => {
    if (!endValue || !this.startValue) {
      return false;
    }
    return endValue.getTime() <= this.startValue.getTime();
  }
  checkTime(i: any): void {
      if (i < 10) {
        i = '0' + i;
      }
      return i;
    }
  onStartChange(date: any): void {
    // let h = parseInt(hour/1000/60/60%24);
    this.startValue = date;
    // console.log(date);
    }
  onEndChange(date: Date): void {
    this.endValue = date;
    // console.log(date);
  }

  handleStartOpenChange(open: boolean): void {
    if (!open) {
      this.endOpen = true;
    }
    // console.log('handleStartOpenChange', open, this.endOpen);
  }

  handleEndOpenChange(open: boolean): void {
    // console.log(open);
    this.endOpen = open;
  }

  // 改变的actorid
  changeActorID(event: any) {
    // console.log(event);
    this.AuditService.findByActor(this.validateForm.value.arg0).subscribe((r) => {
      // console.log(r);
      const tempDataB: any = r;
      this.listOfDataA = tempDataB.Body.findByActorResponse.auditLogList;
      // console.log(this.listOfDataA);
    });
  }
}
