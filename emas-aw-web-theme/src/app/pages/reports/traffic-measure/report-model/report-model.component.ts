// import { Component, OnInit } from '@angular/core';
import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { ReportsService } from '../../reports.service';
import * as moment from 'moment';
import { CommonService, EquipType } from '../../../../service/common.service';
import { NzMessageService } from 'ng-zorro-antd/message';


@Component({
	selector: 'emas-report-model',
	templateUrl: './report-model.component.html',
	styleUrls: ['./report-model.component.css']
})
export class ReportModelComponent implements OnInit {

	@Input() isVisibleMiddle: boolean;
	@Input() reportValue: any;
	@Input() param: any;
	@Output() closeModal = new EventEmitter();
	pagination = true;
	// <td>{{ data.laneId }}</td>
	// <td>{{ data.dateTime | date:'dd/MM/yyyy hh:mm:ss a' }}</td>
	// <td>{{ data.confidenceLevel }}</td>
	// <td>{{ data.expwayDirection }}</td>
	// <td>{{ data.volume }}</td>
	// <td>{{ data.speed }}</td>
	// <td>{{ data.occupancy }}</td>
	// <td>{{ data.headway }}</td>
	// <td>{{ data.class1Count }}</td>
	// <td>{{ data.class2Count }}</td>
	// <td>{{ data.class3Count }}</td>
	// <td>{{ data.class4Count }}</td>
	// <td>{{ data.class5Count }}</td>
	renderHeader = [
		// {
		// 	name: 'Equip ID',
		// 	key: null,
		// 	value: 'equipId',
		// 	isChecked: true
		// },
		// {
		// 	name: 'Lane Type',
		// 	key: null,
		// 	value: 'laneType',
		// 	isChecked: true
		// },
		{
			name: 'Lane Id',
			key: null,
			value: 'laneId',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.laneId
		},
		{
			name: 'Date time',
			key: null,
			value: 'dateTime',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.dateTime
		},
		{
			name: 'km Marking',
			key: null,
			value: 'kmMarking',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.kmMarking
		},
		{
			name: 'Direction',
			key: null,
			value: 'measureType',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.expwayDirection
		},
		{
			name: 'Vol(veh/hr)',
			key: null,
			value: 'volume',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.volume
		},
		{
			name: 'Speed(km/hr)',
			key: null,
			value: 'speed',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.speed
		},
		{
			name: 'Occ(%)',
			key: null,
			value: 'occupancy',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.occupancy
		},
		{
			name: 'Headway',
			key: null,
			value: 'headway',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.headway
		},
		{
			name: 'Class1',
			key: null,
			value: 'class1Count',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.class1Count
		},
		{
			name: 'Class2',
			key: null,
			value: 'class2Count',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.class2Count
		},
		{
			name: 'Class3',
			key: null,
			value: 'class3Count',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.class3Count
		},
		{
			name: 'Class4',
			key: null,
			value: 'class4Count',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.class4Count
		},
		{
			name: 'Class5',
			key: null,
			value: 'class5Count',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.class5Count
		},
	];
	chartOption: any;
	chartOption1: any;
	listOfData = [];
	listOfData2 = [];
	laneTypeList = [];
	directionList = [];
	laneIdList = [];
	yValue = []; // 第一个折线图数据
	yValue2 = []; // 第二个折线图数据
	percentageList = [
		{ label: 100, value: 100 + '%' },
		{ label: 75, value: 75 + '%' },
		{ label: 50, value: 50 + '%' },
	];
	//   pageNum = 1;
	filterStaus = true;
	percentage = 100;
	searchValue: any;
	chartDisable = true;
	// 顶部分页
	pageNum = 1;
	totalPage: number;
	nextDisable = false;
	lastPageDisable = false;
	preDisable = true;
	firstPageDisable = true;
	isLoading = false;

	constructor(
		private reportsService: ReportsService,
		private commonService: CommonService,
		private message: NzMessageService
	) { }

