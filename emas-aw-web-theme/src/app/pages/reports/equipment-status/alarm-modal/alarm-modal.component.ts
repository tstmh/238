import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { EChartOption } from 'echarts';
import * as moment from 'moment';
import { ReportsService } from '../../reports.service';
import { NzMessageService } from 'ng-zorro-antd/message';
import { CommonService, EquipType } from 'src/app/service/common.service';

@Component({
	selector: 'emas-alarm-modal',
	templateUrl: './alarm-modal.component.html',
	styleUrls: ['./alarm-modal.component.css']
})
export class AlarmModalComponent implements OnInit {
	@Input() isVisibleMiddle: boolean;
	@Input() withChart: boolean;
	@Input() reportValue: any;
	@Input() listData: any;
	@Output() closeModal = new EventEmitter();
	option: EChartOption;
	pagination = true;
	renderHeader = [
		{
			name: 'Equip ID',
			key: null,
			value: 'equipID',
			isChecked: true,
			header: 'Equip ID', // excel header for export
			width: 20, // excel header for export
			get: (data) => data.equipStatusDto.equipId
		},
		{
			name: 'Exp Way',
			key: null,
			value: 'expWay',
			isChecked: true,
			header: 'Exp Way', // excel header for export
			width: 20, // excel header for export
			get: (data, reportValue) => reportValue.expWayDescription
		},
		{
			name: 'Km Marking',
			key: null,
			value: 'kmMarking',
			isChecked: true,
			header: 'Km Marking', // excel header for export
			width: 20, // excel header for export
			get: (data) => data.kmMarking
		},
		{
			name: 'Direction',
			key: null,
			value: 'direction',
			isChecked: true,
			header: 'Direction',
			width: 20,
			get: (data) => data.direction
		},
		{
			name: 'Status',
			key: null,
			value: 'status',
			isChecked: true,
			header: 'Status',
			width: 20,
			get: (data) => data.status === '2' ? 'Operational' : 'Not Operational'
		},
		{
			name: 'DateTime',
			key: null,
			value: 'dateTime',
			isChecked: true,
			header: 'DateTime',
			width: 30,
			get: (data) => data.dateTime
		},
	];
	listOfData = [];
	percentageList = [
		{ label: 100, value: 100 + '%' },
		{ label: 75, value: 75 + '%' },
		{ label: 50, value: 50 + '%' },
	];
	// pageNum = 1;
	filterStaus = true;
	percentage = 100;
	searchValue: any;
	xTime = [];
	yStatus = [];
	// topBar pagenation
	pageNum = 1;
	totalPage: number;
	nextDisable = false;
	lastPageDisable = false;
	preDisable = true;
	firstPageDisable = true;

	constructor(
		private reportsService: ReportsService,
		private commonService: CommonService,
		private message: NzMessageService
	) { }

	ngOnInit() {
		this.getData();

	}

	async getData() {
		let pattern = /\+(\d{2}):(\d{2})$/;
		let patternT = /T/;
		const allType = await this.commonService.allType;
		const directionList = allType[EquipType.EXPWAY_DIRECTION];
		this.reportsService.getAllEquipConfig().subscribe(resData => {

			this.listData.forEach(item => {
				for (const res of resData) {
					if (res.equipId === item.equipStatusDto.equipId) {
						item.kmMarking = res.kmMarking;
						item.direction = res.expwayCode + '-' + res.dir;
						break;
					}
				}
				for (const res of directionList) {
					if (item.direction === res.value) {
						item.direction = res.description;
						break;
					}
				}
				directionList.forEach(res => { // 获取direction字段
					if (item.direction === res.value) {
						item.direction = res.description;
					}
				});
				// for change the datetime format
				if (item.dateTime) {
					let matche = item.dateTime.match(pattern);
					if ( matche !== null ) {
						item.dateTime = item.dateTime.replace("+08:00", "");
					}
					let matcheT = item.dateTime.match(patternT);
					if ( matcheT !== null ) {
						item.dateTime = item.dateTime.replace("T", " ");
					}
				}
			});
			this.listOfData = this.listData.sort((a, b) => b.dateTime.localeCompare(a.dateTime));
			this.totalPage = Math.ceil(this.listOfData.length / 10) ? Math.ceil(this.listOfData.length / 10) : 1;
			if (this.totalPage === 1) {
				this.nextDisable = true;
				this.lastPageDisable = true;
			}
			this.getOption();
		});

	}

