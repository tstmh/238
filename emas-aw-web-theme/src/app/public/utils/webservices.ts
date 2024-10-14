import { HttpClient } from '@angular/common/http';
import { Subject, Observable } from 'rxjs';

const urlMap = {
	AWWebService: { url: '/emas-AW_DB-0.0.1-SNAPSHOT/AWWebService', dataType: 0, tNameSpace: 'http://emasext.stee.com.sg/awvms' },
	CTETunDataService: { url: '/emas-AW_DB-0.0.1-SNAPSHOT/CTETunDataService', dataType: 0, tNameSpace: 'http://emasext.stee.com.sg/awvms' },
	CMHWebService: { url: '/emas-cmh-0.0.1-SNAPSHOT/CMHWebService', dataType: 0, tNameSpace: 'http://emasext.stee.com.sg/cmh' },
	OrganizationWebService: { url: '/msap-core/OrganizationWebService', dataType: 0 },
	PermissionWebService: { url: '/msap-core/PermissionWebService', dataType: 1, tNameSpace: 'http://emasext.stee.com.sg/msap-core' },
	RoleWebService: { url: '/msap-core/RoleWebService', dataType: 0, tNameSpace: 'http://emasext.stee.com.sg/msap-core' },
	UserWebService: { url: '/msap-core/UserWebService', dataType: 1, tNameSpace: 'http://emasext.stee.com.sg/msap-core' },
	VMSWebService: { url: '/emas-AW_DB-0.0.1-SNAPSHOT/VMSWebService', dataType: 0, tNameSpace: 'http://emasext.stee.com.sg/awvms' },
	AuditLogWebService: { url: '/msap-core/AuditLogWebService', dataType: 1, tNameSpace: 'http://emasext.stee.com.sg/msap-core' },
	CMHTunWebService: { url: '/emas-cmh-0.0.1-SNAPSHOT/CMHTunWebService', dataType: 0, tNameSpace: 'http://emasext.stee.com.sg/cmh' },
	CommonWebService: { url: '/emas-AW_DB-0.0.1-SNAPSHOT/CommonWebService', dataType: 0, tNameSpace: 'http://emasext.stee.com.sg/awvms' },
	WaterMistService: { url: 'https://ec2-54-151-175-44.ap-southeast-1.compute.amazonaws.com:8089/', dataType: 0 },
	NewLUSService: { url: 'https://ec2-54-151-175-44.ap-southeast-1.compute.amazonaws.com:8089/lus/', dataType: 0 }
};

export function wsSend(fnName: string, param, service: string, dataType?: number) {
	checkLogin();
	localStorage.setItem('runTime', new Date().getTime() + '');
	const subject = new Subject();
	const request = initData(fnName, param, service, dataType);
	const client2 = new XMLHttpRequest();
	client2.open('POST', '/webservice' + urlMap[service].url, true);
	client2.setRequestHeader('Content-Type', 'text/xml');
	client2.send(request);
	client2.onreadystatechange = function () {
		if (client2.readyState === 4) {
			const json_obj = $['xml2json'](client2.responseText);
			subject.next(json_obj);
			subject.unsubscribe();
		}
	};

	return subject;
}

export function httpGet(httpClient: HttpClient, schemaName: string, requestName: string, params?: object): Observable<ResultVO> {
	checkLogin();
	localStorage.setItem('runTime', new Date().getTime() + '');
	let str = []
	let query = ''
	if (params) {
		for (var p in params) {
			if (params.hasOwnProperty(p)) {
				str.push(encodeURIComponent(p) + "=" + encodeURIComponent(params[p]));
			}
		}
		query = str.join("&");
	}
	let url = str.length === 0 ? `${urlMap[schemaName].url}${requestName}` : `${urlMap[schemaName].url}${requestName}?${query}`
	return httpClient.get<ResultVO>(url)
}

export function httpPost(httpClient: HttpClient, schemaName: string, requestName: string, params?: object): Observable<ResultVO> {
	checkLogin();
	localStorage.setItem('runTime', new Date().getTime() + '');
	return httpClient.post<ResultVO>(`${urlMap[schemaName].url}${requestName}`, params)
}

function initData(fnName: string, param: Map<string, any>, service: string, dataType?: number) {
	let request =
		`<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">` +
		'<soap:Body>' +
		`<ns1:${fnName} xmlns:ns1="${urlMap[service].tNameSpace}">`;
	if (dataType !== 0 && !dataType) {
		dataType = urlMap[service].dataType;
	}

	if (param) {
		switch (dataType) {
			case 1:
				request += $['json2xml'](param, { ignoreRoot: true });
				break;
			case 0:
			default:
				const newParam = {}
				for (const key in param) {
					if (key) {
						newParam[`ns1:${key}`] = param[key];
					}
				}
				request += $['json2xml'](newParam, { ignoreRoot: true })
				break;
		}
	}

	request += `</ns1:${fnName}> </soap:Body></soap:Envelope>`;
	return request;
}

export interface ResultVO {
	code: number,
	data: any | any[]
}

export function checkLogin() {
	const IdleTime = Number.parseInt(localStorage.getItem("IdleTime"));
	if (isNaN(IdleTime)) {
		return;
	}
	const logoutTime = IdleTime * 1000 + Number.parseInt(localStorage.getItem("runTime"));
	const curTime = new Date().getTime();
	const timeOutValue = localStorage.getItem("timeOut");
	if (logoutTime > curTime && timeOutValue != "ye4") {
		localStorage.setItem('timeOut', '0');
	} else {
		localStorage.setItem('timeOut', 'ye4');
	}
}