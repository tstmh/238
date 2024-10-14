import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CommonService, EquipType } from '../../../../service/common.service';
import { preserveWhitespacesDefault } from '@angular/compiler';
import { THIS_EXPR } from '@angular/compiler/src/output/output_ast';
import { ReportsService } from '../../reports.service';
import { NzMessageService } from 'ng-zorro-antd/message';

@Component({
	selector: 'emas-alert-report',
	templateUrl: './alert-report.component.html',
	styleUrls: ['./alert-report.component.css']
})
export class AlertReportComponent implements OnInit {
	@Input() isVisibleMiddle: boolean;
	@Input() reportValue: any;
	@Input() param: any;
	@Output() closeModal = new EventEmitter();
	data: any = '';
	tempAlert = [];
	alertType = '';
	direction = '';
	expway = '';
	alertList = [];
	dataTest = [];
	chartOption: any;
	pagination = true;
	// <td>{{ data.alertId }}</td>
	// <td>{{ data.equipConfigDto.equipId}}</td>
	// <td>{{ data.alertType }}</td>
	// <td>{{ data.expway }}</td>

	// <td>{{ data.equipConfigDto.kmMarking}}</td>
	// <td>{{data.direction}}</td>
	// <td>{{data.laneAffected}}</td>
	// <td>{{data.alertDate | date:'dd/MM/yyyy hh:mm:ss a'}}</td>
	// <td>{{data.ackDate | date:'dd/MM/yyyy hh:mm:ss a'}}</td>
	// <td>{{ data.endDate | date:'dd/MM/yyyy hh:mm:ss a' }}</td>
	// <td>{{data.status==1?'true':'false'}}</td>
	renderHeader = [
		{
			name: 'Alert ID',
			key: null,
			value: 'equipType',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.alertId
		},
		{
			name: 'Equip ID',
			key: null,
			value: 'equipID',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.equipConfigDto.equipId
		},
		{
			name: 'Alert Type',
			key: null,
			value: 'equipType',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.alertType
		},
		{
			name: 'Exp Way',
			key: null,
			value: 'expWay',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.expway
		},
		{
			name: 'Km Marking',
			key: null,
			value: 'kmMarking',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.equipConfigDto.kmMarking
		},
		{
			name: 'Direction',
			key: null,
			value: 'direction',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.direction
		},
		{
			name: 'Lane No',
			key: null,
			value: 'laneNo',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.laneAffected
		},
		{
			name: 'Alert Date',
			key: null,
			value: 'alertDate',
			isChecked: true,
			width: 30, // excel header for export
			get: (data) => data.alertDate
		},
		{
			name: 'Ack Date',
			key: null,
			value: 'ackDate',
			isChecked: true,
			width: 30, // excel header for export
			get: (data) => data.ackDate
		},
		{
			name: 'End Date',
			key: null,
			value: 'endDate',
			isChecked: true,
			width: 30, // excel header for export
			get: (data) => data.endDate
		},
		{
			name: 'Ack Status',
			key: null,
			value: 'ackStatus',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.status == 1 ? 'true' : 'false'
		},

	];
	listOfData: any = [
		// {equipType: this.reportValue ? this.reportValue.equipType : ''},
		// {equipID: this.reportValue ? this.reportValue.equipIdList[0] : ''},
	];
	percentageList = [
		{ label: 100, value: 100 + '%' },
		{ label: 75, value: 75 + '%' },
		{ label: 50, value: 50 + '%' },
	];
	// pageNum = 1;
	filterStaus = true;
	percentage = 100;
	searchValue: any;
	// 顶部分页
	pageNum = 1;
	totalPage: number;
	nextDisable = false;
	lastPageDisable = false;
	preDisable = true;
	firstPageDisable = true;
	isLoading = false;
	allType: {};
	constructor(private commonService: CommonService,
		private reportsService: ReportsService,
		private message: NzMessageService
	) { }