	ngOnInit() {
		this.initData();
		this.getAllEquipConfig();
		this.getData();
	}
	async initData() {
		const allType = await this.commonService.allType;
		this.directionList = allType[EquipType.EXPWAY_DIRECTION];
		this.laneTypeList = allType[EquipType.TRAFFIC_MEASURE];
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

	refresh() {
		this.getData();
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
		this.reportsService.print([0, 1], 'measure-report');
	}

	//  print() {
	// 	const num = [0, 1];
	// 	num.forEach(item => {
	// 		document.getElementsByTagName('canvas')[item].setAttribute('id', 'echarts-canvas' + item);
	// 		this['canvas' + item] = document.getElementById('echarts-canvas' + item);
	// 		this['echartsImg' + item] = document.getElementById('echartsImg' + item);
	// 		this['dataURL' + item] = this['canvas' + item].toDataURL();
	// 		this['echartsImg' + item].src = this['dataURL' + item];
	// 	});
	// 	const printelemnt: any = document.getElementById('measure-report');
	// 	const printHtml = printelemnt.innerHTML;
	// 	window.document.body.innerHTML = printHtml;
	// 	setTimeout(() => {
	// 		window.print();
	// 		window.location.reload();
	// 	}, 500);
	//   }


	handleCancelMiddle(): void {
		// console.log('click cancel');
		this.isVisibleMiddle = true;
		this.closeModal.emit();
	}

	pageNumModelChange() {
		this.filterStaus = this.pageNum > 1 ? false : true;
	}

	getAllEquipConfig() {
		this.reportsService.getAllEquipConfig().subscribe(resData => {
			this.listOfData2 = resData;
		});
	}

	getData() {
		this.isLoading = true;
		const data = this.param;
		let pattern = /\+(\d{2}):(\d{2})$/;
		let patternT = /T/;
		// console.log(data);
		this.reportsService.getHistTrafficMeasure(data).subscribe((r) => {
			// console.log(r);
			const result = r['Body'].getHistTrafficMeasureResponse.histTrafficMeasureList;
			const resData = result && [...result].length ? result : [];
			// console.log(this.laneTypeList);
			// console.log(this.directionList);
			this.listOfData2.forEach(item => {
				resData.forEach(item2 => {
					if (item.equipId === item2.equipId) {
						item2.kmMarking = item.kmMarking;
						item2.dir = item.dir;
					}
				});
			});
			resData.forEach((item) => {
				this.laneTypeList.forEach((item2) => {
					if (item.laneType == item2.value) {
						item.laneTypeDescription = item2.description;
					}
				});
				this.directionList.forEach((item3) => {
					if (this.param.expressWay + '-' + item.dir == item3.value) {
						item.expwayDirection = item3.description;
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
			this.listOfData = resData;
			this.totalPage = Math.ceil(this.listOfData.length / 10) ? Math.ceil(this.listOfData.length / 10) : 1;
			if (this.totalPage === 1) {
				this.nextDisable = true;
				this.lastPageDisable = true;
			}
			this.listOfData.length ? this.message.create('success', `Data request successed`) : this.message.create('warning', `No Data`);
			this.param.withChart ? this.getChartData() : this.chartDisable = false; // 是否展示echarts折线图
			this.isLoading = false;
		});
	}

	getChartData() {
		const xAxisData = [];
		this.listOfData.forEach((item) => { // 获取折线条数和横纵标值
			const time = moment(item.dateTime).format('YYYY/M/DD HH:mm');
			this.laneIdList.push(item.laneId);
			xAxisData.push(time);
		});
		this.laneIdList = Array.from(new Set(this.laneIdList));
		// console.log(this.laneIdList);
		this.laneIdList.forEach((item, idx) => { // 获取展示的折线数据
			// tslint:disable-next-line: no-shadowed-variable
			const SpeedValue = [];
			const volValue = [];
			this.listOfData.forEach((item2) => {
				const speed = parseInt(item2.speed, 10);
				if (item2.laneId == item) {
					SpeedValue.push(speed);
					volValue.push(item2.volume);
				} else {
					SpeedValue.push(null); // 折线图断点
					volValue.push(null);
				}
			});
			this.yValue[idx] = {
				name: item,
				type: 'line',
				stack: '总量',
				data: SpeedValue
			};
			this.yValue2[idx] = {
				name: item,
				type: 'line',
				stack: '总量',
				data: volValue
			};
		});
		// console.log(this.yValue);
		// console.log(this.yValue2);
		this.chartOption = {
			title: {
				text: 'Detector/Speed & Lane ID',
				x: 'center',
				align: 'right'
			},
			color: ['lightBlue', 'yellow'],
			tooltip: {
				trigger: 'axis'
			},
			legend: {
				// orient: 'horizontal',
				left: 'center',
				y: '30px',
				data: this.laneIdList
			},
			grid: {
				left: '3%',
				right: '4%',
				bottom: '3%',
				containLabel: true
			},
			toolbox: {
				feature: {
					saveAsImage: {
						title: 'Save image'
					}
				},
				right: 20
			},
			xAxis: {
				type: 'category',
				boundaryGap: false,
				axisLine: { onZero: false },
				data: xAxisData.map(function (str) {
					return str.replace(' ', '\n');
				}),
				axisLabel: {
					rotate: 90, // 旋转角度
					interval: 0  // 设置X轴数据间隔几个显示一个，为0表示都显示
				}
			},
			yAxis: {
				type: 'value',
				name: 'Speed',
				position: 'left',
				nameLocation: 'center',
				nameGap: 20,
				nameTextStyle: {
					fontWeight: 'bold',
					fontSize: 16
				},
			},
			series: this.yValue
		};
		this.chartOption1 = {
			title: {
				text: 'Detector/Volume & Lane ID',
				x: 'center',
				align: 'right'
			},
			color: ['lightBlue', 'yellow'],
			tooltip: {
				trigger: 'axis'
			},
			legend: {
				// orient: 'horizontal',
				left: 'center',
				y: '30px',
				data: this.laneIdList,
			},
			grid: {
				left: '3%',
				right: '4%',
				bottom: '3%',
				containLabel: true
			},
			toolbox: {
				feature: {
					saveAsImage: {
						title: 'Save image'
					}
				},
				right: 20
			},
			xAxis: {
				type: 'category',
				boundaryGap: false,
				axisLine: { onZero: false },
				data: xAxisData.map(function (str) { // 时间换行处理
					return str.replace(' ', '\n');
				}),
				axisLabel: {
					rotate: 90, // 旋转角度
					interval: 0  // 设置X轴数据间隔几个显示一个，为0表示都显示
				},
			},
			yAxis: {
				type: 'value',
				name: 'Volume',
				position: 'left',
				nameLocation: 'center',
				nameGap: 40,
				nameTextStyle: {
					fontWeight: 'bold',
					fontSize: 16
				},
			},
			series: this.yValue2
		};
	}
	sizeModelChange(item): void { }
	submitForm(): void { }
}
