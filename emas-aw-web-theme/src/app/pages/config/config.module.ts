import { NgModule, Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ConfigComponent } from './config.component';
import { RouterModule, Routes } from '@angular/router';
// import { PublicComponentModule } from '../../public/public-component.module';
import { ShareModule } from 'src/app/share/ShareModule';
import { EquipmentComponent } from './equipment/equipment.component';
import { VmsTemplateComponent  } from './vms-template/vms-template.component';
import { PictogramComponent } from './pictogram/pictogram.component';
import { FelsParametersComponent } from './fels-parameters/fels-parameters.component';
import { TemplateDetailComponent } from './vms-template/template-detail/template-detail.component';
import { UpdatePictogramComponent } from './pictogram/update-pictogram/update-pictogram.component';
import { AddPictogramComponent } from './pictogram/add-pictogram/add-pictogram.component';
import { DownloadPictogramComponent } from './pictogram/download-pictogram/download-pictogram.component';
import { EquipmentStatusComponent } from './equipment-status/equipment-status.component';
const COMPONENT = [
    ConfigComponent,
	EquipmentComponent,
    VmsTemplateComponent,
    PictogramComponent,
    FelsParametersComponent,
    TemplateDetailComponent,
    UpdatePictogramComponent,
    AddPictogramComponent,
    DownloadPictogramComponent,
    EquipmentStatusComponent,
];

const routes: Routes = [
    {
        path: '',
        component: ConfigComponent,
        children: [
            // { path: '', redirectTo: 'traffic-alert-page', pathMatch: 'full' },
			{ path: 'equipment', component: EquipmentComponent },
            { path: 'vms-template', component: VmsTemplateComponent },
            { path: 'pictogram', component: PictogramComponent },
            { path: 'fels-parameters', component: FelsParametersComponent },
            // { path: 'equipment-status', component: EquipmentStatusComponent },
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
export class ConfigModule {}
