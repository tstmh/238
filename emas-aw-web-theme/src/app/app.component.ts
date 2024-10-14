import { Component, OnInit, AfterViewInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Router, NavigationEnd, NavigationStart, RoutesRecognized } from '@angular/router';
import { filter } from 'rxjs/operators';
import { BullySubjectService, Bully } from './share/service';
// import { StompClientService } from './service/web-socket.service';
import { SYSTEM_EVENT } from './app.constants';
import { LoginService } from './auth/login.service';
import { CommonService } from './service/common.service';
import { WebSocketService } from './service/websocket.service';

declare const require: any;
const Pace = require('pace');

@Component({
    selector: 'sj-root',
    template: `<router-outlet></router-outlet>`,
})
export class AppComponent implements OnInit, AfterViewInit {
    Pace = Pace;


    constructor(
        private translate: TranslateService,
        private router: Router,
        private bully: BullySubjectService,
        // private stomp: StompClientService,
        private ls: LoginService,
        private commonService: CommonService,
        private webSocketService: WebSocketService
    ) {
        // 添加语言支持
        this.translate.addLangs(['en', 'zh']);

        // 设置默认语言, 一般在无法匹配的时候使用
        this.translate.setDefaultLang('en');


        // 判断本地token是否需要用户登录
        if (!this.ls.autoAccessToken()) {
            // console.warn('token failure, please login!');
        }

        // pace进度配置
        this._setPace();
    }


    // 配置Pace
    private _setPace() {
        // console.log(Pace);
        this.Pace.options = {
            ajax: {
                trackMethods: ['GET', 'POST']
            },
            document: false,
            eventLag: true,
            elements: true,
            // Only show the progress on regular and ajax-y page navigation,
            // not every request
            restartOnRequestAfter: true,
            restartOnPushState: true
        };

        this.router.events.subscribe(event => {
            if (event instanceof NavigationStart) {
                this.Pace.start();
            }

            if (event instanceof RoutesRecognized) {
                this.Pace.restart();
            }
        });
    }

    // 路由跳转事件
    private _routerChangeHandle() {
        this.router.events.pipe(filter(e => e instanceof NavigationEnd)).subscribe(e => {
            const bully: Bully = {
                type: SYSTEM_EVENT.ROUTE_EVENT,
                data: e['urlAfterRedirects']
            };
            this.bully.setSubject(bully);
        });
    }

    ngOnInit() {
        this._routerChangeHandle();

        // this.commonService.GetAllCommonTypeConfig();
    }
    ngAfterViewInit() {
        setTimeout(() => this.webSocketService.init())
    }
}
