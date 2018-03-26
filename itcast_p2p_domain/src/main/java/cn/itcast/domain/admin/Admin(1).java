package cn.itcast.domain.admin;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity  //是一个实体
@Table(name="t_admin")  //映射的表名称
public class Admin implements Serializable{

	@Id  //主键
	@GeneratedValue  //主键生成策略  类似于native
	private int id;
	@Column(name="username")  //写@Column可以指定列名称及特性,如果不写，那么这个属性会在表中生成字段，都取默认值
	private String username;
	
	private String password;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
