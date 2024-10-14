/**
 * @作者: hhr
 * @时间: 2018-01-27 10:36:16
 * @描述: 现金额转大写
 * @使用: {{ 100 | digitUppercase }}
 */
import { Pipe, PipeTransform } from '@angular/core';
import { ToolsService } from '../service';

@Pipe({
    name: 'digitUppercase'
})
export class DigitUppercasePipe implements PipeTransform {
    constructor(private tools: ToolsService) {}

    transform(vl: any): String {
        return this.tools.digitUppercase(vl);
    }
}
