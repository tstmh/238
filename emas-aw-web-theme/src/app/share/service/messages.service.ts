/**
 * @作者: hhr
 * @时间: 2018-06-11 11:24:55
 * @描述: 消息
 */
import { Injectable } from '@angular/core';

@Injectable()
export class MessagesService {
    constructor() {
        this.setMsgItemClass();
    }

    /**
     * 信息
     */
    public info(content: String) {
        this.setMessages({ content, type: 'info' });
    }

    /**
     * 警告
     */
    public warning(content: String) {
        this.setMessages({ content, type: 'warning' });
    }

    /**
     * 错误
     */
    public error(content: String) {
        this.setMessages({ content, type: 'error' });
    }

    /**
     * 成功
     */
    public success(content: String) {
        this.setMessages({ content, type: 'success' });
    }

    /**
     * 设置消息
     */
    private setMessages(messages: any) {
        this.inDomView(messages);
    }

    /**
     * 向DOM中写入消息载体
     */
    private inDomView(_messages: any) {
        const domMsg$ = document.querySelector('.zc-overlay-container-messages');
        if (!domMsg$) {
            const newNode = document.createElement('div');
            newNode.className = 'zc-overlay-container-messages';
            const bodyEl = document.querySelector('body');
            bodyEl.appendChild(newNode);

            this.inMsgItemView(_messages);
            return;
        }
        this.inMsgItemView(_messages);
    }

    /**
     * 向消息载体中写入消息
     */
    private inMsgItemView(_messages: any) {
        const domMsg$ = document.querySelector('.zc-overlay-container-messages');
        const msgItem$ = document.createElement('div');
        msgItem$.className = `zc-message-item ${_messages.type}`;
        msgItem$.innerHTML = _messages.content;
        domMsg$.appendChild(msgItem$);
        setTimeout(() => {
            domMsg$.removeChild(msgItem$);
        }, 3000);
    }

    /**
     * 样式
     */
    private setMsgItemClass() {
        const nod = document.createElement('style');
        const str = `
      .zc-overlay-container-messages{position: fixed;z-index: 1010;width: 100%;top: 100px;left: 0;pointer-events: none;text-align: center;}
      .zc-message-item{position:relative;padding:8px 16px 8px 46px;margin:3px 0;border-radius:4px;
        box-shadow:0 2px 8px rgba(0,0,0,.2);background:#fff;display:inline-block;pointer-events:all;}
      .zc-message-item:before{content:" ";position:absolute;left:18px;width:18px;
        height:18px;background-color:#666;display:inline-block;border-radius:50%;}
      .zc-message-item.info:before{background-color:#636bb4;}
      .zc-message-item.warning:before{background-color:#f9a202;}
      .zc-message-item.error:before{background-color:#e43938;}
      .zc-message-item.success:before{background-color:#009944;}
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
