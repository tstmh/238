import { Injectable } from '@angular/core';
import { NzNotificationService } from 'ng-zorro-antd';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';

export interface Notify {
    type: 'warning' | 'error' | 'blank' | 'success' | 'info';
    path: string;
    body?: NotifyBody;
}

export interface NotifyBody {
    title: string;
    content: string;
}

@Injectable()
export class NotificationService {
    constructor(private _notification: NzNotificationService, private translate: TranslateService) { }

    /**
     * 消息弹窗
     * @param param: Notify
     */
    public notify(param: Notify) {
        const type = param['type'] || 'info';
        const path = param['path'];
        if (!path) {
            throw TypeError('lack lang path');
        }
        if (path) {
            // 先移除其他消息，然后再显示当前消息
            this._notification.remove();
            this.translate.get(path).subscribe(res => {
                this._notification[type](res.title, res.content);
            });
        }
    }
}
