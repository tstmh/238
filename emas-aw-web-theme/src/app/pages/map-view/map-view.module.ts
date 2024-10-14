import { NgModule } from '@angular/core';
import { ShareModule } from 'src/app/share/ShareModule';
import { RouterModule } from '@angular/router';
import { MapViewComponent } from './map-view.component';


@NgModule({
	declarations: [MapViewComponent],
	imports: [
		ShareModule,
		RouterModule.forChild([
			{
				path: '',
				component: MapViewComponent
			}
		])
	]
})
export class MapViewModule { }
