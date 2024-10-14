import { Component, OnInit,Input } from '@angular/core';

@Component({
  selector: 'emas-send-apply',
  templateUrl: './send-apply.component.html',
  styleUrls: ['./send-apply.component.css']
})
export class SendApplyComponent implements OnInit {
  @Input() selectedList: any;
  isSpinning = true;
  isVisible = false;
  listOfData = [];
  renderHeader = [
    {
      value: 'Equip ID And Location',
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

  constructor() { }

  ngOnInit() {
    this.listOfData = this.selectedList;
  }
  showModal() {
    this.isSpinning = true;
    this.listOfData = this.selectedList;
    this.isVisible = true;
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
