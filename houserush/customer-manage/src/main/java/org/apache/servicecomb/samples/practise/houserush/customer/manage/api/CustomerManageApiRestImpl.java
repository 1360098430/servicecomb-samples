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

package org.apache.servicecomb.samples.practise.houserush.customer.manage.api;

import org.apache.servicecomb.provider.pojo.RpcReference;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.apache.servicecomb.samples.practise.houserush.customer.manage.Utils;
import org.apache.servicecomb.samples.practise.houserush.customer.manage.aggregate.Customer;
import org.apache.servicecomb.samples.practise.houserush.customer.manage.aggregate.Qualification;
import org.apache.servicecomb.samples.practise.houserush.customer.manage.rpc.HouseOrderApi;
import org.apache.servicecomb.samples.practise.houserush.customer.manage.rpc.UserApi;
import org.apache.servicecomb.samples.practise.houserush.customer.manage.rpc.po.SaleQualification;
import org.apache.servicecomb.samples.practise.houserush.customer.manage.rpc.po.User;
import org.apache.servicecomb.samples.practise.houserush.customer.manage.service.CustomerManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestSchema(schemaId = "customerManageApiRest")
@RequestMapping("/")
public class CustomerManageApiRestImpl implements CustomerManageApi {

  @RpcReference(microserviceName = "login", schemaId = "userApiRest")
  private UserApi userApi;

  @Autowired
  private CustomerManageService customerManageService;

  @RpcReference(microserviceName = "house-order", schemaId = "houseOrderApiRest")
  private HouseOrderApi houseOrderApi;

  @PostMapping("customers")
  public Customer createCustomer(@RequestBody Customer customer) {
    User user = new User();
    user.setUsername(customer.getRealName());
    user.setPassword("123456");
    User user1 = userApi.createUser(user);
    Qualification qualification =customer.getQualifications().get(0);
    Customer c1 = new Customer();
    c1.setId(user1.getId());
    qualification.setCustomer(c1);

//    //数据同步到sale 表中去
//    List<SaleQualification> saleQualifications = new ArrayList<>();
//    SaleQualification saleQualification = new SaleQualification();
//
//    //saleQualification.setId(c1.getId());
//    saleQualification.setCustomerId(c1.getId());//客户id
//    saleQualification.setQualificationCount(1);//限制资数
//    saleQualification.setSaleId(qualification.getSaleId());//活动id
//
//    saleQualifications.add(saleQualification);
//    houseOrderApi.updateSaleQualification(saleQualifications);
//    //数据同步到sale 表中去

    Customer customer1 = customerManageService.createCustomer(customer);
    Utils.updateCustomersBySql(c1.getId(),customer1.getId());
    return customer1;
  }



  @GetMapping("customers/{id}")
  public Customer findCustomer(@PathVariable int id) {
    return customerManageService.findCustomer(id);
  }

  @PutMapping("customers/{id}")
  public Customer updateCustomer(@PathVariable int id, @RequestBody Customer customer) {
    customer.setId(id);
    return customerManageService.updateCustomer(customer);
  }

  @DeleteMapping("customers/{id}")
  public void removeCustomer(@PathVariable int id) {
    customerManageService.removeCustomer(id);
  }

  @GetMapping("customers")
  public List<Customer> indexCustomers() {
    return customerManageService.indexCustomers();
  }

  @PutMapping(value = "customers/{id}/update_qualifications")
  public Customer updateCustomerQualifications(@PathVariable int id, @RequestBody List<Qualification> qualifications) {
    Customer customer = customerManageService.findCustomer(id);
    customerManageService.updateCustomerQualifications(customer, qualifications);
    // refresh customer
    customer = customerManageService.findCustomer(id);
    return customer;
  }

  @GetMapping("customers/{customerId}/sales/{saleId}/qulification_count")
  public int getQualificationsCount(@PathVariable int customerId, @PathVariable int saleId) {
    return customerManageService.getQualificationsCount(customerId, saleId);

  }
}