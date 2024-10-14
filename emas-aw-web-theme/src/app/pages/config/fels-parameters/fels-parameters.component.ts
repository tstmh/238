import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ConfigService } from '../config.service';
import { CommonService, EquipType } from '../../../service/common.service';
import { NzMessageService } from 'ng-zorro-antd';
@Component({
	// tslint:disable-next-line: component-selector
	selector: 'emas-fels-parameters',
	templateUrl: './fels-parameters.component.html',
	styleUrls: ['./fels-parameters.component.css']
})
export class FelsParametersComponent implements OnInit {
	radioValue = 'A';
	updateItem: any = '';
	updateItemParam: any = '';
	updateItemValue: any = '';
	isVisibleMiddle = false;
	isVisible = false;
	validateForm: FormGroup;
	second = '';
	count = '';
	value = '';
	one = true;
	flesSelect: any = '';
	listOfDataC = '';
	listOfDataA = [];
	listOfDataB = [];
	listOfDataD = [];
	tempArrA = [];
	tempB = [];
	expwayCodeList = [];
	expwayDirection = [];
	allAlertList = [];
	selectedValue = 'lucy';
	controlArray: any[] = [];
	isCollapse = true;
	listOfData = [];
	listOpeStatus = [
		{ label: 'Disable', value: '0' },
		{ label: 'Enable', value: '1' }
	];
	// start
	vmsStatue = false;
	countStatus = false;
	opeStatus = false;