	// topBar pagenation
	pageChange() {
		if (this.pageNum > 1 && this.pageNum < this.totalPage) {
			this.firstPageDisable = false;
			this.lastPageDisable = false;
			this.preDisable = false;
			this.nextDisable = false;
		}
		if (this.pageNum === this.totalPage) {
			this.lastPageDisable = true;
			this.nextDisable = true;
			this.preDisable = false;
			this.firstPageDisable = false;
		}
		if (this.pageNum === 1) {
			this.lastPageDisable = false;
			this.nextDisable = false;
			this.preDisable = true;
			this.firstPageDisable = true;
		}
	}
	nextChange() {
		if (this.pageNum < this.totalPage) {
			this.pageNum++;
			this.preDisable = false;
			this.firstPageDisable = false;
			if (this.pageNum === this.totalPage) {
				this.nextDisable = true;
				this.lastPageDisable = true;
			}
		} else {
			this.nextDisable = true;
			this.lastPageDisable = true;
		}
	}
	preChange() {
		if (this.pageNum > 1) {
			this.pageNum--;
			this.nextDisable = false;
			this.lastPageDisable = false;
			if (this.pageNum === 1) {
				this.preDisable = true;
				this.firstPageDisable = true;
			}
		} else {
			this.preDisable = true;
			this.firstPageDisable = true;
		}
	}

	goLastPage() {
		this.pageNum = this.totalPage;
		this.lastPageDisable = true;
		this.preDisable = false;
		this.nextDisable = true;
		this.firstPageDisable = false;
	}
	goFirstPage() {
		this.pageNum = 1;
		this.firstPageDisable = true;
		this.nextDisable = false;
		this.preDisable = true;
		this.lastPageDisable = false;
	}
	refresh() {
		this.pageNum = 1;
		this.preDisable = true;
		this.firstPageDisable = true;
		if (this.totalPage > 1) {
			this.nextDisable = false;
			this.lastPageDisable = false;
		}
		this.message.create(`success`, 'refresh successed');
	}
	reportPrint() {
		this.pagination = false;  // show all data for print
		setTimeout(() => { this.finishPrint(); }, 100);

	}
	async finishPrint() {
		await this.reportsService.print([0], 'status-report');
		this.pagination = true;
	}
	download() {
		// const header = this.renderHeader.map(data => {
		// 	return data.name;
		// });
		const { startDate, endDate, equipType, expWayDescription, equipID } = this.reportValue;
		const header = [['Start Date:', moment(startDate).format('YYYY/MM/DD HH:mm a'), 'End Date', moment(endDate).format('YYYY/MM/DD HH:mm a')],
		['Equip Type', equipType, 'Exp Way', expWayDescription],
		['Equip ID', equipID]];
		const body = this.listOfData.map(data => {
			return [data.equipStatusDto.equipId,
			this.reportValue.expWayDescription,
			data.kmMarking,
			data.direction,
			data.status === '2' ? 'Operational' : 'Not Operational',
			moment(data.dateTime).format('YYYY/MM/DD HH:mm a')
			];
		});
		this.reportsService.toEcxel(header, this.renderHeader, body, 'Equipment Status');
	}
	getOption() {
		this.option = {
			title: {
				text: 'Equipment Status Vs Data Time'
			},
			xAxis: {
				axisLabel: {
					rotate: 90,
				},
				type: 'category',
				name: 'dateTime',
				boundaryGap: false,
				data: this.xTime
			},
			yAxis: {
				type: 'value',
				name: 'Status'
			},
			legend: {
				data: ['status']
			},
			grid: {
				left: '3%',
				right: '4%',
				bottom: '3%',
				containLabel: true
			},
			series: [{
				data: this.yStatus,
				type: 'line',
				areaStyle: {}
			}]
		};
		this.listData.forEach(item => {
			const newTime = moment(item.dateTime).format('YYYY-MM-dd HH:mm');
			this.xTime.push(newTime);
			this.yStatus.push(item.status);
		});
	}

	submitForm() {

	}
	handleCancelMiddle(): void {
		this.isVisibleMiddle = true;
		this.closeModal.emit();
	}

	pageNumModelChange() {
		this.filterStaus = this.pageNum > 1 ? false : true;
	}

	sizeModelChange() {

	}

}
