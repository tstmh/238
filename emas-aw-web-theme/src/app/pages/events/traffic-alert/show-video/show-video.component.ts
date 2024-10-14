import { Component, OnInit, Input, Output, EventEmitter, AfterViewInit } from '@angular/core';

@Component({
  selector: 'emas-show-video',
  templateUrl: './show-video.component.html',
  styleUrls: ['./show-video.component.css']
})
export class ShowVideoComponent implements OnInit, AfterViewInit {
  @Input() videoUrl: any;
  @Input() isVisibleMiddle: boolean;
  @Output() closeModal = new EventEmitter();
  isAVI = false;

  constructor() { }

  ngOnInit() {
    this.getVideoType();
  }
  
  getVideoType(): void {
    // console.log(this.videoUrl)
    if (this.videoUrl) {
      const urlLength = String(this.videoUrl).length;
      const videoType = this.videoUrl.substring(urlLength-3, urlLength);
      if (videoType == 'avi') {
        this.isAVI = true;
      }
    }
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
}