	// limitDate = true;
	// limitNight = '';
	// durationDate = '';
	// durationNight = '';
	// threshold = '';
	// end
	listTrafficAlert = [
		{ label: 'CTE and CTE Tunnel', value: 0, status: 'Disable' },
		{ label: 'BKE', value: 1, status: 'Enable' },
		{ label: 'ECP', value: 2, status: 'Disable' },
		{ label: 'AYE', value: 3, status: 'Enable' },
		{ label: 'PIE East', value: 4, status: 'Disable' },
		{ label: 'PIE Wast', value: 5, status: 'Enable' },
		{ label: 'TPE', value: 6, status: 'Enable' },
		{ label: 'SLE', value: 7, status: 'Enable' },
		{ label: 'KJE', value: 8, status: 'Enable' }
	];
	constructor(
		private fb: FormBuilder,
		private configService: ConfigService,
		private commonService: CommonService,
		private message: NzMessageService,
	) { }
	secRadioChecked = true;
	couRadioChecked = false;
	osRadioChecked = false;
	updateFles = false;
	tempA = [];
	handleOkMiddle(): void {
		// console.log('click ok');
		this.isVisibleMiddle = false;
		this.updateFles = true;
		this.testData();
	}
	handleCancelMiddle(): void {
		this.isVisibleMiddle = false;
		this.formPatchValue();
	}
	showRadio(e) {
		const _this = this;
		// console.log();
		// console.log(e);
		if (e === 'B') {
			this.validateForm.controls['seconds'].disable();
			this.validateForm.controls['opeStatus'].disable();
			this.validateForm.controls['count'].enable();
			this.osRadioChecked = false;
			this.secRadioChecked = false;
			this.couRadioChecked = true;
			this.validateForm.patchValue({
				seconds: this.tempA[0].value,
				opeStatus: this.flesSelect.value
			});
		} else if (e === 'A') {
			this.validateForm.controls['seconds'].enable();
			this.validateForm.controls['opeStatus'].disable();
			this.validateForm.controls['count'].disable();
			this.osRadioChecked = false;
			this.secRadioChecked = true;
			this.couRadioChecked = false;
			// console.log(this.vmsStatue);
			this.validateForm.patchValue({
				count: this.tempA[1].value,
				opeStatus: this.flesSelect.value
			});
		} else if (e === 'C') {
			this.validateForm.controls['seconds'].disable();
			this.validateForm.controls['opeStatus'].enable();
			this.validateForm.controls['count'].disable();
			this.osRadioChecked = true;
			this.secRadioChecked = false;
			this.couRadioChecked = false;
			this.validateForm.patchValue({
				seconds: this.tempA[0].value,
				count: this.tempA[1].value,
			});
		}
	}
	showModalMiddle(): void {
		// console.log(this.validateForm);
		// this.isVisibleMiddle = true;
		let param = '';
		let value = '';
		let setBy = '';
		setBy = localStorage.getItem('user_name');
		this.configService.getAllSystemParameter().subscribe((rA) => {
			const tempData: any = rA;
			this.listOfData = tempData.Body.getAllSystemParameterResponse.systemParameterList;
			// console.log(this.listOfData);
			const data = [
				{ key: 'POLL_INTERVAL', value: null },
				{ key: 'RETRY_COUNT', value: null },
				{ key: 'OCC_THRESHOLD_DAY', value: null },
				{ key: 'OCC_THRESHOLD_NIGHT', value: null },
				{ key: 'OCC_THRESHOLD_DAY_DURATION', value: null },
				{ key: 'OCC_THRESHOLD_NIGHT_DURATION', value: null },
				{ key: 'OCC_ENABLE_FLAG', value: null }
			];
			this.tempA = data;
			// const arr = Object.keys(data);
			this.listOfData.forEach((item) => {
				data.forEach((temp) => {
					if (item.name === temp.key) {
						temp.value = item.value;
					}
				});
			});
			if (this.secRadioChecked === true) {
				param = 'POLL_INTERVAL';
				value = this.validateForm.value.seconds;
				// console.log(data[0].value);
				if (value === data[0].value) {
					// console.log('未更新');
					this.message.create('warning', `No data to be updated`);
				} else {
					this.isVisibleMiddle = true;
					this.updateItemParam = param;
					this.updateItemValue = value;
				}
			} else if (this.couRadioChecked === true) {
				param = 'RETRY_COUNT';
				value = this.validateForm.value.count;
				if (value === data[1].value) {
					// console.log('未更新');
					this.message.create('warning', `No data to be updated`);
				} else {
					this.isVisibleMiddle = true;
					this.updateItemParam = param;
					this.updateItemValue = value;
				}
			} else if (this.osRadioChecked === true) {
				param = 'OPE_ENABLE_FLAG';
				// console.log(this.validateForm.value.opeStatus);
				// console.log('不为空');
				value = this.validateForm.value.opeStatus;
				if (value === data[2].value) {
					// console.log('未更新');
					this.message.create('warning', `No data to be updated`);
				} else {
					this.isVisibleMiddle = true;
					this.updateItemParam = param;
					this.updateItemValue = value;
				}
			}
			// console.log(param);
			// console.log(value);
			// console.log(setBy);
			// console.log(data);
			this.listOfData.forEach((item) => {
				if (item.name === 'OPE_ENABLE_FLAG') {
					// console.log(item)
					this.value = item.value;
					// console.log(item.value);
					if (item.value === 1) {
						this.flesSelect = { label: 'Enable', value: '1' };
					}
					this.flesSelect = { label: 'Disable', value: '0' };
				}
			});
			// console.log(data);
		});
	}
	cancel() {
		this.formPatchValue();
	}
	closeModal(e) {
		// console.log(e);
		this.isVisibleMiddle = false;
		this.formPatchValue();
	}
	testData() {
		// console.log(e);
		let setBy = '';
		setBy = localStorage.getItem('user_name');
		if (this.updateFles === true) {
			// console.log(this.updateItemParam);
			// console.log(this.updateItemValue);
			this.configService.updateSystemParameter(this.updateItemParam, this.updateItemValue, setBy).subscribe((r) => {
				// console.log(r);
				this.message.create('success', `Please restart MFELS`);
				this.configService.getAllSystemParameter().subscribe((rA) => {
					// console.log(rA)
					const tempData: any = rA;
					this.listOfData = tempData.Body.getAllSystemParameterResponse.systemParameterList;
					// console.log(this.listOfData);
					const data = [
						{ key: 'POLL_INTERVAL', value: null },
						{ key: 'RETRY_COUNT', value: null },
						{ key: 'OCC_THRESHOLD_DAY', value: null },
						{ key: 'OCC_THRESHOLD_NIGHT', value: null },
						{ key: 'OCC_THRESHOLD_DAY_DURATION', value: null },
						{ key: 'OCC_THRESHOLD_NIGHT_DURATION', value: null },
						{ key: 'OCC_ENABLE_FLAG', value: null }
					];
					this.tempA = data;
					// const arr = Object.keys(data);
					this.listOfData.forEach((item) => {
						data.forEach((temp) => {
							if (item.name === temp.key) {
								temp.value = item.value;
							}
						});
					});
					// console.log(data);
					this.listOfData.forEach((item) => {
						if (item.name === 'OPE_ENABLE_FLAG') {
							// console.log(item)
							this.value = item.value;
							// console.log(item.value);
							if (item.value === 1) {
								this.flesSelect = { label: 'Enable', value: '1' };
							}
							this.flesSelect = { label: 'Disable', value: '0' };
						}
					});
					// console.log(this.flesSelect);
					this.formPatchValue();
				});
			});
		}
	}
	ngOnInit() {
		this.initForm();
		this.initData();

		this.validateForm.controls['seconds'].enable();
		this.validateForm.controls['opeStatus'].disable();
		this.validateForm.controls['count'].disable();

		this.getAllData();
		this.getTestData();
		this.getAllDataAlert();
		setTimeout(() => {
			this.mergeData(this.listOfDataC, this.listOfDataA);
		}, 2000);
		setTimeout(() => {
			this.mergeDataA(this.listOfDataB, this.expwayCodeList);
		}, 2000);
	}
	async initData() {
		const allType = await this.commonService.allType;
		this.expwayCodeList = allType[EquipType.EXPWAY_CODE];
		this.expwayDirection = allType[EquipType.EXPWAY_DIRECTION];
		this.getAlertList();
	}
	compareFn = (o1: any, o2: any) => (o1 && o2 ? o1.value === o2.value : o1 === o2);

