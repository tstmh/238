import { Component, OnInit, Input, NgZone } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { EquipmentService } from '../../equipment-control.service';
import { CommandStatusType } from '../../../../service/constants.service';
import { CommonService } from '../../../../service/common.service';
import { ConstantsService } from '../../../../service/constants.service';
// tslint:disable-next-line:max-line-length
import { createVmsTemplate, VmsMsg, VmsMsgPage, VmsMsgPictogram, VmsMsgTextLine, VmsTemplate, PictogramInfo, VmsTemplatePictogram } from './new-modal.interface';
import { DialogService } from 'src/app/share/dialog';


@Component({
  // tslint:disable-next-line:component-selector
  selector: 'emas-new-modal',
  templateUrl: './new-modal.component.html',
  styleUrls: ['./new-modal.component.css']
})
export class NewModalComponent implements OnInit {

  // @Input() isVisible = false;
  isSpinning = false;
  isVisible = false;
  isResVisible = false;
  isImplement = false;
  isRelease = false;
  listOfData = [];
  listOfData1 = [];
  responseArray = [];
  titleName = null;
  // @Input() isVisible = true;
  renderHeader = [
    {
      name: 'S.NO',
      key: 'NO'
    }, {
      name: 'Control Type',
      key: 'Control_Type'
    }, {
      name: 'Equipment',
      key: 'Equipment'
    }, {
      name: 'Command Parameter',
      key: 'Command_Parameter'
    }
  ];
  responseHeader = [
    {
      name: 'Equip ID And Location',
      key: 'EquipId',
    }, {
      name: 'Command Description',
      key: 'CmdDesc',
    }, {
      name: 'Status',
      key: 'CmdStatusName',
    }
  ];

  @Input() selectItem: any;
  @Input() comfirm: any;
  constructor(
    private http: HttpClient,
    private equipmentService: EquipmentService,
    private commonService: CommonService,
    private constantsService: ConstantsService,
    private ngZone: NgZone,
    private dialog: DialogService,
  ) { }

