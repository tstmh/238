import { Component, OnInit, Input, Output, EventEmitter, ViewChild, AfterViewInit } from '@angular/core';
import { ApplyWindowComponent } from '../apply-window/apply-window.component';
import { EquipmentService } from '../../equipment-control.service';

@Component({
  selector: 'emas-send-modal',
  templateUrl: './send-modal.component.html',
  styleUrls: ['./send-modal.component.css']
})
export class SendModalComponent implements OnInit, AfterViewInit {
  @ViewChild(ApplyWindowComponent) private childcomponent: ApplyWindowComponent;
  @Input() selectItem: any;
  @Input() listOfData: any;
  @Input() equipType: any;
  @Input() isVisibleMiddle: boolean;
  @Output() closeModal = new EventEmitter();
  radioValue = '';
  newStatus = '';
  start = '';
  startList = [
    {
      label: 'Stop',
      value: 0
    },
    {
      label: 'Start in forward direction',
      value: 1
    },
    {
      label: 'Start in reverse direction',
      value: 2
    },
    {
      label: 'Start in forward direction in fire mode',
      value: 3
    },
    {
      label: 'Start in reverse direction in fire mode',
      value: 4
    },
  ];
  boost = '';
  boostList = [
    {
      label: '0 Boost light burning in the group',
      value: 0
    },
    {
      label: '1 Boost light burning in the group',
      value: 1
    },
    {
      label: '2 Boost light burning in the group',
      value: 2
    },
    {
      label: '3 Boost light burning in the group',
      value: 3
    },
    {
      label: '4 Boost light burning in the group',
      value: 4
    },
    {
      label: '5 Boost light burning in the group',
      value: 5
    },
  ];
  ventilation = 'Ventilation level 1 required/standby';
  ventilationList = [
    {
      label: 'Ventilation level 1 required/standby',
      value: 1
    }, {
      label: 'Ventilation level 2 required',
      value: 2
    }, {
      label: 'Ventilation level 3 required',
      value: 3
    }, {
      label: 'Ventilation level 4 required',
      value: 4
    }
  ];
  disabled: boolean;
  venl = false;
  venlValue = 'Ventilation level 1 required/standby';
  runi = false;
  runiValue = 'Not Running';
  runs = false;
  runsValue = 'Not Running';
  upsi = false;
  upsiValue = 'No';
  upsm = false;
  upsmValue = 'No';
  doos = false;
  doosValue = 'Closed';
  stas = false;
  stasValue = 'Stop';
  sysb = false;
  sysbValue = 'No';
  sysa = false;
  sysaValue = 'No';
  wasi = false;
  wasiValue = 'On';
  ligc = false;
  ligcValue = 'Night';
  wese = false;
  weseValue = 'Off';
  ease = false;
  easeValue = 'Closed';
  genr = false;
  genrValue = 'No Running';
  gena = false;
  genaValue = 'Not Automatic';
  linl = false;
  linlValue = 'Disabled';
  linp = false;
  linpValue = 'Off';
  stns = false;
  stnsValue = 'Stopped for maintenance';
  onop = false;
  onopValue = 'Off';
  este = false;
  esteValue = 'Hook up';
  tund = false;
  tundValue = 'Off';
  strd = false;
  strdValue = 'Off';
  mode = false;
  modeValue = 'Automatic';
  dews = false;
  dewsValue = 'Off';
  mais = false;
  maisValue = '';
  statusOther = false;
  cirb = false;
  cirbValue = 'Closed';
  keyc = false;
  keycValue = 'Not Local';
  diss = false;
  dissValue = 'Closed';
  ears = false;
  earsValue = 'Closed';
  gateway = false;
  gateway1Value = 'On';
  gateway2Value = 'Off';
  tagm = false;
  tagmValue = '';
  airf = false;
  airfValue = 'Reverse';
  stat = false;
  bool = false;
  rest = false;
  tesb = false;
  wasw = false;
  stac = false;
  onsi = false;
  dooc = false;
  venle = false;
  opec = false;
  aute = false;
  opecb = false;
  linlc = false;
  staco = false;
  numo = false;
  numoValue = '0 Boost light burning in the group';
  senr = true;
  operationalValue = 'Non Operational';
  taggingValue = 'The plant is controlled and monitored';
  StartStop = '';
  stop = '';
  startt = '';
  reset = '';
  radioValuee = 'Start';

  constructor(
    private equipmentService: EquipmentService,
  ) { }

