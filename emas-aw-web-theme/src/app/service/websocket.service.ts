import { Injectable } from '@angular/core';
import { WebSocketUtil } from '../public/utils/websocket'
import { Subject, Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class WebSocketService {

    constructor(private webSocketUtil: WebSocketUtil) {

    }
    abortWebsocket$ = new Subject<void>();
    init() {
        this.webSocketUtil.createWebSocket();
    }

    getMessage(){
        return this.webSocketUtil._Subject;
    }


}

export enum wsMsgType {

}