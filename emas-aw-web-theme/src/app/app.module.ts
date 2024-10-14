import { NgModule } from '@angular/core';
import { BrowserModule, DomSanitizer } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AppRoutingModule } from './app.routing';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { LayoutsModule } from './layouts/layouts.module';
import { ShareModule } from './share/ShareModule';
import { PublicComModule } from './public/public-com.module';
import { AppComponent } from './app.component';

import { MatIconRegistry } from '@angular/material/icon';
import {  NZ_I18N, en_US } from 'ng-zorro-antd';

import { HttpInterceptorProviders } from './blocks/http-interceptor';

import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

import { registerLocaleData } from '@angular/common';
import en from '@angular/common/locales/en';

import {SimpleReuseStrategy} from './service/simpleReuseStrategy';
import {RouteReuseStrategy} from '@angular/router';
import {NgxEchartsModule} from 'ngx-echarts';


registerLocaleData(en);

export function createTranslateHttpLoader(http: HttpClient) {
	return new TranslateHttpLoader(http, '/assets/i18n/', '.json');
}


@NgModule({
	imports: [
		BrowserModule,
		FormsModule,
		ReactiveFormsModule,
		AppRoutingModule,
		HttpClientModule,
		ShareModule.forRoot(),
		LayoutsModule, // ccm
		PublicComModule.forRoot(),
		TranslateModule.forRoot({
			loader: {
				provide: TranslateLoader,
				useFactory: createTranslateHttpLoader,
				deps: [HttpClient]
			}
		}),
		NgxEchartsModule,
		BrowserAnimationsModule,
	],
	declarations: [AppComponent],
	providers: [
		{ provide: RouteReuseStrategy, useClass: SimpleReuseStrategy },
		{ provide: NZ_I18N, useValue: en_US },
		HttpInterceptorProviders
	],
	bootstrap: [AppComponent]
})
export class AppModule {
	constructor(
		matIconRegistry: MatIconRegistry,
		domSanitizer: DomSanitizer
	) {
		matIconRegistry.addSvgIcon('search',
			domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/search.svg'));
	}
}
