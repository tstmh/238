import { Component, OnInit, Input, Output,EventEmitter, AfterViewInit } from '@angular/core';
import { FormGroup, FormBuilder } from "@angular/forms";

@Component({
  selector: 'emas-update-equipment',
  templateUrl: './update-equipment.component.html',
  styleUrls: ['./update-equipment.component.css']
})
export class UpdateEquipmentComponent implements OnInit, AfterViewInit{
  @Input() selectItem: any;
  @Input() isVisibleMiddle: boolean;
  @Output() closeModal= new EventEmitter();

  validateForm: FormGroup;
  controlArray: any[] = [];
  constructor(
    private fb: FormBuilder
  ) { }

  listStatus: any[] = [
    { label: 'Enable', value: 1},
    { label: 'Disable', value: 2},
  ]

  ngOnInit() {
    this.initForm();
  }
  ngAfterViewInit(): void {
    //Called after ngAfterContentInit when the component's view has been initialized. Applies to components only.
    //Add 'implements AfterViewInit' to the class.
    let button : any = document.querySelector('.ant-modal-close'); // 重置弹出框删除图标总是显示边框问题
    button.style.outline = 'unset';
  }

  initForm() {
		this.validateForm = this.fb.group({
      EquipID: [{ value: null, disabled: false }],
      status: [{ value: null, disabled: false }],
		});
	}
	submitForm(): void {
	}
	

  handleOkMiddle(): void {
    this.isVisibleMiddle = false;
    this.closeModal.emit(true);
	  }
	
	  handleCancelMiddle(): void {
    this.isVisibleMiddle = false;
    this.closeModal.emit(false);
	  }

}
