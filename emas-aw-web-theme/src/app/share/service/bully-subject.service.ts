/**
 * @作者: hhr
 * @时间: 2018-04-19 10:48:58
 * @描述: 观察者模式·全局
 */
import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject, Subject } from 'rxjs';
// import { MESSAGE_CHANNEL } from 'app/app.constants';

@Injectable()
export class BullySubjectService {
    protected subject$: Subject<Bully>;
    protected bSubject$: BehaviorSubject<Bully>;

    protected broadcast$Map = new Map();

    constructor() {
        if (!this.subject$) {
            this.subject$ = new Subject<Bully>();
        }

        if (!this.bSubject$) {
            this.bSubject$ = new BehaviorSubject<Bully>(null);
        }
    }

    /**
     * 观察者模式·接收  -- BS -- 用于先订阅后发
     */
    public getSubject(): Observable<Bully> {
        return this.subject$.asObservable();
    }

    /**
     * 观察者模式·发送  -- BS -- 用于先订阅后发
     * @param item Object
     */
    public setSubject(item: Bully) {
        this.subject$.next(item);
    }

    /**
     * 观察者模式·接收  -- BS -- 用于先发后订阅
     */
    public getBSubject(): Observable<Bully> {
        return this.bSubject$.asObservable();
    }

    /**
     * 观察者模式·发送  -- BS -- 用于先发后订阅
     * @param item Object
     */
    public setBSubject(item: Bully) {
        this.bSubject$.next(item);
    }

    /**
     * BS 重置
     */
    public resetBSubject(): void {
        this.bSubject$.next(null);
    }

    /**
     * 注册广播通讯频道,并接受消息
     * @param channel: MESSAGE_CHANNEL 频道
     */
    /* public registerBroadcastReceive(channel: MESSAGE_CHANNEL): Observable<Bully> {
        // const broadcast$ = new BroadcastChannel(channel);
        const key = channel;
        const broadcast$ = new BroadcastChannel(channel);
        const _ms = new Subject<Bully>();

        this.broadcast$Map.set(key, broadcast$);
        broadcast$.addEventListener('message', (res: any) => {
            console.log(res);
            if (!res) {
                return;
            }
            (res['data'] as Bully).carrier = broadcast$;
            _ms.next(res['data']);
        });
        return _ms.asObservable();
    } */

    /**
     * 注册广播通讯频道,并发送消息
     * @param channel: MESSAGE_CHANNEL 频道
     * @param item: Bully 要发送的消息
     */
    /* public registerBroadcastSend(channel: MESSAGE_CHANNEL, item: Bully): void {
        const broadcast$ = new BroadcastChannel(channel);
        broadcast$.postMessage(item);
    } */

    /**
     * 关闭广播频道
     * @param channel: MESSAGE_CHANNEL 频道
     */
    /* public closeBroadcast(channel: MESSAGE_CHANNEL): void {
        const broadcast$ = this.broadcast$Map.get(channel);
        if (broadcast$) {
            broadcast$.close();
            this.broadcast$Map.delete(channel);
        }
    } */
}

export interface Bully {
    type: string;
    data?: any;
    carrier?: any; // 信息载体
    fromGis?: boolean; // 消息是否来自gis
}
