/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.servicecomb.samples.practise.houserush.user.center.api;

import org.apache.servicecomb.provider.pojo.RpcReference;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.apache.servicecomb.samples.practise.houserush.user.center.rpc.CustomerManageApi;
import org.apache.servicecomb.samples.practise.houserush.user.center.rpc.HouseOrderApi;
import org.apache.servicecomb.samples.practise.houserush.user.center.rpc.RealestateApi;
import org.apache.servicecomb.samples.practise.houserush.user.center.rpc.po.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestSchema(schemaId = "userCenterApiRest")
@RequestMapping("/")
public class UserCenterRestApiImpl implements UserCenterApi {

  @RpcReference(microserviceName = "house-order", schemaId = "houseOrderApiRest")
  private HouseOrderApi houseOrderApi;

  @RpcReference(microserviceName = "realestate", schemaId = "realestateApiRest")
  private RealestateApi realestateApi;

  @RpcReference(microserviceName = "customer-manage", schemaId = "customerManageApiRest")
  private CustomerManageApi customerManageApi;

  @Override
  @GetMapping("favorites")
  public List<Favorite> findMyFavorite(@RequestHeader int customerId) {
    List<Favorite> favorites =  houseOrderApi.findMyFavorite(customerId);
    favorites.forEach(favorite -> {
      HouseOrder HouseOrder = houseOrderApi.findOne(favorite.getHouseOrderId());
      House house = realestateApi.findHouse(HouseOrder.getHouseId());
      favorite.setHouseName(house.getName());
      favorite.setPrice(house.getPrice());
      favorite.setBuilDingName(house.getBuilding().getName());
      favorite.setRealestateName(house.getBuilding().getRealestate().getName());
      Sale sale = houseOrderApi.findSaleByRealestateId(house.getBuilding().getRealestate().getId());
      HouseOrder houseOrder = houseOrderApi.findAllByHouseId(HouseOrder.getHouseId());
      favorite.setState(houseOrder.getState());
      favorite.setHouseOrderId(house.getId());
    });
    return favorites;
  }

  @Override
  @GetMapping("favorites/{id}")
  public HouseDetail findByHouseIdDetail(@PathVariable int id){
    House house = realestateApi.findHouse(id);
    HouseDetail houseDetail = new HouseDetail();
    houseDetail.setHouseName(house.getName());
    houseDetail.setPrice(house.getPrice());
    houseDetail.setBuilDingName(house.getBuilding().getName());
    Realestate realestate = house.getBuilding().getRealestate();
    houseDetail.setRealestateName(realestate.getName());
    houseDetail.setHouseOrderId(id);
    houseDetail.setAddress(realestate.getAddress());
    houseDetail.setType(realestate.getType());
    houseDetail.setAvgprice(realestate.getAvgprice());
    houseDetail.setUseyear(realestate.getUseyear());
    houseDetail.setUsernum(realestate.getUsernum());
    houseDetail.setArea(realestate.getArea());
    houseDetail.setBuildname(realestate.getBuildname());
    HouseOrder houseOrder = houseOrderApi.findAllByHouseId(id);
    houseDetail.setState(houseOrder.getState());
    return  houseDetail;
  }

  @GetMapping("buyHouseOrderState")
  public  List<HouseOrder>  findMyBuyHouseNumber(@RequestHeader int customerId){
    List<HouseOrder>  houseOrder = houseOrderApi.findAllByCustomerId(customerId);
    houseOrder.forEach(order ->{
      Integer houseId = order.getHouseId();
      House house = realestateApi.findHouse(houseId);
      order.setHouseName(house.getName());
      order.setPrice(house.getPrice());
      order.setBuilDingName(house.getBuilding().getName());
      order.setRealestateName(house.getBuilding().getRealestate().getName());
    });
    return houseOrder;
  }
}
