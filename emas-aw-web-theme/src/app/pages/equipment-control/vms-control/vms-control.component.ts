import { EquipType } from './../../../service/common.service';
import { Component, OnInit, Input, Output, EventEmitter, AfterViewInit, ViewChild, OnDestroy } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { EquipmentService } from '../equipment-control.service';
import { NzMessageService } from 'ng-zorro-antd';
import { DialogService } from 'src/app/share/dialog';
import { ViewModalComponent } from '../lus/view-modal/view-modal.component';
import { CommonService } from 'src/app/service/common.service';
import {
	VmsMsg, VmsMsgPage, VmsMsgPictogram, VmsMsgTextLine, VmsTemplate, PictogramInfo,
	VmsTemplatePictogram,
	VmsMsgPageDto
} from '../fire-traffic-plan/new-modal/new-modal.interface';
import { query } from '@angular/animations';
import * as moment from 'moment';
import { WebSocketService } from 'src/app/service/websocket.service';
import { takeUntil } from 'rxjs/operators';
import { Observable, Subject } from 'rxjs';

@Component({
	selector: 'emas-vms-control',
	templateUrl: './vms-control.component.html',
	styleUrls: ['./vms-control.component.css']
})
export class VmsControlComponent implements OnInit, OnDestroy {
	@ViewChild(ViewModalComponent) private childcomponent: ViewModalComponent;
	abortWebsocket$ = new Subject<void>();
	unSubscribe = new Subject<void>();
	validateForm: FormGroup;
	controlArray: any[] = [];
	isCollapse = true;
	toggleTimeNum: number = 5;
	listOfData = [];
	selectRightPartStyles = {};
	isVisibleTemplateSelect = false;
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
			value: 'id',
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
			value: 'FileName',
			isChecked: true
		},
		{
			name: 'Height',
			key: null,
			value: 'Height',
			isChecked: true
		},
		{
			name: 'Width',
			key: null,
			value: 'Width',
			isChecked: true
		},
		{
			name: 'Description',
			key: null,
			value: 'Description',
			isChecked: true
		},
	];
	renderHeader2 = [
		{
			name: 'Alarm Desc',
			key: null,
			value: 'id',
			isChecked: true
		},
		{
			name: 'Alarm Time',
			key: null,
			value: 'image',
			isChecked: true
		},
		// {
		// 	name: 'Status',
		// 	key: null,
		// 	value: 'FileName',
		// 	isChecked: true
		// }
	];
	renderHeader3 = [
		{
			name: 'ID',
			key: null,
			value: 'pictogramId',
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
			name: 'Description',
			key: null,
			value: 'pictogramDesc',
			isChecked: true
		},
	];
	selectedValue = 'Toggle Model';
	selectedValue2 = 'Display Model';
	selectedValue3 = 'Page Model';
	listEquipID = [];
	listDisplayEquipIDSearch = [];
	listUploadEquipIDSearch = [];
	listEquipType: Array<{ label: string; value: number }> = [
		{ label: 'equipType', value: 1 },
		{ label: 'equipType', value: 2 },
		{ label: 'equipType', value: 3 }
	];
	dimingTimetable = {
		dimmingLevel1: 1,
		dimmingLevel2: 1,
		dimmingLevel3: 1,
		time1: null,
		time2: null,
		time3: null,
		isSet: false
	}
	listExpWay = [];
	listExpWaySearch = [{ description: 'All', value: 'All' }];
	subSystemList = [];
	listOfPictogram = [];
	isVisible = false;
	selectItem: any = {};
	imgStyle: {};
	textBoxStyle = [];
	imgList = [];
	textList = [];
	textBox = [];
	selectItemIdx = null;
	list = [
		{
			dataList: [
				{ key: 'T', index: 0 },
				{ key: 'E', index: 1 },
				{ key: 'X', index: 2 },
				{ key: 'T', index: 3 },
				{ key: '6', index: 4 },
				{ key: '', index: 5 }
			]
		},
		{
			dataList: [
				{ key: 'T', index: 6 },
				{ key: 'E', index: 7 },
				{ key: 'X', index: 8 },
				{ key: 'T', index: 9 },
				{ key: '1', index: 10 },
				{ key: '1', index: 11 }
			]
		},
		{
			dataList: [
				{ key: 'T', index: 12 },
				{ key: 'E', index: 13 },
				{ key: 'X', index: 14 },
				{ key: 'T', index: 15 },
				{ key: '', index: 16 },
				{ key: '2', index: 17 }
			]
		},
	];
	modalTitle6; // request pixel failure bmp model title;
	textTemplateList = ['T', 'E', 'X', 'T'];
	textColorList = ['blue', 'green', 'lightblue', 'red', 'purple', 'yellow', 'white', 'orange'];
	choosenPic = '';
	choosenPicH = 60;
	choosenPicW = 60;
	selectTemplateDisplay = true;
	isTemplateBlank = false;
	isApplyTemplateVisible = false;
	isSelectLibraryMsgViewVisible = false;
	listOfTemplate = [];
	listOfTemplateDisplay = [];
	availableList = [];
	selectedList = [];
	addData = [];
	clickIndex = null;
	addDisabled = true;
	removeDisabled = true;
	addAllDisabled = false;
	removeAllDisabled = true;
	ratio0: any;
	ratio1: any;
	vmsEquipTypeList = [];
	vmsEquipTypeListSearch = [{ value: 'All' }];
	selectEuipment = {
		dir: '',
		enable: '',
		equipDesc: '',
		equipId: '',
		equipType: '',
		expwayCode: '',
		firmwareVersion: '',
		ipAddress: '',
		kmMarking: '',
		latitude: '',
		longitude: '',
		phase: '',
		propertyCode: '',
		propertyDescription: '',
		subSystemId: '',
		systemId: '',
		trafficDataEnabled: '',
		expWay: ''
	};
	toggleModeList = [];
	displayModeList = [];
	dimmingList = [];
	dimmingResult = null;
	fancontrolList = [];
	fancontrolResult = null;
	pageModeList = [];
	pageMode = '0';
	toggle_mode = '';
	display_mode = '';
	vmsMsgSource =
		{
			height: 0,
			width: 0,
			cmdId: '',
			execId: '',
			systemId: '',
			sender: localStorage.getItem('user_name'),
			equipId: '',
			equipType: '',
			displayMode: '',
			toggleMode: '',
			dateTime: '',
			page: 0,
			vmsMsgPageDtoList: [
				{
					templateId: '',
					vmsTemplateConfigDto: {
						templateName: '',
						templateId: '',
						width: '',
						height: '',
						vmsTemplatePicConfigDtoList: [],
						vmsTemplateTextlineConfigDtoList: []
					},
					vmsPictogramConfigDtoList: [],
					lineTextDtoList: []
				}
			],
			// vmsTemplatePicConfigDtoList: {
			// 	imgList: [],
			// 	textBox: [],
			// 	textBoxStyle: []
			// }
		};

	showOnly = [{
		textList: [],
		imgList: [],
		textBoxStyle: [],
		textBox: [],
		inputSet: [],
		rightPartStyles: {}
	}]; // only for ui display
	statusArr = [
		{
			key: 'opestate',
			value: null
		},
		{
			key: 'psreading',
			value: null
		},
		{
			key: 'signtemp',
			value: null
		},
		{
			key: 'dimlevel',
			value: null
		},
		{
			key: 'dimmode',
			value: null
		},
		{
			key: 'fantempthreshold',
			value: null
		},
		{
			key: 'fanopemode',
			value: null
		},
		{
			key: 'flashontime',
			value: null
		},
		{
			key: 'flashofftime',
			value: null
		}
	];
	operationalList = ['Not Operational', '', 'Operational'];
	isEditable = false;
	applyMessageWindow = '';
	flashingOnTime = 5;
	flashingOffTime = 5;
	temperatureThreshold = 30;
	dimmingLevel = 1;
	tempTemplate: any;
	isAddTemplate = false;
	isDeleteVisible = false;
	changeColorOne = null;
	listVMSPicGroup = [{ description: 'All', value: 0 }];
	vmpPicGroupId = null;
	choosenPictogram: any = {};
	showColorSelectPage = 0;
	addTemplatePlace = '';
	tabShow = false;
	isLoadBlankVisible = false;
	isRequestPixelVisible = false;
	isUpLoadPictogramVisible = false;
	expWay = 'All';
	timerArr = [];
	isSpinning = false;
	isPicSpinning = false;
	sendingType = null;
	vmsMsgCategory: any;
	categoryLibrary: any;
	equipTypeLibrary: any = 'All';
	equipIDLibrary: any;
	gourpIdLibrary: any = 0;
	listOfPictogramLibrary: any[];
	picUploadItem: any;
	isUpLoadPictogramDatabaseVisible: boolean;
	sitePictogram: string;
	sendExpWay: any = 'All';
	isRequestPixelPageVisible = false;
	isFanControlVisible: boolean;
	fancontrolResultRec: any;
	isDimmingModeVisible: boolean;
	dimmingResultRec: string;
	isTipEditable: boolean;
	sortName: string | null = null;
	sortValue: string | null = null;
	displayListOfData: any[];
	expWayCom: any = 'All';
	equipTypeCom: any = 'All';
	alarmList: any = [];
	allType = {};
	constructor(
		private fb: FormBuilder,
		private dialog: DialogService,
		private message: NzMessageService,
		private equipmentService: EquipmentService,
		private commonService: CommonService,
		private webSocketService: WebSocketService,
	) {
	}

	ngOnInit() {
		this.initForm();
		this.initData();

	}
	async initData() {
		this.allType = await this.commonService.allType;
		this.pageModeList = this.allType[EquipType.VMS_PAGE_MODE];
		this.toggleModeList = this.allType[EquipType.VMS_TOGGLE_MODE];
		this.displayModeList = this.allType[EquipType.VMS_DISPLAY_MODE];
		this.vmsEquipTypeList = this.allType[EquipType.VMS_EQUIP_TYPE];
		this.fancontrolList = this.allType[EquipType.VMS_FAN_CONTROL_MODE];
		this.dimmingList = this.allType[EquipType.VMS_DIMMING_MODE];
		this.listExpWay = this.allType[EquipType.EXPWAY_CODE];
		this.vmsMsgCategory = this.allType[EquipType.VMS_MSG_CATEGORY];
		// console.log(this.listVMSPicGroup);
		this.listExpWaySearch.push(...this.listExpWay);
		this.vmsEquipTypeListSearch.push(...this.vmsEquipTypeList);
		this.sort({ key: 'description', value: 'ascend' });
		this.initVMSEquipIdList();
	}
	sort(sort: { key: string; value: string }): void {
		this.sortName = sort.key;
		this.sortValue = sort.value;
		this.search();
	}
	search(): void {
		const data = this.allType[EquipType.VMS_PICGROUP];
		const arr = data.sort((a, b) =>
			this.sortValue === 'ascend'
				? a[this.sortName!] > b[this.sortName!]
					? 1
					: -1
				: b[this.sortName!] > a[this.sortName!]
					? 1
					: -1
		);
		this.listVMSPicGroup.push(...arr);
		// console.log(this.listVMSPicGroup);
	}

	sortP({ key, value }): void {
		this.sortName = key;
		this.sortValue = value;
		this.sortList();
	}
	sortList() {
		if (this.sortName && this.sortValue) {
			const arr = this.listOfPictogram.sort((a, b) =>
				this.sortValue === 'ascend'
					? a[this.sortName] > b[this.sortName]
						? 1
						: -1
					: b[this.sortName] > a[this.sortName]
						? 1
						: -1
			);
			this.listOfPictogram = [...arr];
		}
	}

	modeChange() {
		const { toggle_mode, display_mode } = this.validateForm.value;
		this.toggle_mode = toggle_mode;
		this.display_mode = display_mode;
	}

	filterChange() {
		this.listDisplayEquipIDSearch = [];
		const { equipType, expWay } = this.validateForm.value;
		this.listEquipID.forEach((item) => {
			if ((!equipType || equipType === item.equipType || equipType === 'All') &&
				(!expWay || expWay === item.expwayCode || expWay === 'All')
			) {
				this.listDisplayEquipIDSearch.push(item);
			}
		});
		if (this.listDisplayEquipIDSearch.length > 0) {
			this.validateForm.patchValue({
				equipID: this.listDisplayEquipIDSearch[0],
			});
		}

	}
	initVMSEquipIdList() {
		this.commonService.getEquipConfig$().pipe(takeUntil(this.unSubscribe)).subscribe(e => {
			e.forEach((item) => {
				this.vmsEquipTypeList.forEach((data) => {
					if (item.equipType === data.value) {
						this.listEquipID.push(item);
					}
				});

			});
			this.listDisplayEquipIDSearch = this.listEquipID;
			this.listUploadEquipIDSearch = this.listEquipID;
		});

	}

	searchEquipId(): void {
		// console.log(this.validateForm.value.equipID);
		if (this.validateForm.value.equipID === null) {
			this.message.create('error', `Please Select an Equip Id`);
			return;
		}
		this.selectEuipment = this.validateForm.value.equipID;
		const queryData = {
			equipId: this.selectEuipment.equipId
		};
		this.equipmentService.getVmsMsgByEquipId(queryData).subscribe((r: any) => {
			let temp: any;
			temp = r.Body.getVmsMsgByEquipIdResponse.vmsMsgDto;
			if (!temp)
				return;
			const isTipEditable = this.selectEuipment.equipType === 'tip' || this.selectEuipment.equipType === 'ttp';
			if (isTipEditable) {
				this.validateForm.controls['display_mode'].enable();
			} else {
				this.validateForm.controls['display_mode'].disable();
			}
			// console.log(this.isTipEditable);
			this.vmsMsgSource.page = 0;
			this.vmsMsgSource = { ...this.vmsMsgSource, ...temp };
			this.setRightPartStyles();
			// console.log(this.vmsMsgSource);
			// const _tmp = JSON.stringify(this.vmsMsgSource[0]);
			// this.tempTemplate = JSON.parse(_tmp);
			// this.tempTemplate = this.vmsMsgSource[0];
			this.listExpWay.forEach((expWay) => {
				if (this.selectEuipment.expwayCode === expWay.value) {
					this.selectEuipment.expWay = expWay.description;
				}
			});
			this.validateForm.patchValue({
				toggle_mode: temp.toggleMode,
				display_mode: temp.displayMode
			});
			this.tabShow = true;
			// this.isEditable = true;
		});
		this.statusArr.forEach((item, idx) => {
			const queryEquipStatus = {
				equipId: this.selectEuipment.equipId,
				statusCode: item.key
			};
			this.equipmentService.getStatusByEquipIdAndStatusCode(queryEquipStatus).subscribe((r: any) => {
				let temp: any;
				temp = r.Body.getStatusByEquipIdAndStatusCodeResponse.status;
				item.value = temp;
				if (idx === 0) {
					if (temp === '0' || this.vmsMsgSource.equipType === 'tic') {
						this.isEditable = false;
					} else if (temp === '2') {
						this.isEditable = true;
					}
					item.value = this.operationalList[temp];
				}
				if (idx === 4) {
					this.dimmingResult = temp;
					this.dimmingResultRec = this.getDimmingResult(temp);
				}
				if (idx === 6) {
					this.fancontrolResult = temp;
					this.fancontrolResultRec = this.getFancontrolResult(temp);
				}
			});
		});

		this.getAllVmsTemplate();
		setTimeout(() => {
			this.setSendMsgEquipList();
			this.getTechnicalAlarmByEquipIdList();
		}, 200);
	}

	getDimmingResult(temp) {
		const lableObj = this.dimmingList.find(e => e.value === temp) || {};
		return lableObj.description;
	}
	getFancontrolResult(temp) {
		const fancontrol = this.fancontrolList.find(e => e.value === temp) || {};
		return fancontrol.description;
	}
	initForm() {
		this.validateForm = this.fb.group({
			expWay: [{ value: 'All', disabled: false }],
			equipType: [{ value: 'All', disabled: false }],
			equipID: [{ value: null, disabled: false }],
			equip_type: [{ value: null, disabled: false }],
			equip_id: [{ value: null, disabled: false }],
			ip_address: [{ value: null, disabled: false }],
			toggle_mode: [{ value: null, disabled: false }],
			phase: [{ value: null, disabled: false }],
			exp_way: [{ value: null, disabled: false }],
			update_time: [{ value: null, disabled: false }],
			display_mode: [{ value: null, disabled: false }]
		});
	}

	submitForm(): void {
		// console.log(this.validateForm);
	}

	getAllVmsPictogramConfig(): void {
		const picGroupId = this.vmpPicGroupId;
		// const picGroupId = '';
		const equipType = this.vmsMsgSource.equipType;
		if (this.choosenPicH == 48 && this.choosenPicW == 48) {
			switch (equipType) {
				case 'ttp':
					this.choosenPicH = 30;
					this.choosenPicW = 30;
					break;
				case 'tip':
				case 'tep':
					this.choosenPicH = 60;
					this.choosenPicW = 60;
					break;
				default:
					break;
			}
		}
		if (picGroupId !== 0) {
			this.equipmentService.getVmsPictogramConfigByGroupIdAndDimension(picGroupId, this.choosenPicH, this.choosenPicW).subscribe((r) => {
				const res: any = r;
				let resData = res.Body.getVmsPictogramConfigByGroupIdAndDimensionResponse.vmsPictogramConfigDtoList;
				// console.log(r);
				resData = resData ? resData : [];
				this.listOfPictogram = resData;
			});
		} else {

			this.equipmentService.getAllVmsPictogramConfig().subscribe((r) => {
				const res: any = r;
				const resData = res.Body.getAllVmsPictogramConfigResponse.vmsPictogramConfigDtoList;
				this.listOfPictogram = [];
				resData.forEach(item => {
					if (item.width === this.choosenPicW && item.height === this.choosenPicH) {
						this.listOfPictogram.push(item);
					}
				});
			});
		}
	}

	showIncidentPicModal(index, pages): void {
		this.isVisible = true;
		this.vmsMsgSource.page = pages;
		this.choosenPic = index;
		this.vmpPicGroupId = null;
		this.listOfPictogram = [];
		//console.log('choosenPic: ',this.choosenPic);
	}

	// getImgsrc(img): void {
	// 	let imgSrc = `data:image/png;base64,${img.graphicContents}`;
	// 	let newImg = new Image();
	// 	newImg.src = imgSrc;
	// 	newImg.addEventListener("load", () => {
	// 		this.choosenPicH = newImg.naturalHeight;
	// 		this.choosenPicW = newImg.naturalWidth;
	// 		//console.log('the new img\'s real width and height is ', newImg.naturalWidth, newImg.naturalHeight)
	// 	});
	// }
	getImgsrc(img): void {
		// console.log('img:',img);
		this.choosenPicH = img.height;
		this.choosenPicW = img.width;
	}

	// VMS模板处理
	getAllVmsTemplate(): void {
		this.equipmentService.GetAllVmsTemplate().subscribe((res: any) => {
			// console.log(res);
			let temp: any;
			temp = res.Body.getAllVmsTemplateResponse.vmsTemplateConfigDtoList;
			this.listOfTemplate = temp;
			this.listOfTemplateDisplay = [];
			this.listOfTemplate.forEach((item) => {
				if (item.equipType === this.selectEuipment.equipType && item.phase === this.selectEuipment.phase) {
					this.listOfTemplateDisplay.push(item);
				}
			});
		});
	}

	setRightPartStyles() {
		const settingTemplate = this.vmsMsgSource;
		const vmsPage = settingTemplate.vmsMsgPageDtoList[this.vmsMsgSource.page];
		this.showOnly[this.vmsMsgSource.page] = {
			textList: [],
			imgList: [],
			textBoxStyle: [],
			textBox: [],
			inputSet: [],
			rightPartStyles: {}
		}
		this.transferData(vmsPage);
		const numData = vmsPage.vmsTemplateConfigDto;
		// this.ratio0 = ($('.content0').width() / parseInt(numData.width, 0)).toFixed(2);
		//this.ratio1 = this.getRatio('.content', numData);
		this.ratio1 = this.getRatio('#vms-text', numData);
		this.showOnly[this.vmsMsgSource.page].rightPartStyles = {
			height: settingTemplate ? parseInt(numData.height, 0) * this.ratio1 + 'px' : '80px',
			width: settingTemplate ? parseInt(numData.width, 0) * this.ratio1 + 'px' : '280px',
			// 'height': '50%',
			// 'width': '100%',
			background: settingTemplate ? 'black' : 'lightblue',
			// 'margin-top': this.selectItem ? '50px' : '50px',
			position: settingTemplate ? 'relative' : 'relative'
		};
		this.getCOntentStyle(vmsPage);
	}

	transferData(vmsPage) {
		// console.log('-------first----------');
		// console.log(arr)
		const fontSize = this.allType[EquipType.VMS_FONTTYPE];
		// console.log(fontSize);
		const picConfigDtoList = vmsPage.vmsTemplateConfigDto.vmsTemplatePicConfigDtoList;
		const textlineConfigDtoList = vmsPage.vmsTemplateConfigDto.vmsTemplateTextlineConfigDtoList;
		const flag1 = picConfigDtoList instanceof Array;
		const flag2 = textlineConfigDtoList instanceof Array;
		if (picConfigDtoList && !flag1) {
			// console.log('1');
			const PicConfigDtoListArr = [];
			PicConfigDtoListArr.push(picConfigDtoList);
			const copyA = JSON.stringify(PicConfigDtoListArr);
			vmsPage.vmsTemplateConfigDto.vmsTemplatePicConfigDtoList = JSON.parse(copyA);
		}
		if (vmsPage.vmsTemplateTextlineConfigDtoLis && !flag2) {
			// console.log('2');
			const TextlineConfigDtoLisArr = [];
			TextlineConfigDtoLisArr.push(textlineConfigDtoList);
			const copyB = JSON.stringify(TextlineConfigDtoLisArr);
			vmsPage.vmsTemplateConfigDto.vmsTemplateTextlineConfigDtoList = JSON.parse(copyB);
		}
		if (picConfigDtoList) {
			// console.log('3');
			vmsPage.vmsTemplateConfigDto.vmsTemplatePicConfigDtoList.sort((a, b) => a.picSeq - b.picSeq);
		}
		if (textlineConfigDtoList) {
			// console.log('4');
			vmsPage.vmsTemplateConfigDto.vmsTemplateTextlineConfigDtoList.sort((a, b) => a.textLineNo - b.textLineNo);
			vmsPage.vmsTemplateConfigDto.vmsTemplateTextlineConfigDtoList.forEach((item) => {
				fontSize.forEach((item1) => { // 获取方格的height和width
					if (item.fontTypeId === item1.value) {
						const description = item1.description;
						const descriptionList = description.split('*');
						// console.log('-------height * width----------');
						// console.log(descriptionList);
						item.height = descriptionList[0];
						item.width = descriptionList[1];
					}
				});
			});
		}
	}

	getCOntentStyle(vmsPage) {
		this.pageMode = vmsPage.pageMode || '0';
		const picConfigDtoList = vmsPage.vmsTemplateConfigDto.vmsTemplatePicConfigDtoList;
		// vmsMsgPage?.vmsTemplateConfigDto?.vmsTemplatePicConfigDtoList[indx]?.vmsPictogramConfigDto?.graphicContents
		const textlineConfigDtoList = vmsPage.vmsTemplateConfigDto.vmsTemplateTextlineConfigDtoList;
		const showOnly = {
			textList: [],
			imgList: [],
			textBoxStyle: [],
			textBox: [],
			inputSet: [],
		};
		const ratio = this.ratio1;
		let textBoxIndex = 0;
		if (picConfigDtoList) {
			picConfigDtoList.forEach((item, index) => {
				const data = { // 图片样式<img>
					'height': picConfigDtoList ? item.height * ratio + 'px' : '0px',
					'width': picConfigDtoList ? item.width * ratio + 'px' : '0px',
					// tslint:disable-next-line: max-line-length
					'margin-left': picConfigDtoList ? item.xCord * ratio + 'px' : '0px',
					// tslint:disable-next-line: max-line-length
					'margin-top': picConfigDtoList ? item.yCord * ratio + 'px' : '0px',
					'position': picConfigDtoList ? 'absolute' : 'absolute',
					'border': picConfigDtoList ? '1px solid yellow' : '1px solid yellow',
				};
				showOnly.imgList.push(data);
			});
		}
		if (!vmsPage.vmsPictogramConfigDtoList || vmsPage.vmsPictogramConfigDtoList.length === 0) {
			const tmp = picConfigDtoList || this.selectItem.vmsTemplatePicConfigDtoList;
			if (tmp) {
				const copyItem = JSON.parse(JSON.stringify(tmp));
				vmsPage.vmsPictogramConfigDtoList = copyItem.map(item => item.vmsPictogramConfigDto);
			}
			// vmsPage.vmsPictogramConfigDtoList = copyItem.vmsTemplatePicConfigDtoList;
			// vmsPage.vmsPictogramConfigDtoList.forEach((item, indx) => {
			// 	item.graphicContents = copyItem.vmsTemplatePicConfigDtoList[indx].vmsPictogramConfigDto.graphicContents;
			// });
		}
		if (textlineConfigDtoList) {
			textlineConfigDtoList.forEach((item, index) => {
				showOnly.textList.push({ // 文本框<ul>
					'margin-left': textlineConfigDtoList ? item.xCord * ratio + 'px' : '0px',
					'margin-top': textlineConfigDtoList ? item.yCord * ratio + 'px' : '0px',
					'position': textlineConfigDtoList ? 'absolute' : 'absolute'
				});
				showOnly.textBoxStyle.push({ // 文本框样式<li>
					'height': textlineConfigDtoList ? item.height * ratio - 3 + 'px' : '0px',
					'width': textlineConfigDtoList ? item.width * ratio - 3 + 'px' : '0px',
					'margin-right': textlineConfigDtoList ? item.charSpacing * ratio + 'px' : '0px',
					'border': textlineConfigDtoList ? '1px solid white' : '1px solid white',
					'display': textlineConfigDtoList ? 'inline-block' : 'inline-block',
					// 'background-color': '#000'
				});
				showOnly.textBox.push(item.maxNoChar);
				const tempArr = [];
				let keyList = this.textTemplateList;
				let colorKeyList = [];
				if (vmsPage.lineTextDtoList) {
					keyList = [];
					if (vmsPage.lineTextDtoList[index]) {
						const { textMsg, colorMsg } = vmsPage.lineTextDtoList[index];
						keyList = textMsg.split('');
						colorKeyList = colorMsg.split('');
					}
				}
				for (let i = 0; i < item.maxNoChar; i++) {
					// if(keyList[i]==null||keyList[i].length<1){
					// 	keyList[i]=' ';
					// }
					const data = {
						index: textBoxIndex,
						key: keyList[i],
						color: this.textColorList[colorKeyList[i] - 1] || 'white',
						colorKey: colorKeyList[i]
					};
					tempArr.push(data);
					textBoxIndex++;
				}
				showOnly.inputSet.push(tempArr);
			});
		}
		this.showOnly[this.vmsMsgSource.page] = { ...this.showOnly[this.vmsMsgSource.page], ...showOnly };
	}
	getTextBoxValue() {

	}

	handleOk(): void {
		// console.log('Button ok clicked!');
		this.isVisible = false;
	}

	handleCancel(): void {
		// console.log('Button cancel clicked!');
		this.isVisible = false;
	}

	inputTextOnFocus(idx): void {
		this.selectItemIdx = idx;
	}
	/**
	 * 输入结束后判断自动跳转到下一个textbox,  并重新生成vmsMsgPage里的text内容，用于发送
	 * vmsMsgPageDto: 当前页vms的对象
	 * textRow: 被修改文字的所在行
	 */
	inputFinish(item, vmsMsgPageDto: VmsMsgPageDto, textRow: number): void {
		this.setText(vmsMsgPageDto, textRow);
		if (item !== '') {
			this.selectItemIdx++;
			setTimeout(() => {
				$('.active').focus();
			});
		}
	}
	setText(vmsMsgPageDto: VmsMsgPageDto, textRow: number) {
		let textMsg = '';
		let colorMsg = '';
		this.showOnly[this.vmsMsgSource.page].inputSet[textRow].forEach(e => {
			textMsg += e.key || ' ';
			colorMsg += e.colorKey || '7';
		});
		textMsg = textMsg.replace(/(\s*$)/g, ''); // 去除尾部空格
		colorMsg = colorMsg.substr(0, textMsg.length);
		vmsMsgPageDto.lineTextDtoList[textRow] = { colorMsg, textMsg };
	}

	chooseTemplatePic(item): void {
		this.choosenPictogram = item;
		//console.log("ImgData: ", item);
	}

	selectPictogramComfirm() {
		if (this.choosenPictogram.pictogramId) {
			// tslint:disable-next-line:max-line-length
			this.vmsMsgSource.vmsMsgPageDtoList[this.vmsMsgSource.page].vmsPictogramConfigDtoList[this.choosenPic] = this.choosenPictogram;
			this.isVisible = false;
		} else {
			this.message.create('error', 'Please select a pictogram');
		}
	}

	cancelPictogramComfirm() {
		this.isVisible = false;
	}

	selectTemplate(): void {
		this.isVisibleTemplateSelect = true;
	}

	handleCancelTemplateSelect(): void {
		this.isVisibleTemplateSelect = false;
	}

	selectTemplateComfirm(): void {

		const vmsTemplateConfigDto = JSON.parse(JSON.stringify(this.selectItem));
		const changePage: any = {
			vmsTemplateConfigDto,
			lineTextDtoList: [],
			vmsPictogramConfigDtoList: []
		};

		const pageList = this.vmsMsgSource.vmsMsgPageDtoList;
		if (this.isAddTemplate) {
			if (this.addTemplatePlace === 'delete') {
				this.vmsMsgSource.page = 0;
			} else if (this.addTemplatePlace === 'after') {
				this.vmsMsgSource.page += 1;
				pageList.splice(this.vmsMsgSource.page, 0, null);
			} else if (this.addTemplatePlace === 'before') {
				this.vmsMsgSource.page += 1;
				pageList.splice(this.vmsMsgSource.page, 0, null);
			}
			this.isAddTemplate = false;
			this.addTemplatePlace = '';
		}
		pageList[this.vmsMsgSource.page] = changePage;
		this.vmsMsgSource.vmsMsgPageDtoList = [...pageList]; // 触发tabset 变更检测
		this.setRightPartStyles();
		this.isVisibleTemplateSelect = false;
	}

	cancelTemplateComfirm(): void {
		this.isVisibleTemplateSelect = false;
	}

	changeSelectedTemplate(): void {
		this.selectItem = this.selectTemplateDisplay;
		this.setSelectRightPartStyles();
	}

	sendMessage(): void {
		this.applyMessageWindow = 'Send Message';
		this.isApplyTemplateVisible = true;
	}



	addLi(i, e) {
		// console.log(e);
		if (this.availableList.length !== 0) {
			if (e.ctrlKey) {
				this.addDisabled = false;
				this.clickIndex = i;
				this.availableList[i].active = this.availableList[i].active ? false : true;
			} else {
				this.addDisabled = false;
				this.availableList.forEach((item, index) => {
					item.active = false;
					if (i === index) {
						item.active = true;
					}
				});
			}
		}
	}

	// 右边穿梭框选中的值
	removeLi(i, e) {
		if (this.selectedList.length !== 0) {
			if (e.ctrlKey) {
				this.clickIndex = i;
				this.removeDisabled = false;
				this.selectedList[i].active = this.selectedList[i].active ? false : true;
			} else {
				this.removeDisabled = false;
				this.selectedList.forEach((item, index) => {
					item.active = false;
					if (i === index) {
						// console.log(item);
						item.active = true;
					}
				});
			}
		}
	}


	add() {
		const chooseList = this.getChooseList(this.availableList);
		this.availableList = this.availableList.filter(item => chooseList.indexOf(item) < 0);
		this.selectedList = [...this.selectedList, ...chooseList]; // 将选中的项添加到右边

		this.addDisabled = true;
		this.addAllDisabled = false;
		this.removeDisabled = true;
		this.removeAllDisabled = false;
	}

	addAll() {
		this.availableList.forEach(item => {
			this.selectedList.push(item);
			item.active = false;
		});
		this.availableList = [];
		this.addAllDisabled = true;
		this.removeAllDisabled = false;
	}

	getChooseList(list) {  // 按下ctrl多选的项
		const chooseList = list.filter(item => {
			if (item.active) {
				item.active = false;
				return true;
			} else {
				return false;
			}
		});
		return chooseList;
	}

	remove() {
		const chooseList = this.getChooseList(this.selectedList);
		this.selectedList = this.selectedList.filter(item => chooseList.indexOf(item) < 0);
		this.availableList = [...this.availableList, ...chooseList]; // 将移除的项添加到左边
		this.removeDisabled = true;
		this.addAllDisabled = false;
	}

	removeAll() {
		this.selectedList.forEach(item => {
			this.availableList.push(item);
			item.active = false;
		});
		this.selectedList = [];
		this.removeAllDisabled = true;
		this.addAllDisabled = false;
		this.removeDisabled = true;
	}

	// 模态框右上角×号
	handleApplyTemplateCancel() {
		this.isApplyTemplateVisible = false;
		this.dimmingTimeSet(false);
	}

	loadLibrary() {
		this.isSelectLibraryMsgViewVisible = true;
	}

	handleSelectLibraryMsgViewCancel() {
		this.isSelectLibraryMsgViewVisible = false;
	}

	commonSend(type) {
		const queryEquipIdList = [];


		if (this.selectedList.length !== 0) {
			this.selectedList.forEach((item) => {
				queryEquipIdList.push(item.equipId);
			});
			let content = `Apply to ${this.selectedList.length} equipments`;
			if (type === 'dimmingTime') {
				const { isSet, time1, time2, time3 } = this.dimingTimetable;
				if (isSet &&
					(!time1 || !time2 || !time3)) {
					this.message.create('warning', `Please set all time table`);
					return;
				}
				content = `Config  ${this.selectedList.length} time tables`;
			}
			this.dialog
				.confirm({
					title: this.applyMessageWindow,
					content,
					buttonOkTxt: 'Yes',
					buttonCancelTxt: 'No'
				})
				.subscribe(res => {
					if (res) {
						this.isSpinning = true;
						queryEquipIdList.forEach((item, idx) => {
							if (type === 'dimmingTime') {
								this.sendDimmingTime(item, idx);
							} else {
								this.sendApplyMessage(item, idx);
							}
						});
						this.childcomponent.showModal(true);
						this.getSocket(this.selectedList);
					} else {
						this.cancelSend();
					}
				});

		} else {
			this.message.create('warning', `Please select an item to send`);
		}
	}

	sendDimmingTime(item, idx) {
		this.modalListInt(this.selectedList, idx, 'Config time table', 3);
		this.setDimmingTimeTable(item, this.dimingTimetable).subscribe((data: any) => {
			// this.dimmingTimeSet(false);
		});
	}

	sendApplyMessage(item, idx) {
		// console.log(this.applyMessageWindow);
		/*  CmdResp = 3,//dto
			EquipStatus = 4,//get from database
			TechAlarm = 5,//get from database
			PixelFailureBmpFile = 21,//dto
			UploadPictogram = 23//dto */

		if (this.applyMessageWindow === 'Send Message') {
			// this.vmsMsgSource.execId = 'AW' + '_AW2_' + moment().format('HHmmssSSS'),
			this.vmsMsgSource.execId = `AW_${localStorage.getItem('user_name')}_${moment().format('HHmmssSSS')}`;
			this.vmsMsgSource.cmdId = idx;
			this.vmsMsgSource.sender = localStorage.getItem('user_name');
			this.vmsMsgSource.equipId = item;
			this.vmsMsgSource.toggleMode = this.toggle_mode;
			this.vmsMsgSource.displayMode = this.display_mode;
			let msgPageDtoList = this.vmsMsgSource.vmsMsgPageDtoList;
			let tempName = '';
			for (let dto of msgPageDtoList) {
				dto['vmsTemplateId'] = dto.vmsTemplateConfigDto.templateId;
				tempName = dto.vmsTemplateConfigDto.templateName;
				if (dto.vmsPictogramConfigDtoList) {
					let tempStr = "";
					for (let pdto of dto.vmsPictogramConfigDtoList) {
						if (tempStr.length == 0) {
							tempStr = pdto.pictogramId;
						} else {
							tempStr = `${tempStr},${pdto.pictogramId}`;
						}
					}
					dto['vmsPictogramConfigId'] = tempStr;
				}
				if (!dto.lineTextDtoList) {
					dto.lineTextDtoList = []
				} else {
					// for filter Msg have &<>'"
					dto.lineTextDtoList.forEach(e => {
						e.textMsg = e.textMsg.replace(/&/g, "&amp;");
						e.textMsg = e.textMsg.replace(/</g, "&lt;");
						e.textMsg = e.textMsg.replace(/>/g, "&gt;");
						e.textMsg = e.textMsg.replace(/'/g, "&apos;");
						e.textMsg = e.textMsg.replace(/"/g, "&quot;");
					});
				}
				if (dto.vmsTemplateConfigDto.vmsTemplateTextlineConfigDtoList) {
					dto.vmsTemplateConfigDto.vmsTemplateTextlineConfigDtoList.forEach((item, index) => {
						let tempColor = "";
						let tempStr = "";
						if (!dto.lineTextDtoList[index]) {
							dto.lineTextDtoList[index] = {
								colorMsg: tempColor.padEnd(item.maxNoChar, "7"),
								textMsg: tempStr.padEnd(item.maxNoChar, " ")
							}
						}
						if (dto.lineTextDtoList[index].textMsg.length < 1) {
							dto.lineTextDtoList[index].textMsg = tempStr.padEnd(item.maxNoChar, " ")
						}
					});
				}
				//console.log(dto)
			} // end for
			//console.log('now',tempName,this.vmsMsgSource.vmsMsgPageDtoList)
			if (tempName.toUpperCase().indexOf('BLANK') >= 0) {				
				for (let dtoList of this.vmsMsgSource.vmsMsgPageDtoList) {
					delete dtoList.vmsPictogramConfigDtoList;
					delete dtoList.lineTextDtoList;
					delete dtoList['vmsPictogramConfigId'];
				}
			}
			if (this.vmsMsgSource.equipType === 'tsp') {
				for (let dtoList of this.vmsMsgSource.vmsMsgPageDtoList) {
					delete dtoList.lineTextDtoList
				}
			}
			this.confirmSend(this.vmsMsgSource, idx);
		}
		if (this.applyMessageWindow === 'Setting Dimming') {
			this.setDimming(item, this.dimmingLevel);
		}
		if (this.applyMessageWindow === 'Setting Fan Control') {
			this.setFanControl(item, idx);
		}
		if (this.applyMessageWindow === 'Setting Flashing Time') {
			this.setFlashingTime(item, idx);
		}
		this.modalListInt(this.selectedList, idx, this.applyMessageWindow, 3);
	}
	getSocket(arr): void {
		//console.log(arr);
		this.webSocketService.getMessage().pipe(
			takeUntil(this.abortWebsocket$.asObservable())
		).subscribe((msg: any) => {
			let count = 0;
			// 心跳通信 type === 'HeartBeat'
			try {
				const response = JSON.parse(msg.data);
				if (response.type !== 'HeartBeat') {
					arr.forEach((item, idx) => {
						if (item.equipId === response.EquipId) {
							count++;
							clearTimeout(this.timerArr[idx]);
							item.status = 'Success';
						}
					});
					if (count === this.selectedList.length) {
						this.selectedList = [];
						this.timerArr = [];
						this.abortWebsocket$.next();
						this.childcomponent.closeLoading();
						this.isSpinning = false;
					}
				}
			} catch (e) {
				console.warn('websocket process warn:', e);
			}
		});
	}

	modalListInt(arr, index, text, sendingType): void {
		arr[index].idAndLoca = this.selectedList[index].equipId + '-' + this.selectEuipment.expWay;
		arr[index].equipmentId = this.selectedList[index].equipId;
		arr[index].location = this.selectEuipment.expWay;
		arr[index].command = text;
		arr[index].status = 'Processing';
		const timer = setTimeout(() => {
			arr[index].status = 'Time Out';
			if (index === arr.length - 1) {
				this.timerArr = [];
				this.abortWebsocket$.next();
				this.childcomponent.closeLoading();
				this.isSpinning = false;
			}
		}, 12000);
		this.timerArr.push(timer);
		this.sendingType = sendingType;
	}

	confirmSend(data, idx) {
		// console.log(this.selectEuipment);
		this.equipmentService.AW_CFELS_VMS({ 'vmsMsg': data }).subscribe((res: any) => {
			// console.log(this.selectEuipment);
			this.dimmingTimeSet(false);
		});
	}

	cancelSend() {
		this.childcomponent.closeModal();
	}

	loadBlankMsg() {
		this.isLoadBlankVisible = true;
	}
	handleLoadBlankCancel(): void {
		this.isLoadBlankVisible = false;
	}
	handleLoadBlankOk(): void {
		const _tmp = this.listOfTemplateDisplay.find(t => t.templateName.toLowerCase().indexOf('blank') >= 0);
		this.vmsMsgSource.page = 0;
		this.vmsMsgSource.vmsMsgPageDtoList[0].vmsTemplateConfigDto = JSON.parse(JSON.stringify(_tmp));
		this.vmsMsgSource.vmsMsgPageDtoList = [this.vmsMsgSource.vmsMsgPageDtoList[0]];
		this.setRightPartStyles();
		this.isLoadBlankVisible = false;
	}

	setSendMsgEquipList() {
		this.availableList = [];
		this.listEquipID.forEach((item) => {
			if (item.equipId === this.selectEuipment.equipId) {
				this.selectedList = [item];
				return;
			}
			if (item.equipType === this.selectEuipment.equipType) {
				this.availableList.push(item);
			}
		});
		this.addDisabled = true;
		this.addAllDisabled = false;
		this.removeDisabled = true;
		this.removeAllDisabled = false;
	}

	resetEquipment(): void {
		const _tmp = JSON.stringify(this.selectEuipment);
		const arrTemp = JSON.parse(_tmp);
		this.dialog
			.confirm({
				title: 'Confirmation Window',
				content: `Reset ${arrTemp.equipId}`,
				buttonOkTxt: 'Yes',
				buttonCancelTxt: 'No'
			})
			.subscribe(res => {
				if (res) {
					this.selectedList = [];
					const queryData = {
						cmdId: '1',
						equipId: arrTemp.equipId,
						execId: 'AW' + '_name_' + moment().format('HHmmss'),
						sender: localStorage.getItem('user_name'),
						equipmentId: arrTemp.equipId,
						location: arrTemp.expwayCode,
						command: 'Reset.',
						status: 'TimeOut'
					};
					this.equipmentService.AW_CFELS_Reset({ 'reset': queryData }).subscribe((res: any) => {
						this.selectedList.push(queryData);
						this.getSocket(this.selectedList);
						this.modalListInt(this.selectedList, 0, 'Reset.', 3);
						this.childcomponent.showModal(true);
					});
				}
			});
	}
	handleRequestPixelCancel(): void {
		this.isRequestPixelVisible = false;
	}
	handleRequestPixelPageCancel(): void {
		this.isRequestPixelPageVisible = false;
	}
	handleRequestPixelOk(): void {
		this.isRequestPixelVisible = false;
		this.isSpinning = true;
		this.sitePictogram = null;
		this.selectedList = [];
		const queryData = {
			equipId: this.selectEuipment.equipId,
			sender: localStorage.getItem('user_name'),
		};
		this.equipmentService.AW_CFELS_PixelFailureBMPFile(queryData).subscribe((res: any) => {
		});
		this.webSocketService.getMessage().pipe(
			takeUntil(this.abortWebsocket$.asObservable())
		).subscribe((msg: any) => {
			// 心跳通信 type === 'HeartBeat'
			try {
				const response = JSON.parse(msg.data);
				if (response.type !== 'HeartBeat') {
					clearTimeout(this.timerArr[0]);
					this.isSpinning = false;
					this.timerArr = [];
					this.abortWebsocket$.next();
					this.modalTitle6 = 'Date Time: ' + response.DateTime;
					this.sitePictogram = this.arrayBufferToBase64(response.Image);
				}
			} catch (e) {
				console.warn('websocket process warn:', e);
			}
		});
		const timer = setTimeout(() => {
			this.timerArr = [];
			this.abortWebsocket$.next();
			this.isSpinning = false;
			this.message.create('error', `Time Out!`);
		}, 30000);
		this.timerArr.push(timer);
		this.isRequestPixelPageVisible = true;
	}

	requestPixelFailureBMP(): void {
		this.isRequestPixelVisible = true;
	}



	updateDimmingMode() {
		this.isDimmingModeVisible = true;
	}
	handleDimmingModeCancel() {
		this.isDimmingModeVisible = false;
	}
	handleDimmingModeOk() {
		this.isDimmingModeVisible = false;
		this.selectDimming();
	}
	selectDimming() {
		this.isApplyTemplateVisible = true;
		this.applyMessageWindow = 'Setting Dimming';
		this.availableList = this.listDisplayEquipIDSearch;
	}

	setDimming(item, dimmingLevel) {
		const queryData = {
			'dimming': {
				execId: 'AW' + '_name_' + moment().format('HHmmss'),
				cmdId: '1',
				sender: localStorage.getItem('user_name'),
				equipId: item,
				systemId: this.selectEuipment.systemId,
				dimMode: this.dimmingResult,
				dimLevel: this.dimmingResult === '0' ? dimmingLevel : 0
			}
		};
		this.equipmentService.AW_CFELS_Dimming(queryData).subscribe((data: any) => {

		});
	}
	setDimmingTimeTable(item, timeLevel) {
		let vmsTimetableConfig: any = {
			execId: 'AW' + '_name_' + moment().format('HHmmss'),
			cmdId: '1',
			sender: localStorage.getItem('user_name'),
			equipId: item,
			systemId: this.selectEuipment.systemId,
		};
		vmsTimetableConfig = { ...vmsTimetableConfig, ...timeLevel };
		vmsTimetableConfig.time1 = moment(vmsTimetableConfig.time1).format('HH:mm');
		vmsTimetableConfig.time2 = moment(vmsTimetableConfig.time2).format('HH:mm');
		vmsTimetableConfig.time3 = moment(vmsTimetableConfig.time3).format('HH:mm');
		return this.equipmentService.AW_CFELS_VMSTimetable({ vmsTimetableConfig })
	}
	commandExpWayChange() {
		this.getEquipIdAvailableList();
	}
	commandEquipTypeChange() {
		this.getEquipIdAvailableList();
	}
	getEquipIdAvailableList() {
		this.availableList = [];
		this.listDisplayEquipIDSearch.forEach(item => {
			if ((this.expWayCom === 'All' || item.expwayCode === this.expWayCom) &&
				(this.equipTypeCom === 'All' || item.equipType === this.equipTypeCom)) {
				this.availableList.push(item);
			}
		});
	}

	updateFanControl() {
		this.isFanControlVisible = true;
	}
	handleFanControlCancel() {
		this.isFanControlVisible = false;
	}
	handleFanControlOk() {
		// console.log(this.fancontrolResult);
		this.isFanControlVisible = false;
		this.selectFancontrol();
	}

	selectFancontrol() {
		this.isApplyTemplateVisible = true;
		this.applyMessageWindow = 'Setting Fan Control';
		this.availableList = this.listDisplayEquipIDSearch;
	}

	setFanControl(item, idx) {
		const queryData = {
			'fanOpeMode': {
				execId: 'AW' + '_name_' + moment().format('HHmmss'),
				cmdId: '1',
				sender: localStorage.getItem('user_name'),
				equipId: item,
				opeMode: this.fancontrolResult === '2' ? this.temperatureThreshold : 1
			}
		};
		this.equipmentService.AW_CFELS_FanOpeMode(queryData).subscribe((data: any) => {

		});
	}



	onTimeCheck() {
		this.applyMessageWindow = 'Setting Flashing Time';
		this.isApplyTemplateVisible = true;
		this.availableList = this.listDisplayEquipIDSearch;

	}

	setFlashingTime(item, idx) {
		const queryData = {
			'arg0': {
				execId: 'AW' + '_name_' + moment().format('HHmmss'),
				cmdId: '1',
				sender: localStorage.getItem('user_name'),
				equipId: item,
				offTime: this.flashingOnTime,
				onTime: this.flashingOnTime
			}
		};
		this.equipmentService.AW_CFELS_FlashingTime(queryData).subscribe((data: any) => {

		});
	}

	setSelectRightPartStyles() {
		this.transferSelectData();
		// console.log(this.selectItem);
		if ($('.content0').height() / $('.content0').width() > parseInt(this.selectItem.height, 0) / parseInt(this.selectItem.width, 0)) {

		}
		this.ratio0 = this.getRatio('.content0', this.selectItem);
		// const height = parseInt(this.selectItem.height, 0) * this.ratio1;
		// if (height > $('.content0').height()) {
		// 	this.ratio0 = ($('.content0').height() / parseInt(this.selectItem.height, 0)).toFixed(2);
		// }
		this.selectRightPartStyles = {
			'height': this.selectItem ? this.selectItem.height * this.ratio0 + 'px' : '80px',
			'width': this.selectItem ? this.selectItem.width * this.ratio0 + 'px' : '280px',
			'background': this.selectItem ? 'black' : 'lightblue',
			'position': this.selectItem ? 'relative' : 'relative'
		};
		this.getSelectCOntentStyle();
	}

	getRatio(content, item) {
		if ($(content).height() / $(content).width() > parseInt(item.height, 0) / parseInt(item.width, 0)) {
			return ($(content).width() / parseInt(item.width, 0)).toFixed(2);
		}
		return ($(content).height() / parseInt(item.height, 0)).toFixed(2);
	}
	transferSelectData() {
		const fontSize = this.allType[EquipType.VMS_FONTTYPE];
		const picConfigDtoList = this.selectItem.vmsTemplatePicConfigDtoList;
		const textlineConfigDtoList = this.selectItem.vmsTemplateTextlineConfigDtoList;
		const flag1 = picConfigDtoList instanceof Array;
		const flag2 = textlineConfigDtoList instanceof Array;
		if (picConfigDtoList && !flag1) {
			const PicConfigDtoListArr = [];
			PicConfigDtoListArr.push(picConfigDtoList);
			this.selectItem.vmsTemplatePicConfigDtoList = PicConfigDtoListArr;
		}
		if (this.selectItem.vmsTemplateTextlineConfigDtoLis && !flag2) {
			const TextlineConfigDtoLisArr = [];
			TextlineConfigDtoLisArr.push(textlineConfigDtoList);
			this.selectItem.vmsTemplateTextlineConfigDtoList = TextlineConfigDtoLisArr;
		}
		if (picConfigDtoList) {
			this.selectItem.vmsTemplatePicConfigDtoList.sort((a, b) => a.picSeq - b.picSeq);
		}
		if (textlineConfigDtoList) {
			this.selectItem.vmsTemplateTextlineConfigDtoList.sort((a, b) => a.textLineNo - b.textLineNo);
			this.selectItem.vmsTemplateTextlineConfigDtoList.forEach((item) => {
				fontSize.forEach((item1) => { // 获取方格的height和width
					if (item.fontTypeId === item1.value) {
						const description = item1.description;
						const descriptionList = description.split('*');
						// console.log('-------height * width----------');
						// console.log(descriptionList);
						item.height = descriptionList[0];
						item.width = descriptionList[1];
					}
				});
			});
		}
	}

	getSelectCOntentStyle() {
		const picConfigDtoList = this.selectItem.vmsTemplatePicConfigDtoList;
		const textlineConfigDtoList = this.selectItem.vmsTemplateTextlineConfigDtoList;
		this.textList = [];
		this.imgList = [];
		this.textBoxStyle = [];
		this.textBox = [];
		if (picConfigDtoList) {
			this.selectItem.vmsTemplatePicConfigDtoList.forEach((item, index) => {
				this.imgList[index] = { // 图片样式<img>
					'height': picConfigDtoList ? item.height * this.ratio0 + 'px' : '0px',
					'width': picConfigDtoList ? item.width * this.ratio0 + 'px' : '0px',
					// tslint:disable-next-line: max-line-length
					'margin-left': picConfigDtoList ? item.xCord * this.ratio0 + 'px' : '0px',
					// tslint:disable-next-line: max-line-length
					'margin-top': picConfigDtoList ? item.yCord * this.ratio0 + 'px' : '0px',
					'position': picConfigDtoList ? 'absolute' : 'absolute',
					'border': picConfigDtoList ? '1px solid yellow' : '1px solid yellow',
				};
			});
		}
		if (textlineConfigDtoList) {
			this.selectItem.vmsTemplateTextlineConfigDtoList.forEach((item, index) => {
				this.textList[index] = { // 文本框<ul>
					'margin-left': textlineConfigDtoList ? item.xCord * this.ratio0 + 'px' : '0px',
					'margin-top': textlineConfigDtoList ? item.yCord * this.ratio0 + 'px' : '0px',
					'position': textlineConfigDtoList ? 'absolute' : 'absolute'
				};
				this.textBoxStyle[index] = { // 文本框样式<li>
					'height': textlineConfigDtoList ? item.height * this.ratio0 - 1 + 'px' : '0px',
					'width': textlineConfigDtoList ? item.width * this.ratio0 - 1 + 'px' : '0px',
					'margin-right': textlineConfigDtoList ? item.charSpacing * this.ratio0 + 'px' : '0px',
					'border': textlineConfigDtoList ? '1px solid white' : '1px solid white',
					'display': textlineConfigDtoList ? 'inline-block' : 'inline-block'
				};
				this.textBox.push(item.maxNoChar);
			});
		}
	}

	addTemplate(place): void {
		this.addTemplatePlace = place;
		this.isAddTemplate = true;
		this.selectTemplate();

	}

	tabChange(args: any): void {
		console.log(args)
	}

	deleteTemplate(): void {
		const { vmsMsgPageDtoList } = this.vmsMsgSource;
		if (!vmsMsgPageDtoList || vmsMsgPageDtoList.length === 1) {
			// this.message.create('warning', `Can not delete first page`);
			// return;
			this.addTemplatePlace = 'delete';
			this.isAddTemplate = true;
			this.selectTemplate();
			return;
		}
		this.isDeleteVisible = true;
	}

	handleDeleteOk(): void {
		this.vmsMsgSource?.vmsMsgPageDtoList.splice(this.vmsMsgSource.page, 1);
		this.isDeleteVisible = false;
	}

	handleDeleteCancel(): void {
		this.isDeleteVisible = false;
	}

	showColorSelect(item, index, pages) {
		// console.log(pages);
		this.showColorSelectPage = pages;
		this.changeColorOne = item;
	}

	changeInputColor(item, index, vmsMsgPageDto: VmsMsgPageDto, textRow: number) {
		// console.log(item);
		this.changeColorOne.color = item;
		this.changeColorOne.colorKey = index + 1;
		this.setText(vmsMsgPageDto, textRow);
	}

	getDataBySelectVmsLibrary(data) {
		this.vmsMsgSource.vmsMsgPageDtoList = data.vmsLibraryPageDtoList;
		this.setRightPartStyles();
		this.handleSelectLibraryMsgViewCancel();
	}

	handleUpLoadPictogramCancel(): void {
		this.isUpLoadPictogramVisible = false;
	}
	searchUploadPic(): void {
		this.isUpLoadPictogramVisible = true;
	}
	getVmsPictogramConfigByGroupIdAndEquipId() {
		if (!this.equipIDLibrary) {
			this.message.create('error', 'Please select an Equip id');
			return;
		}
		this.listOfPictogramLibrary = [];
		this.isPicSpinning = true;
		this.equipmentService.getVmsPictogramConfigByGroupIdAndEquipId(this.gourpIdLibrary, this.equipIDLibrary.equipId).subscribe((res: any) => {
			this.isPicSpinning = false;
			const data = res.Body.getVmsPictogramConfigByGroupIdAndEquipIdResponse;
			if (data) {
				this.listOfPictogramLibrary = data.vmsPictogramConfigDtoList;
			}
		});
	}

	uploadEquipTypeChange(): void {
		this.listUploadEquipIDSearch = [];
		this.listEquipID.forEach((item) => {
			if ((this.expWay === 'All' || item.expwayCode === this.expWay)
				&& (this.equipTypeLibrary === 'All' || item.equipType === this.equipTypeLibrary)) { // 全部equipid值
				this.listUploadEquipIDSearch.push(item);
			}
		});
		this.equipIDLibrary = this.listUploadEquipIDSearch[0];
	}
	uploadexpWayChange(): void {
		this.uploadEquipTypeChange();
	}
	upLoadPic(item): void {
		this.isUpLoadPictogramDatabaseVisible = true;
		this.isSpinning = true;
		this.picUploadItem = item;
		const data = {
			equipId: this.selectEuipment.equipId,
			pictogramId: this.picUploadItem.pictogramId,
			sender: localStorage.getItem('user_name')
		}
		this.equipmentService.AW_CFELS_UploadPictogram(data).subscribe((data: any) => {

		});
		this.webSocketService.getMessage().pipe(
			takeUntil(this.abortWebsocket$.asObservable())
		).subscribe((msg: any) => {
			// 心跳通信 type === 'HeartBeat'
			try {
				const response = JSON.parse(msg.data);
				if (response.type !== 'HeartBeat') {
					clearTimeout(this.timerArr[0]);
					this.timerArr = [];
					this.abortWebsocket$.next();
					this.sitePictogram = this.arrayBufferToBase64(response.Image);
					this.isSpinning = false;
				}
			} catch (e) {
				console.warn('websocket process warn:', e);
			}

		});
		const timer = setTimeout(() => {
			this.timerArr = [];
			this.abortWebsocket$.next();
			this.isSpinning = false;
			this.message.create('error', `Time Out!`);
		}, 12000);
		this.timerArr.push(timer);
	}
	handleUpLoadPictogramDatabaseCancel(): void {
		this.isUpLoadPictogramDatabaseVisible = false;
	}
	arrayBufferToBase64(buffer) {
		let binary = '';
		const bytes = new Uint8Array(buffer);
		const len = bytes.byteLength;
		for (let i = 0; i < len; i++) {
			binary += String.fromCharCode(bytes[i]);
		}
		return window.btoa(binary);
	}
	sendExpWayChange(): void {
		this.availableList = [];
		if (this.sendExpWay === 'All') {
			this.availableList = this.listEquipID;
		} else {
			this.listEquipID.forEach(item => {
				if (item.expwayCode === this.sendExpWay) {
					this.availableList.push(item);
				}
			});
		}
	}
	getTechnicalAlarmByEquipIdList() {
		const queryData = {
			equipId: this.selectEuipment.equipId
		}
		this.equipmentService.getTechnicalAlarmByEquipId(queryData).subscribe((r: any) => {
			const temp = r.Body.getTechnicalAlarmByEquipIdResponse.technicalAlarmList;
			//if (temp)
			this.alarmList = temp;
		});
	}

	checkDimmingTime() {
		const { time1, time3 } = this.dimingTimetable;
		let time2 = this.dimingTimetable.time2;
		if (time1 && time2 && time1 > time2) {
			this.dimingTimetable.time2 = new Date(time1);
			time2 = this.dimingTimetable.time2;
		}
		if (time2 && time3 && time2 > time3) {
			this.dimingTimetable.time3 = new Date(time2);
		}
	}
	dimmingTimeSet(isSet) {
		this.dimingTimetable.isSet = isSet ? isSet : !this.dimingTimetable.isSet;
		if (!this.dimingTimetable.isSet) {
			this.dimingTimetable.time1 = null;
			this.dimingTimetable.time2 = null;
			this.dimingTimetable.time3 = null;
		}
	}
	ngOnDestroy() {
		this.unSubscribe.next();
		this.unSubscribe.complete();
	}
}