  ngOnInit() {
    this.getData();
  }
  // tslint:disable-next-line:use-life-cycle-interface
  ngAfterViewInit() {
    const button: any = document.querySelector('.ant-modal-close'); // 重置弹出框删除图标总是显示边框问题
    button.style.outline = 'unset';
  }
  // 获取csv文件内容
  getData() {
    this.http.get('assets/Traffic-Plans/Fire_Plan_Detail.csv', { responseType: 'text' })
      .subscribe(data => {
        const items = data.split('\n');
        const newItems = [];
        items.forEach(item => {
          if (item && item.trim().length > 0) {
            const newItem = item.split(',');
            // tslint:disable-next-line:max-line-length
            newItems.push({ 'PP_ID': newItem[0], 'Description': newItem[1], 'Command_NO': newItem[9], 'Control_Type': newItem[11], 'Eqiupment': newItem[12], 'Command_Parameter': newItem[13] });

          }
        });
        this.listOfData1 = newItems;
      });
  }
  // 点击tr
  cliclTr(i, data) {
    this.listOfData.forEach((item, index) => {
      item.active = false;
      if (i === index) {
        // 点击每个tr给与样式active
        item.active = true;
      }
    });
  }
  // 模态框Implement按钮
  Implement() {
    if (this.comfirm === true) {
      this.dialog
        .confirm({
          title: 'Confirmation Window',
          content: `${this.selectItem.description}? PLEASE ENSURE THE COMMANDS LIST
          BEFOFR IMPLEMENTING THE PREDEFINED PLAN`,
          buttonOkTxt: 'Yes(Y)',
          buttonCancelTxt: 'No(N)'
        })
        .subscribe(res => {
          if (res) {
            this.confirmSend();
          } else {
            this.cancelSend();
          }
        });
    } else {
      this.confirmSend();
    }
  }
  cancelSend() {
    // this.childcomponent.closeModal();
  }
  confirmSend() {
    this.titleName = 'Implement';
    this.isSpinning = true;
    const execId = 'AW_' + new Date().getTime();
    const responseArray = [];
    let requestCount = 0;

    this.listOfData.forEach((equip) => {
      const equipType = equip.Eqiupment.substring(2, 5);
      let substr = equip.Eqiupment.substring(2);
      let cd;
      let response;
      switch (equip.Control_Type) {
        case 'cdd':
        case 'cds':
        case 'cdw':
        case 'cde':
          if (equip.Eqiupment.startsWith('ta') || equip.Eqiupment.startsWith('tb')) {

            equip.Eqiupment = substr;
            const msg = this.parseCmdV1(equip.Eqiupment, equipType, equip.Command_Parameter);
            // tslint:disable-next-line:max-line-length
            cd = this.CommandDetail(execId, equip.Command_NO, equip.Eqiupment, 'Send message', '', equip.Control_Type, equip.Command_Parameter);
            response = this.ImplementPlanVMScdd(cd, equipType, msg);
            responseArray.push(cd);
          } else if (equip.Eqiupment.startsWith('tc')) {
            equip.Eqiupment = substr;
            cd = this.CommandDetail(execId, equip.Command_NO, equip.Eqiupment,
              'Send message', '', equip.Control_Type, equip.Command_Parameter);
            this.equipmentService.getVmsMsgByEquipId({ 'equipId': equip.Eqiupment }).subscribe(res => {
              const TrvTimeMsg = res['Body']['getVmsMsgByEquipIdResponse']['vmsMsgDto'];

              const lnTxtDto = TrvTimeMsg.vmsMsgPageDtoList[0].lineTextDtoList;
              let msg = null;
              if (lnTxtDto != null) {

                msg = this.parseCmdV2(TrvTimeMsg, msg, equip, equipType);
              }
              response = this.ImplementPlanVMScdd(cd, equipType, msg);

            });
            responseArray.push(cd);
          } else if (equip.Eqiupment.startsWith('al')) { // //LUS ControlCommand
            const felsCode = 'al';
            substr = equip.Eqiupment.substring(6);

            cd = this.CommandDetail(execId, equip.Command_NO, substr, 'Send message', '', equip.Control_Type, equip.Command_Parameter);
            if (cd.EquipId.startsWith('t')) {

              response = this.equipmentService.AW_CFELS_LUS({
                'lusMsg': {
                  attrName: cd.CTRLTYPE,
                  attrValue: cd.CMDPARA,
                  equipId: cd.EquipId,
                  felsCode: felsCode,
                  equipmentCode: equipType
                }
              })
            }

            // tslint:disable-next-line:max-line-length
            // string logLus = "HASH TAG #### Send message(cdd) for Implement Plan completed # PPID : {0} # Cmd No : {1} # Ctrl Type : {2} # Equip Id :{3} # Cmd Para : {4}";
            // HelperClass.LogInfo(string.Format(logLus, equip.PPID, equip.CMDNO, equip.CTRLTYPE, equip.EquipID, equip.CMDPARA));
            responseArray.push(cd);
          } else if (equip.Eqiupment.startsWith('ax')) { // //PMCS ControlCommand
            const felsCode = 'ax';
            substr = equip.Eqiupment.substring(6);
            equip.Eqiupment = substr;
            cd = this.CommandDetail(execId, equip.Command_NO, substr, 'Send message', '', equip.Control_Type, equip.Command_Parameter);
            response =  this.implementPlanPMCS(cd, felsCode, equipType);
            // tslint:disable-next-line:max-line-length
            // string logPmcs = "PMCS send message(cdd) for Implement Plan completed ,  PPID : {0} , Cmd No : {1} , Ctrl Type : {2} ,Equip Id :{3} , Cmd Para : {4}";
            // HelperClass.LogInfo(string.Format(logPmcs, equip.PPID, equip.CMDNO, equip.CTRLTYPE, equip.EquipID, equip.CMDPARA));
            responseArray.push(cd);
          }

          break;

        case 'cdl':
          if (equip.Eqiupment.startsWith('ta') || equip.Eqiupment.startsWith('tb') || equip.Eqiupment.startsWith('tc')) {

            equip.Eqiupment = substr;
            cd = this.CommandDetail(execId, equip.Command_NO, substr, 'Set dimming mode', '', equip.Control_Type, equip.Command_Parameter);


            if (cd.EquipId.startsWith('tip') || cd.EquipId.startsWith('tep') || cd.EquipId.startsWith('ttp')) {
              // creating a object 'dDto' of the class 'dimmingDto'which is in the CMHWebservice. Then
              // send this object to the backend.
              response = this.equipmentService.AW_CFELS_Dimming({
                'dimming': {
                  execId: cd.ExecId,
                  cmdId: cd.CmdId,
                  sender: cd.Sender,
                  equipId: cd.EquipId,
                  systemId: this.constantsService.SystemID,
                  dimMode: 1,
                  dimLevel: 0
                }
              })

            }
            // tslint:disable-next-line:max-line-length
            // string logVmsCdl = "HASH TAG #### Send dimming level(cdl) for Implement Plan completed #  PPID : {0} # Cmd No : {1} # Ctrl Type : {2} # Equip Id :{3} # Cmd Para : {4}";
            // HelperClass.LogInfo(string.Format(logVmsCdl, equip.PPID, equip.CMDNO, equip.CTRLTYPE, equip.EquipID, equip.CMDPARA));
            responseArray.push(cd);
          }
          break;
        case 'DL':
          break;
        default:
          break;
      }

      if (response) {
        requestCount += 1 ;
        response.requestCount = requestCount
        response.subscribe((data: any) => {
          if (!data.Body.Fault) {
            cd.CmdStatus = CommandStatusType.Success;
            cd.CmdStatusName = 'Success';
          } else {
            cd.CmdStatus = CommandStatusType.TimeOut;
            cd.CmdStatusName = 'TimeOut';
          }
          if(response.requestCount === requestCount)  //最后一次请求更新dom
          {
            this.responseArray = responseArray;
            this.isSpinning = false;
            this.isResVisible = true;
          }
        });
      }
    });
  }
  ImplementPlanVMScdd(cd, equipType, msg) {
    if (cd.EquipId.startsWith('tip') || cd.EquipId.startsWith('tep') || cd.EquipId.startsWith('ttp')) {

      const vmsMsgDto = this.ToVmsMsgDto(cd.ExecId, cd.CmdId, cd.Sender, cd.EquipId, 'emas', equipType, msg);
      return this.equipmentService.AW_CFELS_VMS({ 'vmsMsg': vmsMsgDto })
    }
  }

