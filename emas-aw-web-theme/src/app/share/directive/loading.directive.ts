/**
 * @作者: hhr
 * @时间: 2018-07-06 14:25:52
 * @描述: 加载提示
 * @使用: <div [zcLoading]="boolean"></div>
 */
import { Directive, ElementRef, Input } from '@angular/core';

@Directive({
    // tslint:disable-next-line:directive-selector
    selector: '[zcLoading]'
})
export class LoadingDirective {
    constructor(private el: ElementRef) {}

    //  input本身检测数据变化
    @Input()
    set zcLoading(condition: boolean) {
        const loading2 = this.el.nativeElement.querySelector('.app-loading');
        const els = this.el.nativeElement;
        const children = els.children;

        if (!condition && loading2) {
            // 移除节点关闭loading
            els.removeChild(loading2);

            // 子元素放开
            if (children.length > 0) {
                for (let i = 0; i < children.length; i++) {
                    children[i].style.display = 'block';
                    children[i].removeAttribute('style');
                }
            }
        }
        if (condition && !loading2) {
            // 添加loading
            const newNode = document.createElement('div');
            // newNode.style.textAlign = 'center';
            newNode.innerHTML = '<span class="app-loading-icon"></span><span style="color:#bbb;">正在加载，请稍候…</span>';
            newNode.className = 'app-loading';
            // 子元素隐藏
            if (children.length > 0) {
                for (let i = 0; i < children.length; i++) {
                    children[i].style.display = 'none';
                }
            }
            // 添加loading元素
            els.appendChild(newNode);
        }
    }
}
