import { Injectable, ApplicationRef, ComponentFactoryResolver, Injector } from '@angular/core';
import { Subject ,  Observable } from 'rxjs';

import { DialogContainerComponent } from './dialog-container.component';


// 对话框


@Injectable()
export class DialogService {

  private _confirm_: any;
  private subject = new Subject<any>();
  constructor(private resolver: ComponentFactoryResolver
    , private injector: Injector
    , private app: ApplicationRef) {

  }


  setSubject(event: any) {
    this.subject.next(event);
  }

  getSubject(): Observable<any> {
    return this.subject.asObservable();
  }


  /**
   * 确认对话框
   * @param {any} event [{title:"标题",content:"内容",buttonOkTxt:"确定按钮文字",buttonCancelTxt:"取消按钮文字"}]
   */
  public confirm(event: any): Observable<Boolean> {
    const _cf = new Subject<Boolean>();
    this.openDialog(event);
    this._confirm_ = this.getSubject().subscribe(res => {
      if (res.type === 'btnStatus') {
        _cf.next(res.data);
        this.closeDialog();
      }
    });
    return _cf.asObservable();
  }

  public getDialog(): Observable<any> {
    // return this.subject.asObservable();
    const _gd = new Subject<Boolean>();
    this.getSubject().subscribe(res => {
      if (res.type === 'dialog') {
        _gd.next(res.data);
      }
    });
    return _gd.asObservable();
  }

  /**
   * 打开
   * @param {[any]} event [{title:"笔筒",content:"孙菲菲"}]
   */
  private openDialog(event: any) {
    this.inDomView();
    // this.subject.next( event );
    this.setSubject({ type: 'dialog', data: event });
  }

  /**
   * 关闭
   */
  private closeDialog() {
    const parent = document.querySelector('body');
    const child = document.querySelector('.jsw-overlay-container-dialog');
    if (child) {
      parent.removeChild(child);
    }
    this._confirm_.unsubscribe();
  }

  /**
   * 向DOM中写入对话框组件载体
   */
  private inDomView() {
    const newNodes = document.querySelector('.jsw-overlay-container-dialog');
    if (!newNodes) {
      const newNode = document.createElement('div');
      newNode.className = 'jsw-overlay-container-dialog';

      const bodyEl = document.querySelector('body');
      bodyEl.appendChild(newNode);

      const factory = this.resolver.resolveComponentFactory(DialogContainerComponent);

      const ref = factory.create(this.injector, [], newNode);
      this.app.attachView(ref.hostView);
    }
  }

}
