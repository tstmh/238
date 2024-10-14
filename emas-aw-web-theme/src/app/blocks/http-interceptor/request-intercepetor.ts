import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpHeaders, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { MessagesService } from 'src/app/share/service';

@Injectable()
export class RequestInterceptor implements HttpInterceptor {
    constructor(private msg: MessagesService) {}

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any> | any> {
        let _headers = new HttpHeaders()
            // .set('Authorization', this.ls.getAccessToken() + '')
            // .set('X-Requested-With', 'XMLHttpRequest')
            .set('Content-Type', 'application/json');
        req.headers.keys().forEach(item => {
            _headers = _headers.set(item, req.headers.get(item));
        });

        const authReq = req.clone({
            headers: _headers
        });

        return next.handle(authReq).pipe(
            catchError((err: HttpErrorResponse) => {
                this.handleError(err.status);
                throw err;
            })
        );
    }

    // 报错信息
    handleError(status) {
        if (status === 0) {
            this.msg.error(`${status} please checkout the network`);
        } else if (status === 401) {
            // this.msg.error(`user token out of date`);
            // this.ls.logOut();
        } else if (status === 404) {
            this.msg.error('The requested resource does not exist');
        } else if (status === 500) {
            this.msg.error('server error, please try again later');
        } else {
            // this.msg.error('未知错误，请检查网络');
            console.error('an unknown error');
        }
    }
}
