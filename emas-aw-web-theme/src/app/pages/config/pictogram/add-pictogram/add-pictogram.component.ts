import { Component, OnInit, Input, Output, EventEmitter, AfterViewInit, DoCheck } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { CommonService, EquipType } from '../../../../service/common.service';
import { ConfigService } from '../../config.service';
import { NzMessageService } from 'ng-zorro-antd/message';


@Component({
  // tslint:disable-next-line: component-selector
  selector: 'emas-add-pictogram',
  templateUrl: './add-pictogram.component.html',
  styleUrls: ['./add-pictogram.component.css']
})
export class AddPictogramComponent implements OnInit, AfterViewInit, DoCheck {
  // @Input() selectItem: any;
  @Input() addIsVisibleMiddle: boolean;
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
  pencelStatu = true;
  height: any;
  width: any;
  addData: string[] = [];
  removeData: string[] = [];
  listSize: any = [];
  expWayList: any = [
    { label: 'All', value: 0 }
  ];
  equipTypeList: any = [];
  addDisabled = true;
  removeDisabled = true;
  addAllDisabled = true;
  removeAllDisabled = true;
  addAckDisabled = true;
  imgData = {
    name: null,
    width: null,
    height: null,
    url: null,
    base64url: null
};
  selectItem = {
    graphicContents: null,
    picFileName: null
  };

  constructor(
    private fb: FormBuilder,
    private commonService: CommonService,
    private configService: ConfigService,
    private message: NzMessageService
  ) { }

  ngOnInit() {
    // this.getGroupId(); // get getGroupId
    this.initData(); // get expway
    this.initForm();
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
      expWay: [{ value: null, disabled: false }],
      equipType: [{ value: null, disabled: false }],
      pictogramId: [{ value: 0, disabled: false }],
      description: [{ value: null, disabled: false }],
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

  ngDoCheck(): void { // add确认操作按钮状态
    const desc = this.validateForm.value.description;
    const pictogramId = this.validateForm.value.pictogramId;
    const groupId = this.validateForm.value.groupId;
    const fileName = this.selectItem.picFileName;
    // tslint:disable-next-line: no-non-null-assertion
    // tslint:disable-next-line: max-line-length
    if (desc != null && pictogramId >= 0 && groupId >= 0 && fileName != null && this.rightTransferList.length && this.height && this.width) {
      this.addAckDisabled = false;
    }
  }

  handleCancelMiddle(): void {
    // console.log('click cancel');
    this.addIsVisibleMiddle = false;
    this.closeModal.emit();
  }

  handleOkMiddle(): void {
    // console.log('click ok');
    this.addAck();
    this.addIsVisibleMiddle = false;
    this.closeModal.emit(true);
  }

  choice(item) {
    this.selectEquipId = item;
  }

  groupIdChange() {
    const picGroupId = this.validateForm.value.groupId;
    this.listSize = [];
    this.pencelStatu = false;
    // console.log(picGroupId);
    this.configService.getPictogramDimensionByGroupId(picGroupId).subscribe((r) => {
      // console.log(r);
      const res: any = r;
      const resData = res.Body.getPictogramDimensionByGroupIdResponse.list || [];
      resData.forEach((item) => {
        this.listSize.push({ label: item, value: item });
      });
    });
  }

  // 穿梭框
  addChoice(i) {
    if (this.leftTransferList.length) {
      this.addDataIdx = i;
      this.addDisabled = false;
    }
  }
  removeChoice(i) {
    if (this.rightTransferList.length) {
      this.removeDataIdx = i;
      this.removeDisabled = false;
    }
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
      this.addAllDisabled = this.leftTransferList.length ? false : true;
      this.removeAllDisabled = this.rightTransferList.length ? false : true;
      // this.imgData.base64url ? this.addAckDisabled = false : this.message.create('warning', `Please upload a picture`);
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
      this.removeAllDisabled = this.rightTransferList.length ? false : true;
    }
  }

  addAll() {
    for (const data of this.leftTransferList) {
      this.rightTransferList.push(data);
    }
    this.leftTransferList = [];
    this.addDisabled = true;
    this.addAllDisabled = this.leftTransferList.length ? false : true;
    this.removeAllDisabled = this.rightTransferList.length ? false : true;
    // this.imgData.base64url ? this.addAckDisabled = false : this.message.create('warning', `Please upload a picture`);
  }
  removeAll() {
    for (const data of this.rightTransferList) {
      this.leftTransferList.push(data);
    }
    // this.EndVailableList=this.grantedRoleList
    this.rightTransferList = [];
    this.removeDisabled = true;
    this.addAllDisabled = this.leftTransferList.length ? false : true;
    this.removeAllDisabled = this.rightTransferList.length ? false : true;
  }


  penCilButtom() {
    this.selectPicModel = true;
  }

  // select pictogram
  closeSelectPicModel() {
    this.selectPicModel = false;
    // console.log('select cancel');
  }

  selectPicHandleOkModel() {
    this.selectPicModel = false;
    this.equipTypeList = [];
    this.selectChoice();
    this.getEquipType();
    // console.log('select ok');
  }

  selectChoice() {
    // console.log(this.validateForm);
    const size = this.validateForm.value.size;
    let list = [];
    list = size.split(' * ');
    this.height = list[0];
    this.width = list[1];
  }

