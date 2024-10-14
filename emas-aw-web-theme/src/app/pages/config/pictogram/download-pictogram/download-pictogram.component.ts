import { Subscription } from 'rxjs';
import { Component, OnInit, Input, Output, EventEmitter, AfterViewInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { NzMessageService } from 'ng-zorro-antd/message';
import { CommonService, EquipType } from '../../../../service/common.service';
import { ConfigService } from '../../config.service';

@Component({
  // tslint:disable-next-line: component-selector
  selector: 'emas-download-pictogram',
  templateUrl: './download-pictogram.component.html',
  styleUrls: ['./download-pictogram.component.css']
})
export class DownloadPictogramComponent implements OnInit, AfterViewInit {
  @Input() selectItem: any;
  @Input() downloadVisibleMiddle: boolean;
  @Input() listGroupID;
  @Output() closeModal = new EventEmitter();

  validateForm: FormGroup;
  picGroupIdLabel: any;
  selectEquipId: any;
  leftTransferList = [];
  rightTransferList = [];
  addDataIdx = null;
  removeDataIdx = null;
  selectPicModel = false;
  height: any;
  width: any;
  addData: string[] = [];
  removeData: string[] = [];
  listSize: any = [];
  expWayList: any = [
    { label: 'All', value: 'All' }
  ];
  equipTypeList: any = [];
  addDisabled = true;
  removeDisabled = true;
  addAllDisabled = true;
  removeAllDisabled = true;
  downloadDisabled = true;

  constructor(
    private fb: FormBuilder,
    private commonService: CommonService,
    private message: NzMessageService,
    private configService: ConfigService
  ) { }

  ngOnInit() {
    this.initForm();
    this.getGroupId(); // get getGroupId
    this.initData();
    this.getEquipType(); // get equipType

  }
  async initData() {
    const allType = await this.commonService.allType;
    const expWayCodeList = allType[EquipType.EXPWAY_CODE];
    expWayCodeList.forEach((item) => {
      this.expWayList.push({ label: item.description, value: item.value });
    });
  }

  initForm() {
    this.validateForm = this.fb.group({
      expWay: [{ value: 'All', disabled: false }],
      equipType: [{ value: 'tip', disabled: false }],
      pictogramId: [{ value: this.selectItem.pictogramId, disabled: false }],
      description: [{ value: this.selectItem.pictogramDesc, disabled: false }],
      groupId: [{ value: null, disabled: false }],
      size: [{ value: null, disabled: false }],
    });
  }

  submitForm(): void {
    // console.log(this.validateForm);
  }

  // tslint:disable-next-line: use-life-cycle-interface
  ngAfterViewInit(): void {
    const button: any = document.querySelector('.ant-modal-close'); // 重置弹出框删除图标总是显示边框问题
    button.style.outline = 'unset';
    $('.ant-modal-body').css('padding', '0'); // 修改template Detail弹出框样式

  }
  handleCancelMiddle(): void {
    // console.log('click cancel');
    this.downloadVisibleMiddle = false;
    this.closeModal.emit();
  }

  handleOkMiddle(): void {
    // console.log('click ok');
    this.downLoadAck();
    this.downloadVisibleMiddle = false;
    this.closeModal.emit(true);
  }

  getGroupId() {
    // console.log(this.listGroupID);
    this.listGroupID.forEach((item) => {
      if (item.value == this.selectItem.picGroupId) {
        this.picGroupIdLabel = item.label;
      }
    });
  }

  choice(item) {
    this.selectEquipId = item;
  }

  // 穿梭框
  addChoice(i) {
    this.addDataIdx = i;
    this.addDisabled = false;
  }
  removeChoice(i) {
    this.removeDataIdx = i;
    this.removeDisabled = false;
  }
  add() {
    if (this.addDataIdx != null) {
      this.addData = this.leftTransferList.splice(this.addDataIdx, 1);
      for (const data of this.addData) {
        this.rightTransferList.push(data);
      }
      this.addData = [];
      this.addDataIdx = null;
      this.addDisabled = true;
      this.downloadDisabled = false;
      this.addAllDisabled = this.leftTransferList.length ? false : true;
      this.removeAllDisabled = this.rightTransferList.length ? false : true;
    }
  }
  remove() {
    if (this.removeDataIdx != null) {
      this.removeData = this.rightTransferList.splice(this.removeDataIdx, 1);
      for (const data of this.removeData) {
        this.leftTransferList.push(data);
      }
      this.removeData = [];
      this.removeDataIdx = null;
      this.removeDisabled = true;
      this.addAllDisabled = this.leftTransferList.length ? false : true;
      this.downloadDisabled = this.rightTransferList.length ? false : true;
    }
  }

  addAll() {
    for (const data of this.leftTransferList) {
      this.rightTransferList.push(data);
    }
    this.leftTransferList = [];
    this.downloadDisabled = false;
    this.addDisabled = true;
    this.addAllDisabled = this.leftTransferList.length ? false : true;
    this.removeAllDisabled = this.rightTransferList.length ? false : true;
  }
  removeAll() {
    for (const data of this.rightTransferList) {
      this.leftTransferList.push(data);
    }
    // this.EndVailableList=this.grantedRoleList
    this.rightTransferList = [];
    this.removeDisabled = true;
    this.downloadDisabled = true;
    this.addAllDisabled = this.leftTransferList.length ? false : true;
    this.removeAllDisabled = this.rightTransferList.length ? false : true;
  }

  getEquipType() {
    this.height = this.selectItem.height;
    this.width = this.selectItem.width;
    this.configService.getAllVmsTemplate().subscribe((r) => {
      this.equipTypeList = [];
      const filterList = new Set();
      const res: any = r;
      const resData = res.Body.getAllVmsTemplateResponse.vmsTemplateConfigDtoList;
      resData.forEach((item) => {
        if (item.vmsTemplatePicConfigDtoList) {
          const picDtoListArr = [...item.vmsTemplatePicConfigDtoList]; // 对于vmsTemplatePicConfigDtoList只有一条数据时不是array
          item.vmsTemplatePicConfigDtoList = picDtoListArr;
          picDtoListArr.forEach((item1) => {
            // tslint:disable-next-line: triple-equals
            if (item1.height == this.height && item1.width == this.width) {
              filterList.add(item.equipType);
            }
          });
        }
      });
      const arr = [...filterList];
      this.equipTypeList = arr.map(item => { return { 'label': item, 'value': item } });
      this.validateForm.get('equipType').setValue(arr[0]);
      this.resetEquipIds();
    });
  }
  resetEquipIds() {
    const expwayCode = this.validateForm.value.expWay;
    const equipType = this.validateForm.value.equipType;
    const transferData = [];
    this.leftTransferList = [];
    this.rightTransferList = [];
    if (expwayCode && equipType) {
      this.commonService.getEquipConfig$().subscribe(resData => {
        resData.forEach((item) => {
          if ((expwayCode === 'All' || item.expwayCode === expwayCode) && item.equipType === equipType) {
            transferData.push(item.equipId);
          }
        });
        this.filterTransferDataByExpWay(transferData);
      });
    }
  }

  filterTransferDataByExpWay(transferData) {
    // 选中行的equipIdList中存在与获取的transferData列表中相等的数据则跳出循环
    this.leftTransferList = transferData.filter((v) => this.selectItem.equipIdList.indexOf(v) < 0);
    this.addAllDisabled = this.leftTransferList.length ? false : true;

  }

  downLoadAck() {
    const data = {
      checksum: this.selectItem.checksum,
      equipIdList: this.selectItem.equipIdList ? this.selectItem.equipIdList : null,
      graphicContents: this.selectItem.graphicContents,
      height: this.selectItem.height,
      picFileName: this.selectItem.picFileName,
      picGroupId: this.selectItem.picGroupId,
      pictogramDesc: this.selectItem.pictogramDesc,
      pictogramId: this.selectItem.pictogramId,
      sender: 1,
      width: this.selectItem.width
    };
    // console.log(data);
    this.configService.AW_CFELS_PictogramSet(data).subscribe((r) => {
      // console.log(r);
      const res: any = r;
      const result = res.Body.AW_CFELS_PictogramSetResponse.result; // o success, 1 error
      // tslint:disable-next-line: max-line-length
      result == 1 ? this.message.create('error', `The download command is fail send to MFELS!`) : this.message.create('success', `The download command is successfull send to MFELS!`);
    });
  }

}
