import { Component, OnInit, Input, Output, EventEmitter, AfterViewInit } from '@angular/core';
import { NzMessageService } from 'ng-zorro-antd/message';
import { ConfigService } from '../../config.service';

@Component({
  // tslint:disable-next-line: component-selector
  selector: 'emas-update-pictogram',
  templateUrl: './update-pictogram.component.html',
  styleUrls: ['./update-pictogram.component.css']
})
export class UpdatePictogramComponent implements OnInit {
  @Input() selectItem: any;
  @Input() isVisibleMiddle: boolean;
  @Input() listGroupID;
  @Output() closeModal = new EventEmitter();

  picGroupIdLabel: any;
  selectEquipId: any;
  imgData = {
    name: null,
    width: null,
    height: null,
    url: null,
    base64url: null
};

  constructor(
    private message: NzMessageService,
    private configService: ConfigService
  ) { }

  ngOnInit() {
    this.getGroupId();
  }

  // tslint:disable-next-line: use-life-cycle-interface
  ngAfterViewInit(): void {
    const button: any = document.querySelector('.ant-modal-close'); // 重置弹出框删除图标总是显示边框问题
    button.style.outline = 'unset';
    $('.ant-modal-body').css('padding', '0'); // 修改template Detail弹出框样式

  }
  handleCancelMiddle(): void {
    this.isVisibleMiddle = false;
    this.closeModal.emit();
  }

  handleOkMiddle(): void {
    this.updateAck();
    this.isVisibleMiddle = false;
    this.closeModal.emit(true);
  }

  getGroupId() {
    this.listGroupID.forEach((item) => {
      if (item.value == this.selectItem.picGroupId) {
        this.picGroupIdLabel = item.label;
      }
    });
  }

  choice(item) {
    this.selectEquipId = item;
  }

  checkImageSize(file: File) {
    const img = new Image(); // create image
    img.src = window.URL.createObjectURL(file);
    img.onload = () => {
        const width = img.naturalWidth;
        const height = img.naturalHeight;
        this.imgData.width = width;
        this.imgData.height = height;
        this.imgData.url = img.src;
        this.imgData.name = file.name.split('.')[0];
        if (this.imgData.width == this.selectItem.width && this.imgData.height == this.selectItem.height) {
          this.selectItem.picFileName = this.imgData.name;
          this.getBase64(file);
          this.message.create('success', `The image size is ${height} * ${width}`);
        } else {
          this.message.create('error', `The image size is incorrect!`);
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
    });
    reader.readAsDataURL(file);
}

  // beforeUpload = (file: File) => {
  //   this.checkImageSize(file);
  // }

  beforeUpload(e) {
    const file = e.srcElement.files[0]; // 获取上传图片的file对象
    this.checkImageSize(file);
  }

  updateAck() {
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
    // tslint:disable-next-line: max-line-length
    this.configService.AW_CFELS_PictogramSet(data).subscribe((r) => {
      const res: any = r;
      const result = res.Body.AW_CFELS_PictogramSetResponse.result; // o success, 1 error
      // tslint:disable-next-line: max-line-length
      result == 1 ? this.message.create('error', `The download command is fail send to MFELS!`) : this.message.create('success', `The download command is successfull send to MFELS!`);
    });
  }

}