	ngOnInit() {
		this.initData();
	}
	async initData() {
		this.allType = await this.commonService.allType;
		const expway = this.allType[EquipType.EXPWAY_CODE];
		const listAlertList = this.allType[EquipType.ALERT_TYPE];
		listAlertList.forEach((item) => {
			const data = this.reportValue.alertType.findIndex((value) => value === item.value);
			if (data !== -1) {
				this.tempAlert.push(item.description)
			}
		});
		expway.forEach((item) => {
			if (item.value === this.reportValue.expWay) {
				this.reportValue.expWay = item.description;
				this.listOfData.forEach((itemA) => {
					itemA.expWay = item.description;
				});
			}
		});
		this.getAlertList();
	}
	// 顶部分页
	pageChange() {
		// console.log(this.pageNum);
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
	getAlertList() {
		this.isLoading = true;
		const data = this.param;
		let temp: any = {}
		let tempfalse: any = {}
		let pattern = /\+(\d{2}):(\d{2})$/;
		let patternT = /T/;
		const expway = this.allType[EquipType.EXPWAY_CODE];
		const listAlertList = this.allType[EquipType.ALERT_TYPE];
		const direction = this.allType[EquipType.EXPWAY_DIRECTION];
		this.reportsService.getHistTrafficAlert(data).subscribe((r) => {
			const res: any = r;
			const count = res.Body.getHistTrafficAlertResponse.histTrafficAlertList ? res.Body.getHistTrafficAlertResponse.histTrafficAlertList : [];
			this.listOfData = count;
			this.totalPage = Math.ceil(this.listOfData.length / 10);
			const xAxisData = [];
			if (this.totalPage === 0) {
				this.totalPage = 1;
			}
			this.listOfData.forEach((item) => {
				if (temp.alertType !== item.alertType) {
					temp.push({ alertType: item.alertType })
				}
				// for change the datetime format
				if (item.alertDate) {
					let matche = item.alertDate.match(pattern);
					if ( matche !== null ) {
						item.alertDate = item.alertDate.replace("+08:00", "");
					}
					let matcheT = item.alertDate.match(patternT);
					if ( matcheT !== null ) {
						item.alertDate = item.alertDate.replace("T", " ");
					}
				}
				if (item.ackDate) {
					let matche = item.ackDate.match(pattern);
					if ( matche !== null ) {
						item.ackDate = item.ackDate.replace("+08:00", "");
					}
					let matcheT = item.ackDate.match(patternT);
					if ( matcheT !== null ) {
						item.ackDate = item.ackDate.replace("T", " ");
					}
				}
				if (item.endDate) {
					let matche = item.endDate.match(pattern);
					if ( matche !== null ) {
						item.endDate = item.endDate.replace("+08:00", "");
					}
					let matcheT = item.endDate.match(patternT);
					if ( matcheT !== null ) {
						item.endDate = item.endDate.replace("T", " ");
					}
				}				
				
				expway.forEach((itemA) => {
					if (itemA.value === item.equipConfigDto.expwayCode) {
						item.expway = itemA.description;
					}
				})
				listAlertList.forEach((itemB) => {
					if (itemB.value === item.alertCode) {
						item.alertType = itemB.description
					}
				})

				direction.forEach((itemC) => {
					if (itemC.name === "expway_direction" && itemC.value === item.equipConfigDto.expwayCode + "-" + item.equipConfigDto.dir.toString()
					) {
						item.direction = itemC.description
					}
				})

				xAxisData.push(item.alertType);
				// const res = new Map();
				// return xAxisData.filter((a) => !res.has(a) && res.set(a, 1))
			})
			const tempTrueList = [];
			const tempFalselist = [];
			this.listOfData.forEach((item) => {
				if (item.status === '1') {
					tempTrueList.push(item);
				} else {
					tempFalselist.push(item);
				}
			});
			for (let i = 0, l = tempTrueList.length; i < l; i++) {
				const item = tempTrueList[i].alertType;
				temp[item] = (temp[item] + 1) || 1;
			}
			for (let i = 0, l = tempFalselist.length; i < l; i++) {
				const item = tempFalselist[i].alertType;
				tempfalse[item] = (tempfalse[item] + 1) || 1;
			}
			this.chartOption = {
				title: {
					text: 'Traffic Alert Rate(count)',
					x: 'center',
					align: 'right',
					// 	textStyle: {
					// 		color: '#cccccc'

					// }
				},
				color: ['#3398DB'],
				tooltip: {
					trigger: 'axis',
					axisPointer: {            // 坐标轴指示器，坐标轴触发有效
						type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
					}
				},
				legend: {
					y: '20px',
					data: ['true', 'false'],
					// textStyle: {
					// 	color: '#fff'
					// }
				},
				grid: {
					left: '3%',
					right: '4%',
					bottom: '3%',
					containLabel: true
				},
				xAxis: [
					{
						type: 'category',
						data: Object.keys(temp),
						axisTick: {
							alignWithLabel: true
						},
						// axisLabel: {
						// 	show: true,
						// 	textStyle: {
						// 		color: '#cccccc'
						// 	}
						// },
						// axisLine: {
						// 	lineStyle: {
						// 		type: 'solid',
						// 		color: '#cccccc',
						// 		width: '1'
						// 	}
						// }
					}
				],
				yAxis: [

					{
						name: 'Total Alert',
						position: 'left',
						nameLocation: 'center',
						right: '3%',
						type: 'value',
						// axisLabel: {
						// 	show: true,
						// 	textStyle: {
						// 		color: '#cccccc'
						// 	}
						// },
						// axisLine: {
						// 	lineStyle: {
						// 		type: 'solid',
						// 		color: '#cccccc',
						// 		width: '1'
						// 	}
						// }
					},
				],
				series: [
					{
						name: '',
						type: 'bar',
						stack: '',
						itemStyle: {
							normal: {
								barBorderColor: 'rgba(0,0,0,0)',
								color: 'rgba(0,0,0,0)'
							},
							emphasis: {
								barBorderColor: 'rgba(0,0,0,0)',
								color: 'rgba(0,0,0,0)'
							}
						},
						data: []
					},

					{
						name: 'true',
						type: 'bar',
						stack: 'true',
						barWidth: '0%',
						label: {
							normal: {
								show: true,
								position: 'top'
							}
						},
						data: Object.values(temp)
					},
					{
						name: 'false',
						type: 'bar',
						stack: 'false',
						itemStyle: {
							normal: {
								barBorderColor: 'red',
								color: 'red'
							},
						},
						label: {
							normal: {
								show: true,
								position: 'top'
							}
						},
						data: Object.values(tempfalse)
					},
				]
			}
			this.isLoading = false;
		});
		this.totalPage = Math.ceil(this.listOfData.length / 10) ? Math.ceil(this.listOfData.length / 10) : 1;
		if (this.totalPage === 1) {
			this.nextDisable = true;
			this.lastPageDisable = true;
		}
	}
	refresh(): void {
		this.getAlertList();
		this.pageNum = 1;
		this.preDisable = true;
		this.firstPageDisable = true;
		if (this.totalPage > 1) {
			this.nextDisable = false;
			this.lastPageDisable = false;
		}
		this.message.create(`success`, 'refresh successed');
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
	submitForm(): void { }
	reportPrint() {
		this.reportsService.print([0], 'alert-report');
	}

}
