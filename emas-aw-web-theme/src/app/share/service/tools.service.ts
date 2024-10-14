import { Injectable } from '@angular/core';
import { NzTreeNode } from 'ng-zorro-antd';

@Injectable()
export class ToolsService {
    /**
     * 深度复制
     */
    isClone(obj) {
        if (null == obj || 'object' !== typeof obj) {
            return obj;
        }
        if (obj instanceof Date) {
            const copy = new Date();
            copy.setTime(obj.getTime());
            return copy;
        }
        if (obj instanceof Array) {
            const copy = [];
            for (let i = 0, len = obj.length; i < len; ++i) {
                copy[i] = this.isClone(obj[i]);
            }
            return copy;
        }
        if (obj instanceof Object) {
            const copy = {};
            for (const attr in obj) {
                if (obj.hasOwnProperty(attr)) {
                    copy[attr] = this.isClone(obj[attr]);
                }
            }
            return copy;
        }
        throw new Error("Unable to copy obj! Its type isn't supported.");
    }

    /**
     * 删除对象中指定属性
     * @param obj  [受检查对象]
     * @param keys [指定属性]
     * 使用方法：isObjectDelKay(obj, "id")
     */
    isObjectDelKay(obj, keys) {
        if (!Array.isArray(obj)) {
            for (const i in obj) {
                if (obj.hasOwnProperty(i)) {
                    if (i === keys) {
                        delete obj[i];
                    }
                    if (Array.isArray(obj[i])) {
                        this.isObjectDelKay(obj[i], keys);
                    }
                }
            }
        } else {
            for (const i in obj) {
                if (obj.hasOwnProperty(i)) {
                    this.isObjectDelKay(obj[i], keys);
                }
            }
        }
        return obj;
    }

    /**
     * Guid
     * @param size 长度
     */
    isGuid(size: number): string {
        let guid = '';
        for (let i = 1; i <= size; i++) {
            const n = Math.floor(Math.random() * 16.0).toString(16);
            guid += n;
        }
        return guid;
    }

    /**
     * bold转文件带下载
     * @param _file_data 文件流 bold
     * @param _file_name 下载文件名称指定
     */
    isDownload(_file_data: any, _file_name?: string) {
        const blob = new Blob([_file_data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        const objectUrl = URL.createObjectURL(blob);
        const a = document.createElement('a');
        document.body.appendChild(a);
        a.setAttribute('style', 'display:none');
        a.setAttribute('href', objectUrl);
        a.setAttribute('download', _file_name || '默认文件名称');
        a.click();
        document.body.removeChild(a);
        // 释放URL地址
        URL.revokeObjectURL(objectUrl);
    }

    /**
     * 判断样式是否存在
     * @param elem element
     * @param cls 样式名称
     */
    hasClass(elem, cls) {
        cls = cls || '';
        // 当cls没有参数时，返回false
        if (cls.replace(/\s/g, '').length === 0) {
            return false;
        }
        return new RegExp(' ' + cls + ' ').test(' ' + elem.className + ' ');
    }

    /**
     * 添加样式名
     * @param ele element
     * @param cls 样式名称
     */
    addClass(ele, cls) {
        if (!this.hasClass(ele, cls)) {
            ele.className = ele.className === '' ? cls : ele.className + ' ' + cls;
        }
    }

    /**
     * 删除样式名
     * @param richtxt 对象
     * @param cls 样式名
     */
    delClass(richtxt: any, cls: string) {
        let obj_class = ' ' + richtxt.className + ' '; // 获取 class 内容, 并在首尾各加一个空格. ex) 'abc    bcd' -> ' abc    bcd '
        obj_class = obj_class.replace(/(\s+)/gi, ' '); // 将多余的空字符替换成一个空格. ex) ' abc    bcd ' -> ' abc bcd '
        let removed = obj_class.replace(' ' + cls + ' ', ' '); // 在原来的 class 替换掉首尾加了空格的 class. ex) ' abc bcd ' -> 'bcd '
        removed = removed.replace(/(^\s+)|(\s+$)/g, ''); // 去掉首尾空格. ex) 'bcd ' -> 'bcd'
        richtxt.className = removed; // 替换原来的 class.
    }

    /**
     * 随机生成颜色
     */
    getRandomColor() {
        // tslint:disable-next-line:no-bitwise
        return '#' + ('00000' + ((Math.random() * 0x1000000) << 0).toString(16)).slice(-6);
    }

    /**
     * 现金额转大写
     */
    digitUppercase(n) {
        const fraction = ['角', '分'];
        const digit = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'];
        const unit = [['元', '万', '亿'], ['', '拾', '佰', '仟']];
        const head = n < 0 ? '欠' : '';
        n = Math.abs(n);
        let s = '';
        for (let i = 0; i < fraction.length; i++) {
            s += (digit[Math.floor(n * 10 * Math.pow(10, i)) % 10] + fraction[i]).replace(/零./, '');
        }
        s = s || '整';
        n = Math.floor(n);
        for (let i = 0; i < unit[0].length && n > 0; i++) {
            let p = '';
            for (let j = 0; j < unit[1].length && n > 0; j++) {
                p = digit[n % 10] + unit[1][j] + p;
                n = Math.floor(n / 10);
            }
            s = p.replace(/(零.)*零$/, '').replace(/^$/, '零') + unit[0][i] + s;
        }
        return (
            head +
            s
                .replace(/(零.)*零元/, '元')
                .replace(/(零.)+/g, '零')
                .replace(/^整$/, '零元整')
        );
    }

    /**
     * @desc 判断初始值是否为所需类型，不是则返回备用值（一般为该类型的空值）
     * @param original 原始值
     * @param backup 备用值
     */
    factory(original: any, backup: any = ''): any {
        function type(o: any) {
            return Object.prototype.toString.call(o).slice(8, -1);
        }

        const srcType = type(original);
        const desType = type(backup);

        if (srcType === desType) {
            return original;
        } else {
            return backup;
        }
    }

    /**
     * @desc 取值函数
     *
     */
    calc(obj: any, path: string | Array<string>, type: any): any {
        path = Array.isArray(path)
            ? path
            : path
                  .replace(/\[/g, '.')
                  .replace(/\]/g, '')
                  .split('.');
        const result = path.reduce((o, k) => (o || {})[k], obj);
        if (result || result === 0) {
            return path.reduce((o, k) => (o || {})[k], obj);
        } else {
            return this.factory(obj, type);
        }
    }
    /**@desc 把多个树结构打平成数组, 不包括父节点 */
    treeFlat(obj: NzTreeNode[]): NzTreeNode[] {
        return obj.reduce((prevArr, cnode) => {
            if (cnode.children && cnode.children.length > 0) {
                return [...prevArr, ...this.treeFlat(cnode.children)];
            } else {
                return [...prevArr, cnode];
            }
        }, []);
    }
    /**
     *生成树形结构数组
     */
    createTreeData(original, childIdProp, parentIdProp) {
        let index = 0;
        const tree = [];
        let item = original[0];
        const treeObj = {};
        const len = original.length;
        let id: any, parent: any, parentId: number;

        // 生成treeObj字典
        original.forEach(row => (treeObj[row[childIdProp]] = row));

        while (index < len) {
            item = original[index++];
            id = item[childIdProp];
            parentId = item[parentIdProp];
            if (parentId || parentId === 0) {
                parent = treeObj[parentId];
                if (parent) {
                    parent.children ? parent.children.push(item) : (parent.children = [item]);
                }
            } else {
                tree.push(item);
            }
        }
        // console.log(tree);
        return tree;
    }
}
