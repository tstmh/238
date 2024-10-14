import { NgModule, Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { UserManagementComponent } from './user-management.component';
import { RouterModule, Routes } from '@angular/router';
// import { PublicComponentModule } from '../../public/public-component.module';
import { ShareModule } from 'src/app/share/ShareModule';
import { UserRoleComponent } from './user-role/user-role.component';
import { UserProfileComponent } from './user-profile/user-profile.component';

const COMPONENT = [
    UserManagementComponent,
    UserRoleComponent,
    UserProfileComponent
];

const routes: Routes = [
    {
        path: '',
        component: UserManagementComponent,
        children: [
            // { path: '', redirectTo: 'traffic-alert-page', pathMatch: 'full' },
            { path: 'user-role', component: UserRoleComponent },
            { path: 'user-profile', component: UserProfileComponent }
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
export class UserManagementModule {}

