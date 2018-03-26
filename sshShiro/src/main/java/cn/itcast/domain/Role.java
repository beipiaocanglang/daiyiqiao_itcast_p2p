package cn.itcast.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

//角色表
@Entity
@Table(name = "t_role")
public class Role {

	@Id
	@GeneratedValue
	private int id;
	private String roleName;// 角色名称
	// 用户与角色关联，由角色来维护映射关系
	@ManyToMany
	@JoinTable(name = "u_r", joinColumns = {
			@JoinColumn(name = "r_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "u_id", referencedColumnName = "id") })
	private Set<User> users = new HashSet<User>();
	@ManyToMany(mappedBy="roles")
	private Set<Permission> ps = new HashSet<Permission>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

}
