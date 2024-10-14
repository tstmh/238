import { Component, OnInit } from '@angular/core';
import { ToolsService } from 'src/app/share/service/tools.service';
import { NzSubMenuComponent } from 'ng-zorro-antd';

export interface MenuItem {
	title: string;
	route?: string;
	id?: string; // 有children元素的menu的id , 无childrensubmenu或submenu无id
	icon?: string;
	exponded?: boolean; // 控制 有children的节点的展开状态
	active?: boolean; //  控制 有children的节点的激活状态
	children?: Array<MenuItem>;
	// 后台返回的数据结构
	accessId?: number;
	rightName?: string;
	parentId?: string;
	url?: string;
	description?: string;
}

@Component({
  selector: 'sj-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.css']
})
export class SidenavComponent implements OnInit {
	menuList: Array<MenuItem> = [];
	isCollapsed = false;
	private _submenuRefMap: Map<string, NzSubMenuComponent> = new Map<string, NzSubMenuComponent>();
	private _currentId = null; // 用于收缩后记住点击的submenu所在的menu的id
  constructor(
	private tools: ToolsService
  ) {
	  this.menuList = [
			{
				id: this.tools.isGuid(8),
				title: 'Layout.Navbar.Data_Visualization',
				icon: 'iconfont icon-dashboard- color-fff',
				exponded: false,
				active: false,
				children: [
					{
						title: 'Layout.Navbar.Overall',
						route: '/ccm/data-visualization/overall'
					},
					{
						title: 'Layout.Navbar.Incident_Analytics',
						route: '/ccm/data-visualization/incident-analytics'
					},
					{
						title: 'Layout.Navbar.Traffic_Analytics',
						route: '/ccm/data-visualization/traffic-analytics'
					},
					{
						title: 'Layout.Navbar.Equipment_Analytics',
						route: '/ccm/data-visualization/equipment-analytics'
					}
				]
			},
			// {
			// 	title: 'GIS',
			// 	icon: 'iconfont icon-ditu color-fff',
			// 	route: '/gis'
			// },
			{
				id: this.tools.isGuid(8),
				title: 'Layout.Navbar.Dissemination_Management',
				icon: 'iconfont icon-diqiu color-fff',
				exponded: false,
				active: false,
				children: [
					{
						title: 'Layout.Navbar.Message_Library',
						route: '/ccm/dissemination-management/message-library'
					},
					{
						title: 'Layout.Navbar.Message_Category',
						route: '/ccm/dissemination-management/message-category'
					},
					{
						title: 'Layout.Navbar.Graphic',
						route: '/ccm/dissemination-management/graphic'
					},
					{
						title: 'Layout.Navbar.Message_Template',
						route: '/ccm/dissemination-management/message-template'
					}
				]
			},
			{
				id: this.tools.isGuid(8),
				title: 'Layout.Navbar.Traffic_Management',
				icon: 'iconfont icon--jiaotongbiaozhipai color-fff',
				exponded: false,
				active: false,
				children: [
					{
						title: 'Layout.Navbar.Traffic_Alert',
						route: '/ccm/traffic-management/traffic-alert-page'
					},
					{
						title: 'Layout.Navbar.Incident_Record',
						route: '/ccm/traffic-management/incident-record'
					},
					{
						title: 'Layout.Navbar.Incident_Logs',
						route: '/ccm/traffic-management/incident-logs'
					}
				]
			},
			{
				id: this.tools.isGuid(8),
				title: 'Layout.Navbar.Equipment_Management',
				icon: 'iconfont icon-app_icons-- color-fff',
				exponded: false,
				active: false,
				children: [
					{
						title: 'Layout.Navbar.Equipment_List',
						route: '/ccm/equipment-management/equipment-list'
					},
					{
						title: 'Layout.Navbar.Equipment_Alarm',
						route: '/ccm/equipment-management/equipment-alarm'
					}
				]
			},
			{
				title: 'Layout.Navbar.Security_Management',
				icon: 'iconfont icon-anquan- color-fff',
				route: '/ccm/security-management'
			},
			{
				title: 'Layout.Navbar.Data_Management',
				icon: 'iconfont icon-riqi color-fff',
				route: '/ccm/data-management'
			},
			{
				id: this.tools.isGuid(8),
				title: 'Layout.Navbar.User_Management',
				icon: 'iconfont icon-user color-fff',
				exponded: false,
				active: false,
				children: [
					{
						title: 'Layout.Navbar.Manage_Users',
						route: '/ccm/user-management/manage-users'
					},
					{
						title: 'Layout.Navbar.Manage_Role',
						route: '/ccm/user-management/manage-role'
					},
					{
						title: 'Layout.Navbar.Manage_Access_Rights',
						route: '/ccm/user-management/manage-access-rights'
					},
					{
						title: 'Layout.Navbar.Manage_Organization',
						route: '/ccm/user-management/manage-organization'
					}
				]
			},
			{
				title: 'Layout.Navbar.Log_Management',
				icon: 'iconfont icon-icon-- color-fff',
				route: '/ccm/log-management'
			}
		];
   }

  ngOnInit() {
	//   console.log('wdnmmb');
  }

  // 控制 有children的节点的展开状态
	expandMenu(current: number, nzMenuCom?: NzSubMenuComponent, isChild?: boolean) {
		// 1. 点击子菜单就跳路由版本,手风琴模式
        /* this.menuList.forEach((menu, i) => {
            if (menu.exponded !== undefined) {
                if (i === current) {
                    menu.active = true;
                    if (!isChild) {
                        menu.exponded = !menu.exponded;
                    } else {
						menu.exponded = true;
						// 这里有坑, 虽然nzSubmenu的open属性已变为false,但展开侧边栏后,nzSubmenu会恢复之前展开状态,随后展开状态不在受exponded属性控制,原因暂不明,解决办法在下面
                        if (this.isCollapsed) {
                            // 收缩状态下才记录
                            this._currentId = menu.id;
                        }
                    }
                    // 保存点击过的submenu的component ref
                    const key = menu.id;
                    if (!this._submenuRefMap.has(key)) {
                        this._submenuRefMap.set(key, nzMenuCom);
                    }
                } else {
                    menu.exponded = false;
                    menu.active = false;
                }
            }
        }); */
		// 2. 点击菜单不会跳路由,非手风琴模式
		this.menuList.forEach((menu, i) => {
			if (i === current) {
				// 如果是当前点击的节点
				if (menu.exponded !== undefined) {
					if (!isChild) {
						// 非子项,只控制菜单收缩
						menu.exponded = !menu.exponded;
					} else {
						// 子项保证菜单展开,并且控制active,这里注释掉代码是因为_setActiveRouter已经干了这个事情了
						// menu.active = true;
						// menu.exponded = true;

						// 收缩状态下记录点击过的子项的父级id
						if (this.isCollapsed) {
							this._currentId = menu.id;
						}
					}

					// 保存点击过的submenu的component ref
					const key = menu.id + '';
					if (!this._submenuRefMap.has(key)) {
						this._submenuRefMap.set(key, nzMenuCom);
					}
				}
			}
			// 下面的工作已由 _setActiveRouter替代
            /* else {
				if (isChild || this.menuList[current].exponded === undefined) {
					// 当前点击节点如果是子节点或者是不可展开节点
					if (menu.exponded !== undefined) {
						// menu.active = false;
					}
				}
			} */
		});
	}
}
