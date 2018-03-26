package cn.itcast.action;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.domain.User;

@Controller
@Namespace("/user")
public class LoginAction extends ActionSupport implements ModelDriven<User> {

	private User user = new User();

	public User getModel() {
		return user;
	}

	@Action(value = "login", results = { @Result(name = "success", location = "/success.jsp"),
			@Result(name = "failer", location = "/login.jsp") })
	public String login() {
		// 认证操作
		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken upt = new UsernamePasswordToken(user.getUsername(), user.getPassword());
		try {
			subject.login(upt); // 如果不抛出异常，代表登录成功
			return "success";
		} catch (Exception e) {
			this.addActionError("用户名密码错误");
		}
		return "failer";
	}

}
