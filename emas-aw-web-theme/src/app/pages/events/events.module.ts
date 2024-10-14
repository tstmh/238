import { NgModule, Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { EventsComponent } from './events.component';
import { RouterModule, Routes } from '@angular/router';
// import { PublicComponentModule } from '../../public/public-component.module';
import { ShareModule } from 'src/app/share/ShareModule';

import { TrafficAlertComponent } from './traffic-alert/traffic-alert.component';
import { TrafficAlertResultComponent } from './traffic-alert-result/traffic-alert-result.component';
import { TrafficAlertRiComponent } from './traffic-alert-ri/traffic-alert-ri.component';
import { TechnicalAlarmComponent } from './technical-alarm/technical-alarm.component';
import { TrafficMeasureComponent } from './traffic-measure/traffic-measure.component';
import { FieldEquipComponent } from './field-equip/field-equip.component';
import { ClusteringNetworkComponent } from './clustering-network/clustering-network.component';
import { NetworkAComponent } from './network-a/network-a.component';
import { NetworkBComponent } from './network-b/network-b.component';
import { SystemSoftwareComponent } from './system-software/system-software.component';
import { ShowImageComponent } from './traffic-alert/show-image/show-image.component';
import { ShowVideoComponent } from './traffic-alert/show-video/show-video.component';
import { NgxEchartsModule } from 'ngx-echarts';

const COMPONENT = [
	EventsComponent,
	TrafficAlertComponent,
    TrafficAlertResultComponent,
    TrafficAlertRiComponent,
	TechnicalAlarmComponent,
	TrafficMeasureComponent,
    FieldEquipComponent,
    ClusteringNetworkComponent,
    NetworkAComponent,
    NetworkBComponent,
    SystemSoftwareComponent,
    ShowImageComponent,
    ShowVideoComponent
];

const routes: Routes = [
    {
        path: '',
        component: EventsComponent,
        children: [
            // { path: '', redirectTo: 'traffic-alert-page', pathMatch: 'full' },
            { path: 'traffic-measure', component: TrafficMeasureComponent,data: { reuseStrategy: true }  },
            { path: 'traffic-alert', component: TrafficAlertComponent,data: { reuseStrategy: true }  },
            { path: 'traffic-alert-rv', component: TrafficAlertResultComponent,data: { reuseStrategy: true }  },
            { path: 'traffic-alert-ri', component: TrafficAlertRiComponent,data: { reuseStrategy: true }  },
            { path: 'technical-alarm', component: TechnicalAlarmComponent ,data: { reuseStrategy: false } },
            { path: 'field-equip', component: FieldEquipComponent ,data: { reuseStrategy: true } },
            { path: 'clustering-network', component: ClusteringNetworkComponent ,data: { reuseStrategy: true } },
            { path: 'network-a', component: NetworkAComponent,data: { reuseStrategy: true }  },
            { path: 'network-b', component: NetworkBComponent ,data: { reuseStrategy: true } },
            { path: 'system-software', component: SystemSoftwareComponent ,data: { reuseStrategy: true } }
            // { path: 'incident-record', component: IncidentRecordComponent },
            // { path: 'incident-logs', component: IncidentLogsComponent }
        ]
    }
];

@NgModule({
    imports: [ShareModule, FormsModule, ReactiveFormsModule, RouterModule.forChild(routes), NgxEchartsModule],
    declarations: COMPONENT
})
export class EventsModule {}
