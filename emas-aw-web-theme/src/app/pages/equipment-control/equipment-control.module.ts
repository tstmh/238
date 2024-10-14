import { NgModule, Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { EquipmentControlComponent } from './equipment-control.component';
import { RouterModule, Routes } from '@angular/router';
// import { PublicComponentModule } from '../../public/public-component.module';
import { ShareModule } from 'src/app/share/ShareModule';
import { LusComponent } from './lus/lus.component';
import { VmsControlComponent } from './vms-control/vms-control.component';
import { PmcsComponent } from './pmcs/pmcs.component';
import { FireAlarmPanelComponent } from './fire-alarm-panel/fire-alarm-panel.component';
import { FireTrafficPlanComponent } from './fire-traffic-plan/fire-traffic-plan.component';
import { EquipmentHousingComponent } from './equipment-housing/equipment-housing.component';
import { UpdateEquipmentComponent } from './equipment-housing/update-equipment/update-equipment.component';
import { SendCommandComponent } from './lus/send-command/send-command.component';
import { SurveillanceSystemComponent } from './surveillance-system/surveillance-system.component';
import { WaterMistSystemComponent } from './water-mist-system/water-mist-system.component';
import { NewPmcsComponent } from './new-pmcs/new-pmcs.component';
import { CommandSendComponent } from './water-mist-system/command-send/command-send.component';
import { ApplyCommandComponent } from './water-mist-system/apply-command/apply-command.component';
import { SendApplyComponent } from './water-mist-system/send-apply/send-apply.component';
import { NewModalComponent } from './fire-traffic-plan/new-modal/new-modal.component';
import { ApplyModalComponent } from './lus/apply-modal/apply-modal.component';
import { ViewModalComponent } from './lus/view-modal/view-modal.component';
import { NewLusComponent } from './new-lus/new-lus.component';
import { ViewLusComponent } from './new-lus/view-lus/view-lus.component';
import { SendModalComponent } from './pmcs/send-modal/send-modal.component';
import { ApplyWindowComponent } from './pmcs/apply-window/apply-window.component';
import { CommandModalComponent } from './pmcs/command-modal/command-modal.component';
import { EcPubilcTableComponent } from './ec-pubilc-table/ec-pubilc-table.component';
import { VmsControlLibraryComponent } from './vms-control/vms-conrtol-library/vms-conrtol-library.component';


const COMPONENT = [
    EquipmentControlComponent,
    LusComponent,
    VmsControlComponent,
    PmcsComponent,
    FireAlarmPanelComponent,
    FireTrafficPlanComponent,
    EquipmentHousingComponent,
    UpdateEquipmentComponent,
    SurveillanceSystemComponent,
    WaterMistSystemComponent,
    NewPmcsComponent,
    CommandSendComponent,
    ApplyCommandComponent,
    SendApplyComponent,
    SendCommandComponent,
    NewModalComponent,
    ApplyModalComponent,
    ViewModalComponent,
    NewLusComponent,
    ViewLusComponent,
    SendModalComponent,
    ApplyWindowComponent,
    CommandModalComponent,
    EcPubilcTableComponent,
    VmsControlLibraryComponent
];

const routes: Routes = [
    {
        path: '',
        component: EquipmentControlComponent,
        children: [
            // { path: '', redirectTo: 'traffic-alert-page', pathMatch: 'full' },
            { path: 'lus', component: LusComponent, data: { reuseStrategy: true } },
            { path: 'vms-control', component: VmsControlComponent, data: { reuseStrategy: true } },
            { path: 'pmcs', component: PmcsComponent, data: { reuseStrategy: true } },
            { path: 'fire-alarm-panel', component: FireAlarmPanelComponent, data: { reuseStrategy: true } },
            { path: 'fire-traffic-plan', component: FireTrafficPlanComponent, data: { reuseStrategy: true } },
            { path: 'equipment-housing', component: EquipmentHousingComponent, data: { reuseStrategy: true } },
            { path: 'water-mist-system', component: WaterMistSystemComponent, data: { reuseStrategy: false } },
            { path: 'new-pmcs', component: NewPmcsComponent, data: { reuseStrategy: true } },
            { path: 'new-lus', component: NewLusComponent, data: { reuseStrategy: false } },
            { path: 'surveillance-system', component: SurveillanceSystemComponent,data: { reuseStrategy: false } },
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
export class EquipmentControlModule { }
