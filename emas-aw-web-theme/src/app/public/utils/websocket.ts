import { Injectable, SystemJsNgModuleLoader } from '@angular/core';
import { Subject } from 'rxjs';
@Injectable()
export  class WebSocketUtil  {
    _WebSocket: any;
    private lockReconnect = false; //避免重复连接
    private protocol = window.location.protocol.replace('http', 'ws');
    // get location host
    private host = window.location.host;
    // websocket instantiation
    private wsUrl = `${this.protocol}//${this.host}/web/myWS?username=`+new Date().getTime();
    private subject = new Subject();

    public get _Subject()  {
        return this.subject
    }

    constructor(){ 
    }

    private  _HeartBeat = {
        timeout: 8000, //8秒
        timeoutObj: null,
        serverTimeoutObj: null,
        reset() {
            clearTimeout(this.timeoutObj);
            clearTimeout(this.serverTimeoutObj);
            return this;
        },
        start(webSocket) {

            this.timeoutObj = setTimeout(()=>{
                //这里发送一个心跳，后端收到后，返回一个心跳消息，
                //onmessage拿到返回的心跳就说明连接正常
                webSocket.send('{"result":{"agentId":"' + 1 + '"},"type":"HeartBeat"}');
                this.serverTimeoutObj = setTimeout(function () { //如果超过一定时间还没重置，说明后端主动断开了
                    webSocket.close(); //如果onclose会执行reconnect，我们执行ws.close()就行了.如果直接执行reconnect 会触发onclose导致重连两次
                }, this.timeout)
            }, this.timeout);

        }
    }

    private onclose(e) {
        this.reconnect(this.wsUrl);
    };
    private  onerror(e) {
        // console.log('websocket onerror',e);
        this.reconnect(this.wsUrl);
    };
    private  onopen(e) {
        this._HeartBeat.reset();
        this._HeartBeat.start(this._WebSocket);
    };

    private onmessage(evt) {
        this.subject.next(evt)
        this._HeartBeat.reset();
        this._HeartBeat.start(this._WebSocket);
        // SocketQueue.GetMessageType(evt.data);
    };

    private  reconnect(url) {
        if (this.lockReconnect) return;
        this.lockReconnect = true;
        //没连接上会一直重连，设置延迟避免请求过多
        setTimeout(()=> {
            this.createWebSocket(url);
            this.lockReconnect = false;
        }, 3000);
    }

    createWebSocket(url:string = this.wsUrl) {
        try {
            this._WebSocket = new WebSocket(url);
            this._WebSocket.onclose = (e)=>{this.onclose(e)} ;
            this._WebSocket.onerror = (e)=>{this.onerror(e)} ; 
            this._WebSocket.onopen = (e)=>{this.onopen(e)} ; 
            this._WebSocket.onmessage = (e)=>{this.onmessage(e)} ; 
        } catch (e) {
            this.reconnect(url);
        }
    }
}