  ToVmsMsgDto(execId, cmdId, sender, equipId, systemId, equipType, msg: VmsMsg) {
    try {

      const pages = new Array(msg.VmsMsgPages.length);
      for (const item of msg.VmsMsgPages) {
        let txtLinesCnt = 0;
        const textLines = new Array(item.TextLines.length);

        for (const tl of item.TextLines) {
          let text = '';
          let colors = '';
          for (const tlItem of tl.RichCharCollection) {
            text += (this.isNullOrEmpty(tlItem.Text) ? ' ' : tlItem.Text);
            colors += tlItem.Color;
          }
          // textLines[tl.Template.Number - 1] = new lineTextDto() { textMsg = text.toString(), colorMsg = colors.toString() };
          // textLines[txtLinesCnt] = new lineTextDto() { textMsg = text.toString(), colorMsg = colors.toString() };
          textLines[txtLinesCnt] = { textMsg: text.toString(), colorMsg: colors.toString() };
          txtLinesCnt++;
        }
        let picIds = '';
        for (const p of item.Pictograms.sort((a, b) => a.Template.Sequence - b.Template.Sequence)) {
          picIds += p.Pictogram.id.toString() + ',';
        }

        pages[item.Number - 1] = {
          'lineTextDtoList': textLines,
          'pageMode': item.mode,
          'pageNo': item.Number,
          'toggleTime': item.toggleTime,
          'vmsPictogramConfigId': this.trimEnd(picIds, ','),
          'vmsTemplateId': item.Template.id,
          'equipId': equipId,
        };
      }

      return {
        execId,
        cmdId,
        sender,
        systemId,
        equipType,
        equipId,
        'displayMode': msg.displayMode,
        'toggleMode': msg.toggleMode,
        'dateTime': new Date(),
        'vmsMsgPageDtoList': pages
      };

    } catch (e) {
      // console.log(e);

    }
  }
  isNullOrEmpty(text) {

    if (!text || text === '') {
      return true;
    }
    return false;
  }
  trimEnd(str, c) {
    if (c == null || c === '') {

      const rg = /s/;
      let i = str.length;
      while (rg.test(str.charAt(--i))) { }
      return str.slice(0, i + 1);
    } else {
      const rg = new RegExp(c);
      let i = str.length;
      while (rg.test(str.charAt(--i))) { }
      return str.slice(0, i + 1);
    }
  }
  parseCmdV1(equipId, equipType, cmdParam): VmsMsg {
    let msgObj = null;
    // tslint:disable-next-line:max-line-length
    const parts = cmdParam.split('^'); // part 1 - 9 (indexes 0 to 8) where part[6] - pictogram id list, part[7] - text color code list, part[8] - text line list
    if (parts.length > 3) {
      msgObj = this.Common_Template_parser(equipId, equipType, parts);
    }
    return msgObj;
  }

