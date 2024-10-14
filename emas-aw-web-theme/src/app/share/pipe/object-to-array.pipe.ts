/**
 * @作者: hhr
 * @时间: 2018-07-06 10:32:18
 * @描述: object 转 array
 */
import { Pipe, PipeTransform, Injectable } from '@angular/core';

@Pipe({
    name: 'objToArr'
})
export class ObjectToArrayPipe implements PipeTransform {
    /**
     * @param array '{a: '_a', b: '_b'}'
     * return [{key: 'a', value: '_a'}, {key: 'b', value: '_b'}]
     */
    transform(array: any): any[] {
        const _list = [];
        if (array) {
            for (const key in array) {
                if (array.hasOwnProperty(key)) {
                    const element = Object.assign({}, { key, value: array[key] });
                    _list.push(element);
                }
            }
        }
        return _list;
    }
}
