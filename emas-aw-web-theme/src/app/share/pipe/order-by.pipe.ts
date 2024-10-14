/**
 * @作者: hhr
 * @时间: 2018-01-27 10:32:19
 * @描述: 排序
 * @使用: <element *ngFor="let item of items | orderBy:'name':true ">
 */
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'orderBy'
})
export class OrderByPipe implements PipeTransform {
    /**
     * @param array 排序对象
     * @param field 排序字段
     * @param reverse 是否倒序
     */
    transform(array: any[], field: string, reverse?: boolean): any[] {
        array.sort((a: any, b: any) => {
            if (a[field] < b[field]) {
                return -1;
            } else if (a[field] > b[field]) {
                return 1;
            } else {
                return 0;
            }
        });
        if (reverse) {
            return array.reverse();
        }
        return array;
    }
}
