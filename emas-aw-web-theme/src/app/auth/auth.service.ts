import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
// import { ConfigService } from 'app/config/config.service';
// import { USER_API_URL } from 'app/app.constants';
import { Observable } from 'rxjs';

export const httpOptions: Object = {
    headers: new HttpHeaders({
        // 'Content-Type': 'application/json',
        Authorization: 'No Auth'
    })
};
@Injectable({
    providedIn: 'root'
})
export class AuthService {
    // private _login = this.config.apiUrl.login;
    // private _user = this.config.apiUrl.user;
    // private apiUrl_user_online = USER_API_URL;
    // private _login = `http://192.168.0.58:9586`;

	constructor(private http: HttpClient,
		//  private config: ConfigService
		 ) {}

    // login(param): Observable<any> {
    //     const url = `${this._login}/login`;
    //     return this.http.post<any>(url, param);
    // }
    // getRoleName(param): Observable<any> {
    //     const url = `${this.apiUrl_user_online}/userMgt/user/login?userName=${param.userName}&token=${param.token}&time=${param.time}`;
    //     return this.http.post<any>(url, {}, httpOptions);
    // }
    // getRoleNameLogOut(param): Observable<any> {
    //     const url = `${this.apiUrl_user_online}/userMgt/user/logOut?userName=${param.userName}&token=${param.token}&time=${param.time}`;
    //     return this.http.post<any>(url, {}, httpOptions);
    // }

    // /**
    //  * 根据角色id 查询权限 GET /user/getAccessRightByRoleId
    //  * @param roleId
    //  */
    // getAccessRightByRoleId(roleId): Observable<any> {
    //     const url = `${this._user}/userMgt/user/getAccessRightByRoleId?roleId=${roleId}`;
    //     return this.http.get<any>(url);
    // }
}
