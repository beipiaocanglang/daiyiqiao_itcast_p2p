package cn.itcast.service.impl;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.itcast.service.IBookService;

@Service
public class BookServiceImpl implements IBookService {
	

	
	@RequiresRoles("admin")
	public void update() {
		System.out.println("book update....");
		
	}

	public void delete() {
		System.out.println("book delete....");
	}

	public void add() {
		System.out.println("book add....");
	}

	public void find() {
		System.out.println("book find....");
	}
}
