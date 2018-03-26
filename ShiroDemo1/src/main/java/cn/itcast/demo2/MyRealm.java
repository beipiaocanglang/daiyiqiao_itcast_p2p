package cn.itcast.demo2;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.Realm;

public class MyRealm implements Realm {

	// 具体的认证操作
	public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		// 1.得到用户名与密码
		char[] credentials = (char[]) token.getCredentials(); // char数组 ---就是密码
		Object principal = token.getPrincipal(); // 用户名
		// System.out.println(new String(credentials)+" "+principal);
		// //[C@25b64d tom
		// 2.判断是否正确
		if ("tom".equals(principal) && "123".equals(new String(credentials))) {
			return new SimpleAuthenticationInfo(principal, credentials, getName());
		}
		// 3.响应
		return null;
	}

	// realm的名称
	public String getName() {
		return "myrealm1";
	}

	// 指定token的类型是UsernamePasswordToken
	public boolean supports(AuthenticationToken token) {
		return token instanceof UsernamePasswordToken;
	}

}
