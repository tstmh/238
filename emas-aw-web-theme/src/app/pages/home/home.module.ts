import { NgModule } from '@angular/core';
import { HomeComponent } from './home.component';
import { RouterModule } from '@angular/router';
import { ShareModule } from 'src/app/share/ShareModule';

@NgModule({
    imports: [ShareModule, RouterModule.forChild([{ path: '', component: HomeComponent }])],
    declarations: [HomeComponent]
})
export class HomeModule { }
