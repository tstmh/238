import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { ReportsService } from '../../reports.service';
import { CommonService, EquipType } from '../../../../service/common.service';
import { NzMessageService } from 'ng-zorro-antd/message';

@Component({
	selector: 'emas-alarm-report',
	templateUrl: './alarm-report.component.html',
	styleUrls: ['./alarm-report.component.css']
})
export class AlarmReportComponent implements OnInit {
	@Input() isVisibleMiddle: boolean;
	@Input() reportValue: any;
	@Input() AlarmReportParam: any;
	@Output() closeModal = new EventEmitter();
	pagination = true;
	renderHeader = [
		{
			name: 'Alarm ID',
			key: null,
			value: 'alarmID',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.alarmId
		},
		{
			name: 'Equip ID',
			key: null,
			value: 'equipID',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.equipId
		},
		{
			name: 'Exp Way',
			key: null,
			value: 'expWay',
			isChecked: true,
			width: 20, // excel header for export
			get: (data, reportValue) => reportValue.expWayDescription
		},
		{
			name: 'Km Marking',
			key: null,
			value: 'kmMarking',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.kmMarking
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
			name: 'Start_DateTime',
			key: null,
			value: 'startDateTime',
			isChecked: true,
			width: 30, // excel header for export
			get: (data) => data.startDate
		},
		{
			name: 'End_DateTime',
			key: null,
			value: 'endDateTime',
			isChecked: true,
			width: 30, // excel header for export
			get: (data) => data.endDate
		},
		{
			name: 'Ack_DateTime',
			key: null,
			value: 'ackDateTime',
			isChecked: true,
			width: 30, // excel header for export
			get: (data) => data.ackDate
		},
		{
			name: 'Alarm Desc',
			key: null,
			value: 'alarmDesc',
			isChecked: true,
			width: 30, // excel header for export
			get: (data) => data.alarmDescription
		},
	];
	listOfData = [];
	listOfData2 = [];
	directionList = [];
	alarmDescriptionList = [];
	chartOption: any;
	percentageList = [
		{ label: 100, value: 100 + '%' },
		{ label: 75, value: 75 + '%' },
		{ label: 50, value: 50 + '%' },
	];
	expWayArry = [
		{ description: 'AYE', value: '74' },
		{ description: 'BKE', value: '72' },
		{ description: 'CTE & CTE Tunnel', value: '71' },
		{ description: 'ECP', value: '73' },
		{ description: 'KJE', value: '78' },
		{ description: 'PIE East', value: '75' },
		{ description: 'PIE West', value: '76' },
		{ description: 'SLE', value: '79' },
		{ description: 'TPE', value: '77' }
	];
	pageNum = 1;
	totalPage: number;
	nextDisable = false;
	lastPageDisable = false;
	preDisable = true;
	firstPageDisable = true;
	percentage = 100;
	searchValue: any;
	nowDate = new Date;
	isLoading = false;

	constructor(
		private reportsService: ReportsService,
		private commonService: CommonService,
		private message: NzMessageService
	) { }

	ngOnInit() {
		this.initData();
		this.getExpwayDesciption();
		this.getAllEquipConfig();
		this.getData();
	}
	async initData() {
		const allType = await this.commonService.allType;
		this.directionList = allType[EquipType.EXPWAY_DIRECTION];
	}

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

	reportPrint() {
		this.reportsService.print([0], 'alarm-report');
	}
	//   print() {
	// 	document.getElementsByTagName('canvas')[0].setAttribute('id', 'echarts-canvas');
	// 	const canvas: any = document.getElementById('echarts-canvas');
	// 	const echartsImg: any = document.getElementById('echartsImg');
	// 	const dataUrl = canvas.toDataURL(); // canvas base64码
	// 	echartsImg.src = dataUrl; // 将canvas转化成图片打印
	// 	const printelemnt: any = document.getElementById('alarm-report');
	// 	const printHtml = printelemnt.innerHTML;
	// 	window.document.body.innerHTML = printHtml;
	// 	setTimeout(() => {
	// 		window.print();
	// 		window.location.reload();
	// 	}, 500);
	//   }

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

