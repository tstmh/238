import { Component, OnInit, OnDestroy } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import { DialogService } from 'src/app/share/dialog';
import { ToolsService } from 'src/app/share/service';
import { AppMenu } from './interface';
import { SystemService } from 'src/app/service/system.service';


@Component({
	selector: 'sj-home',
	templateUrl: './home.component.html',
	styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy {


	constructor(public system: SystemService) {
	}



	ngOnInit() {


	}

	ngOnDestroy() {

	}
}
