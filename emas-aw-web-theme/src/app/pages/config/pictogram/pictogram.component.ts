import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ConfigService } from '../config.service';
import { UserService } from '../../user-management/user-management.service';
import { CommonService, EquipType } from '../../../service/common.service';
import { NzMessageService } from 'ng-zorro-antd/message';


@Component({
	// tslint:disable-next-line: component-selector
	selector: 'emas-pictogram',
	templateUrl: './pictogram.component.html',
	styleUrls: ['./pictogram.component.css']
})
export class PictogramComponent implements OnInit {
	renderHeader = [
		{
			name: 'ID',
			key: null,
			value: 'id',
			isChecked: true
		},
		{
			name: 'Group Id',
			key: null,
			value: 'picGroupId',
			isChecked: true
		},
		{
			name: 'Image',
			key: null,
			value: 'image',
			isChecked: true
		},
		{
			name: 'File Name',
			key: null,
			value: 'picFileName',
			isChecked: true
		},
		{
			name: 'Height(pixels)',
			key: null,
			value: 'height',
			isChecked: true
		},
		{
			name: 'Width(pixels)',
			key: null,
			value: 'width',
			isChecked: true
		},
		{
			name: 'Size(bytes)',
			key: null,
			value: 'size',
			isChecked: true
		},
		{
			name: 'Description',
			key: null,
			value: 'pictogramDesc',
			isChecked: true
		},
		{
			name: 'Download EquipIds',
			key: null,
			value: 'downloadEquipIds',
			isChecked: true
		}
	];
	isLoading = false;

	validateForm: FormGroup;
	controlArray: any[] = [];
	isCollapse = true;
	choiceItemID: any;
	choiceItem: any;
	choiceEquipId: any;
	isVisibleMiddle = false;
	addIsVisibleMiddle = false;
	downloadVisibleMiddle = false;
	downloadStatu = true;
	listOfData = [];
	listGroupID = [];

	listSize = [
		{ label: 'All', value: '-1*-1' },
	];
	sortName: string | null = null;
	sortValue: string | null = null;
	pageIndex = 1;
	constructor(
		private fb: FormBuilder,
		private configService: ConfigService,
		private userService: UserService,
		private commonService: CommonService,
		private message: NzMessageService
	) { }

	ngOnInit() {
		this.initForm();
		// console.log(this.validateForm);
		this.initData();

	}

