import { Component, OnInit, Input} from '@angular/core';
import { UserService } from '../../pages/user-management/user-management.service';

@Component({
  selector: 'sj-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {
  pageChange: any;
  @Input() navigationEnd: any;
  totalPage: 1;
  componentName: string;

  constructor(private userService: UserService
  ) {
    userService.$addData.subscribe((res) => {
			if (res) {
				// console.log(res);
				this.pageChange = res;
				// console.log(this.pageChange);
			} else {
				this.pageChange = 1;
        // console.log(this.pageChange);
			}
		});
		userService.$totalPage.subscribe((res) => {
       // console.log(res)
      this.totalPage = 1;

      if (res) {
        this.totalPage = res;
      } else {
        // console.log('2222222222222222')
        this.totalPage = 1;
      }
		});
   }
  ngOnChanges() {
    this.getComponetName();
    this.pageChange = 1;
    this.totalPage = 1;
    // console.log(this.navigationEnd)
    // console.log(localStorage.getItem('page'))
  }
  ngOnInit() {
    this.pageChange = 1;
    this.totalPage = 1;
    // this.getLocalStorage();
   // setInterval(this.getLocalStorage(),1000)
  }
  // getLocalStorage() {
  //   localStorage.setItem('page',1);
  //   this.oldLocal = localStorage.getItem('page');
  //   this.totalPage = localStorage.getItem('totalPage');
  //   setInterval(() => {
  //     this.oldLocal = localStorage.getItem('page');
  //     this.totalPage = localStorage.getItem('totalPage');
  //   }, 100);
  // }

  getComponetName() {
    let getUrlArry, getLastOne;
    let tempArr: any;
    getUrlArry = this.navigationEnd['url'].split('/');
    getLastOne = getUrlArry[getUrlArry.length - 1];
    if (getLastOne.includes('-')) {
      let lastOneArry;
      lastOneArry = getLastOne.split('-');
      if (lastOneArry[0] === 'vms' || lastOneArry[0] === 'fels') {
        lastOneArry[0] = lastOneArry[0].toUpperCase();
        this.componentName = lastOneArry.join(' ');

        // this.componentName = lastOneArry.join(' ').slice(0,-4);
      } else {
        // if(lastOneArry[0] == 'traffic' || lastOneArry[0] == 'technical'|| lastOneArry[0] == 'clustering' || lastOneArry[0] == 'network' || lastOneArry[0] == 'system' || lastOneArry[0] == 'field'){
        //   this.componentName = lastOneArry.join(' ').slice(0, -4);
        // } else {
          this.componentName = lastOneArry.join(' ');
        // }
      }
    } else {
      if (getLastOne === 'lus' || getLastOne === 'pmcs') {
        this.componentName =  getLastOne.toUpperCase();

        // this.componentName =  getLastOne.toUpperCase().slice(0,-4);
      } else {
        this.componentName = getLastOne;
      }
    }
  }
}
