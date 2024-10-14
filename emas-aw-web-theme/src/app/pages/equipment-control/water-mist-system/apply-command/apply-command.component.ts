import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { DialogService } from 'src/app/share/dialog';
import { NzMessageService } from 'ng-zorro-antd';
import { SendApplyComponent } from '../send-apply/send-apply.component';
import { EquipmentService } from '../../equipment-control.service';
import { WebSocketService } from 'src/app/service/websocket.service';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import * as moment from 'moment';

@Component({
  selector: 'emas-apply-command',
  templateUrl: './apply-command.component.html',
  styleUrls: ['./apply-command.component.css']
})
export class ApplyCommandComponent implements OnInit {
  @ViewChild(SendApplyComponent) private childcomponent: SendApplyComponent;
  abortWebsocket$ = new Subject<void>();
  isVisible = false;
  isConfirmLoading = false;
  @Input() selectItem: any;
  @Input() listOfData: any;
  @Input() radioValue: any;
  availableList = [];
  selectedList = [];
  chooseLi = [];
  addData = [];
  clickIndex = null;
  addDisabled = true;
  removeDisabled = true;
  addAllDisabled = false;
  removeAllDisabled = true;
  isSpinning = false;
  b = [];
  timerArr = [];
  equipType = null;
  count = 0;
  commandList = [
    {key: 'cdr', value: 'Run/Reset'},
    {key: 'cdo', value: 'Open'},
    {key: 'cdc', value: 'Close'},
    {key: 'cds', value: 'Stop'}];
  commandStr = '';

  constructor(
    private dialog: DialogService,
    private message: NzMessageService,
    private equipmentService: EquipmentService,
    private webSocketService: WebSocketService,
  ) { }

  ngOnInit() {
    this.firstLoad();
    this.equipType = this.selectItem.equipType;
    // const queryData = {
    //   equipId: this.selectItem.equipId
    // };
    // this.equipmentService.getWMSEquipStatusByEquipId(queryData).subscribe(item => {
    //   console.log('this ID Data:',item)
    //   if(item.code ==0 ){
    //     if (item.data.equipStatusList) {
    //       for (const data of item.data.equipStatusList) {
    //         if (data.equipmentId === this.selectItem.equipmentId) {
    //           this.selectItem.radioValue = data.attrValue;
    //           break;
    //         }
    //       }
    //     }
    //   }      
    // });
  }

  // 模态框出现时的一些初始赋值等操作
  firstLoad() {
    // console.log('selected:',this.selectItem);
    this.availableList = Array.from(this.listOfData);
    this.availableList.forEach((item, index) => {
      if (item.equipId === this.selectItem.equipId) {
        this.availableList.splice(index, 1);
        this.selectedList.push(item);
        this.removeDisabled = false;
      }
      item.active = false;
    });
    this.addDisabled = true;
    this.addAllDisabled = false;
    this.removeDisabled = true;
    this.removeAllDisabled = false;
  }
  