	log(value: { value: string; }): void {
		// console.log(value);
	}
	getAllData() {
		this.configService.getAllSystemParameter().subscribe((r) => {
			const tempData: any = r;
			this.listOfData = tempData.Body.getAllSystemParameterResponse.systemParameterList;
			// console.log(this.listOfData);
			const data = [
				{ key: 'POLL_INTERVAL', value: null },
				{ key: 'RETRY_COUNT', value: null },
				{ key: 'OCC_THRESHOLD_DAY', value: null },
				{ key: 'OCC_THRESHOLD_NIGHT', value: null },
				{ key: 'OCC_THRESHOLD_DAY_DURATION', value: null },
				{ key: 'OCC_THRESHOLD_NIGHT_DURATION', value: null },
				{ key: 'OCC_ENABLE_FLAG', value: null }
			];
			this.tempA = data;
			// const arr = Object.keys(data);
			this.listOfData.forEach((item) => {
				data.forEach((temp) => {
					if (item.name === temp.key) {
						temp.value = item.value;
					}
				});
			});
			// console.log(data);
			this.listOfData.forEach((item) => {
				if (item.name === 'OPE_ENABLE_FLAG') {
					// console.log(item)
					this.value = item.value;
					// console.log(item.value);
					if (item.value === 1) {
						this.flesSelect = { label: 'Enable', value: '1' };
					}
					this.flesSelect = { label: 'Disable', value: '0' };
				}
			});
			// console.log(this.flesSelect);
			this.formPatchValue();
		});
	}
	getAlertList() {

		this.configService.getAllSystemParameter().subscribe((r) => {
			// console.log(r);
			const tempData: any = r;
			const tempDataA = [
				{ key: 'ALERT_SENDING_FLAG_71', enabled: null, value: 'CTE and CTE Tunnel' },
				{ key: 'ALERT_SENDING_FLAG_72', enabled: null, value: 'BKE' },
				{ key: 'ALERT_SENDING_FLAG_73', enabled: null, value: 'ECP' },
				{ key: 'ALERT_SENDING_FLAG_74', enabled: null, value: 'AYE' },
				{ key: 'ALERT_SENDING_FLAG_75', enabled: null, value: 'PIE East' },
				{ key: 'ALERT_SENDING_FLAG_76', enabled: null, value: 'PIE West' },
				{ key: 'ALERT_SENDING_FLAG_77', enabled: null, value: 'TPE' },
				{ key: 'ALERT_SENDING_FLAG_78', enabled: null, value: 'SLE' },
				{ key: 'ALERT_SENDING_FLAG_78', enabled: null, value: 'KJE' },


			];
			const tempDataB = [];
			this.tempB = tempData.Body.getAllSystemParameterResponse.systemParameterList;
			// console.log(this.tempB)
			// console.log(this.tempB)
			this.tempB.forEach((item) => {
				tempDataA.forEach((itemA) => {
					if (item.name === itemA.key) {
						itemA.enabled = item.value;
					}
				});
			});
			// console.log(tempDataA);
			this.allAlertList = tempDataA;
		});
	}
	initForm() {
		this.validateForm = this.fb.group({
			seconds: [{ value: null, disabled: false }],
			count: [{ value: null, disabled: false }],
			opeStatus: [{ value: null, disabled: false }],
			day: [{ value: null, disabled: true }],
			night: [{ value: null, disabled: true }],
			durationDay: [{ value: null, disabled: true }],
			durationNight: [{ value: null, disabled: true }],
			edThreshould: [{ value: null, disabled: true }],
			trafficAlert: [{ value: null, disabled: false }],
			secRadio: [{ value: null, disabled: false }],
			couRadio: [{ value: null, disabled: false }],
			osRadio: [{ value: null, disabled: false }],
			radio: [{ value: 'A', disabled: false }],
		});
	}
	formPatchValue(): void {
		// console.log(this.flesSelect.value);
		// console.log(this.tempA);
		// console.log(this.listOpeStatus[0].value);
		// console.log(this.listOpeStatus[0].value === this.flesSelect.value);
		this.validateForm.patchValue({
			seconds: this.tempA[0].value,
			count: this.tempA[1].value,
			opeStatus: this.flesSelect.value,
			day: this.tempA[2].value,
			night: this.tempA[3].value,
			durationDay: this.tempA[4].value,
			durationNight: this.tempA[5].value,
			edThreshould: this.tempA[6].value === '1' ? 'Enable' : 'Disable',
			trafficAlert: this.allAlertList,
			secRadio: this.secRadioChecked,
			couRadio: this.couRadioChecked,
			osRadio: this.osRadioChecked,
		});
		// console.log(this.validateForm)
	}
	// 获取到traffic alert数据
	getTestData(): void {
		this.configService.GetAllEquipConfig().subscribe((r) => {
			const tempData: any = r;
			this.listOfDataA = tempData.Body.getAllEquipConfigResponse.equipConfigDtoList;
		});
	}
	getAllDataAlert(): void {
		this.configService.GetTechnicalAlarmByEquipTypeAndExpwayCode().subscribe((r) => {
			// console.log(r);
			const tempData: any = r;
			this.listOfDataC = tempData.Body.getTechnicalAlarmByEquipTypeAndExpwayCodeResponse.technicalAlarmList;
		});
	}
	mergeData(arrayA, arrayB): void {
		// console.log(arrayA);
		// console.log(arrayB);
		arrayA.forEach((itemA) => {
			arrayB.forEach((itemB) => {
				if (itemA.equipId === itemB.equipId) {
					// console.log('work');
					const data = { ...itemA, ...itemB };
					this.listOfDataB = [...this.listOfDataB, data];
				}
			});
		});
		// console.log(this.listOfDataB);
		this.listOfDataB.forEach((item) => {
			const tempDir = item.expwayCode + '-' + item.dir;
			this.expwayCodeList.forEach((dataA) => {
				if (item.expwayCode === dataA.value) {
					item.expWay = dataA.description;
				}
			});

			this.expwayDirection.forEach((dataB) => {
				if (tempDir === dataB.value) {
					item.direction = dataB.description;
				}
			});
			this.listOfDataB.forEach((itemA) => {
				let unique = true;
				this.tempArrA.forEach((j) => {
					if (j.expway === itemA.expway) {

						unique = false;
					}
				});
				if (unique) {
					this.tempArrA.push(itemA);
				}

			});
		});
	}
	mergeDataA(arrayA, arrayB): void {
		// console.log(arrayA);
		// console.log(arrayB);
		arrayA.forEach((itemA) => {
			arrayB.forEach((itemB) => {
				if (itemA.expwayCode === itemB.value && itemA.expWay === itemB.description) {
					// itemB.enable = itemA.enable
					// console.log('work');
					// console.log(itemA)
					const data = { ...itemA, ...itemB };
					this.listOfDataD = [...this.listOfDataD, data];
				}
			});
		});
		// console.log(this.listOfDataD);
		// console.log(arrayB)

	}
	changeradio(event) {
		// console.log(event);
		// console.log('222222222222');
	}
	submitForm(): void {
		// console.log(this.validateForm);
	}
}
