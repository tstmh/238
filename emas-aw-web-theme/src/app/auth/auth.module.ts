import { NgModule } from '@angular/core';
import { ShareModule } from 'src/app/share/ShareModule';
import { LoginComponent } from './login.component';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
    declarations: [LoginComponent],
    imports: [ ShareModule, ReactiveFormsModule],
    exports: [LoginComponent],
    providers: []
})
export class AuthModule {}
