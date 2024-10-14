import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import * as moment from 'moment';
import { NzMessageService } from 'ng-zorro-antd/message';
import { ReportsService } from '../reports.service';

@Component({
	selector: 'export-btn',
	templateUrl: './export-btn.component.html',
	styleUrls: ['./export-btn.component.css']
})
export class ExportBtnComponent implements OnInit {

	@Input() renderHeader: any;
	@Input() reportValue: any;
	@Input() listOfData: any;
	@Input() reportId: any;
	@Output() changePagination = new EventEmitter();
	pagination = true;


	constructor(
		private reportsService: ReportsService,
		private message: NzMessageService
	) { }

	ngOnInit() {

	}



	reportPrint(savePDF?) {
		this.changePagination.emit(false); // show all data for print
		setTimeout(() => { this.finishPrint(savePDF); }, 100);

	}
	async finishPrint(savePDF) {
		await this.reportsService.print(null, this.reportId, savePDF);
		this.changePagination.emit(true);
	}
	// toWord() {
	// 	this.changePagination.emit(false); // show all data for print
	// 	setTimeout(() => { this.finishToWord() }, 100);
	// }
	// async finishToWord() {
	// 	// await this.reportsService.toWord(this.reportId);
	// 	this.changePagination.emit(true);
	// }
	download(isWord?) {
		const a2Str = (arr) => arr instanceof Array ? arr.toString() : arr;
		const mKey = (key) => key ?
			(key = key.replace(/([A-Z])/g, ' $1'), key = key.substr(0, 1).toUpperCase() + key.substr(1))
			: key;

		const reportParam = this.reportValue;
		let index = 0;
		const exportShowParam = [];
		for (const [key, value] of Object.entries(this.reportValue)) {
			if (key === 'withChart') {
				continue;
			}
			if (index % 2 === 0) {
				exportShowParam.push([mKey(key), a2Str(value), '']);
			} else {
				exportShowParam[exportShowParam.length - 1].push(mKey(key));
				exportShowParam[exportShowParam.length - 1].push(a2Str(value));
			}
			index++;
		}
		const body = this.listOfData.map(data => {
			return this.renderHeader.map(render => render.get(data, reportParam));
		});
		if (isWord) {
			this.reportsService.toWord(exportShowParam, this.renderHeader, body, this.reportId);
		} else {
			this.reportsService.toEcxel(exportShowParam, this.renderHeader, body, this.reportId);
		}
	}

}
