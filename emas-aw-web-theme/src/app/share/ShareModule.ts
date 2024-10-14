/**
 * @作者: hhr
 * @时间: 2018-04-19 14:51:24
 * @描述: 共享模块
 */

import { NgModule, ModuleWithProviders } from '@angular/core';

import { BullySubjectService, MessagesService, ToolsService, NotificationService } from './service';
import { LoadingDirective, RepeatClickDirective, ZmMovableModalDirective } from './directive';
import { OrderByPipe, SearchListPipe, ObjectToArrayPipe, SafeHtmlPipe, DigitUppercasePipe } from './pipe';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { DialogContainerComponent, DialogComponent, DialogService } from './dialog';
import { GisGuardService } from './guard';
import { DemoNgZorroAntdModule } from './ng-zorro-antd.module';
// import {} from './pipe';

const COMPONENT = [DialogContainerComponent, DialogComponent];

const DIRECTIVE = [LoadingDirective, RepeatClickDirective, ZmMovableModalDirective];

const SERVICE = [MessagesService, ToolsService, DialogService, NotificationService, GisGuardService];

const PIPE = [OrderByPipe, SearchListPipe, ObjectToArrayPipe, SafeHtmlPipe, DigitUppercasePipe];

@NgModule({
    imports: [CommonModule, TranslateModule, DemoNgZorroAntdModule],
    exports: [COMPONENT, DIRECTIVE, PIPE, CommonModule, TranslateModule, DemoNgZorroAntdModule],
    declarations: [COMPONENT, DIRECTIVE, PIPE],
    providers: [SERVICE],
    entryComponents: [DialogContainerComponent]
})
export class ShareModule {
    static forRoot(): ModuleWithProviders<ShareModule> {
        return {
            ngModule: ShareModule,
            providers: [BullySubjectService]
        };
    }
}

// declarations: [],   // 用到的组件，指令，管道
// providers: [],      // 依赖注入服务
// imports: [],        // 导入需要的模块
// exports: [],        // 导出的模块，跨模块交流
// entryComponents: [] // 需提前编译好的模块
// bootstrap: []       // 设置根组件
