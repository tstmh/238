import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'emas-instrusion-detection',
  templateUrl: './instrusion-detection.component.html',
  styleUrls: ['./instrusion-detection.component.css']
})
export class InstrusionDetectionComponent implements OnInit {
  renderHeader = [
		{
			name: 'Alarm ID',
			key: null,
			value: 'alarmID',
			isChecked: true
		},
		{
			name: 'Equip ID',
			key: null,
			value: 'equipID',
			isChecked: true
		},
		{
			name: 'Exp Way',
			key: null,
			value: 'expWay',
			isChecked: true
		},
		{
			name: 'Km Marking Direction',
			key: null,
			value: 'kmMarkingDirection',
			isChecked: true
    },
    {
			name: 'Start_DateTime',
			key: null,
			value: 'startDateTime',
			isChecked: true
    },
    {
			name: 'End_DateTime',
			key: null,
			value: 'endDateTime',
			isChecked: true
    },
    {
			name: 'Ack_DateTime',
			key: null,
			value: 'ackDateTime',
			isChecked: true
    },
    {
			name: 'Alarm Desc',
			key: null,
			value: 'alarmDesc',
			isChecked: true
    },
  ];
  validateForm: FormGroup;
	controlArray: any[] = [];
	isCollapse = true;
	listOfData = [];
  listOfData2 = [];
   // nz-date-picker
   startValue: Date | null = null;
   endValue: Date | null = null;
   endOpen = false;
	listEquipID: Array<{ label: string; value: number }> = [
		{ label: 'All', value: 1},
		{ label: 'EquipID', value: 2},
		{ label: 'EquipID', value: 3}
  ];
  listAlarmDesc: Array<{ label: string; value: number }> = [
		{ label: 'All', value: 1},
		{ label: 'Location', value: 2},
		{ label: 'Location', value: 3}
  ];
  listExpWay: Array<{ label: string; value: number }> = [
		{ label: 'All', value: 1},
		{ label: 'ExpWay', value: 2},
		{ label: 'ExpWay', value: 3}
  ];
  listEquipType: Array<{ label: string; value: number }> = [
		{ label: 'All', value: 1},
		{ label: 'EquipType', value: 2},
		{ label: 'EquipType', value: 3}
  ];

	constructor(private fb: FormBuilder) { }

	ngOnInit() {
		this.initForm();
		// console.log(this.validateForm);
	}

	initForm() {
		this.validateForm = this.fb.group({
      equipID: [{ value: null, disabled: false }],
      expWay: [{ value: null, disabled: false }],
      equipType: [{ value: null, disabled: false }],
      alarmDesc: [{ value: null, disabled: false }],
      startDate: [{ value: new Date(), disabled: false }],
      endDate: [{ value: new Date(), disabled: false }],
		});
	}
	submitForm(): void {
		// console.log(this.validateForm);
  }
  // nz-date-picker
  disabledStartDate = (startValue: Date): boolean => {
    if (!startValue || !this.endValue) {
      return false;
    }
    return startValue.getTime() > this.endValue.getTime();
  };

  disabledEndDate = (endValue: Date): boolean => {
    if (!endValue || !this.startValue) {
      return false;
    }
    return endValue.getTime() <= this.startValue.getTime();
  };

  onStartChange(date: Date): void {
    this.startValue = date;
  }

  onEndChange(date: Date): void {
    this.endValue = date;
  }

  handleStartOpenChange(open: boolean): void {
    if (!open) {
      this.endOpen = true;
    }
  }

  handleEndOpenChange(open: boolean): void {
    this.endOpen = open;
  }

}
