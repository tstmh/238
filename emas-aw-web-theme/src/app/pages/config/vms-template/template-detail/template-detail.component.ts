import { Component, OnInit, Input, Output, EventEmitter, AfterViewInit } from '@angular/core';
import { CommonService, EquipType } from '../../../../service/common.service';


@Component({
  selector: 'emas-template-detail',
  templateUrl: './template-detail.component.html',
  styleUrls: ['./template-detail.component.css']
})
export class TemplateDetailComponent implements OnInit, AfterViewInit {
  @Input() selectItem: any;
  @Input() isVisibleMiddle: boolean;
  @Output() closeModal = new EventEmitter();

  rightPartStyles: {};
  imgStyle: {};
  textBoxStyle = [];
  imgList = [];
  textList = [];
  textBox = [];
  allType = {};

  constructor(
    private commonService: CommonService
  ) { }

  ngOnInit() {
    this.initData();
  }

  ngAfterViewInit(): void {
    let button: any = document.querySelector('.ant-modal-close'); // 重置弹出框删除图标总是显示边框问题
    button.style.outline = 'unset';
    $('.ant-modal-body').css('padding', '0'); // 修改template Detail弹出框样式
    // tslint:disable-next-line: deprecation
    $('.redBorder').click(function (e) { // 点击方格改变边框颜色
      $('.redBorder').css('border', '1px solid white');
      $(this).css('border', '1px solid red');
    });

  }
  handleCancelMiddle(): void {
    this.isVisibleMiddle = false;
    this.closeModal.emit();
  }
  async initData() {
    this.allType = await this.commonService.allType;
    this.setRightPartStyles();
  }
  setRightPartStyles() {
    this.transferData();
    this.rightPartStyles = {
      'height': this.selectItem ? this.selectItem.height * 1.9 + 'px' : '80px',
      'width': this.selectItem ? this.selectItem.width * 1.9 + 'px' : '280px',
      'background': this.selectItem ? 'black' : 'lightblue',
      'margin-top': this.selectItem ? '50px' : '50px',
      'position': this.selectItem ? 'relative' : 'relative'
    };
    this.getCOntentStyle();
  }

  transferData() {

    const fontSize = this.allType[EquipType.VMS_FONTTYPE];
    const picConfigDtoList = this.selectItem.vmsTemplatePicConfigDtoList;
    const textlineConfigDtoList = this.selectItem.vmsTemplateTextlineConfigDtoList;
    const flag1 = picConfigDtoList instanceof Array;
    const flag2 = textlineConfigDtoList instanceof Array;
    if (picConfigDtoList && !flag1) {
      const PicConfigDtoListArr = [];
      PicConfigDtoListArr.push(picConfigDtoList);
      this.selectItem.vmsTemplatePicConfigDtoList = PicConfigDtoListArr;
    }
    if (this.selectItem.vmsTemplateTextlineConfigDtoLis && !flag2) {
      const TextlineConfigDtoLisArr = [];
      TextlineConfigDtoLisArr.push(textlineConfigDtoList);
      this.selectItem.vmsTemplateTextlineConfigDtoList = TextlineConfigDtoLisArr;
    }
    if (picConfigDtoList) {
      this.selectItem.vmsTemplatePicConfigDtoList.sort((a, b) => a.picSeq - b.picSeq);
    }
    if (textlineConfigDtoList) {
      this.selectItem.vmsTemplateTextlineConfigDtoList.sort((a, b) => a.textLineNo - b.textLineNo);
      this.selectItem.vmsTemplateTextlineConfigDtoList.forEach((item) => {
        fontSize.forEach((item1) => { // 获取方格的height和width
          if (item.fontTypeId === item1.value) {
            const description = item1.description;
            const descriptionList = description.split('*');
            item.height = descriptionList[0];
            item.width = descriptionList[1];
          }
        });
      });
    }
  }

  getCOntentStyle() {
    const picConfigDtoList = this.selectItem.vmsTemplatePicConfigDtoList;
    const textlineConfigDtoList = this.selectItem.vmsTemplateTextlineConfigDtoList;
    if (picConfigDtoList) {
      this.selectItem.vmsTemplatePicConfigDtoList.forEach((item, index) => {
        this.imgList[index] = { // 图片样式<img>
          'height': picConfigDtoList ? item.height * 1.9 + 'px' : '0px',
          'width': picConfigDtoList ? item.width * 1.9 + 'px' : '0px',
          // tslint:disable-next-line: max-line-length
          'margin-left': picConfigDtoList ? item.xCord * 1.9 + 'px' : '0px',
          // tslint:disable-next-line: max-line-length
          'margin-top': picConfigDtoList ? item.yCord * 1.9 + 'px' : '0px',
          'position': picConfigDtoList ? 'absolute' : 'absolute',
          'border': picConfigDtoList ? '1px solid yellow' : '1px solid yellow',
        };
      });
    }
    if (textlineConfigDtoList) {
      this.selectItem.vmsTemplateTextlineConfigDtoList.forEach((item, index) => {
        this.textList[index] = { // 文本框<ul>
          'margin-left': textlineConfigDtoList ? item.xCord * 1.9 + 'px' : '0px',
          'margin-top': textlineConfigDtoList ? item.yCord * 1.9 + 'px' : '0px',
          'position': textlineConfigDtoList ? 'absolute' : 'absolute'
        };
        this.textBoxStyle[index] = { // 文本框样式<li>
          'height': textlineConfigDtoList ? item.height * 1.9 - 1 + 'px' : '0px',
          'width': textlineConfigDtoList ? item.width * 1.9 - 1 + 'px' : '0px',
          'margin-right': textlineConfigDtoList ? item.charSpacing * 1.9 + 'px' : '0px',
          'border': textlineConfigDtoList ? '1px solid white' : '1px solid white',
          'display': textlineConfigDtoList ? 'inline-block' : 'inline-block'
        };
        this.textBox.push(item.maxNoChar);
      });
    }
  }

}
