import { NgModule } from '@angular/core';
import { TestComComponent } from './test-com.component';
import { RouterModule } from '@angular/router';

@NgModule({
	declarations: [TestComComponent],
	imports: [
		RouterModule.forChild([
			{ path: '', component: TestComComponent }
		])
	]
})
export class TestComModule { }
