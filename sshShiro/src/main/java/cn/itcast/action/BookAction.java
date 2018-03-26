package cn.itcast.action;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionSupport;

import cn.itcast.service.IBookService;

@Controller
@Namespace("/book")
public class BookAction extends ActionSupport {

	@Autowired
	private IBookService bookService;

	@Action(value = "update", results = { @Result(name = "error", location = "/error.jsp") })
	public String update() {
		try {
			System.out.println("book update");
			bookService.update();
		} catch (UnauthorizedException e) {
			return "error";
		}
		return null;
	}

	// @RequiresRoles("admin")
	@RequiresPermissions("delete")
	@Action("delete")
	public String delete() {
		System.out.println("book delete");
		bookService.delete();
		return null;
	}

	@RequiresRoles("admin")
	@Action("add")
	public String add() {
		System.out.println("book add");
		bookService.add();
		return null;
	}

	@RequiresPermissions("find")
	@Action("find")
	public String find() {
		System.out.println("book find");
		bookService.find();
		return null;
	}
}
