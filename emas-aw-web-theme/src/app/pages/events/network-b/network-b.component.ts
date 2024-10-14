import { Component, OnInit } from '@angular/core';
import { EventsService } from '../events.service';
import { CommonService } from '../../../service/common.service';

@Component({
  // tslint:disable-next-line: component-selector
  selector: 'emas-network-b',
  templateUrl: './network-b.component.html',
  styleUrls: ['./network-b.component.css']
})
export class NetworkBComponent implements OnInit {
  cfelsList: any = [];
  ticssList: any = [];
  dcssList: any = [];
  scssList: any = [];
  wcssList: any = [];
  group1List: any = [];
  nfwList: any = [];
  fewList: any = [];
  tcwList: any = [];
  jfwList: any = [];
  jeyesList: any = [];
  bfwList: any = [];
  bcwList: any = [];
  eswList: any = [];
  rcsList: any = [];
  // img
  nfwImg: any = [
    {
      url: '../../../../assets/img/Images/SS_EventsTab/firewall.png',
      equipId: 'nfw_01',
      status: 0
    },
    {
      url: '../../../../assets/img/Images/SS_EventsTab/firewall.png',
      equipId: 'nfw_02',
      status: 0
    },
    {
      url: '../../../../assets/img/Images/SS_EventsTab/firewall.png',
      equipId: 'nfw_03',
      status: 0
    },
  ];

  fewImg: any = [
    {
      url: '../../../../assets/img/Images/SS_EventsTab/switch.png',
      equipId: 'few_01',
      status: 0
    },
    {
      url: '../../../../assets/img/Images/SS_EventsTab/switch.png',
      equipId: 'few_02',
      status: 0
    },
    {
      url: '../../../../assets/img/Images/SS_EventsTab/switch.png',
      equipId: 'few_03',
      status: 0
    },
  ];

  tcwImg: any = [
    {
      url: '../../../../assets/img/Images/SS_EventsTab/switch.png',
      equipId: 'tcw_01',
      status: 0
    },
    {
      url: '../../../../assets/img/Images/SS_EventsTab/switch.png',
      equipId: 'tcw_02',
      status: 0
    },
    {
      url: '../../../../assets/img/Images/SS_EventsTab/switch.png',
      equipId: 'tcw_03',
      status: 0
    },
  ];

  eswImg: any = [
    {
      url: '../../../../assets/img/Images/SS_EventsTab/switch.png',
      equipId: 'esw_01',
      status: 0
    },
    {
      url: '../../../../assets/img/Images/SS_EventsTab/switch.png',
      equipId: 'esw_02',
      status: 0
    },
  ];

  bfwImg: any = [
    {
      url: '../../../../assets/img/Images/SS_EventsTab/firewall.png',
      equipId: 'bfw_01',
      status: '0'
    },
    {
      url: '../../../../assets/img/Images/SS_EventsTab/firewall.png',
      equipId: 'bfw_02',
      status: '0'
    },
  ];

  bcwImg: any = [
    {
      url: '../../../../assets/img/Images/SS_EventsTab/switch.png',
      equipId: 'bcw_01',
      status: 0
    },
    {
      url: '../../../../assets/img/Images/SS_EventsTab/switch.png',
      equipId: 'bcw_02',
      status: 0
    },
  ];

  jfwImg: any = [
    {
      url: '../../../../assets/img/Images/SS_EventsTab/firewall.png',
      equipId: 'jfw_01',
      status: 0
    },
    {
      url: '../../../../assets/img/Images/SS_EventsTab/firewall.png',
      equipId: 'jfw_02',
      status: 0
    },
  ];
  cfelsImg: any = [
    {
      equipId: 'cfels_01',
      status: 0
    }
  ];
  ticssImg: any = [
    {
      equipId: 'ticss_01',
      status: 0
    }
  ];
  dcssImg: any = [
    {
      equipId: 'dcss_01',
      status: 0
    }
  ];
  scssImg: any = [
    {
      equipId: 'scss_01',
      status: 0
    }
  ];
  wcssImg: any = [
    {
      equipId: 'wcss_01',
      status: 0
    }
  ];
  group1Img: any = [
    {
      equipId: 'group1_01',
      status: 0
    }
  ];
  jeyesImg: any = [
    {
      equipId: 'jeyes_01',
      status: 0
    }
  ];
  rcsImg: any = [
    {
      equipId: 'rcs_01',
      status: 0
    }
  ];
  typeList = ['cfels', 'ticss', 'dcss', 'scss', 'wcss', 'group1', 'nfw', 'few', 'tcw', 'jfw', 'jeyes', 'bfw', 'bcw', 'esw', 'rcs'];

  constructor(
    private eventsService: EventsService,
    private commonService: CommonService
  ) { }

  ngOnInit() {
    this.getAllData();
    this.getAllStatus();
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
