import { Component, OnInit, TemplateRef, OnDestroy, AfterViewInit, ViewChild } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { TabRouterService } from './tab-router.service'

@Component({
	selector: 'tab-router',
	templateUrl: './tab-router.component.html',
	styleUrls: ['./tab-router.component.css']
})
export class TabRouterComponent implements OnInit, AfterViewInit {
	tabs = [];

	constructor(public tabSvr: TabRouterService) {
		this.tabs = tabSvr.tabs;
	}
	tabSelect(tab) {
		this.tabSvr.routerTo(tab);
	}
	closeTab(tab, i) {
		if (i === this.tabSvr.index && i < (this.tabs.length - 1)) {
			this.tabSvr.routerTo(this.tabs[i + 1]);
		}
		this.tabSvr.closeTab(tab);

	}
	ngOnInit() {
	}

	ngAfterViewInit() { }
}
