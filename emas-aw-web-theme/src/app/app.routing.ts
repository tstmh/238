import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LayoutsComponent } from './layouts/layouts.component';
import { LoginComponent } from './auth/login.component';
const routes: Routes = [
	{
		path: '',
		redirectTo: 'login',
		pathMatch: 'full'
	},
	{
		path: 'emas',
		component: LayoutsComponent,
		children: [
			{ path: '', redirectTo: 'home', pathMatch: 'full' },
			{ path: 'home', loadChildren: () => import('./pages/home/home.module').then(m => m.HomeModule) },
			{ path: 'map', loadChildren: () => import('./pages/map-view/map-view.module').then(m => m.MapViewModule) },
			{ path: 'config', loadChildren: () => import('./pages/config/config.module').then(m => m.ConfigModule) },
			{ path: 'events', loadChildren: () => import('./pages/events/events.module').then(m => m.EventsModule) },
			{ path: 'equipment-control', loadChildren: () => import('./pages/equipment-control/equipment-control.module').then(m => m.EquipmentControlModule) },
			{ path: 'audit-trail', loadChildren: () => import('./pages/audit-trail/audit-trail.module').then(m => m.AuditTrailModule) },
			{ path: 'reports', loadChildren: () => import('./pages/reports/reports.module').then(m => m.ReportsModule) },
			{ path: 'user-management', loadChildren: () => import('./pages/user-management/user-management.module').then(m => m.UserManagementModule) },
			{ path: 'password-management', loadChildren: () => import('./pages/password-management/password-management.module').then(m => m.PasswordManagementModule) }
		]
	},
	// { path: 'map', loadChildren: './pages/map-view/map-view.module#MapViewModule' },
	{ path: 'login', component: LoginComponent },
	{ path: '**', redirectTo: 'login', pathMatch: 'full' }
];

@NgModule({
	imports: [RouterModule.forRoot(routes, { useHash: true })],
	exports: [RouterModule]
})

export class AppRoutingModule { }
