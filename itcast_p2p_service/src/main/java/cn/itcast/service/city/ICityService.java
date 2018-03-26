package cn.itcast.service.city;

import java.util.List;

import cn.itcast.domain.city.City;

public interface ICityService {

	List<City> findProvinces();

	List<City> findByParentCityAreaNum(String cityAreaNum);

}
