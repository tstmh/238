/**
 * @作者: hhr
 * @时间: 2018-07-06 14:27:18
 * @描述: 避免重复触发click事件
 * @注意: 目前只是针对 botton、input、option 等具有具有disabled属性的标签启用
 * @使用: <button [zcRepeatClick]="boolean">button</button>
 */
import { Directive } from '@angular/core';
import { Input, ElementRef } from '@angular/core';
import { ToolsService } from '../service';

@Directive({
    // tslint:disable-next-line:directive-selector
    selector: '[zcRepeatClick]'
})
export class RepeatClickDirective {
    constructor(private el: ElementRef, private isTools: ToolsService) {
        this.setMsgItemClass();
    }

    @Input('zcRepeatClick')
    set status(_status: any) {
        // console.log(_status, this.el.nativeElement.nodeName);
        this.disabledSwitch(_status);
    }

    private disabledSwitch(status: boolean) {
        if (status) {
            this.disabledOff();
        } else {
            this.disabledOn();
        }
    }
    private disabledOff = () => {
        this.el.nativeElement.disabled = 'disabled';
        this.isTools.addClass(this.el.nativeElement, 'zc-btn-loading');
    };
    private disabledOn = () => {
        this.el.nativeElement.disabled = '';
        this.isTools.delClass(this.el.nativeElement, 'zc-btn-loading');
    };

    /**
     * 样式
     */
    private setMsgItemClass() {
        const nod = document.createElement('style');
        const str = `
      .zc-btn-loading{opacity: 0.6;}
      .zc-btn-loading:before{
        width: 12px;
        height: 12px;
        border: 2px solid #fff;
        border-top: 2px solid rgba(139, 139, 139, 0.5);
        border-radius: 50%;
        content: ' ';
        display: inline-block;
        position: relative;
        top:1px;
        left:-5px;
        -webkit-animation:rotate 1s linear infinite;
        -moz-animation:rotate 1s linear infinite;
        -o-animation:rotate 1s linear infinite;
		animation:rotate 1s linear infinite;
		background:transparent !important;
      }
      @-webkit-keyframes rotate{
        0%  {
          -webkit-transform:rotate(0deg);
          -moz-transform:rotate(0deg);
          -o-transform:rotate(0deg);
          transform:rotate(0deg);
        }
        100% {
          -webkit-transform:rotate(360deg);
          -moz-transform:rotate(360deg);
          -o-transform:rotate(360deg);
          transform:rotate(360deg);
        }
      }
      @keyframes rotate{
        0%  {
          -webkit-transform:rotate(0deg);
          -moz-transform:rotate(0deg);
          -o-transform:rotate(0deg);
          transform:rotate(0deg);
        }
        100% {
          -webkit-transform:rotate(360deg);
          -moz-transform:rotate(360deg);
          -o-transform:rotate(360deg);
          transform:rotate(360deg);
        }
      }
    `;
        nod.type = 'text/css';
        if (nod['styleSheet']) {
            // ie下
            nod['styleSheet'].cssText = str;
        } else {
            nod.innerHTML = str;
        }
        document.getElementsByTagName('head')[0].appendChild(nod);
    }
}
