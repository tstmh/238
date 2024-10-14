import { Directive, ElementRef, Renderer2, AfterViewInit } from '@angular/core';
import { fromEvent } from 'rxjs';
import { filter, map, takeUntil, concatAll } from 'rxjs/operators';
@Directive({
    // tslint:disable-next-line:directive-selector
    selector: '[zmMovableModal]'
})
export class ZmMovableModalDirective implements AfterViewInit {


    constructor(private elementRef: ElementRef, private render: Renderer2) {
        // console.log(elementRef);
        // console.log(render);
    }
    ngAfterViewInit() {
        const modalElement = this.getModalElement();

        // let modalTitleElement = this.getModalTitleElement();

        const mouseDown = fromEvent<MouseEvent>(this.elementRef.nativeElement, 'mousedown');
        const mouseUp = fromEvent<MouseEvent>(document, 'mouseup');
        const mouseMove = fromEvent<MouseEvent>(document, 'mousemove');
        mouseDown.pipe(
            filter((e: any) => e.target.className && e.target.className.indexOf('ant-modal-header') > -1),
            map(e => {
                const { left, top } = (e.target as any).getBoundingClientRect();
                const clickOffsetX = e.clientX - left;
                const clickOffsetY = e.clientY - top;
                this.render.setStyle(modalElement, 'position', 'absolute');
                return {
                    clickOffsetX,
                    clickOffsetY
                };
            }),
            map(({ clickOffsetX, clickOffsetY }) => {
                return mouseMove.pipe(

                    map(moveEvent =>
                        ({
                            x: moveEvent.clientX - clickOffsetX,
                            y: moveEvent.clientY - clickOffsetY
                        })),
                    takeUntil(mouseUp),
                    takeUntil(mouseDown),
                );

            }),
            concatAll(),

        )
            .subscribe(({ x, y }) => {
                modalElement.style.left = `${x}px`;
                modalElement.style.top = `${y}px`;
            });

    }
    getModalElement() {
        return this.elementRef.nativeElement.querySelector('.ant-modal');
    }
    getModalTitleElement() {
        return this.elementRef.nativeElement.querySelector('.ant-modal-header');
    }
}
