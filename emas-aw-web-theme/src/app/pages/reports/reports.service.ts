
import { map } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { wsSend } from 'src/app/public/utils/webservices';
import { of } from 'rxjs';
import * as ExcelJS from 'exceljs';
import { Document, Table, TableRow, TableCell, Paragraph, VerticalAlign, Packer, Media, TextRun, ShadingType, HeadingLevel, AlignmentType } from 'docx';
import { saveAs } from 'file-saver';
import * as moment from 'moment';
const { Buffer } = require('buffer');
@Injectable({
    providedIn: 'root'
})
export class ReportsService {

    private equipConfig;
    getEquipTypeBySubSytem(data) {
        return wsSend('getEquipTypeBySubSytem', { 'subSystem': data }, 'CommonWebService');
    }

    getAllTechAlarmConfig() {
        return wsSend('getAllTechAlarmConfig', {}, 'CommonWebService');
    }

    getAllEquipConfig() {
        if (!this.equipConfig) {
            const request$ = wsSend('getAllEquipConfig', {}, 'CommonWebService');
            request$.subscribe((res: any) => {
                this.equipConfig = res.Body.getAllEquipConfigResponse.equipConfigDtoList;
            });
            return request$.pipe(map((res: any) => res.Body.getAllEquipConfigResponse.equipConfigDtoList));
        }
        return of(this.equipConfig);
    }

    getTrafficAlertResult(data) {
        if (data.AIType == 'video') {
            return wsSend('getTrafficAlertResultVideo', data, 'AWWebService');
        } else {
            return wsSend('getTrafficAlertResultImage', data, 'AWWebService');
        }
        
    }
    getHistTrafficAlert(data) {
        // console.log(data)
        return wsSend('getHistTrafficAlert', data, 'AWWebService');
    }
    getCountOfHistTrafficMeasure(data) {
        return wsSend('getCountOfHistTrafficMeasure', data, 'AWWebService');
    }
    getHistTechAlarm(data) {
        return wsSend('getHistTechAlarm', data, 'AWWebService');
    }
    // equipStatus
    getCountOfHistEquipStatus(param) {
        return wsSend('getCountOfHistEquipStatus', param, 'AWWebService');
    }
    getHistEquipStatusByEquipIdAndDate(param) {
        return wsSend('getHistEquipStatusByEquipIdAndDate', param, 'AWWebService');
    }

    getHistTrafficMeasure(data) {
        return wsSend('getHistTrafficMeasure', data, 'AWWebService')
    }
    /*generate Report模块打印方法说明
     @method print
     @param{数组} eNum 表示要打印的echarts图数量
     @param{字符串} name 你要选区的dom节点id名称
     参考 traffic measure和technical alarm界面
 */
    print(eNum: number[], name: string, savePDF?) {

        this.cavansToImg(name, eNum, savePDF);
        document.querySelector('.cdk-overlay-container')['style'].display = 'none';
        return new Promise<void>((resolve, reject) => {
            setTimeout(() => {
                window.print();
                resolve();
                document.querySelector('.cdk-overlay-container')['style'].display = '';
                document.getElementById('for-print').innerHTML = '';
            }, 500);
        });
    }
    /**
     * 将固定数据转化成excel导出
     * @param header  excel 上面部分
     * @param tbTitle excel表内容的表头
     * @param body  excel表内容
     * @param id dom id
     */
    toEcxel(header, tbTitle, body, id) {
        // console.log('header:', header[0])
        header[0][1] = this.formatDate(header[0][1]);
        header[0][4] = this.formatDate(header[0][4]);
        // console.log('header:', header[0])
        const workbook = new ExcelJS.Workbook();
        const sheet = workbook.addWorksheet('My Sheet');
        sheet.addRows(header);
        const titlePos = header.length + 1;
        sheet.addTable({
            name: 'MyTable',
            ref: `A${titlePos}`,
            headerRow: true,
            totalsRow: true,
            style: {
                theme: 'TableStyleLight1',
                showRowStripes: true,
            },
            columns: tbTitle,
            rows: body,
        });
        const echartCanvas: any = document.getElementsByTagName('canvas');
        for (let index = 0; index < echartCanvas.length; index++) {
            const canvas = echartCanvas[index];
            const base64Src = canvas.toDataURL();
            const imageId2 = workbook.addImage({
                base64: base64Src,
                extension: 'png',
            });
            const pos: any = {
                tl: { col: 0, row: titlePos + body.length + 2 + 5 * index },
                ext: { width: canvas.width, height: canvas.height }
            };
            sheet.addImage(imageId2, pos);
        }
        const row: any = sheet.getRow(titlePos);
        row.fill = {
            type: 'pattern',
            pattern: 'solid',
            fgColor: { argb: 'FFFFFFFF' },
        };
        row._cells.forEach((cell, index) => {
            cell.style = {
                font: {
                    bold: true, color: { argb: 'FFFFFFFF' },
                },
                fill: {
                    type: 'pattern',
                    pattern: 'solid',
                    fgColor: { argb: 'FF6495ED' },
                }
            };
            sheet.getColumn(index + 1).width = tbTitle[index].width;
        });
        const fileName = id + '-' + moment().format('YYYY-MM-DD-HH:mm a');
        workbook.xlsx.writeBuffer().then(data => {
            const blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8' });
            const a = document.createElement('a');
            a.href = URL.createObjectURL(blob);
            a.download = fileName; // 这里填保存成的文件名
            a.click();
            URL.revokeObjectURL(a.href);
            a.remove();
        });
    }
    // format DateTime for Ecxel
    formatDate(dateTime) {
        let tempDateTime = new Date(dateTime);
        let tempTime = tempDateTime.getTime();
        let tempOffset = tempDateTime.getTimezoneOffset();
        tempDateTime = new Date(tempTime - tempOffset * 60 * 1000);
        let tempDate = tempDateTime.toJSON();
        tempDate = tempDate.replace("T", " ");
        tempDate = tempDate.replace("Z", "");
        let dateList = tempDate.split(".");
        tempDate = dateList[0];
        return tempDate;
    }

