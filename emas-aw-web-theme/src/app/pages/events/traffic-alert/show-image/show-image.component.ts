import { Component, OnInit, Input, Output, EventEmitter, AfterViewInit } from '@angular/core';
import { ConstantsService } from '../../../../service/constants.service';

@Component({
  selector: 'emas-show-image',
  templateUrl: './show-image.component.html',
  styleUrls: ['./show-image.component.css']
})
export class ShowImageComponent implements OnInit, AfterViewInit {
  @Input() imgUrl: any;
  @Input() isVisibleMiddle: boolean;
  @Output() closeModal = new EventEmitter();

  fluxUrl = '';
  constructor(
    private constantsService: ConstantsService
  ) { }

  ngOnInit() {
    this.checkImageURL();
  }

  // tslint:disable-next-line: use-life-cycle-interface
  ngAfterViewInit(): void {
    let button: any = document.querySelector('.ant-modal-close'); // 重置弹出框删除图标总是显示边框问题
    button.style.outline = 'unset';
  }

  handleCancelMiddle(): void {
    // console.log('click cancel');
    this.isVisibleMiddle = false;
    this.closeModal.emit();
  }

  checkImageURL () {
    this.fluxUrl = this.constantsService.FluxUrl;
    const index = this.imgUrl.indexOf(this.fluxUrl);
    // console.log('index:', index);
    if (index >= 0) {
      this.imgUrl = this.imgUrl.replace(this.fluxUrl, '/');
    }
    // console.log('imgUrl:', this.imgUrl);
  }
}