  parseCmdV2(TrvTimeMsg, msg, equip, equipType, isBlankTemplate?): VmsMsg {
    const textLinesTTP: Array<VmsMsgTextLine> = new Array[TrvTimeMsg.vmsMsgPageDtoList[0].lineTextDtoList.Length];
    for (let k = 2; k < TrvTimeMsg.vmsMsgPageDtoList[0].lineTextDtoList.Length; k++) {
      const msgTextLine: VmsMsgTextLine = { 'RichCharCollection': [] };
      const textArry = TrvTimeMsg.vmsMsgPageDtoList[0].lineTextDtoList[k].textMsg.toCharArray();
      const colorArry = TrvTimeMsg.vmsMsgPageDtoList[0].lineTextDtoList[k].colorMsg.toCharArray();
      for (let i = 0; i < textArry.Length; i++) {
        msgTextLine.RichCharCollection.push(textArry[i].toString(), colorArry[i].toString());
      }
      textLinesTTP[k] = msgTextLine;
    }

    const vmsPicDto = TrvTimeMsg.vmsMsgPageDtoList[0].vmsPictogramConfigDtoList;
    const picsTTP: Array<PictogramInfo> = [];
    if (vmsPicDto != null) {
      for (const picDtos of vmsPicDto) {
        picsTTP.push({
          id: picDtos.pictogramId,
          groupId: picDtos.picGroupId,
          height: picDtos.height,
          width: picDtos.width,
          description: picDtos.pictogramDesc,
          checkSum: picDtos.checksum,
          fileName: picDtos.picFileName,
          // image: BitmapManagerFireTraffic.ToBitmapImage(bytes: picDtos.graphicContents),
          size: picDtos.graphicContents.Length,
          EquipIds: picDtos.equipIdList
        });
      }

    }
    msg = this.ttpParser(equip.Eqiupment, equipType, equip.CMDPARA, textLinesTTP, picsTTP, isBlankTemplate);
    return msg;
  }
  Common_Template_parser(equipId, equipType, parts): VmsMsg {
    const pageMode = parts[3].length > 0 ? parts[3].split('~') : null;
    const pageNumberTotal = pageMode.length;
    const templateList = parts[4].length > 0 ? parts[4].split('~') : null;
    const toggleTime = parts[5].length > 0 ? parts[5].split('~') : null;
    const pictList = parts[6].length > 0 ? parts[6].split('~') : null;
    // tslint:disable-next-line:max-line-length
    const colorCodeList = parts[7].length > 0 ? parts[7].split('~') : null; // color codes respective to text lines seperated by ` - carrot symbol
    // tslint:disable-next-line:max-line-length
    const textLineList = parts[8].length > 0 ? parts[8].split('~') : null; // text lines respective to color codes seperated by ` - carrot symbol
    const msgpagesi = [];
    let msg: VmsMsg = null;
    try {
      for (let page = 0; page < pageNumberTotal; page++) {
        const templateInPage = templateList[page];
        const vmsTemplate: VmsTemplate = {
          // tslint:disable-next-line:radix
          id: Number.parseInt(templateInPage),
          EquipType: equipType,
          Name: null,
          Height: 0,
          Width: 0,
          Phase: 0,
          Pictograms: null,
          TextLines: null,
        };
        const textLinesInPage = textLineList[page].split('`');
        const colorCodesInPage = colorCodeList[page].split('`');

        const textLinesi = new Array(textLinesInPage.length);

        for (let k = 0; k < textLinesInPage.length; k++) {

          const msgTextLine: VmsMsgTextLine = { RichCharCollection: [] };
          const textArry = textLinesInPage[k].split('');
          const colorArry = colorCodesInPage[k].split('');

          for (let i = 0; i < textArry.length; i++) {
            msgTextLine.RichCharCollection.push({ 'Text': textArry[i].toString(), 'Color': colorArry[i].toString() });
          }
          textLinesi[k] = msgTextLine;
        }

        let pictogramsi: Array<VmsMsgPictogram> = [];
        let msgPage: VmsMsgPage;
        if (pictList != null) {
          const pictogramInPage = pictList[page].split('`');    // ^1412'1613~
          pictogramsi = pictogramInPage == null ? null : new Array(pictogramInPage.length);

          for (let m = 0; m < pictogramInPage.length; m++) {
            // tslint:disable-next-line:radix
            const pinfo: PictogramInfo = { 'id': Number.parseInt(pictogramInPage[m]) };
            const tmpPictInfo = {
              'Sequence': m + 1,
              'Height': 0,
              'Width': 0,
              'X': 0,
              'Y': 0,
              'DefaultImage': null,
            };

            const msgPictogram: VmsMsgPictogram = { 'Pictogram': pinfo, 'Template': tmpPictInfo };
            pictogramsi[m] = msgPictogram;
          }
          msgPage = this.VmsMsgPage(
            page + 1, vmsTemplate, pictogramsi, textLinesi,
            // tslint:disable-next-line:radix
            Number.parseInt(pageMode[page]),
            // tslint:disable-next-line:radix
            Number.parseInt(toggleTime[page]));

        } else if (pictList == null) {
          pictogramsi = null;
          msgPage = this.VmsMsgPage(
            page + 1, vmsTemplate, pictogramsi, textLinesi,
            // tslint:disable-next-line:radix
            Number.parseInt(pageMode[page]),
            // tslint:disable-next-line:radix
            Number.parseInt(toggleTime[page]));
        }
        msgpagesi[page] = msgPage;
      }
      msg = this.VmsMsg(equipId, new Date(),
        // tslint:disable-next-line:radix
        Number.parseInt((parts[0])),
        // tslint:disable-next-line:radix
        Number.parseInt((parts[1])), msgpagesi);
    }
    finally {
      // console.log(e);
    }
    return msg;
  }

