package cn.itcast.action.admin;

import java.io.IOException;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.action.common.BaseAction;
import cn.itcast.domain.admin.Admin;
import cn.itcast.service.admin.IAdminService;

@Controller
@Namespace("/account")
@Scope("prototype")
public class AdminAction extends BaseAction {

	@Autowired
	private IAdminService adminService;

	@Action("login")
	public void login() {
		// 在函数中通过response向浏览器写回json数据
		// 获取请求参数
		String username = this.getRequest().getParameter("username");
		String password = this.getRequest().getParameter("password");

		// System.out.println(username + " " + password);
		Admin admin = adminService.login(username, password);
		try {
			if (admin != null) {
				// 成功
				this.getResponse().getWriter().write("{\"status\":\"1\"}");
			} else {
				// 失败

				this.getResponse().getWriter().write("{\"status\":\"0\"}");
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				this.getResponse().getWriter().write("{\"status\":\"-999\"}");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
