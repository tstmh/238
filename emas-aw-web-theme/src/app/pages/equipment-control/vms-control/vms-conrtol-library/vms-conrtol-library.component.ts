
import { Component, OnInit, Input, Output, EventEmitter, AfterViewInit, ViewChild, OnDestroy } from '@angular/core';
import { EquipmentService } from '../../equipment-control.service';

@Component({
	selector: 'emas-vms-conrtol-library',
	templateUrl: './vms-conrtol-library.component.html',
	styleUrls: ['./vms-conrtol-library.component.css']
})
export class VmsControlLibraryComponent implements OnInit {
	expWay;
	@Input() selectEuipment;
	categoryLibrary;
	listOfLibrary;
	selectLibrary: any = {}; // select library of library list panel ,to get vms by library id.
	isSpinning;
	@Input() listExpWay;
	@Input() vmsMsgCategory;
	@Output() selectEmit =  new EventEmitter();
	renderHeaderLibrary = [
		{
			name: 'Display Sequence',
			key: null,
			value: 'id',
			isChecked: true
		},
		{
			name: 'Page1 Text',
			key: null,
			value: 'image',
			isChecked: true
		},
		{
			name: 'Page2 Text',
			key: null,
			value: 'FileName',
			isChecked: true
		}
	];
	constructor(
		private equipmentService: EquipmentService
	) {
	}

	ngOnInit() {
	}
	searchVmsLibraryByExpwayCodeAndEquipTypeAndCategory(): void {
		const queryData = {
			'expwayCode': this.expWay,
			'equipType': this.selectEuipment.equipType,
			'category': this.categoryLibrary,
			'phase': this.selectEuipment.phase

		};
		this.equipmentService.getVmsLibraryByExpwayCodeAndEquipTypeAndCategory(queryData).subscribe((data: any) => {
			const response = data.Body.getVmsLibraryByExpwayCodeAndEquipTypeAndCategoryResponse;
			this.listOfLibrary = [];
			if (response) {
				response.vmsLibraryDtoList?.forEach((item, index) => {
					let page1Text = '';
					let page2Text = '';
					const pageList = item.vmsLibraryPageDtoList || [];
					pageList[0]?.lineTextDtoList?.forEach(line => page1Text += `${line.textMsg || ''} `);
					pageList[1]?.lineTextDtoList?.forEach(line => page2Text += `${line.textMsg || ''} `);
					this.listOfLibrary.push({ id: item.id, displaySeq: item.displaySeq, page1Text, page2Text });
				});
			}

		});
	}
	chooseLibrary(item) {
		this.selectLibrary = item;
	}
	getDataBySelectVmsLibrary() {
		this.equipmentService.getVmsLibraryById(this.selectLibrary.id).subscribe((res: any) => {
			const response = res.Body.getVmsLibraryByIdResponse;
			if (response && response.vmsLibraryDto) {
				this.selectEmit.next(response.vmsLibraryDto);
				// this.vmsMsgSource[0].vmsMsgPageDtoList = response.vmsLibraryDto.vmsLibraryPageDtoList;
				// this.setRightPartStyles();
			}
		});
	}

}