  ttpParser(equipId, equipType, cmdParam, msgTextLines: Array<VmsMsgTextLine>, pictograms: Array<PictogramInfo>, isBlankTemplate?): VmsMsg {
    const parts = cmdParam.split('^');
    const pageMode = parts[3].Length > 0 ? parts[3].split('~') : null;
    const pageNumberTotal = pageMode.Length;
    const templateList = parts[4].Length > 0 ? parts[4].split('~') : null;
    const toggleTime = parts[5].Length > 0 ? parts[5].split('~') : null;
    const pictList = parts[6].Length > 0 ? parts[6].split('~') : null;
    // tslint:disable-next-line:max-line-length
    const colorCodeList = parts[7].Length > 0 ? parts[7].split('~') : null; // color codes respective to text lines seperated by ` - carrot symbol
    // tslint:disable-next-line:max-line-length
    const textLineList = parts[8].Length > 0 ? parts[8].split('~') : null; // text lines respective to color codes seperated by ` - carrot symbol
    const msgpagesi: Array<VmsMsgPage> = new Array[pageNumberTotal];
    let msg: VmsMsg = null;
    for (let page = 0; page < pageNumberTotal; page++) {
      const templateInPage = templateList[page];
      const vmsTemplate: VmsTemplate = {
        // tslint:disable-next-line:radix
        id: Number.parseInt(templateInPage),
        EquipType: equipType,
        Name: null,
        Height: 0,
        Width: 0,
        Phase: 0,
        Pictograms: null,
        TextLines: null,
      };

      const textLinesInPage = textLineList[page].split('`');
      const colorCodesInPage = colorCodeList[page].split('`');

      const textLinesi: Array<VmsMsgTextLine> = new Array(msgTextLines.length);

      for (let k = 0; k < msgTextLines.length; k++) {
        const msgTextLine: VmsMsgTextLine = { RichCharCollection: [] };
        if (k <= 1) {
          const textArry = isBlankTemplate ? '' : textLinesInPage[k].toCharArray();
          const colorArry = isBlankTemplate ? '' : colorCodesInPage[k].toCharArray();

          for (let i = 0; i < textArry.Length; i++) {
            msgTextLine.RichCharCollection.push(textArry[i].toString(), colorArry[i].toString());
          }
          textLinesi[k] = msgTextLine;
        }
        if (k > 1) {
          textLinesi[k] = msgTextLines[k];
        }
      }

      let pictogramsi: Array<VmsMsgPictogram> = new Array(pictograms.length);
      let msgPage: VmsMsgPage;
      if (pictograms != null) {
        for (let m = 0; m < pictograms.length; m++) {
          const pinfo: PictogramInfo = pictograms[m];

          const tmpPictInfo: VmsTemplatePictogram = {
            'Sequence': m + 1,
            'Height': 0,
            'Width': 0,
            'X': 0,
            'Y': 0,
            'DefaultImage': pinfo,
          };
          const msgPictogram: VmsMsgPictogram = { 'Pictogram': pinfo, 'Template': tmpPictInfo };
          //  const msgPictogram = new VmsMsgPictogram(pinfo, tmpPictInfo);
          pictogramsi[m] = msgPictogram;
        }
        msgPage = this.VmsMsgPage(
          page + 1, vmsTemplate, pictogramsi, textLinesi,
          // tslint:disable-next-line:radix
          Number.parseInt(pageMode[page]),
          // tslint:disable-next-line:radix
          Number.parseInt(toggleTime[page]));

      } else if (pictograms == null) {
        pictogramsi = null;
        msgPage = this.VmsMsgPage(page + 1, vmsTemplate, pictogramsi, textLinesi,
          // tslint:disable-next-line:radix
          Number.parseInt(pageMode[page]),
          // tslint:disable-next-line:radix
          Number.parseInt(toggleTime[page]));
      }
      msgpagesi[page] = msgPage;
    }
    msg = this.VmsMsg(equipId, new Date(),
      // tslint:disable-next-line:radix
      Number.parseInt((parts[0])),
      // tslint:disable-next-line:radix
      Number.parseInt((parts[1])), msgpagesi);
    return msg;
  }