	initForm() {
		this.validateForm = this.fb.group({
			groupID: [{ value: -1, disabled: false }],
			size: [{ value: '-1*-1', disabled: false }],
		});
	}
	async initData() {
		const allType = await this.commonService.allType;
		const picGroupId = allType[EquipType.VMS_PICGROUP];
		this.getGroupId(picGroupId);
		// this.getAllData();
	}
	submitForm(): void {
		// console.log(this.validateForm);
		this.searchData();
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

	getGroupId(picGroupId) {
		this.listGroupID = [];
		picGroupId.forEach((item) => {
			this.listGroupID.push({ label: item.description, value: item.value });
		});
		this.listGroupID.sort((a, b) => a.label < b.label ? -1 : 1);
		this.listGroupID.unshift({ label: 'All', value: -1 });
	}

	getSize(res) {
		const allSizeList = [];
		res.forEach((item) => {
			allSizeList.push({ label: item.height + '*' + item.width, value: item.height + '*' + item.width });
		});
		this.listSize = this.getSelectList(allSizeList, 'label');
	}

	getSelectList(arr, key) { // 对象数组去重
		const array = [{ label: 'All', value: '-1*-1' }];
		const map = new Map();
		arr.forEach((item) => {
			if (!map.has(item[key])) {
				map.set(item[key], item);
			}
		});
		const uniqueList = [...map.values()];
		// console.log(uniqueList);
		uniqueList.forEach((data) => {
			array.push({ label: data[key], value: data[key] });
		});
		return array;
	}

	getAllData() {
		const list = [];
		this.isLoading = true;
		this.configService.getAllVmsPictogramConfig().subscribe((r) => {
			// console.log(r);
			this.isLoading = false;
			const res: any = r;
			const resData = res.Body.getAllVmsPictogramConfigResponse.vmsPictogramConfigDtoList;
			resData.forEach(data => {
				if (data.equipIdList && data.equipIdList instanceof Object === false) { // 对于equipIdList只有一条数据时不是array
					// console.log(data.equipIdList);
					const equipIdListArr = [];
					equipIdListArr.push(data.equipIdList);
					data.equipIdList = equipIdListArr;
				}
				this.getImgByteSize(data);
			});
			this.getSize(resData);
			resData.forEach((item) => {
				if (item.equipIdList !== undefined) { // 当equipIdList不存在时，前台不展示，操作update后，equipIdList会变成空，也就是返回数据时就没有equipIdList
					list.push(item);
				}
			});
			this.listOfData = list;
			// tslint:disable-next-line: max-line-length
			this.listOfData.length ? this.message.create('success', `Data request succeeded`) : this.message.create('warning', `No Data`);
			this.pageIndex = 1;
			const ttt: number = this.listOfData.length / 10;
			const yu = this.listOfData.length % 10;
			if (yu < 5) {
				// localStorage.setItem('totalPage', (ttt + 0.5).toFixed(0));
				this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
			} else {
				// localStorage.setItem('totalPage', ttt.toFixed(0));
				this.userService.$totalPage.next(ttt.toFixed(0));
			}
		});
	}

	groupIdChange() {
		const picGroupId = this.validateForm.value.groupID;
		// console.log(picGroupId);
		picGroupId ? this.configService.getPictogramDimensionByGroupId(picGroupId).subscribe((r) => {
			// console.log(r);
			const res: any = r;
			const resData = res.Body.getPictogramDimensionByGroupIdResponse.list || [];
			this.listSize =  [{ label: 'All', value: '-1*-1' }] ;
			resData.forEach((item) => {
				this.listSize.push({ label: item, value: item });
			});
		}) : this.configService.getAllVmsPictogramConfig().subscribe((r) => {
			const res: any = r;
			const resData = res.Body.getAllVmsPictogramConfigResponse.vmsPictogramConfigDtoList;
			this.getSize(resData);
		});
	}

	searchData() {
		const picGroupId = this.validateForm.value.groupID;
		const size = this.validateForm.value.size;
		this.isLoading = true;
		// if (size && picGroupId) { // picGroupId和size筛选
			const sizeList = size.split('*');
			// console.log(sizeList);
			const height = sizeList[0];
			const width = sizeList[1];
			
			this.configService.getVmsPictogramConfigByGroupIdAndDimension(picGroupId, height, width).subscribe((r) => {
				// console.log(r);
				this.isLoading = false;
				const res: any = r;
				let resData = res.Body.getVmsPictogramConfigByGroupIdAndDimensionResponse.vmsPictogramConfigDtoList;
				// console.log(resData instanceof Array);
				resData = resData instanceof Array ? resData : []; // 解决返回一条数据时类型是object
				resData.forEach(data => {
					if (data.equipIdList && data.equipIdList instanceof Object === false) { // 对于equipIdList只有一条数据时不是array
						// console.log(data.equipIdList);
						const equipIdListArr = [];
						equipIdListArr.push(data.equipIdList);
						data.equipIdList = equipIdListArr;
					}
					this.getImgByteSize(data);
				});
				this.listOfData = resData;
				// tslint:disable-next-line: max-line-length
				this.listOfData.length ? this.message.create('success', `Data request succeeded`) : this.message.create('warning', `No Data`);
				this.pageIndex = 1;
				const ttt: number = this.listOfData.length / 10;
				const yu = this.listOfData.length % 10;
				if (yu < 5) {
					// localStorage.setItem('totalPage', (ttt + 0.5).toFixed(0));
					this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
				} else {
					// localStorage.setItem('totalPage', ttt.toFixed(0));
					this.userService.$totalPage.next(ttt.toFixed(0));
				}
			});
		// } 
		// else if (picGroupId && !size) { // picGroupId筛选
		// 	this.configService.getAllVmsPictogramConfig().subscribe((r) => {
		// 		// console.log(r);
		// 		const res: any = r;
		// 		const listOfData2 = [];
		// 		const listOfData3 = [];
		// 		const resData = res.Body.getAllVmsPictogramConfigResponse.vmsPictogramConfigDtoList;
		// 		resData.forEach(data => {
		// 			if (data.equipIdList && data.equipIdList instanceof Object === false) { // 对于equipIdList只有一条数据时不是array
		// 				// console.log(data.equipIdList);
		// 				const equipIdListArr = [];
		// 				equipIdListArr.push(data.equipIdList);
		// 				data.equipIdList = equipIdListArr;
		// 			}
		// 			if (data.picGroupId == picGroupId) {
		// 				this.getImgByteSize(data);
		// 				listOfData2.push(data);
		// 			}
		// 		});
		// 		listOfData2.forEach((item) => {
		// 			if (item.equipIdList !== undefined) { // 当equipIdList不存在时，前台不展示，操作update后，equipIdList会变成空，也就是返回数据时就没有equipIdList
		// 				listOfData3.push(item);
		// 			}
		// 		});
		// 		this.listOfData = listOfData3;
		// 		// tslint:disable-next-line: max-line-length
		// 		this.listOfData.length ? this.message.create('success', `Data request succeeded`) : this.message.create('warning', `No Data`);
		// 		this.pageIndex = 1;
		// 		const ttt: number = this.listOfData.length / 10;
		// 		const yu = this.listOfData.length % 10;
		// 		// console.log(this.listOfData.length)
		// 		if (yu < 5) {
		// 			// localStorage.setItem('totalPage', (ttt + 0.5).toFixed(0));
		// 			this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
		// 			// console.log((ttt + 0.5).toFixed(0))

		// 		} else {
		// 			// localStorage.setItem('totalPage', ttt.toFixed(0));
		// 			this.userService.$totalPage.next(ttt.toFixed(0));
		// 		}
		// 	});
		// } else if (!picGroupId && size) { // size筛选
		// 	const listOfData2 = [];
		// 	const listOfData3 = [];
		// 	const sizeList = size.split('*');

		// 	// console.log(sizeList);
		// 	const height = sizeList[0];
		// 	const width = sizeList[1];
		// 	this.configService.getAllVmsPictogramConfig().subscribe((r) => {
		// 		// console.log(r);
		// 		const res: any = r;
		// 		const resData = res.Body.getAllVmsPictogramConfigResponse.vmsPictogramConfigDtoList;
		// 		resData.forEach((item) => {
		// 			if (item.equipIdList && item.equipIdList instanceof Object === false) { // 对于equipIdList只有一条数据时不是array
		// 				const equipIdListArr = [];
		// 				equipIdListArr.push(item.equipIdList);
		// 				item.equipIdList = equipIdListArr;
		// 			}
		// 			if (item.height == height && item.width == width) {
		// 				this.getImgByteSize(item);
		// 				listOfData2.push(item);
		// 			}
		// 		});
		// 		listOfData2.forEach((item) => {
		// 			if (item.equipIdList !== undefined) { // 当equipIdList不存在时，前台不展示，操作update后，equipIdList会变成空，也就是返回数据时就没有equipIdList
		// 				listOfData3.push(item);
		// 			}
		// 		});
		// 		this.listOfData = listOfData3;
		// 		// tslint:disable-next-line: max-line-length
		// 		this.listOfData.length ? this.message.create('success', `Data request succeeded`) : this.message.create('warning', `No Data`);
		// 		const ttt: number = this.listOfData.length / 10;
		// 		const yu = this.listOfData.length % 10;
		// 		if (yu < 5) {
		// 			// localStorage.setItem('totalPage', (ttt + 0.5).toFixed(0));
		// 			this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
		// 		} else {
		// 			// localStorage.setItem('totalPage', ttt.toFixed(0));
		// 			this.userService.$totalPage.next(ttt.toFixed(0));
		// 		}
		// 	});
		// } else {
		// 	this.getAllData();
		// }
	}

	getImgByteSize(data) {
		if (data.graphicContents) { // 获取base64图片byte大小
			const equalIndex = data.graphicContents.indexOf('=');
			if (equalIndex > 0) {
				const str = data.graphicContents.substring(0, equalIndex);
				const strLength = str.length;
				const fileLength = strLength - (strLength / 8) * 2; // 真实的图片byte大小
				data.size = Math.floor(fileLength); // 向下取整
			} else {
				const strLength = data.graphicContents.length;
				const fileLength = strLength - (strLength / 8) * 2;
				data.size = Math.floor(fileLength); // 向下取整
			}
		} else {
			data.size = null;
		}
	}

	// sort
	sort(sort: { key: string; value: string }): void {
		// console.log(sort);
		this.sortName = sort.key;
		this.sortValue = sort.value;
		this.search();
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
		if (yu < 5) {
			// localStorage.setItem('totalPage', (ttt + 0.5).toFixed(0));
			this.userService.$totalPage.next((ttt + 0.5).toFixed(0));
		} else {
			// localStorage.setItem('totalPage', ttt.toFixed(0));
			this.userService.$totalPage.next(ttt.toFixed(0));
		}
	}
	pageChange(e) {
		this.userService.$addData.next(e);
	}
	choice(item) {
		this.choiceItemID = item.pictogramId;
		this.choiceItem = item;
		this.downloadStatu = false;
	}

	choiceEquipIdList(indx, item) { // 为选中的Download EquipIds > li
		this.choiceEquipId = indx;
		this.choiceItemID = item.pictogramId;
	}

	showModalMiddle(item): void {
		this.isVisibleMiddle = true;
		this.choiceItemID = item.pictogramId;
		this.choiceItem = item;
	}

	addShowModalMiddle() {
		// console.log('add pictogram');
		this.addIsVisibleMiddle = true;
		// console.log(this.addIsVisibleMiddle);
	}
	downloadShowModalMiddle() {
		// console.log('download pictogram');
		this.downloadVisibleMiddle = true;
	}
	closeModal() {
		this.isVisibleMiddle = false;
		this.addIsVisibleMiddle = false;
		this.downloadVisibleMiddle = false;
		this.downloadStatu = true;
	}
}