  ngOnInit() {
    if (this.radioValue === '') {
      this.disabled = true;
    } else {
      this.disabled = false;
    }
    if (this.ventilation && this.dooc || this.boost || this.start) {
      this.disabled = false;
    }
    this.changeModal();
  }
  ngAfterViewInit(): void {
    let element = document.getElementsByClassName('ant-modal-body');
    element[0].setAttribute('style', 'min-height: 400px');
  }
  // 根据不同下拉框值展示不同模态框的内容
  changeModal() {
    switch (this.equipType) {
      case 'Visibility Meter': {
        this.stat = true;
        break;
      }
      case 'Vehicle Door': {
        this.doos = true;    //std
        this.dooc = true;
        this.getSelectData(['std']);
        break;
      }
      case 'Tunnel Section Fan': {
        this.venl = true;   //stv
        this.venle = true;
        this.getSelectData(['stv']);
        break;
      }
      case 'Tunnel Main Pump': {
        this.stas = true;   //sts
        this.stac = true;
        this.getSelectData(['sts']);
        break;
      }
      case 'Tunnel Box': {
        this.opec = true;
        break;
      }
      case 'Split Air Con': {
        this.runi = true;   //str
        this.stas = true;   //sts
        this.stac = true;
        this.getSelectData(['sts', 'str']);
        break;
      }
      case 'SOS Phone': {
        this.opec = true;
        break;
      }
      case 'Room Temperature':
      case 'RIO With Melsecnet/H Slave & Modbus Module':
      case 'RIO In Tunnel':
      case 'RIO In Building':
      case 'PMCS NMS Workstation':
      case 'PMCS Layer 3 Switch':
      case 'PMCS ETS Workstation':
      case 'PMCS ETS':
      case 'PMCS AVRPFS':
      case 'LV UPS Under Voltage Relay':
      case 'LV MDB Under Voltage Relay':
      case 'LV Emergency Panel Under-Voltage Relay':
      case 'LV DC Power Device':
      case 'LV Annunciator Panel':
      case 'HV Public Utility Incoming Feeder Voltage':
      case 'HV Public Utility Incoming Feeder Current':
      case 'HV Busbar Protection':
      case 'High Voltage Transformer Monitoring':
      case 'High Voltage DC Power Device':
      case 'Fire Cabinet in Tunnel':
      case 'Fan Panel Emergency':
      case 'CS Uninterruptible Power Supply':
      case 'Common Alarm In 22kV Switchgear':
      case 'Carbon Monoxide Meter - Internal':
      case 'Carbon Monoxide Meter - External': {
        this.senr = false;
        break;
      }
      case 'Pressurization Fan': {
        this.runi = true;   //str
        this.stac = true;
        this.getSelectData(['str']);
        break;
      }
      case 'PLC Cluster': {
        this.sysa = true;   //sta
        this.sysb = true;   //std
        this.senr = false;
        this.getSelectData(['sta', 'std']);
        break;
      }
      case 'Photometer': {
        this.wasi = true;   //stw
        this.ligc = true;   //cdg
        this.wasw = true;
        this.getSelectData(['stw', 'cdg']);
        break;
      }
      case 'Pedestrian Door': {
        this.wese = true;   //stw
        this.doos = true;   //std
        this.ease = true;   //ste
        this.onsi = true;
        this.getSelectData(['stw', 'std', 'ste']);
        break;
      }
      case 'Normal Fan': {
        this.runs = true;   //sts
        this.rest = true;
        this.getSelectData(['sts']);
        break;
      }
      case 'LV UPS Normal Circuit Breaker':
      case 'LV UPS Maintenance Circuit Breaker': {
        this.cirb = true;   //stc
        this.senr = false;
        this.getSelectData(['stc']);
        break;
      }
      case 'LV UPS': {
        this.upsi = true;   //sto
        this.upsm = true;   //stm
        this.senr = false;
        this.getSelectData(['sto', 'stm']);
        break;
      }
      case 'LV MDB Outgoing Feeder': {
        this.cirb = true;   //stc
        this.keyc = true;   //stk
        this.opecb = true;
        this.getSelectData(['stc', 'stk']);
        break;
      }
      case 'LV MDB Incoming Feeder': {
        this.cirb = true;   //stc
        this.keyc = true;   //stk
        this.senr = false;
        this.getSelectData(['stc', 'stk']);
        break;
      }
      case 'LV Manual Transfer Switch with Trip Indication':
      case 'LV Manual Transfer Switch':
      case 'LV Emergency Panel Outgoing Feeder':
      case 'LV Emergency Panel Incoming Feeder':
      case 'LV Automatic Transfer Switch with Trip Indication':
      case 'LV Automatic Transfer Switch': {
        this.cirb = true;   //stc
        this.senr = false;
        this.getSelectData(['stc']);
        break;
      }
      case 'LV Diesel Generator': {
        this.cirb = true;   //stc
        this.genr = true;   //str
        this.gena = true;   //stg
        this.rest = true;
        this.getSelectData(['stc', 'str', 'stg']);
        break;
      }
      case 'Line Lighting Control South':
      case 'Line Lighting Control North': {
        this.linl = true;   //stl
        this.linlc = true;
        this.getSelectData(['stl']);
        break;
      }
      case 'Lighting Panel': {
        this.linp = true;   //sts
        this.senr = false;
        this.getSelectData(['sts']);
        break;
      }
      case 'Jet Fan': {
        this.stns = true;   //stn
        this.stas = true;   //sts
        this.rest = true;
        this.staco = true;
        this.getSelectData(['stn', 'sts']);
        break;
      }
      case 'Intermediate Loop Feeder Cubicle New Class':
      case 'HV Public Utility Incoming Feeder Power & Breaker': {
        this.cirb = true;   //stc
        this.keyc = true;   //stk
        this.diss = true;   //std
        this.senr = false;
        this.getSelectData(['stc', 'stk', 'std']);
        break;
      }
      case 'Intermediate Loop Feeder Cubicle Class':
      case 'High Voltage Transformer Feeder':
      case 'High Voltage Outgoing Feeder':
      case 'High Voltage Incoming Feeder From Other Building':
      case 'High Voltage Bus Coupler': {
        this.cirb = true;   //stc
        this.keyc = true;   //stk
        this.diss = true;   //std
        this.ears = true;   //ste
        this.senr = false;
        this.getSelectData(['stc', 'stk', 'std', 'ste']);
        break;
      }
      case 'Fan Panel Power': {
        this.onop = true;   //sts
        this.senr = false;
        this.getSelectData(['sts']);
        break;
      }
      case 'ES Phone': {
        this.este = true;   //stt
        this.senr = false;
        this.getSelectData(['stt']);
        break;
      }
      case 'Emergency Staircase': {
        this.tund = true;   //stt
        this.strd = false;   //stt
        this.senr = false;
        this.taggingValue = '';
        this.getSelectData(['stt']);
        break;
      }
      case 'De-Watering Sump':
      case 'Cleaning Sump': {
        this.mode = true;   //stc
        this.dews = true;   //sts
        this.aute = true;
        this.getSelectData(['stc', 'sts']);
        break;
      }
      case 'De-Watering Pump': {
        this.stas = true;   //sts
        this.rest = true;
        this.getSelectData(['sts']);
        break;
      }
      case 'Communication Gateway': {
        this.gateway = true;
        this.senr = false;
        break;
      }
      case 'Cleaning Pump': {
        this.runi = true;   //str
        this.rest = true;
        this.getSelectData(['str']);
        break;
      }
      case 'Breaching Inlet Door': {
        this.doos = true;    //std
        this.senr = false;
        this.getSelectData(['std']);
        break;
      }
      case 'Boost Lighting Switching Unit': {
        this.numo = true;   //stl
        this.bool = true;
        this.getSelectData(['stl']);
        break;
      }
      case 'Boost Lighting Group': {
        this.tesb = true;
        break;
      }
      case 'Anemometer': {
        this.airf = true;   //cdg
        this.senr = false;
        this.getSelectData(['cdg']);
        break;
      }
      case 'LV Bus Coupler': {
        this.cirb = true;   //stc
        this.keyc = true;   //stk
        this.senr = false;
        this.getSelectData(['stc', 'stk']);
        break;
      }
    }
  }
  getSelectData(indxData) {
    indxData.forEach(item => {
      const queryData = {
        felsCode: this.selectItem.felsCode,
        equipType: this.selectItem.equipmentCode,
        attrName: item
      };
      this.equipmentService.getEquipStatusByEquipType(queryData).subscribe((r) => {
        const tempData: any = r;
        if (tempData.Body.getEquipStatusByEquipTypeResponse.equipStatusList) {
          const newData = tempData.Body.getEquipStatusByEquipTypeResponse.equipStatusList;
          newData.forEach(item1 => {
            if (item1.equipmentId == this.selectItem.equipmentId) {
              let str = item1.attrName + 'ValueDesc';
              this.selectItem[str] = item1.attrValueDesc;
            }
          });
        }
      });
    });
    //console.log('NewSelectData:', this.selectItem)
  }