  CommandDetail(ExecId, CmdId, EquipId, CmdDesc, EquipLocation, CTRLTYPE, CMDPARA) {
    return {
      ExecId,
      CmdId,
      Sender: localStorage.getItem('user_name'),
      EquipId,
      StartTime: new Date(),
      CmdDesc,
      CmdStatus: CommandStatusType.Processing,
      CmdStatusName: 'Processing',
      EquipLocation,
      CTRLTYPE,
      CMDPARA,
    };
  }

  VmsMsgPage(pageNumberi, templatei, pictogramsi, textLinesi, pageModei, toggleTimei): VmsMsgPage {
    const msgPage = {
      'Number': pageNumberi,
      'mode': pageModei,
      'toggleTime': toggleTimei,
      'Template': templatei,
      'Pictograms': pictogramsi ? pictogramsi : new Array(templatei == null ? 0 : templatei.Pictograms.length()),
      'TextLines': textLinesi ? textLinesi : new Array(templatei == null ? 0 : templatei.TextLines.length()),
    };
    return msgPage;
  }

  VmsMsg(EquipId, UpdateTime = null, displayMode = 2, toggleMode = 0, VmsMsgPages: Array<VmsMsgPage> = null): VmsMsg {
    let EquipType = null;
    let ExpWayCode = null;
    let Phase = null;
    let IpAddr = null;
    if (!EquipId && EquipId !== '') {
      const equipConfig = this.commonService.getEquipConfigById(EquipId);
      if (equipConfig != null) {
        EquipType = equipConfig.equipType;
        ExpWayCode = equipConfig.expwayCode;
        Phase = equipConfig.phase;
        IpAddr = equipConfig.ipAddress;
      }
      VmsMsgPages = VmsMsgPages ? VmsMsgPages : [];
    }
    return { EquipId, EquipType, ExpWayCode, UpdateTime, displayMode, toggleMode, VmsMsgPages, Phase, IpAddr };
  }

