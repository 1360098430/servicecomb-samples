package org.apache.servicecomb.samples.practise.houserush.user.center.rpc;

import org.apache.servicecomb.samples.practise.houserush.user.center.rpc.po.Favorite;
import org.apache.servicecomb.samples.practise.houserush.user.center.rpc.po.HouseOrder;
import org.apache.servicecomb.samples.practise.houserush.user.center.rpc.po.Sale;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

public interface HouseOrderApi {
  List<Favorite> findMyFavorite(int customerId);

  Sale findSaleByRealestateId(int realestateId);

  HouseOrder findOne(int houseOrderId);

  List<Sale> indexSales();
  //查询我的订单状态
  @GetMapping("sales/findAllByCustomerId")
  public List<HouseOrder> findAllByCustomerId(@RequestHeader int cucustomerId);
}
