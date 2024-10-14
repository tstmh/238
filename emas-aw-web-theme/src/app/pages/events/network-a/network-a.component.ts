import { Component, OnInit } from '@angular/core';
import { EventsService } from '../events.service';
import { CommonService } from '../../../service/common.service';
@Component({
  // tslint:disable-next-line: component-selector
  selector: 'emas-network-a',
  templateUrl: './network-a.component.html',
  styleUrls: ['./network-a.component.css']
})
export class NetworkAComponent implements OnInit {
  cesList: any = [];
  mesList: any = [];
  dbsList: any = [];
  cdsList: any = [];
  up2List: any = [];
  nwtList: any = [];
  awtList: any = [];
  bksList: any = [];
  fcwList: any = [];
  bkwList: any = [];
  rouList: any = [];
  tasList: any = [];
  ecsList: any = [];
  vtlList: any = [];
  aswList: any = [];
  listOfData = [];
  typeList = ['ces', 'mes', 'dbs', 'cds', 'up2', 'nwt', 'awt', 'bks', 'fcw', 'bkw', 'rou', 'tas', 'ecs', 'vtl', 'asw'];
  cesImg = [
    {
      url: '../../../../assets/img/Images/SS_EventsTab/server.png',
      equipId: 'ces_01',
      status: 0
    },
    {
      url: '../../../../assets/img/Images/SS_EventsTab/server.png',
      equipId: 'ces_02',
      status: 0
    },
    {
      url: '../../../../assets/img/Images/SS_EventsTab/server.png',
      equipId: 'ces_03',
      status: 0
    }
  ];
  mesImg = [
    {
      url: '../../../../assets/img/Images/SS_EventsTab/server.png',
      equipId: 'mes_01',
      status: 0
    },
    {
      url: '../../../../assets/img/Images/SS_EventsTab/server.png',
      equipId: 'mes_02',
      status: 0
    },
    {
      url: '../../../../assets/img/Images/SS_EventsTab/server.png',
      equipId: 'mes_03',
      status: 0
    },
    {
      url: '../../../../assets/img/Images/SS_EventsTab/server.png',
      equipId: 'mes_04',
      status: 0
    },
    {
      url: '../../../../assets/img/Images/SS_EventsTab/server.png',
      equipId: 'mes_05',
      status: 0
    }
  ];
  dbsImg = [
    {
      url: '../../../../assets/img/Images/SS_EventsTab/server.png',
      equipId: 'dbs_01',
      status: 0
    },
    {
      url: '../../../../assets/img/Images/SS_EventsTab/server.png',
      equipId: 'dbs_02',
      status: 0
    }
  ];
  cdsImg = [
    {
      url: '../../../../assets/img/Images/SS_EventsTab/storage.png',
      equipId: 'cds_01',
      status: 0
    },
  ];
  up2Img = [
    {
      url: '../../../../assets/img/Images/SS_EventsTab/server.png',
      equipId: 'up2_01',
      status: 0
    },
    {
      url: '../../../../assets/img/Images/SS_EventsTab/server.png',
      equipId: 'up2_02',
      status: 0
    }
  ];
  nwtImg = [
    {
      url: '../../../../assets/img/Images/SS_EventsTab/aw.png',
      equipId: 'nwt_01',
      status: 0
    },
    {
      url: '../../../../assets/img/Images/SS_EventsTab/aw.png',
      equipId: 'nwt_02',
      status: 0
    },
  ];
  awtImg = [
    {
      url: '../../../../assets/img/Images/SS_EventsTab/aw.png',
      equipId: 'awt_01',
      status: 0
    },
    {
      url: '../../../../assets/img/Images/SS_EventsTab/aw.png',
      equipId: 'awt_02',
      status: 0
    },
    {
      url: '../../../../assets/img/Images/SS_EventsTab/aw.png',
      equipId: 'awt_03',
      status: 0
    },
  ];
  bksImg = [
    {
      url: '../../../../assets/img/Images/SS_EventsTab/server.png',
      equipId: 'bks_01',
      status: 0
    }
  ];
  fcwImg = [
    {
      url: '../../../../assets/img/Images/SS_EventsTab/server.png',
      equipId: 'fcw_01',
      status: 0
    }
  ];
  bkwImg = [
    {
      url: '../../../../assets/img/Images/SS_EventsTab/server.png',
      equipId: 'bkw_01',
      status: 0
    }
  ];
  rouImg = [
    {
      url: '../../../../assets/img/Images/SS_EventsTab/switch.png',
      equipId: 'rou_01',
      status: 0
    }
  ];
  tasImg = [
    {
      url: '../../../../assets/img/Images/SS_EventsTab/server.png',
      equipId: 'tas_01',
      status: 0
    }
  ];
  ecsImg = [
    {
      url: '../../../../assets/img/Images/SS_EventsTab/server.png',
      equipId: 'ecs_01',
      status: 0
    }
  ];
  vtlImg = [
    {
      url: '../../../../assets/img/Images/SS_EventsTab/vtl.png',
      equipId: 'vtl_01',
      status: 0
    }
  ];
  aswImg = [
    {
      url: '../../../../assets/img/Images/SS_EventsTab/switch.png',
      equipId: 'asw_01',
      status: 0
    },
    {
      url: '../../../../assets/img/Images/SS_EventsTab/switch.png',
      equipId: 'asw_02',
      status: 0
    },
  ];

  constructor(
    private eventsService: EventsService,
    private commonService: CommonService
  ) { }

  ngOnInit() {
    this.getAllData();
    this.getAllStatus(); // 防止Ws只发送一次数据，无法获取数据
    this.commonService.GetMessage().subscribe(res => {
      // console.log(res);
      if (res.type === 'eqtTypeChange') {
        this.getAllData();
        this.getAllStatus();
      }
    });
  }
  getAllData() {
    this.typeList.forEach((item) => {
      this[item + 'List'] = this.commonService.equipTypeObject[item];
    });
  }

  getStatus(imgList, typeList) {
    imgList.forEach((item) => {
      typeList.forEach((data) => {
        if (item.equipId === data.equipId) {
          // console.log('===');
          item.status = data.status;
        }
      });
    });
  }

  getAllStatus() {
    this.typeList.forEach((item) => {
      this.getStatus(this[item + 'Img'], this[item + 'List']);
    });
  }

}