  stasto(e) {
    this.StartStop = 'StartStop';
    console.log("Str:", e)
    this.selectItem.attrName = e
  }
  sta(e) {
    this.disabled = false;
    this.StartStop = 'Start';
    console.log("Str:", e)
    this.selectItem.attrValue = '1';
  }
  sto(e) {
    this.disabled = false;
    this.StartStop = 'Stop';
    this.selectItem.attrValue = '0';
  }
  
  startChange(e) {
    this.disabled = false;
    this.selectItem.attrValue = e;
  }
  boostChange(e) {
    this.disabled = false;
    this.selectItem.attrValue = e;
  }
  ventilationChange(e) {
    this.disabled = false;
    this.selectItem.attrValue = e;
  }
  radioChange(e) {
    this.disabled = false;
    let str = e;
    //console.log("Str:", str)
    if (str.length > 3) {
      this.selectItem.attrName = str.substr(0, 3);
      this.selectItem.attrValue = str.substr(4, 1);
    } else {
      this.selectItem.attrName = str
    }
  }
  apply(): void {
    this.childcomponent.showModal();
  }

  clear(): void {
    this.radioValue = '';
    this.disabled = true;
  }

  close() {
    this.isVisibleMiddle = false;
    this.closeModal.emit(true);
    this.selectItem.active = false;
  }
}
