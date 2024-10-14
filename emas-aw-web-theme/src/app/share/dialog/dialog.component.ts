import { animate, keyframes, state, style, transition, trigger } from '@angular/animations';
import { DialogService } from './dialog.service';
import { Component } from '@angular/core';

// 确认对话框

@Component({
  selector: 'itmp-dialog',
  template: `
    <div class="itmp-mask" [@bg]="visibility" (click)="refDialogClose(false)"></div>
    <div itmpDrag class="itmp-dialog" [@flyInOut]="visibility">
      <div class="itmp-dialog-header flex flex-items-center">
        <i class="itmp-dialog-close-icon iconfont icon-fork cur-p" (click)="refDialogClose(false)"></i>
        <i class="iconfont icon-delete" style="font-size: 20px;margin-right: 5px;"></i>
        <span class="itmp-dialog-title" [innerHTML]="(dialogConfig.title || '标题') | translate"></span>
      </div>
      <div class="itmp-dialog-body">
        <i class="iconfont icon-wenhao" style="color:#ffbf00;"></i>
        <div class="itmp-dialog-content" [innerHTML]="(dialogConfig.content || '内容') | translate"></div>
      </div>
      <div class="itmp-dialog-operate drag-exclude">
        <a (click)="refDialogClose(false)" class="itmp-dialog-btn">
          {{ (dialogConfig.buttonCancelTxt || "取消") | translate }}
        </a>
        <a (click)="refDialogClose(true)" class="itmp-dialog-btn itmp-dialog-btn-ok">
          {{ (dialogConfig.buttonOkTxt || "确定") | translate }}
        </a>
      </div>
    </div>
  `,
  styles: [`
    .itmp-mask {
      z-index: 10;
      position: fixed;
      top: 0;
      right: 0;
      left: 0;
      bottom: 0;
      background-color: #373737;
      background-color: rgba(55, 55, 55, .6);
      height: 100%;
      filter: alpha(opacity=50);
    }

    .itmp-dialog {
      margin: 3px 0;
      border-radius: 4px;
      background: #fff;
      display: inline-block;
      pointer-events: all;
      position: absolute;
      top: 200px;
      left: 50%;
      margin-left: -200px;
      right: 0;
      width: 400px;
      z-index: 3100;
      box-shadow: 1px 1px 20px rgba(0,0,0,.3);
    }

    .itmp-dialog-header {
      padding: 10px 20px;
      position: relative;
      height: 46px;
      text-align: left;
      border-bottom: 2px solid #eee;
    }

    .itmp-dialog-body {
      padding: 30px 40px;
      text-align: left;
    }

    .itmp-dialog-operate {
      padding:20px 40px;
    }

    .itmp-dialog-cancel {
      margin-left: 20px;
    }

    .itmp-dialog-btn {
      width:80px;
      display: inline-block;
      margin-bottom: 0;
      font-weight: 500;
      text-align: center;
      cursor: pointer;
      border: 1px solid transparent;
      white-space: nowrap;
      user-select: none;
      transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
      position: relative;
      color: #999;
      background-color: #fff;
      border-color: #d9d9d9;
      padding: 0 15px;
      font-size: 14px;
      border-radius: 4px;
      height: 32px;
      line-height: 30px;
      font-size: 12px;
    }

    .itmp-dialog-btn:hover {
      border-color: #108ee9;
    }

    .itmp-dialog-btn-ok {
      color: #fff !important;
      margin-left: 10px;;
      background-color: #108ee9;
      border-color: #108ee9;
    }

    .itmp-dialog-body .iconfont {
      font-size: 24px;
      margin-right: 16px;
      padding: 0 1px;
      float: left;
    }

    .itmp-dialog-title {
      color: rgba(0, 0, 0, .65);
      font-weight: 700;
      font-size: 14px;
    }

    .itmp-dialog-content {
      font-size: 18px;
      color: rgba(0, 0, 0, .65);
      margin-top: 8px;
      text-align: cetner;
    }

    .itmp-dialog-close-icon {
      position: absolute;
      top: 9px;
      right: 12px;
      font-weight: bold;
      font-size: 18px;
    }
  `],
  animations: [ // 动画的内容 ps：后期可以封装成动画库
    trigger('flyInOut', [
      // void
      state('void', style({ opacity: 0, transform: 'scale(0.0)' })),
      // state 控制不同的状态下对应的不同的样式
      state('shown', style({ opacity: 1, transform: 'scale(1.0)' })),
      state('hidden', style({ opacity: 0, transform: 'scale(0.0)' })),
      // transition 控制状态到状态以什么样的方式来进行转换
      transition('* => *', animate('150ms'))
    ]),
    trigger('bg', [
      state('void', style({ opacity: 0 })),
      state('shown', style({ opacity: 1 })),
      state('hidden', style({ opacity: 0 })),
      transition('* => *', animate('200ms'))
    ])
  ]
})

export class DialogComponent {
  visibility = 'shown';

  // 信息载体
  dialogConfig: any;
  constructor(private dialog: DialogService) {
    this.getDialog();
  }


  // 实时获取信息
  private getDialog() {
    this.dialog.getDialog().subscribe(res => {
      this.dialogConfig = res;
      this.visibility = 'shown';
    });
  }

  /**
   * 关闭
   * @param {boolean} status [ok: true  cance: false]
   */
  refDialogClose(status: boolean) {
    this.visibility = 'hidden';
    setTimeout(() => {
      this.dialog.setSubject({ type: 'btnStatus', data: status });
    }, 150);
  }

}
