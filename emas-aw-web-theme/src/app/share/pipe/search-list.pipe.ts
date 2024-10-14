/**
 * @作者: hhr
 * @时间: 2018-07-06 10:32:45
 * @描述: 筛选
 * @使用: <element *ngFor="let item of items | searchList:'name':'张三' ">
 */
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'searchList',
    pure: true
})

/**
 * @param array 筛选对象
 * @param field 筛选字段
 * @param search 搜索内容
 */
export class SearchListPipe implements PipeTransform {
    transform(array: any[], field: string, search: string): any[] {
        if (!array || !array.length || !search) {
            return array;
        } else {
            const list = [];
            array.forEach((item, index) => {
                if (item[field].indexOf(search) !== -1) {
                    list.push(item);
                }
            });

            return list;
        }
    }
}
