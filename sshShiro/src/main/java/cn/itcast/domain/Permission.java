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

//权限
@Entity
@Table(name = "t_permission")
public class Permission {
	@Id
	@GeneratedValue
	private int id;

	private String permissionName;
	
	//角色与权限多对多关系，由权限来维护关系
	@ManyToMany
	@JoinTable(name = "r_p", joinColumns = {
			@JoinColumn(name = "p_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "r_id", referencedColumnName = "id") })
	private Set<Role> roles=new HashSet<Role>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPermissionName() {
		return permissionName;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}

}