    toWord(header, tbTitle, body, id) {
        const doc = new Document();
        const section: any = [new Paragraph({
            text: document.querySelector(`#${id} .topTitle`).textContent,
            heading: HeadingLevel.HEADING_1,
            alignment: AlignmentType.CENTER
        })];
        const rows = [];
        header.forEach(hd => {
            const children = [];
            hd.forEach(headerText => {
                let text = headerText;
                if (headerText instanceof Date) {
                    text = moment(headerText).format('YYYY/MM/DD HH:mm a');
                }
                text = text + '  ';
                children.push(new TextRun({
                    text,
                    bold: true,
                }));
            });
            section.push(new Paragraph({}));
            section.push(new Paragraph({ children }));
        });
        section.push(new Paragraph({}));
        const titleRow = tbTitle.map(tb => {
            return new TableCell({
                children: [new Paragraph({ text: tb.name })],
                verticalAlign: VerticalAlign.CENTER,
                shading: { fill: '6495ED', color: '6495ED' },
            });
        });
        rows.push(new TableRow({ children: titleRow }));
        body.forEach((row, index) => {
            const tableRow = { children: [], style: 'row' + index % 2 };
            let shading = null;
            if (index % 2 === 1) {
                shading = { fill: '#d8d8d8', color: '#d8d8d8' };
            }
            row.forEach((cell) => {
                tableRow.children.push(new TableCell({
                    children: [new Paragraph({ text: cell })],
                    verticalAlign: VerticalAlign.CENTER,
                    shading
                }));
            });
            rows.push(new TableRow(tableRow));
        });
        section.push(new Table({ rows, alignment: AlignmentType.CENTER }));
        const echartCanvas: any = document.getElementsByTagName('canvas');
        for (let index = 0; index < echartCanvas.length; index++) {
            const canvas = echartCanvas[index];
            const base64 = canvas.toDataURL().replace(/^data:image\/\w+;base64,/, '');
            const image = Media.addImage(doc, Buffer.from(base64, 'base64'), canvas.width / 2, canvas.height / 2);
            section.push(new Paragraph(image));
        }
        doc.addSection({
            children: section,
        });
        const fileName = id + '-' + moment().format('YYYY-MM-DD-HH:mm a');
        Packer.toBlob(doc).then((blob) => {
            saveAs(blob, fileName);
        });
    }

    cavansToImg(name, eNum?, savePDF?) {
        let printelemnt: any = document.getElementById(name).innerHTML;
        if (savePDF) {
            printelemnt = ` <span class='alert-print' style="color: red;align-self: flex-start;margin-left: 16px;">
                             Change Printer To 'Save as PDF'</span>${printelemnt}`;
        }
        document.getElementById('for-print').innerHTML = printelemnt;
        const echartCanvas = document.querySelectorAll('#for-print canvas');
        if (!eNum) {
            eNum = [];
            for (let i = 0; i < echartCanvas.length; i++) {
                eNum.push(i);
            }
        }
        if (echartCanvas.length > 0) {
            echartCanvas.forEach((e: any) => {
                e.parentNode.parentNode.remove();
            });

            eNum.forEach(item => {
                const img: any = document.querySelector('#for-print #echartsImg' + item);
                const canvas: any = document.querySelectorAll('canvas')[item];
                if (img) {
                    img.src = canvas.toDataURL();
                }
            });
        }
    }

}

export interface ExcelHeader {
    header: string;
    key: string;
    width: number;
}