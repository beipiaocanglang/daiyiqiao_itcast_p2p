package cn.itcast.dao.city;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.itcast.domain.city.City;

public interface ICityDAO extends JpaRepository<City, Integer> {

	List<City> findByParentCityAreaNumIsNull();

	List<City> findByParentCityAreaNum(String cityAreaNum);

}
