import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ConfigService } from '../config.service';
import { UserService } from '../../user-management/user-management.service';
import { NzMessageService } from 'ng-zorro-antd/message';
import { CommonService, EquipType } from '../../../service/common.service';


@Component({
	// tslint:disable-next-line: component-selector
	selector: 'sj-vms-template',
	templateUrl: './vms-template.component.html',
	styleUrls: ['./vms-template.component.css'],
})
export class VmsTemplateComponent implements OnInit {
	renderHeader = [
		{
			name: 'Template Id',
			key: null,
			value: 'templateId',
			isChecked: true
		},
		{
			name: 'Equip Type',
			key: null,
			value: 'equipType',
			isChecked: true
		},
		{
			name: 'Phase',
			key: null,
			value: 'phase',
			isChecked: true
		},
		{
			name: 'Height',
			key: null,
			value: 'height',
			isChecked: true
		},
		{
			name: 'Width',
			key: null,
			value: 'width',
			isChecked: true
		},
		{
			name: 'Template Name',
			key: null,
			value: 'templateName',
			isChecked: true
		},
		{
			name: 'Template',
			key: null,
			value: 'template',
			isChecked: true
		}
	];

	listOfData: any = [];

	validateForm: FormGroup;
	controlArray: any[] = [];
	isCollapse = true;
	isVisibleMiddle = false;
	selectItem: any = '';
	selectItemId: number;
	listEquipType = [
		{ label: 'All', value: null },
	];

	sortName: string | null = null;
	sortValue: string | null = null;
	pageIndex = 1;
	allType = {};
	constructor(
		private fb: FormBuilder,
		private configService: ConfigService,
		private userService: UserService,
		private message: NzMessageService,
		private commonService: CommonService
	) {
	}

	ngOnInit() {
		this.initForm();
		this.initData();
		this.getAllData('init');
	}
	async initData() {
		this.allType = await this.commonService.allType;
	}

	initForm() {
		this.validateForm = this.fb.group({
			equipType: [{ value: null, disabled: false }]
		});
	}

	submitForm(): void {
		// console.log(this.validateForm);
		const value = this.validateForm.value.equipType;
		// console.log(value);
		if (value) {
			this.configService.getVmsTemplateByEquipType(value).subscribe((r) => {
				// console.log(r);
				const searchData: any = r;
				const vmsTemplateByEquipTypeData = searchData.Body.getVmsTemplateByEquipTypeResponse.vmsTemplateConfigDtoList;
				// 排序
				const sortData = vmsTemplateByEquipTypeData.sort((data1, data2) => data1.templateId - data2.templateId);
				this.listOfData = sortData;
				this.listOfData.forEach((item, idx) => {
					this.setRightPartStyles(item);
				});
				this.listOfData.length ? this.message.create('success', `Data request succeeded`) : this.message.create('warning', `No Data`);
				this.pageIndex = 1;
				const ttt: number = this.listOfData.length / 10;
				const yu = this.listOfData.length % 10;
				// console.log(this.listOfData.length)
				if (yu < 5) {
					// localStorage.setItem('totalPage', (ttt + 0.5).toFixed(0));
					this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
					// console.log((ttt + 0.5).toFixed(0))
				} else {
					// localStorage.setItem('totalPage', ttt.toFixed(0));
					this.userService.$totalPage.next(ttt.toFixed(0));
				}
			});
		} else {
			this.getAllData();
		}
	}

	// choice(item): void {
	// 	this.selectItemId = item.equipIdLocation;
	// 	this.selectItem = item;
	// 	console.log(this.selectItem);
	// }
	showModalMiddle(item): void {
		this.isVisibleMiddle = true;
		this.selectItemId = item.templateId;
		this.selectItem = item;
		// console.log(this.selectItem);
	}

	closeModal() {
		this.isVisibleMiddle = false;
	}

	getAllData(init?) {
		this.configService.getAllVmsTemplate().subscribe((r: any) => {
			// console.log(r);
			const allVmeTemplateData = r.Body.getAllVmsTemplateResponse.vmsTemplateConfigDtoList;
			// 排序测试
			const sortData = allVmeTemplateData.sort((data1, data2) => data1.templateId - data2.templateId);
			this.listOfData = sortData;
			this.listOfData.forEach((item, idx) => {
				this.setRightPartStyles(item);
			});
			//console.log(this.listOfData);
			this.listOfData.length ? this.message.create('success', `Data request succeeded`) : this.message.create('warning', `No Data`);
			this.pageIndex = 1;
			if (init && init === 'init') {
				this.configService.getSelectList(this.listOfData, 'equipType', this.listEquipType);
			}
			const ttt: number = this.listOfData.length / 10;
			const yu = this.listOfData.length % 10;
			// console.log(this.listOfData.length)
			if (yu < 5) {
				// localStorage.setItem('totalPage', (ttt + 0.5).toFixed(0));
				this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
				// console.log((ttt + 0.5).toFixed(0))
			} else {
				// localStorage.setItem('totalPage', ttt.toFixed(0));
				this.userService.$totalPage.next(ttt.toFixed(0));
			}
		});
	}

	// sort
	sort(sort: { key: string; value: string }): void {
		// console.log(sort);
		this.sortName = sort.key;
		this.sortValue = sort.value;
		this.search();
	}

	pageChange(e) {
		this.userService.$addData.next(e);
	}

