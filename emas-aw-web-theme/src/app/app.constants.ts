// These constants are injected via webpack environment variables.
// You can add more variables in webpack.common.js or in profile specific webpack.<dev|prod>.js files.
// If you change the values in the webpack config files, you need to re run webpack to update the application
/* 
export const VERSION = process.env.VERSION;
export const DEBUG_INFO_ENABLED: boolean = !!process.env.DEBUG_INFO_ENABLED;
export const SERVER_API_URL = process.env.SERVER_API_URL;
export const BUILD_TIMESTAMP = process.env.BUILD_TIMESTAMP;
export const ENVIRONMENT = process.env.NODE_ENV;

export const ITMP_HOST_NAME = process.env.ITMP_HOST_NAME; // 应用地址
export const INCIDENT_API_URL = process.env.INCIDENT_API_URL; // VIET_ITMP_incident
export const USER_API_URL = process.env.USER_API_URL; // VIET_ITMP_user
export const ALERT_API_URL = process.env.ALERT_API_URL; // VIET_ITMP_alert
export const ALARM_API_URL = process.env.ALARM_API_URL; // VIET_ITMP_alarm
export const EQUIPMENT_API_URL = process.env.EQUIPMENT_API_URL; // VIET_ITMP_equipment
export const ACTIVE_MQ_URL = process.env.ACTIVE_MQ_URL; // VIET_ITMP_activemq
export const GIS_SERVICE_URL = process.env.GIS_SERVICE_URL; // VIET_ITMP_geoserver
export const LOGIN_API_URL = process.env.LOGIN_API_URL; // LOGIN
export const API_GATEWAY = process.env.API_GATEWAY; // gateway */

// message type use to register diff typeof Channel
export const enum MESSAGE_CHANNEL {
    LANGUAGE_CHANNEL = 'LANGUAGE_CHANNEL', // languge channel
    MAP_CHANNEL = 'MAP_CHANNEL', // map channel
    CCM_CHANNEL = 'CCM_CHANNEL' // from to main pages
}

// system event
export const enum SYSTEM_EVENT {
    ROUTE_EVENT = 'route-event',
    MAP_WIN_CHANGE = 'map-win-change',
    CCM_WIN_CHANGE = 'ccm-win-change',
    CCM_NAVIGATE = 'ccm-navigate',
    LANG_CHANGE = 'lang-change',
    CREATE_EVENT = 'create-event', // 设备或事件创建
    EQUIPMENT_POSITION = 'equipment-position', // 设备定位
    INCIDENT_CREATE = 'incident-create', // 事件创建
    ALARM_POSITION = 'alarm-position', // 设备报警定位
    ALARM_EVENT = 'alarm-event',
    INCIDENT_POSITION = 'incident-position', // 事件告警定位
    ALERT_POSITION = 'alert-position', // alert告警定位
    ALERT_EVENT = 'alert-event' // 告警事件
}
