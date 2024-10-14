import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'emas-command-modal',
  templateUrl: './command-modal.component.html',
  styleUrls: ['./command-modal.component.css']
})
export class CommandModalComponent implements OnInit {
  @Input() selectedList: any;
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
}
