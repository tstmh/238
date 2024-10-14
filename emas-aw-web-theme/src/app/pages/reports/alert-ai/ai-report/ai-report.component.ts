import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CommonService, EquipType } from '../../../../service/common.service';
import { ReportsService } from '../../reports.service';
import { NzMessageService } from 'ng-zorro-antd/message';

@Component({
  selector: 'emas-ai-report',
  templateUrl: './ai-report.component.html',
  styleUrls: ['./ai-report.component.css']
})
export class AiReportComponent implements OnInit {
  @Input() isVisibleMiddle: boolean;
	@Input() reportValue: any;
	@Input() param: any;
	@Output() closeModal = new EventEmitter();
	data: any = '';
	tempAlert = [];
	resultType = '';
	direction = '';
	expway = '';
	alertList = [];
	dataTest = [];
	chartOption: any;
	pagination = true;
  	renderHeader = [
		{
			name: 'Equip ID',
			key: null,
			value: 'equipID',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.equipConfigDto.equipId
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
			name: 'Result Type',
			key: null,
			value: 'outputResult',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.outputResult
		},
		{
			name: 'Output Reason',
			key: null,
			value: 'outputReason',
			isChecked: true,
			width: 30, // excel header for export
			get: (data) => data.outputReason
		},
		{
			name: 'Output Time',
			key: null,
			value: 'outputTime',
			isChecked: true,
			width: 30, // excel header for export
			get: (data) => data.outputTime
		},
		{
			name: 'AI Time',
			key: null,
			value: 'aiTime',
			isChecked: true,
			width: 30, // excel header for export
			get: (data) => data.aiTime
		},
		{
			name: 'Fovshift Value',
			key: null,
			value: 'fovshiftvalue',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.fovshiftvalue
		},
		{
			name: 'Sent ITPT',
			key: null,
			value: 'sent',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.sent == '1' ? 'Sent' : 'Not Sent'
		},
		{
			name: 'Video/Image URL',
			key: null,
			value: 'aiUrl',
			isChecked: true,
			width: 20, // excel header for export
			get: (data) => data.aiUrl
		}
	];
	listOfData: any = [
	];
	percentageList = [
		{ label: 100, value: 100 + '%' },
		{ label: 75, value: 75 + '%' },
		{ label: 50, value: 50 + '%' },
	];
	filterStaus = true;
	percentage = 100;
	searchValue: any;
	pageNum = 1;
	totalPage: number;
	nextDisable = false;
	lastPageDisable = false;
	preDisable = true;
	firstPageDisable = true;
	allType: {};
	isLoading = false;

  constructor(
    private commonService: CommonService,
    private reportsService: ReportsService,
		private message: NzMessageService
  ) { }

  ngOnInit(): void {
	this.getType();
    this.initData();
  }
  async initData() {
		this.allType = await this.commonService.allType;
		const expway = this.allType[EquipType.EXPWAY_CODE];
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
	getType() {
		let tempData = this.param.resultType;
		this.tempAlert = tempData;
		// console.log('tempData:',tempData);
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
		let temp: any = {};
		let temptrue: any = {};
		let tempfalse: any = {};
		let typeList = [];
		if (data.resultType == "all") {
			temp = {'True' : 'True', 'False' : 'False'};
		} else if (data.resultType == "true") {
			temp = {'True' : data.resultType};
		} else {
			temp = {'False' : data.resultType};
		}
		typeList = Object.keys(temp);
		// console.log('typeList:',typeList);
		const expway = this.allType[EquipType.EXPWAY_CODE];
		// const listAlertList = this.allType[EquipType.ALERT_TYPE];
		const direction = this.allType[EquipType.EXPWAY_DIRECTION];
		this.reportsService.getTrafficAlertResult(data).subscribe((r) => {
			// console.log('r:',r);
			const res: any = r;
			if (!res.Body.Fault) {
				if (data.AIType == 'video') {
					const count = res.Body.getTrafficAlertResultVideoResponse.trafficAlertResultVideoList ? res.Body.getTrafficAlertResultVideoResponse.trafficAlertResultVideoList : [];
					this.listOfData = count;
				} else {
					const count = res.Body.getTrafficAlertResultImageResponse.trafficAlertResultImageList ? res.Body.getTrafficAlertResultImageResponse.trafficAlertResultImageList : [];
					this.listOfData = count;
				}
				this.totalPage = Math.ceil(this.listOfData.length / 10);
				const xAxisData = [];
				if (this.totalPage === 0) {
					this.totalPage = 1;
				}
				// console.log('data:',this.listOfData)
				this.listOfData.forEach((item) => {
					if (data.AIType == 'video') {
						item['aiUrl'] = item.videoUrl;
						item['fovshiftvalue'] = 'NA';
					} else {
						item['aiUrl'] = item.imageUrl;
						item['outputResult'] = item.outputResult === '1' ? 'True' : 'False';
					}
					item['sentType'] = item.sent === '1' ? 'Sent' : 'Not Sent';
					expway.forEach((itemA) => {
						if (itemA.value === item.equipConfigDto.expwayCode) {
							item.expway = itemA.description;
						}
					});
					direction.forEach((itemC) => {
						if (itemC.name === "expway_direction" && itemC.value === item.equipConfigDto.expwayCode + "-" + item.equipConfigDto.dir.toString()
						) {
							item.direction = itemC.description
						}
					});
	
					xAxisData.push(item.outputReason);
				});
				const tempTrueList = [];
				const tempFalselist = [];
				this.listOfData.forEach((item) => {
					if (item.sentType === 'Sent') {
						tempTrueList.push(item);
					} else {
						tempFalselist.push(item);
					}
				});
				typeList.forEach((ite) => {
					temptrue[ite] = 0;
					tempfalse[ite] = 0;
				});
				// console.log('tempTrueList:',tempTrueList)
				for (let i = 0, l = tempTrueList.length; i < l; i++) {
					const item = tempTrueList[i].outputResult;
					temptrue[item] = (temptrue[item] + 1) || 1;
				}			
				// console.log('tempFalselist:',tempFalselist)
				for (let i = 0, l = tempFalselist.length; i < l; i++) {
					const item = tempFalselist[i].outputResult;
					tempfalse[item] = (tempfalse[item] + 1) || 1;
				}			
				// console.log('temptrue:',temptrue)
				// console.log('tempfalse:',tempfalse)
				this.chartOption = {
					title: {
						text: 'Traffic Alert Result Rate(count)',
						x: 'center',
						align: 'right',
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
						data: ['true', 'false']
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
							}
						}
					],
					yAxis: [
						{
							name: 'Total Alert',
							position: 'left',
							nameLocation: 'center',
							right: '3%',
							type: 'value'
						}
					],
					series: [
						{
							name: 'sent',
							type: 'bar',
							stack: 'true',
							barWidth: '0%',
							label: {
								normal: {
									show: true,
									position: 'top'
								}
							},
							data: Object.values(temptrue)
						},
						{
							name: 'not sent',
							type: 'bar',
							stack: 'false',
							itemStyle: {
								normal: {
									barBorderColor: 'red',
									color: 'red'
								}
							},
							label: {
								normal: {
									show: true,
									position: 'top'
								}
							},
							data: Object.values(tempfalse)
						}
					]
				}
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
