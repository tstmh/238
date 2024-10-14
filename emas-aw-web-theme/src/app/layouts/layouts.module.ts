import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LayoutsComponent } from './layouts.component';
import { ShareModule } from '../share/ShareModule';
import { RouterModule } from '@angular/router';
import { FooterComponent } from './footer/footer.component';
import { HeaderComponent } from './header/header.component';
import { SidenavComponent } from './sidenav/sidenav.component';
import { TabRouterComponent } from './tab-router/tab-router.component';
import { AuthModule } from 'src/app/auth/auth.module';
import { PublicComModule } from '../public/public-com.module';
// import { PublicModalComponent } from './public-modal/public-modal.component';

const COMPONENT = [
	LayoutsComponent,
	FooterComponent,
	HeaderComponent,
	SidenavComponent,
	TabRouterComponent,
];

@NgModule({
	declarations: COMPONENT,
	exports: COMPONENT,
	imports: [
		PublicComModule,
		CommonModule,
		ShareModule,
		RouterModule,
		AuthModule
	]
})
export class LayoutsModule { }
