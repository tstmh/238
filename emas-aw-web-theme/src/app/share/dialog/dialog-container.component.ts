import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'itmp-dialog-container',
  template: `
  <div class="itmp-dialog-box user-select">
    <itmp-dialog></itmp-dialog>
  </div>
  `,
  styles: [`
  .itmp-dialog-box{position: fixed;z-index:3000;width: 100%;top:0;left:0;bottom:0;text-align: center;}
  `]
})
export class DialogContainerComponent {

  constructor() {

  }

}
