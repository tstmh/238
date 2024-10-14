import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { CommonService, EquipType } from '../../../service/common.service';
import { EventsService } from '../events.service';
import { NzMessageService } from 'ng-zorro-antd';
import * as _ from 'lodash';
declare const Print: any;
@Component({
	// tslint:disable-next-line:component-selector
	selector: 'sj-field-equip',
	templateUrl: './field-equip.component.html',
	styleUrls: ['./field-equip.component.css']
})
export class FieldEquipComponent implements OnInit {

	constructor(
		private fb: FormBuilder,
		private commonService: CommonService,
		private eventsService: EventsService,
		private message: NzMessageService,
	) { }
	renderHeader = [
		{
			name: 'Equip ID',
			key: null,
			value: 'equipId',
			isChecked: true
		},
		{
			name: 'StatusCode',
			key: null,
			value: 'statusCode',
			isChecked: true
		},
		{
			name: 'Status',
			key: null,
			value: 'status',
			isChecked: true
		},
		{
			name: 'Time',
			key: null,
			value: 'time',
			isChecked: true
		}
	];

	listOfData = [];
	wheelFlag = false;
	validateForm: FormGroup;
	controlArray: any[] = [];
	subSystem = '';
	expWay = '';
	equipTypes = '';
	isCollapse = true;
	listSubSystem = [];
	listExpWay = [];
	imgUrl = '';
	listEquipType = [];
	expwayCodeList = [];
	sortName: string | null = null;
	sortValue: string | null = null;
	listOfDisplayData = [...this.listOfData];
	divLeft: Number = null;
	divTop: Number = null;
	ngOnInit() {
		this.initForm();
		this.getAllData();
		const div = document.getElementById('pic');
		this.divLeft = div.offsetLeft;
		this.divTop = div.offsetTop;
		// console.log(div.offsetLeft, div.offsetTop);
	}
	initForm() {
		this.validateForm = this.fb.group({
			subSystem: [{ value: null, disabled: false }],
			location: [{ value: null, disabled: false }],
			expWay: [{ value: null, disabled: false }],
			equipType: [{ value: null, disabled: false }]
		});
	}
	submitForm(): void {
		// console.log(this.validateForm);
	}

	// 进入页面是获取一个默认数据列表
	getFirstData(a, b, c) {
		const queryData = {
			equipType: a,
			expwayCode: b,
			subsystem: c
		};
		this.eventsService.getEquipStatusByEquipTypeAndExpwayCode(queryData).subscribe((r) => {
			const res: any = r;
			const resData = res.Body.getEquipStatusByEquipTypeAndExpwayCodeResponse.equipStatusList;
			if (resData) {
				this.listOfData = resData;
			} else {
				this.message.create('error', `No Data`);
				this.listOfData = [];
			}
			this.listOfDisplayData = [...this.listOfData];
		});
	}
	// 获取Subsystem和Exp way下拉框数据
	async getAllData() {
		if (this.listSubSystem) {
			this.subSystem = 'dcss';
		}
		// 页面初始化时获取equipType
		this.eventsService.getEquipTypeBySubSytem(this.subSystem).subscribe((r) => {
			const res: any = r;
			const resData = res.Body.getEquipTypeBySubSytemResponse.commonTypeConfigList;
			if (resData) {
				if ((resData instanceof Array)) { // 当后台返回数据为一个时类型为object，多个时为array
					this.listEquipType = [{ label: 'All', value: 0 }];
					resData.forEach((item) => {
						this.listEquipType.push({ label: item.value, value: item.value });
					});
				} else {
					this.listEquipType = [{ label: 'All', value: 0 }];
					this.listEquipType.push({ label: resData.value, value: resData.value });
				}
				// 给equipType下拉框赋值
				this.equipTypes = this.listEquipType[1].label;
			}
		});
		const allType = await this.commonService.allType;

		this.expwayCodeList = allType[EquipType.EXPWAY_CODE].sort((a, b) => a.description.localeCompare(b.description));
		this.expwayCodeList.forEach((item) => {
			this.listExpWay.push({ label: item.description, value: item.value });
		});
		// 给expWay下拉框赋值
		this.validateForm.get('expWay').setValue(this.listExpWay[0].value);
		this.setImgUrl();
		this.commonService.getEquipConfig$().subscribe((equipConfig) => {
			const subSysSet = new Set();
			equipConfig.forEach(r => r.propertyCode === '0' && subSysSet.add(r.subSystemId));
			this.listSubSystem = Array.from(subSysSet).sort();
			this.getFirstData(this.equipTypes, this.expWay, this.subSystem);

		});

	}