  Exit() {
    this.isVisible = false;
  }
  // 模态框Release按钮
  Release() {
    this.titleName = 'Release';
    this.isSpinning = true;
    const execId = 'AW_' + new Date().getTime();
    const responseArray = [];
    let requestCount = 0;
    this.listOfData.forEach((equip,index) => {
      const equipType = equip.Eqiupment.substring(2, 3);
      let substr = equip.Eqiupment.substring(2);
      let cd;
      let response;
      switch (equip.Control_Type) {
        case 'cdd':
          if (equip.Eqiupment.startsWith('ta') || equip.Eqiupment.startsWith('tb')) {
            equip.Eqiupment = substr;
            const msg = this.Blank_Template_Parser(equip.Eqiupment, equipType);
            cd = this.CommandDetail(execId, equip.Command_NO, equip.Eqiupment, 'Send message', '', 'cdd', 'BlankTemplate');
            response = this.ImplementPlanVMScdd(cd, equipType, msg);
            responseArray.push(cd);
          } else if (equip.Eqiupment.startsWith('tc')) {
            equip.Eqiupment = substr;
            cd = this.CommandDetail(execId, equip.Command_NO, equip.Eqiupment,
              'Send message', '', equip.Control_Type, equip.Command_Parameter);
            this.equipmentService.getVmsMsgByEquipId({ 'equipId': equip.Eqiupment }).subscribe(res => {
              const TrvTimeMsg = res['Body']['getVmsMsgByEquipIdResponse']['vmsMsgDto'];
              const lnTxtDto = TrvTimeMsg.vmsMsgPageDtoList[0].lineTextDtoList;
              let msg = null;
              if (lnTxtDto != null) {
                msg = this.parseCmdV2(TrvTimeMsg, msg, equip, equipType, true);
              }
              response = this.ImplementPlanVMScdd(cd, equipType, msg);
            });
            responseArray.push(cd);
          } else if (equip.Eqiupment.startsWith('al')) { // //LUS ControlCommand
            const felsCode = 'al';
            substr = equip.Eqiupment.substring(6);
            cd = this.CommandDetail(execId, equip.Command_NO, substr, 'Send message', '', 'cdd', '0');
            if (cd.EquipId.startsWith('t')) {
              response = this.equipmentService.AW_CFELS_LUS({
                'lusMsg': {
                  attrName: cd.CTRLTYPE,
                  attrValue: cd.CMDPARA,
                  equipId: cd.EquipId,
                  felsCode: felsCode,
                  equipmentCode: equipType
                }
              })
            }
            responseArray.push(cd);
          } else if (equip.Eqiupment.startsWith('ax')) { // //PMCS ControlCommand
            const felsCode = 'ax';
            substr = equip.Eqiupment.substring(6);
            equip.Eqiupment = substr;
            cd = this.CommandDetail(execId, equip.Command_NO, substr, 'Send message', '', 'cdd', '0');
            response =  this.implementPlanPMCS(cd, felsCode, equipType);
            responseArray.push(cd);
          }
          break;
        case 'cdl':
          if (equip.Eqiupment.startsWith('ta') || equip.Eqiupment.startsWith('tb') || equip.Eqiupment.startsWith('tc')) {

            equip.Eqiupment = substr;
            cd = this.CommandDetail(execId, equip.Command_NO, substr, 'Set dimming mode', '', 'cdl', '1,0');


            if (cd.EquipId.startsWith('tip') || cd.EquipId.startsWith('tep') || cd.EquipId.startsWith('ttp')) {
              // creating a object 'dDto' of the class 'dimmingDto'which is in the CMHWebservice. Then
              // send this object to the backend.
              response = this.equipmentService.AW_CFELS_Dimming({
                'dimming': {
                  execId: cd.ExecId,
                  cmdId: cd.CmdId,
                  sender: cd.Sender,
                  equipId: cd.EquipId,
                  systemId: this.constantsService.SystemID,
                  dimMode: 1,
                  dimLevel: 0
                }
              })

            }
            // tslint:disable-next-line:max-line-length
            // string logVmsCdl = "HASH TAG #### Send dimming level(cdl) for Implement Plan completed #  PPID : {0} # Cmd No : {1} # Ctrl Type : {2} # Equip Id :{3} # Cmd Para : {4}";
            // HelperClass.LogInfo(string.Format(logVmsCdl, equip.PPID, equip.CMDNO, equip.CTRLTYPE, equip.EquipID, equip.CMDPARA));
            responseArray.push(cd);
          }

          break;
        case 'cds':
        case 'cdw':
        case 'cde':
          if (equip.Eqiupment.startsWith('ax')) {
            const felsCode = 'ax';
            substr = equip.Eqiupment.substring(6);
            equip.Eqiupment = substr;
            cd = this.CommandDetail(execId, equip.Command_NO, substr, 'Send message', '', equip.Control_Type, '0');
            response = this.implementPlanPMCS(cd, felsCode, equipType);
            responseArray.push(cd);
          }

          break;
        default:
          break;
      }
      if (response) {
        requestCount += 1;
        response.requestCount = requestCount
        response.subscribe((data: any) => {
          if (!data.Body.Fault) {
            cd.CmdStatus = CommandStatusType.Success;
            cd.CmdStatusName = 'Success';
          } else {
            cd.CmdStatus = CommandStatusType.TimeOut;
            cd.CmdStatusName = 'TimeOut';
          }      
          if(response.requestCount === requestCount)  //最后一次请求更新dom
          {
            this.responseArray = responseArray;
            this.isSpinning = false;
            this.isResVisible = true;
          }
        
        });
      }
    });

  }
  implementPlanPMCS(cd, felsCode, equipType) {
    if (cd.EquipId.startsWith('f') || cd.EquipId.startsWith('vx') || cd.EquipId.startsWith('es') || cd.EquipId.startsWith('px')) {
     return this.equipmentService.AW_CFELS_PMCS({
        'pmcsMsg': {
          attrName: cd.CTRLTYPE,
          attrValue: cd.CMDPARA,
          felsCode: felsCode,
          equipId: cd.EquipId,
          equipmentCode: equipType
        }
      })
    }
  }
  Blank_Template_Parser(equipId, equipType) {
    let vmsTemplate = null;
    if (equipType === 'tip') {
      vmsTemplate = createVmsTemplate(600, equipType, 'Blank Msg', 82, 280, 1, null, null);
      // tslint:disable-next-line:max-line-length
      // string logVmsBlankTemplate = "RELEASE VMS BLANK TEMPLATE DETAILS: , TemplateId : {0} , EquipType : {1} , Message : {2} , Height : {3} , Width : {4} , Phase : {5}";
      // tslint:disable-next-line:max-line-length
      // HelperClass.LogInfo(string.Format(logVmsBlankTemplate, vmsTemplate.Id, vmsTemplate.EquipType, vmsTemplate.Name, vmsTemplate.Height, vmsTemplate.Width, vmsTemplate.Phase));
    } else if (equipType === 'tep') {
      vmsTemplate = createVmsTemplate(805, equipType, 'Blank Msg', 88, 288, 1, null, null);
      // tslint:disable-next-line:max-line-length
      // string logVmsBlankTemplate = "RELEASE VMS BLANK TEMPLATE DETAILS: , TemplateId : {0} , EquipType : {1} , Message : {2} , Height : {3} , Width : {4} , Phase : {5}";
      // tslint:disable-next-line:max-line-length
      // HelperClass.LogInfo(string.Format(logVmsBlankTemplate, vmsTemplate.Id, vmsTemplate.EquipType, vmsTemplate.Name, vmsTemplate.Height, vmsTemplate.Width, vmsTemplate.Phase));
    }
    const msgPage = this.VmsMsgPage(1, vmsTemplate, null, null, 0, 5);
    const msgpagesi: Array<VmsMsgPage> = new Array(1);
    msgpagesi[0] = msgPage;
    const msg = this.VmsMsg(equipId, new Date(), 2, 0, msgpagesi);
    // tslint:disable-next-line:max-line-length
    // string logReleaseVmsMsg = "RELEASE VMS MESSAGE: , TemplateId : {0} , EquipType : {1} , Message : {2} , Height : {3} , Width : {4} , Phase : {5}";
    // tslint:disable-next-line:max-line-length
    // HelperClass.LogInfo(string.Format(logReleaseVmsMsg, vmsTemplate.Id, vmsTemplate.EquipType, vmsTemplate.Name, vmsTemplate.Height, vmsTemplate.Width, vmsTemplate.Phase));
    return msg;
  }
  handleCancel() {
    this.isVisible = false;
  }
  // 显示模态框
  showModal(): void {
    this.isVisible = true;
    const a = [];
    let i = 1;
    this.listOfData1.forEach(item => {
      if (this.selectItem.ppID === item.PP_ID) {
        a.push({
          ...item,
          'NO': `${i++}`,
        });
      }
    });
    this.listOfData = a;
    console.log(this.listOfData)
  }
  sort(sort: { key: string; value: string }, sortArry: string) {


    const newArray = this.listOfData.sort((a, b) => ('' + a[sort.key]).localeCompare(b[sort.key]));
    this.ngZone.run(() => {
      this.listOfData = newArray;
    });

  }


}
