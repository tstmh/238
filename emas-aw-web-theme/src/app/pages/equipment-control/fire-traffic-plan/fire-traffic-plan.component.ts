import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { HttpClient, HttpResponse, HttpHeaders } from '@angular/common/http';
import { NewModalComponent } from './new-modal/new-modal.component';
import { EventsService } from '../../events/events.service';
import { ToolsService } from 'src/app/share/service';

@Component({
	// tslint:disable-next-line:component-selector
	selector: 'emas-fire-traffic-plan',
	templateUrl: './fire-traffic-plan.component.html',
	styleUrls: ['./fire-traffic-plan.component.css']
})
export class FireTrafficPlanComponent implements OnInit {
	@ViewChild(NewModalComponent) private childcomponent: NewModalComponent;
	renderHeader = [
		{
			name: 'PP ID',
			key: null,
			value: 'ppID',
			isChecked: true
		},
		{
			name: 'Description',
			key: null,
			value: 'description',
			isChecked: true
		}
	];
	comfirm = false;
	selectItem = null;
	isVisible = false;
	listOfData = [];
	equipSelect = null;
	validateForm: FormGroup;
	controlArray: any[] = [];
	isCollapse = true;
	imp = false;
	sel = false;
	listSelect: Array<{ label: string; value: number }> = [
		{ label: 'All', value: 1 },
		{ label: 'Traffic', value: 2 },
		{ label: 'Activation', value: 3 },
		{ label: 'Deactivation', value: 4 },
		{ label: 'Testing', value: 5 },
	];
	sortName: string | null = null;
	sortValue: string | null = null;
	listOfDisplayData = [...this.listOfData];
	displayListOfData = [];
	constructor(
		private fb: FormBuilder,
		private http: HttpClient,
		private tools: ToolsService,
		private eventsService: EventsService,
	) { }

	ngOnInit() {
		this.getAllList();
		this.initForm();
		this.getFireAlarm()
	}

	//加载Technical Alarm
	getFireAlarm(){		
		const subSystemId = 'group1';
		const equipType = 'fir';
		const expwayCode = 'af';
		const queryData = {
			propertyCode: [0]
		};		
		queryData['subsystemId'] = subSystemId;
		queryData['expwayCode'] = expwayCode;		
		queryData['equipType'] = equipType;

		this.eventsService.GetTechnicalAlarmByEquipTypeAndExpwayCode(queryData).subscribe((res: any) => {
			const tempData: any = res.Body.getTechnicalAlarmByEquipTypeAndExpwayCodeResponse.technicalAlarmList;
			this.mergeData(tempData);
		});		
	}
	mergeData(displayList = []): void {
		for (const display of displayList) {
			this.displayListOfData.push(display['equipId']);
		}
		//console.log('Data: ',this.displayListOfData);
	}

	// 获取csv文件内容
	getAllList() {
		this.http.get('assets/Traffic-Plans/PP_Fire_Plan_Detail.csv', { responseType: 'text' })
			.subscribe(data => {
				const items = data.split('\n');
				const newItems = [];
				items.forEach(item => {
					if (item && item.trim().length > 0) {
						const newItem = item.split(',');
						newItems.push({ ppID: newItem[0], description: newItem[1], active: false });
					}
				});
				this.listOfData = newItems;
				this.listOfDisplayData = [...this.listOfData];
			});
		this.equipSelect = 'All';
		this.sel = true;
		this.imp = true;
	}
	// 下拉框改变时把全部数据赋给listOfData
	getList(a) {
		this.http.get('assets/Traffic-Plans/PP_Fire_Plan_Detail.csv', { responseType: 'text' })
			.subscribe(data => {
				const items = data.split('\n');
				const newItems = [];
				items.forEach(item => {
					if (item && item.trim().length > 0) {
						const newItem1 = item.split(',');
						newItems.push({ ppID: newItem1[0], description: newItem1[1], active: false });
					}
				});
				this.listOfData = newItems;
				const newItem = [];
				newItems.forEach(item => {
					const trueOrFalse = item.description.indexOf(a) !== -1;
					if (trueOrFalse) {
						newItem.push(item);
					}
					this.listOfData = newItem;
					this.listOfDisplayData = [...this.listOfData];
				});
			});
	}
	// 下拉框改变搜索数据
	equipSearch() {
		$('.imp').css({ border: 'unset', background: 'gray' });
		$('.sel').css({ border: 'unset', background: 'gray' });
		this.sel = true;
		this.imp = true;
		if (this.equipSelect === 'All') {
			this.getAllList();
		} else if (this.equipSelect === 'Traffic') {
			this.http.get('assets/Traffic-Plans/PP_Fire_Plan_Detail.csv', { responseType: 'text' })
				.subscribe(data => {
					const items = data.split('\n');
					const newItems = [];
					items.forEach(item => {
						if (item && item.trim().length > 0) {
							const newItem1 = item.split(',');
							newItems.push({ ppID: newItem1[0], description: newItem1[1], active: false });
						}
					});
					this.listOfData = newItems;
					const newItem = [];
					newItems.forEach(item => {
						const trueOrFalse = item.description.indexOf('Seek SOE approval') !== -1;
						const trueOrFalse1 = item.description.indexOf('Tunnel Fire Plan') !== -1;
						if (trueOrFalse) {
							newItem.push(item);
						} else if (trueOrFalse1) {
							newItem.push(item);
						}
						this.listOfData = newItem;
						this.listOfDisplayData = [...this.listOfData];
					});
				});
		} else if (this.equipSelect === 'Activation') {
			this.getList(this.equipSelect);
		} else if (this.equipSelect === 'Deactivation') {
			this.getList(this.equipSelect);
		} else if (this.equipSelect === 'Testing') {
			this.getList(this.equipSelect);
		}
	}
	// 点击tr
	cliclTr(i, data) {
		this.listOfData.forEach((item, index) => {
			item.active = false;
			if (i === index) {
				// 点击每个tr给与样式active
				item.active = true;
			}
		});
		this.selectItem = data;
		// 判断选中的tr中是否包含这些字段决定禁用两个按钮
		const trueOrFalse = this.selectItem.description.indexOf('Seek SOE approval') !== -1;
		const trueOrFalse1 = this.selectItem.description.indexOf('Testing') !== -1;
		const trueOrFalse2 = this.selectItem.description.indexOf('Tunnel Fire Plan') !== -1;
		if (trueOrFalse || trueOrFalse1 || trueOrFalse2) {
			this.sel = false;
			this.imp = true;
			$('.imp').css({ border: 'unset', background: 'gray' });
			$('.sel').css({ border: '#d9d9d9', background: '#f5f5f5' });
		} else {
			this.sel = true;
			this.imp = false;
			$('.imp').css({ border: '#d9d9d9', background: '#f5f5f5' });
			$('.sel').css({ border: 'unset', background: 'gray' });
		}
	}
	// ImplementPlan按钮
	ImplementPlan() {
		this.comfirm = false;
		this.childcomponent.showModal();
		// this.isVisible = true;
	}
	// Select按钮
	Select() {
		this.comfirm = true;
		this.childcomponent.showModal();
	}


	initForm() {
		this.validateForm = this.fb.group({
			Select: [{ value: null, disabled: false }],
		});
	}
	submitForm(): void {
	}
	// sort data
	sort(sort: { key: string; value: string }): void {
		this.sortName = sort.key;
		this.sortValue = sort.value;
		this.search();
	}
	search(): void {
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

}