	// 根据Subsystem的改变来获取equipType
	ngModelChange() {
		// subSystem改变时改变图片
		const subSystemValue = this.validateForm.value.subSystem;
		this.setImgUrl();
		// this.listEquipType = [{ label: 'All', value: 0 }];
		this.eventsService.getEquipTypeBySubSytem(subSystemValue).subscribe((r) => {
			const res: any = r;
			const resData = res.Body.getEquipTypeBySubSytemResponse.commonTypeConfigList;
			if (resData) {
				if ((resData instanceof Array)) {
					this.listEquipType = [{ label: 'All', value: 0 }]; // 当后台返回数据为一个时类型为object，多个时为array
					resData.forEach((item) => {
						this.listEquipType.push({ label: item.value, value: item.value });
					});
				} else {
					this.listEquipType = [{ label: 'All', value: 0 }];
					this.listEquipType.push({ label: resData.value, value: resData.value });
				}
				this.equipTypes = this.listEquipType[0].label;
			}
		});
	}
	setImgUrl() {
		const { subSystem, expWay } = this.validateForm.value;
		this.imgUrl = this.eventsService.covertToImage(subSystem, expWay);
	}
	search() {
		const subSystemValue = this.validateForm.value.subSystem;
		const expWay = this.validateForm.value.expWay;
		const equipType = this.validateForm.value.equipType;
		if (equipType === 'All') {
			const queryData1 = {
				expwayCode: expWay,
				subsystem: subSystemValue
			};
			this.eventsService.getEquipStatusByEquipTypeAndExpwayCode(queryData1).subscribe((r) => {
				const res: any = r;
				const resData = res.Body.getEquipStatusByEquipTypeAndExpwayCodeResponse.equipStatusList;
				if (resData) {
					this.listOfData = resData;
				} else {
					this.message.create('error', `No Data`);
					this.listOfData = [];
				}
				this.listOfDisplayData = [...this.listOfData];
			});
		} else {
			const queryData = {
				equipType: equipType,
				expwayCode: expWay,
				subsystem: subSystemValue
			};
			this.eventsService.getEquipStatusByEquipTypeAndExpwayCode(queryData).subscribe((r) => {
				const res: any = r;
				const resData = res.Body.getEquipStatusByEquipTypeAndExpwayCodeResponse.equipStatusList;
				if (resData) {
					this.listOfData = resData;
				} else {
					this.message.create('error', `No Data`);
					this.listOfData = [];
				}
				this.listOfDisplayData = [...this.listOfData];
			});
		}
	}
	print() {
		const bdhtml = window.document.body.innerHTML;
		const newWindow = window.open(`${this.imgUrl}`, 'newWindow');
		// 定义打印区域起始字符，根据这个截取网页局部内容
		const sprnstr = '<!--startprint-->'; // 打印区域开始的标记
		const eprnstr = '<!--endprint-->';   // 打印区域结束的标记
		let prnhtml = bdhtml.substr(bdhtml.indexOf(sprnstr) + 17);
		prnhtml = prnhtml.substring(0, prnhtml.indexOf(eprnstr));
		// 还原网页内容
		newWindow.document.body.innerHTML = prnhtml;
		// 开始打印
		newWindow.print();
	}

	// sort data
	sort(sort: { key: string; value: string }): void {
		this.sortName = sort.key;
		this.sortValue = sort.value;
		this.sortData();
	}
	sortData(): void {
		const data = this.listOfData;
		if (this.sortName && this.sortValue) {
			this.listOfDisplayData = [];
			const arr = data.sort((a, b) =>
				this.sortValue === 'ascend'
					// tslint:disable-next-line:no-non-null-assertion
					? a[this.sortName!] > b[this.sortName!]
						? 1
						: -1
					// tslint:disable-next-line:no-non-null-assertion
					: b[this.sortName!] > a[this.sortName!]
						? 1
						: -1
			);
			this.listOfDisplayData = [...arr];
		} else {
			this.listOfDisplayData = [...data];
		}
	}
	over() {
		const mask = document.getElementById('mask') as HTMLElement;
		mask.style.display = 'block';
	}
	out() {
		const mask = document.getElementById('mask') as HTMLElement;
		mask.style.display = 'none';
	}
	move(e) {
		e = e || event;
		const img = document.getElementById('img');
		const mask = document.getElementById('mask');
		const div = document.getElementById('pic');
		// 鼠标的横纵坐标
		const x = e.clientX;
		const y = e.clientY;
		// 放大镜的宽高
		const maskHeight = mask.offsetHeight;
		const maskWidth = mask.offsetWidth;
		// 外层div的宽高
		const divHeight = img.offsetHeight;
		const divWidth = img.offsetWidth;
		if (x <= divWidth && y <= divHeight && x >= -divWidth && y >= -divHeight) {
			// 放大镜的上边框和左边框距离外层div的距离
			mask.style.left = e.pageX - $('#pic').offset().left - 80 + div.scrollLeft + 'px';
			mask.style.top = e.pageY - $('#pic').offset().top - 80 + div.scrollTop + 'px';
			mask.style.backgroundSize = `${img.offsetWidth}px ${img.offsetHeight}px`;
			const bl = 1.5 * mask.offsetLeft;
			const bt = 1.5 * mask.offsetTop;
			mask.style.backgroundPosition = '-' + 4.75 * (bl + maskWidth / 2) + 'px ' + '-' + 4.75 * (bt + maskHeight / 2) + 'px';
		}
		mask.style.backgroundImage = `url(\'${this.imgUrl}\')`;
	}

	wheel(e) {
		e = e || event;
		if (!this.wheelFlag) {
			this.wheelFlag = true;
			setTimeout(() => {
				this.wheelFlag = false;
				const div = document.getElementById('pic') as HTMLElement;
				const mask = document.getElementById('mask');
				// 放大镜的上边框和左边框距离外层div的距离
				mask.style.left = e.pageX - $('#pic').offset().left - 80 + div.scrollLeft + 'px';
				mask.style.top = e.pageY - $('#pic').offset().top - 80 + div.scrollTop + 'px';
				const bl = 1.5 * mask.offsetLeft;
				const bt = 1.5 * mask.offsetTop;
				const maskHeight = mask.offsetHeight;
				const maskWidth = mask.offsetWidth;
				mask.style.backgroundPosition = '-' + 4.75 * (bl + maskWidth / 2) + 'px ' + '-' + 4.75 * (bt + maskHeight / 2) + 'px';
			}, 250);
		}
	}
}
