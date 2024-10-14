import { Component, OnInit, Input, Output, EventEmitter, AfterViewInit, ViewChild } from '@angular/core';
import { DialogService } from 'src/app/share/dialog';
import { ViewModalComponent } from '../view-modal/view-modal.component';
import { NzMessageService } from 'ng-zorro-antd';
import { EquipmentService } from '../../equipment-control.service';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'emas-apply-modal',
  templateUrl: './apply-modal.component.html',
  styleUrls: ['./apply-modal.component.css']
})
export class ApplyModalComponent implements OnInit {
  @ViewChild(ViewModalComponent) private childcomponent: ViewModalComponent;
  isVisible = false;
  isConfirmLoading = false;
  @Input() selectItem: any;
  @Input() listOfData: any;
  @Input() radioValue;
  availableList = [];
  selectedList = [];
  chooseLi = [];
  firstLoadLi = [];
  addData = [];
  clickIndex = null;
  addDisabled = true;
  removeDisabled = true;
  addAllDisabled = false;
  removeAllDisabled = true;
  isSpinning = false;
  newData = [];
  constructor(
    private dialog: DialogService,
    private message: NzMessageService,
    private equipmentService: EquipmentService,
  ) { }

  ngOnInit() {
    this.firstLoad();
    const queryData = {
      felsCode: this.selectItem.felsCode,
      equipType: this.selectItem.equipmentCode,
      attrName: 'stg'
    };
    this.equipmentService.getEquipStatusByEquipType(queryData).subscribe((r: any) => {
      const tempData = r.Body.getEquipStatusByEquipTypeResponse;
      if (tempData.equipStatusList) {
        for (const item of tempData.equipStatusList) {
          if (item.equipmentId === this.selectItem.equipmentId) {
            this.selectItem.radioValue = item.attrValue;
            break;
          }
        }
      }
    });
  }
  // 模态框出现时的一些初始赋值等操作
  firstLoad() {
    this.availableList = Array.from(this.listOfData);
    this.availableList.forEach((item, index) => {
      if (item.equipmentId === this.selectItem.equipmentId) {
        this.firstLoadLi = this.availableList.splice(index, 1);
      }
    });
    this.firstLoadLi.forEach(item => {
      this.selectedList.push(item);
      this.removeDisabled = false;
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
            // console.log(item);
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
        this.chooseLi.push({ ...item });
        item.equipmentId = null;
      }
    });
    this.availableList = this.availableList.filter(item => item.equipmentId);
    // this.chooseLi = this.availableList.splice(this.clickIndex, 1);
    this.chooseLi.forEach(item => { // 将选中的项添加到右边
      this.selectedList.push(item);
      this.removeDisabled = false;
      // item.active = false;
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
        item.statusId = null;
      }
    });
    this.selectedList = this.selectedList.filter(item => item.statusId);
    // this.chooseLi = this.selectedList.splice(this.clickIndex, 1);
    this.chooseLi.forEach(item => {
      item.active = false;
      this.availableList.push(item);
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

  // 模态框右上角×号
  handleCancel() {
    this.isVisible = false;
  }
  Send() {
    if (this.selectedList.length !== 0) {
      this.dialog
        .confirm({
          title: 'Confirmation Window',
          content: `Apply to ${this.selectedList.length} equipments`,
          buttonOkTxt: 'Yes(Y)',
          buttonCancelTxt: 'No(N)'
        })
        .subscribe(res => {
          if (res) {
            this.confirmSend();
          } else {
            this.cancelSend();
          }
        });
    } else {
      this.message.create('warning', `Please select an item to send`);
    }
  }
  confirmSend() {
    this.selectItem = Array.from(this.selectedList);
    // console.log(this.selectItem);
    this.isSpinning = true;
    this.selectItem.forEach(eles => {
      const queryData = {
        lusMsg: {
          attrName: 'cdd',
          attrValue: this.radioValue,
          equipId: eles.equipmentId,
          equipmentCode: eles.equipmentCode,
          felsCode: eles.felsCode,
        }
      };
      this.equipmentService.AW_CFELS_LUS(queryData).subscribe((r) => {
        const tempData: any = r;
        if (tempData.Body.AW_CFELS_LUSResponse.result === '0') {
          setTimeout(() => {
            this.isSpinning = false;
            this.childcomponent.showModal();
            this.childcomponent.closeLoading();
          }, 2000);
          this.selectedList.forEach(ele => {
            ele['command'] = 'Send message';
            ele['status'] = 'Success';
          });
        } else {
          setTimeout(() => {
            this.isSpinning = false;
            this.childcomponent.showModal();
            this.childcomponent.closeLoading();
          }, 2000);
          this.selectedList.forEach(ele => {
            ele['command'] = 'Send message';
            ele['status'] = 'Failed';
          });
        }
      });
    });    
  }
  cancelSend() {
    this.childcomponent.closeModal();
    this.childcomponent.closeLoading();
  }
  Cancel() {
    this.isVisible = false;
  }
  showModal() {
    this.isVisible = true;
  }
}
