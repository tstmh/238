import { NgModule, Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AuditTrailComponent } from './audit-trail.component';
import { RouterModule, Routes } from '@angular/router';
// import { PublicComponentModule } from '../../public/public-component.module';
import { ShareModule } from 'src/app/share/ShareModule';
import { UserActivityComponent } from './user-activity/user-activity.component';


const COMPONENT = [
    AuditTrailComponent,
    UserActivityComponent
];

const routes: Routes = [
    {
        path: '',
        component: AuditTrailComponent,
        children: [
            // { path: '', redirectTo: 'traffic-alert-page', pathMatch: 'full' },
            { path: 'user-activity', component: UserActivityComponent }
            // { path: 'traffic-measure-page', component: TrafficMeasureComponent },
            // { path: 'traffic-alert-page', component: TrafficAlertComponent },
            // { path: 'technical-alarm-page', component: TechnicalAlarmComponent }
            // { path: 'incident-record', component: IncidentRecordComponent },
            // { path: 'incident-logs', component: IncidentLogsComponent }
        ]
    }
];

@NgModule({
    imports: [ShareModule, FormsModule, ReactiveFormsModule, RouterModule.forChild(routes)],
    declarations: COMPONENT
})
export class AuditTrailModule {}