	search(): void {
		const data = this.listOfData;
		/** sort data **/
		if (this.sortName && this.sortValue) {
			const arr = data.sort((a, b) =>
				this.sortValue === 'ascend'
					// tslint:disable-next-line: no-non-null-assertion
					? a[this.sortName!] > b[this.sortName!]
						? 1
						: -1
					// tslint:disable-next-line: no-non-null-assertion
					: b[this.sortName!] > a[this.sortName!]
						? 1
						: -1
			);
			this.listOfData = [...arr];
		} else {
			this.listOfData = [...data];
		}
		const ttt: number = this.listOfData.length / 10;
		const yu = this.listOfData.length % 10;
		// console.log(this.listOfData.length)
		if (yu < 5) {
			// localStorage.setItem('totalPage', (ttt + 0.5).toFixed(0));
			this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
			// console.log((ttt + 0.5).toFixed(0))
		} else {
			// localStorage.setItem('totalPage', ttt.toFixed(0));
			this.userService.$totalPage.next(ttt.toFixed(0));
		}
	}
	// add template to table

	setRightPartStyles(item) {
		this.transferData(item);
		const rightPartStyles = {
			'height': item ? item.height * 1.9 + 'px' : '60px',
			'width': item ? item.width * 1.9 + 'px' : '200px',
			'background': item ? 'black' : 'lightblue',
			// 'margin-top': item ? '30px' : '30px',
			'position': item ? 'relative' : 'relative'
		};
		item.rightPartStyles = rightPartStyles;
		this.getCOntentStyle(item);
	}

	transferData(item) {
		const fontSize =  this.allType[EquipType.VMS_FONTTYPE];
		const picConfigDtoList = item.vmsTemplatePicConfigDtoList;
		const textlineConfigDtoList = item.vmsTemplateTextlineConfigDtoList;
		const flag1 = picConfigDtoList instanceof Array;
		const flag2 = textlineConfigDtoList instanceof Array;
		if (picConfigDtoList && !flag1) {
			const PicConfigDtoListArr = [];
			PicConfigDtoListArr.push(picConfigDtoList);
			item.vmsTemplatePicConfigDtoList = PicConfigDtoListArr;
		}
		if (item.vmsTemplateTextlineConfigDtoLis && !flag2) {
			const TextlineConfigDtoLisArr = [];
			TextlineConfigDtoLisArr.push(textlineConfigDtoList);
			item.vmsTemplateTextlineConfigDtoList = TextlineConfigDtoLisArr;
		}
		if (picConfigDtoList) {
			item.vmsTemplatePicConfigDtoList.sort((a, b) => a.picSeq - b.picSeq);
		}
		if (textlineConfigDtoList) {
			item.vmsTemplateTextlineConfigDtoList.sort((a, b) => a.textLineNo - b.textLineNo);
			item.vmsTemplateTextlineConfigDtoList.forEach((data) => {
				fontSize.forEach((item1) => { // 获取方格的height和width
					if (data.fontTypeId === item1.value) {
						const description = item1.description;
						const descriptionList = description.split('*');
						data.height = descriptionList[0];
						data.width = descriptionList[1];
					}
				});
			});
		}
	}

	getCOntentStyle(item) {
		const picConfigDtoList = item.vmsTemplatePicConfigDtoList;
		const textlineConfigDtoList = item.vmsTemplateTextlineConfigDtoList;
		const imgList = [];
		const textList = [];
		const textBoxStyle = [];
		const textBox = [];
		if (picConfigDtoList) {
			item.vmsTemplatePicConfigDtoList.forEach((data, index) => {
				imgList[index] = { // 图片样式<img>
					'height': picConfigDtoList ? data.height * 1.9 + 'px' : '0px',
					'width': picConfigDtoList ? data.width * 1.9 + 'px' : '0px',
					// tslint:disable-next-line: max-line-length
					'margin-left': picConfigDtoList ? data.xCord * 1.9 + 'px' : '0px',
					// tslint:disable-next-line: max-line-length
					'margin-top': picConfigDtoList ? data.yCord * 1.9 + 'px' : '0px',
					'position': picConfigDtoList ? 'absolute' : 'absolute',
					'border': picConfigDtoList ? '1px solid yellow' : '1px solid yellow',
				};
			});
			item.imgList = imgList;
		}
		if (textlineConfigDtoList) {
			item.vmsTemplateTextlineConfigDtoList.forEach((data, index) => {
				textList[index] = { // 文本框<ul>
					'margin-left': textlineConfigDtoList ? data.xCord * 1.9 + 'px' : '0px',
					'margin-top': textlineConfigDtoList ? data.yCord * 1.9 + 'px' : '0px',
					'position': textlineConfigDtoList ? 'absolute' : 'absolute'
				};
				textBoxStyle[index] = { // 文本框样式<li>
					'height': textlineConfigDtoList ? data.height * 1.9 - 1 + 'px' : '0px',
					'width': textlineConfigDtoList ? data.width * 1.9 - 1 + 'px' : '0px',
					'margin-right': textlineConfigDtoList ? data.charSpacing * 1.9 + 'px' : '0px',
					'border': textlineConfigDtoList ? '1px solid white' : '1px solid white',
					'display': textlineConfigDtoList ? 'inline-block' : 'inline-block'
				};
				textBox.push(data.maxNoChar);
			});
			item.textList = textList;
			item.textBoxStyle = textBoxStyle;
			item.textBox = textBox;
		}
	}
}