	getAllEquipConfig() {
		this.reportsService.getAllEquipConfig().subscribe(resData => {
			this.listOfData2 = resData;
		});
	}

	getData() {
		this.isLoading = true;
		let pattern = /\+(\d{2}):(\d{2})$/;
		let patternT = /T/;
		this.reportsService.getHistTechAlarm(this.AlarmReportParam).subscribe((r) => {
			// console.log(r);
			const res: any = r;
			const totalAlarmDesc = [];
			const resData = res.Body.getHistTechAlarmResponse.histTechAlarmList ? res.Body.getHistTechAlarmResponse.histTechAlarmList : [];
			this.listOfData2.forEach(item => {
				resData.forEach(item2 => {
					if (item.equipId === item2.equipId) {
						item2.kmMarking = item.kmMarking;
						item2.dir = item.dir;
					}
				});
			});
			resData.forEach(item => {
				this.directionList.forEach(item2 => { // 获取direction字段
					if (this.reportValue.expWay + '-' + item.dir === item2.value) {
						item.direction = item2.description;
					}
				});
				if (item.alarmDescription) {
					totalAlarmDesc.push(item.alarmDescription);
				}
				// for change the datetime format
				if (item.startDate) {
					let matche = item.startDate.match(pattern);
					if ( matche !== null ) {
						item.startDate = item.startDate.replace("+08:00", "");
					}
					let matcheT = item.startDate.match(patternT);
					if ( matcheT !== null ) {
						item.startDate = item.startDate.replace("T", " ");
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
			});
			this.listOfData = resData;
			this.totalPage = Math.ceil(this.listOfData.length / 10) ? Math.ceil(this.listOfData.length / 10) : 1;
			if (this.totalPage === 1) {
				this.nextDisable = true;
				this.lastPageDisable = true;
			}
			this.alarmDescriptionList = totalAlarmDesc;
			this.getChartData();
			this.isLoading = false;
		});
	}

	getChartData() {
		const totalAlarm = [];
		const alarmDescList = Array.from(new Set(this.alarmDescriptionList));
		alarmDescList.forEach((item, i) => {
			totalAlarm[i] = 0;
			this.alarmDescriptionList.forEach(item2 => {
				if (item === item2) {
					totalAlarm[i]++;
				}
			});
		});
		this.chartOption = {
			title: {
				text: 'Technical Alarm Desc Rate(count)',
				x: 'center',
				align: 'right'
			},
			color: ['#3398DB'],
			tooltip: {
				trigger: 'axis',
				axisPointer: {            // 坐标轴指示器，坐标轴触发有效
					type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
				}
			},
			toolbox: {
				feature: {
					saveAsImage: {
						title: 'Save image'
					}
				},
				right: 20

			},
			legend: {
				y: '20px',
				data: ['Alarm Desc']
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
					name: 'Alarm Desc',
					position: 'bottom',
					nameLocation: 'center',
					nameGap: 120,
					nameTextStyle: {
						fontWeight: 'bold',
						fontSize: 16
					},
					data: alarmDescList,
					axisTick: {
						alignWithLabel: true
					},
					axisLabel: {
					// 	rotate: 90, // 旋转角度
						interval: 0  // 设置X轴数据间隔几个显示一个，为0表示都显示
					}
				}
			],
			yAxis: {
				name: 'Total Alarm',
				position: 'left',
				nameLocation: 'center',
				nameGap: 20,
				nameTextStyle: {
					fontWeight: 'bold',
					fontSize: 16
				},
				type: 'value'
			},
			series: [
				{
					name: 'Alarm Desc',
					type: 'bar',
					barWidth: '60%',
					label: {
						normal: {
							show: true,
							position: 'top'
						}
					},
					data: totalAlarm
				}
			]
		};
	}

	getExpwayDesciption() {
		this.expWayArry.forEach(item => {
			if (this.reportValue.expWay === item.value) {
				this.reportValue.expWayDescription = item.description;
			}
		});
	}
	handleCancelMiddle(): void {
		this.isVisibleMiddle = true;
		this.closeModal.emit();
	}

	pageNumModelChange() {
		this.nextDisable = this.pageNum > 1 ? false : true;
	}

	sizeModelChange() {

	}
	submitForm(): void { }

}