  // 左边穿梭框选中的值
  addLi(i, e) {
    if (this.availableList.length !== 0) {
      if (e.ctrlKey) {
        this.addDisabled = false;
        this.clickIndex = i;
        this.availableList[i].active = this.availableList[i].active ? false : true;
      } else {
        this.availableList.forEach((item, index) => {
					item.active = false;
					if (i === index) {
            this.addDisabled = false;
						item.active = true;
					}
				});
      }
    }
  }
  // 右边穿梭框选中的值
  removeLi(i, e) {
    if (this.selectedList.length !== 0) {
      if (e.ctrlKey) {
        this.clickIndex = i;
        this.removeDisabled = false;
        this.selectedList[i].active = this.selectedList[i].active ? false : true;
      } else {
        this.selectedList.forEach((item, index) => {
					item.active = false;
					if (i === index) {
            this.removeDisabled = false;
						item.active = true;
					}
				});
      }
    }
  }
  add() {
    this.availableList.forEach(item => { // 获取选中的项
      if (item.active) {
        this.chooseLi.push({...item});
      }
    });
    this.availableList = this.availableList.filter(item => !item.active);
    this.chooseLi.forEach(item => { // 将选中的项添加到右边
      this.selectedList.push(item);
      this.removeDisabled = false;
    });
    this.selectedList.forEach(item => {
      item.active = false;
    });
    this.chooseLi = [];
    this.addDisabled = true;
    this.addAllDisabled = false;
    this.removeDisabled = true;
    this.removeAllDisabled = false;
  }
  addAll() {
    this.availableList.forEach(item => {
      this.selectedList.push(item);
      item.active = false;
    });
    this.availableList = [];
    this.addAllDisabled = true;
    this.removeAllDisabled = false;
  }
  remove() {
    this.selectedList.forEach(item => { // 获取选中的项
      if (item.active) {
        this.chooseLi.push(item);
        item.statusId = true;
      }
    });
    this.selectedList = this.selectedList.filter(item => !item.statusId);
    this.chooseLi.forEach(item => {
      item.active = false;
      this.availableList.push(item);
    });
    this.availableList.forEach(item => {
      item.active = false;
    });
    this.chooseLi = [];
    this.removeDisabled = true;
    this.addAllDisabled = false;
  }
  removeAll() {
    this.selectedList.forEach(item => {
      this.availableList.push(item);
      item.active = false;
    });
    this.selectedList = [];
    this.removeAllDisabled = true;
    this.addAllDisabled = false;
    this.removeDisabled = true;
  }
  Send() {
    const queryEquipIdList = [];
    if (this.selectedList.length !== 0) {
      this.selectedList.forEach((item) => {
				queryEquipIdList.push(item.equipId);
			});
      this.dialog
        .confirm({
          title: 'Confirmation Window',
          content: `Apply to ${this.selectedList.length} equipments. Seek SOE approval before launching? PLEASE ENSURE THE COMMAND BEFORE SENDING OUT.`,
          buttonOkTxt: 'Yes(Y)',
          buttonCancelTxt: 'No(N)'
        })
        .subscribe(res => {
          if (res) {
            this.isSpinning = true;
						queryEquipIdList.forEach((item, idx) => {
              this.confirmSend(item, idx);
            });
            this.childcomponent.showModal();
						this.getSocket(this.selectedList);
          } else {
            this.cancelSend();
          }
        });
    } else {
      this.message.create('warning', `Please select an item to send`);
    }
  }
  getSocket(arr): void {
		//console.log(arr);
		this.webSocketService.getMessage().pipe(
			takeUntil(this.abortWebsocket$.asObservable())
		).subscribe((msg: any) => {
			//let count = 0;
			// 心跳通信 type === 'HeartBeat'
			try {
				const response = JSON.parse(msg.data);
        // console.log('response:',response)
				if (response.type !== 'HeartBeat') {
					arr.forEach((item, idx) => {
						if (item.equipId === response.EquipId) {
							this.count++;
              if (response.Status == 0) {
                item.status = 'Command Failed';
              } else if (response.Status == 1) {
                item.status = 'Success';
              } else if (response.Status == 2) {
                item.status = 'PLC Error';
              } else {
                item.status = 'Time Out';
              }
							clearTimeout(this.timerArr[idx]);
						}
					});
          //console.log('count:',this.count,', selectedList:',this.selectedList)
					if (this.count === this.selectedList.length) {
						//this.selectedList = [];
						this.timerArr = [];
						this.abortWebsocket$.next();
						this.childcomponent.closeLoading();
            this.count = 0;
						this.isSpinning = false;
					}
				}
			} catch (e) {
				console.warn('websocket process warn:', e);
			}
		});
	}
  confirmSend(item, idx) {
    const queryData = {
      wmcsMsg:{
        equipId: item,
        equipType: this.equipType,
        attributeName: this.radioValue,
        cmdValue: '1',
        sender: 'AW_user',
        exeId: `AW_${localStorage.getItem('user_name')}_${moment().format('HHmmssSSS')}`,
        cmdId: '0'
      }
    };
    //console.log(queryData)
    this.equipmentService.AW_CFELS_WMS(queryData);//.subscribe(item => {
      //console.log(item)
      // const tempData: any = item;
      // console.log("Response:",tempData)
      // if (tempData.Body.Fault){
      //   setTimeout(() => {
      //     this.isSpinning = false;
      //     this.childcomponent.showModal();
      //   }, 2000);
      //   this.selectedList.forEach(ele => {
      //     ele['command'] = 'Send message';
      //     ele['status'] = 'Time Out';
      //   });
      // }else if (tempData.Body.waterMistControlResponse.result === '0') {
      //   setTimeout(() => {
      //     this.isSpinning = false;
      //     this.childcomponent.showModal();
      //   }, 2000);
      //   this.selectedList.forEach(ele => {
      //     ele['command'] = 'Send message';
      //     ele['status'] = 'Success';
      //   });
      // } else {
      //   setTimeout(() => {
      //     this.isSpinning = false;
      //     this.childcomponent.showModal();
      //   }, 2000);
      //   this.selectedList.forEach(ele => {
      //     ele['command'] = 'Send message';
      //     ele['status'] = 'Failed';
      //   });
      // }
    //});
    this.commandList.forEach(ele => {
      if(ele.key === this.radioValue) {
        this.commandStr = ele.value;
      }
    })
    this.modalListInt(this.selectedList, idx, `Send ${this.commandStr} Command`);
  }
  modalListInt(arr, index, text): void {
		arr[index].equipmentId = this.selectedList[index].equipId;
		arr[index].command = text;
		arr[index].status = 'Processing';
		const timer = setTimeout(() => {
			arr[index].status = 'Time Out';
			if (index === arr.length - 1) {
				this.timerArr = [];
				this.abortWebsocket$.next();
				this.childcomponent.closeLoading();
				this.isSpinning = false;
			}
		}, 12000);
		this.timerArr.push(timer);
	}
  cancelSend() {
    this.childcomponent.closeModal();
  }
  Cancel() {
    this.isVisible = false;
  }
  // 模态框右上角×号
  handleCancel() {
    this.isVisible = false;
  }

  showModal() {
    this.isVisible = true;
  }

}
