import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BullySubjectService } from 'src/app/share/service/bully-subject.service';
import { MenuItem } from '../layouts.component'
import { from } from 'rxjs';
// import { SYSTEM_EVENT, MESSAGE_CHANNEL } from 'app/app.constants';
import { SimpleReuseStrategy } from 'src/app/service/simpleReuseStrategy'
@Injectable({
	providedIn: 'root'
})

export class TabRouterService {
	tabs: MenuItem[] = [];
	index = 0;
	constructor(
		private router: Router,
	) {
	}

	actionTab(menu) {
		if (!menu) { return; }
		menu.displayTitle = menu.title;

		if (menu.route === '/emas/reports/equipment-status') {
			menu.displayTitle = 'Equipment Status Report';
		}
		if (menu.route === '/emas/reports/traffic-measure') {
			menu.displayTitle = 'Traffic Measure Report';
		}
		if (menu.route === '/emas/reports/traffic-alert') {
			menu.displayTitle = 'Traffic Alert Report';
		}
		if (menu.route === '/emas/reports/technical-alarm') {
			menu.displayTitle = 'Technical Alarm Report';
			// menu.title = 'Technical Alarm Report'
		}
		if (menu.route === '/emas/reports/alert-ai') {
			menu.displayTitle = 'Alert AI Report';
		}
		for (const i in this.tabs) {
			if (this.tabs[i].route === menu.route) {
				this.index = Number.parseInt(i);
				return;
			}
		}
		this.tabs.push(menu);
		this.index = this.tabs.length - 1;


	}

	closeTab(menu) {
		// 最后一个不允许关闭
		if (1 === this.tabs.length) return;


		this.tabs.splice(this.tabs.indexOf(menu), 1);
		// 删除复用
		setTimeout(e => SimpleReuseStrategy.deleteRouteSnapshot(menu.route), 1000);
	}

	routerTo(menu) {
		this.router.navigateByUrl(menu.route).finally();
	}


	closeAll() {
		this.tabs = [];
		this.index = 0;
		SimpleReuseStrategy.deleteRouteSnapshot();
	}




}
