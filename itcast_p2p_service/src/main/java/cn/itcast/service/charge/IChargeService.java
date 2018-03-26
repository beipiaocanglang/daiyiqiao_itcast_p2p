package cn.itcast.service.charge;

public interface IChargeService {

	boolean charge(double money, String bankCardNum, int userid);

}
