import { Component, OnInit, Input } from '@angular/core';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'emas-view-modal',
  templateUrl: './view-modal.component.html',
  styleUrls: ['./view-modal.component.css']
})
export class ViewModalComponent implements OnInit {
  @Input() selectedList: any;
  @Input() newData: any;
  isVisible = false;
  listOfData = [];
  renderHeader = [
    {
      value: 'Equip ID',
      label: 1
    },
    {
      value: 'Location',
      label: 1
    },
    {
      value: 'Command Description',
      label: 1
    },
    {
      value: 'Status',
      label: 1
    },
  ];
  isSpinning = true;
  locIsShow = true;
  constructor() { }

  ngOnInit() {
    this.listOfData = this.selectedList;
  }

  showModal(vms?) {
    this.isSpinning = true;
    this.listOfData = this.selectedList;
    this.isVisible = true;
    if (vms) {
      this.locIsShow = false;
    } else {
      this.locIsShow = true;
    }
  }
  closeModal() {
    this.isVisible = false;
    this.listOfData = [];
  }
  handleCancel() {
    this.isVisible = false;
    this.listOfData = [];
  }
  closeLoading() {
    this.isSpinning = false;
  }
}
