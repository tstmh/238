import { NgModule, Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ReportsComponent } from './reports.component';
import { RouterModule, Routes } from '@angular/router';
// import { PublicComponentModule } from '../../public/public-component.module';
import { ShareModule } from 'src/app/share/ShareModule';
import { TechnicalAlarmComponent } from './technical-alarm/technical-alarm.component';
import { InstrusionDetectionComponent } from './instrusion-detection/instrusion-detection.component';
import { AlarmReportComponent } from './technical-alarm/alarm-report/alarm-report.component';
import { TrafficMeasureComponent } from './traffic-measure/traffic-measure.component';
import { TechnicalAlertComponent } from './technical-alert/technical-alert.component';
import { EquipmentStatusComponent } from './equipment-status/equipment-status.component';
import { AlarmModalComponent } from './equipment-status/alarm-modal/alarm-modal.component';
import { ReportModelComponent } from './traffic-measure/report-model/report-model.component';
import { AlertAiComponent } from './alert-ai/alert-ai.component'
import { AlertReportComponent } from './technical-alert/alert-report/alert-report.component';
import { AiReportComponent } from './alert-ai/ai-report/ai-report.component';
import { NgxEchartsModule } from 'ngx-echarts';
import { ExportBtnComponent } from './component/export-btn.component';

const COMPONENT = [
    ReportsComponent,
    TechnicalAlarmComponent,
    InstrusionDetectionComponent,
    AlarmReportComponent,
    AlertReportComponent,
    TrafficMeasureComponent,
    TechnicalAlertComponent,
    EquipmentStatusComponent,
    AlarmModalComponent,
    EquipmentStatusComponent,
    ReportModelComponent,
    ExportBtnComponent,
    AlertAiComponent,
    AiReportComponent
];

const routes: Routes = [
    {
        path: '',
        component: ReportsComponent,
        children: [
            // { path: '', redirectTo: 'traffic-alert-page', pathMatch: 'full' },
            { path: 'technical-alarm', component: TechnicalAlarmComponent,data: { reuseStrategy: true }  },
            { path: 'instrusion-detection', component: InstrusionDetectionComponent ,data: { reuseStrategy: true } },
            { path: 'traffic-measure', component: TrafficMeasureComponent ,data: { reuseStrategy: true } },
            { path: 'traffic-alert', component: TechnicalAlertComponent,data: { reuseStrategy: true }  },
            { path: 'equipment-status', component: EquipmentStatusComponent ,data: { reuseStrategy: true } },
            { path: 'alert-ai', component: AlertAiComponent ,data: { reuseStrategy: true } }
            // { path: 'traffic-measure-page', component: TrafficMeasureComponent },
            // { path: 'traffic-alert-page', component: TrafficAlertComponent },
            // { path: 'technical-alarm-page', component: TechnicalAlarmComponent }
            // { path: 'incident-record', component: IncidentRecordComponent },
            // { path: 'incident-logs', component: IncidentLogsComponent }
        ]
    }
];

@NgModule({
    imports: [ShareModule, FormsModule, ReactiveFormsModule, RouterModule.forChild(routes), NgxEchartsModule],
    declarations: COMPONENT
})
export class ReportsModule {}

