import { Component, OnInit } from '@angular/core';
import { CommonService } from '../../../service/common.service';

@Component({
  // tslint:disable-next-line: component-selector
  selector: 'emas-clustering-network',
  templateUrl: './clustering-network.component.html',
  styleUrls: ['./clustering-network.component.css']
})
export class ClusteringNetworkComponent implements OnInit {
  cesList: any = [];
  mesList: any = [];
  iswList: any = [];
  cdsList: any = [];
  dmcList: any = [];
  dbsList: any = [];
  typeList = ['ces', 'mes', 'isw', 'cds', 'dmc', 'dbs'];

  constructor(
    private commonService: CommonService
  ) { }

  ngOnInit() {
    this.getAllData();
  }

  getAllData() {
    this.typeList.forEach((item) => {
      this[item + 'List'] = this.commonService.equipTypeObject[item];
    });
  }

}