  getEquipType() {
    this.configService.getAllVmsTemplate().subscribe((r) => {
      // console.log(r);
      const list = [];
      const res: any = r;
      const resData = res.Body.getAllVmsTemplateResponse.vmsTemplateConfigDtoList;
      resData.forEach((item) => {
        // tslint:disable-next-line: triple-equals
        // tslint:disable-next-line: max-line-length
        const flag = item.vmsTemplatePicConfigDtoList instanceof Array;
        if (item.vmsTemplatePicConfigDtoList && flag === false) { // 对于vmsTemplatePicConfigDtoList只有一条数据时不是array
          const picDtoListArr = [];
          picDtoListArr.push(item.vmsTemplatePicConfigDtoList);
          item.vmsTemplatePicConfigDtoList = picDtoListArr;
        }
      });
      resData.forEach((item) => { // 根据height和width是否相等来获取equiptype
        if (item.vmsTemplatePicConfigDtoList) {
          item.vmsTemplatePicConfigDtoList.forEach((item1) => {
            if (item1.height == this.height && item1.width == this.width) {
              list.push(item.equipType);
            }
          });
        }
      });
      const filterList = new Set(list); // 简单数组去重
      if (filterList) { // 点击select获取左侧穿梭框初始数据
        const firstType = [...filterList][0];
        this.validateForm.value.equipType = firstType; // 获取到equiptype后给表单equipType赋初始值[...filterList][0]
        this.filterByequipType();
      }
      [...filterList].forEach((item) => { // 获取equipType下拉框数据
        this.equipTypeList.push({ label: item, value: item });
      });
    });
  }

  filterByequipType() {
    // tslint:disable-next-line: max-line-length
    const equipType = this.validateForm.value.equipType ? this.validateForm.value.equipType : this.equipTypeList[0].value; // 解决expway筛选时equipType没有值
    this.leftTransferList = [];
    this.rightTransferList = [];
    this.configService.getDimmingTimetableByEquipType(equipType).subscribe((r) => {
      const res: any = r;
      const resData = res.Body.getDimmingTimetableByEquipTypeResponse.vmsTimetableConfigDtoList || [];
      resData.forEach((item) => {
        this.leftTransferList.push(item.equipId);
      });
      this.addAllDisabled = this.leftTransferList.length ? false : true;
    });
  }

  filterByexpWay() { // 根据expway筛选
    this.leftTransferList = [];
    this.rightTransferList = [];
    const expwayCode = this.validateForm.value.expWay;
    const equipType = this.validateForm.value.equipType;
    if (expwayCode && equipType) { // 操作expway和equiptype
      this.equipTypeStatus(expwayCode, equipType);
    } else if (expwayCode && !equipType && this.equipTypeList.length) { // 只操作expway
      const equipType1 = this.equipTypeList[0].value;
      this.equipTypeStatus(expwayCode, equipType1);
    } else { // expway为All时
      // tslint:disable-next-line: max-line-length
      this.equipTypeList.length ? this.filterByequipType() : this.message.create('warning', `Please complete the previous operation first.`);
    }
  }

  equipTypeStatus(expwayCode, equipType) {
    this.configService.GetAllEquipConfig().subscribe((r) => {
      // console.log(r);
      const res: any = r;
      const resData = res.Body.getAllEquipConfigResponse.equipConfigDtoList;
      resData.forEach((item) => {
        if (item.expwayCode == expwayCode && item.equipType == equipType) {
          this.leftTransferList.push(item.equipId);
        }
      });
    this.addAllDisabled = this.leftTransferList.length ? false : true;
    });
  }

  checkImageSize(file: File) {
    const img = new Image(); // create image
    img.src = window.URL.createObjectURL(file);
    img.onload = () => {
        const width = img.naturalWidth;
        const height = img.naturalHeight;
        // console.log('图片大小', width, height);
        this.imgData.width = width;
        this.imgData.height = height;
        this.imgData.url = img.src;
        this.imgData.name = file.name.split('.')[0];
        if (this.imgData.width == this.width && this.imgData.height == this.height) {
          this.selectItem.picFileName = file.name;
          this.getBase64(file);
          this.message.create('success', `The image size is ${height} * ${width}`);
        } else {
          this.message.create('error', `The image size is not ${this.height} * ${this.width}`);
        }
        window.URL.revokeObjectURL(img.src);
    };
  }

  getBase64(file: File) {
    const reader = new FileReader();
    reader.addEventListener('load', () => {
      const base64Str = reader.result.toString();
      this.imgData.base64url = base64Str.split('base64,')[1]; //获取上传图片的base64码
      this.selectItem.graphicContents = this.imgData.base64url;
      // console.log(this.selectItem.graphicContents);
    });
    reader.readAsDataURL(file);
}

  // beforeUpload = (file: File) => {
  //   this.checkImageSize(file);
  // }

  beforeUpload(e) {
    const file = e.srcElement.files[0]; // 获取上传图片的file对象
    // console.log(file);
    this.checkImageSize(file);
  }

  addAck() {
    const userId = localStorage.getItem('user_name');
    const data = {
      checksum: 0,
      equipIdList: this.rightTransferList ? this.rightTransferList : null,
      graphicContents: this.selectItem.graphicContents,
      height: this.height,
      picFileName: this.selectItem.picFileName,
      picGroupId: this.validateForm.value.groupId,
      pictogramDesc: this.validateForm.value.description,
      pictogramId: this.validateForm.value.pictogramId,
      sender: userId,
      width: this.width
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

