package cn.itcast.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;

public class ShiroTest2 {

	public static void main(String[] args) {
		// 创建一个SecurityManager工厂，此处使用的是ini配置文件初始化SecurityManager
		Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro-realm.ini");
		// 通过工厂获取一个SecurityManager
		SecurityManager instance = factory.getInstance();
		// 绑定给SecurityUtils
		SecurityUtils.setSecurityManager(instance);
		// 获取一个Subject
		Subject subject = SecurityUtils.getSubject();
		// 创建用户名密码验证的token
		UsernamePasswordToken upt = new UsernamePasswordToken("tom", "123");
		try {
			// 登录
			subject.login(upt);
			System.out.println("身份验证成功");
		} catch (AuthenticationException e) {
			System.out.println(e);
			System.out.println("身份验证失败");
		}
		// 退出
		subject.logout();
	}
}
