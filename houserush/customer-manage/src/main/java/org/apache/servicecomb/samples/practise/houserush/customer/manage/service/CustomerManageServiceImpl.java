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

package org.apache.servicecomb.samples.practise.houserush.customer.manage.service;

import org.apache.servicecomb.provider.pojo.RpcReference;
import org.apache.servicecomb.samples.practise.houserush.customer.manage.aggregate.Customer;
import org.apache.servicecomb.samples.practise.houserush.customer.manage.aggregate.Qualification;
import org.apache.servicecomb.samples.practise.houserush.customer.manage.dao.CustomerDao;
import org.apache.servicecomb.samples.practise.houserush.customer.manage.dao.QualificationDao;
import org.apache.servicecomb.samples.practise.houserush.customer.manage.rpc.HouseOrderApi;
import org.apache.servicecomb.samples.practise.houserush.customer.manage.rpc.UserApi;
import org.apache.servicecomb.samples.practise.houserush.customer.manage.rpc.po.SaleQualification;
import org.apache.servicecomb.samples.practise.houserush.customer.manage.rpc.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CustomerManageServiceImpl implements CustomerManageService {

  @Autowired
  private CustomerDao customerDao;

  @Autowired
  private QualificationDao qualificationDao;

  @RpcReference(microserviceName = "house-order", schemaId = "houseOrderApiRest")
  private HouseOrderApi houseOrderApi;

  @RpcReference(microserviceName = "login", schemaId = "userApiRest")
  private UserApi userApi;

  @PersistenceContext
  private EntityManager em;

  @Override
  @Transactional
  public Customer createCustomer(Customer customer) {
    User user = new User();
    user.setUsername(customer.getRealName());
    user.setPassword("123456");
    User user1 = userApi.createUser(user);
    //购房资格集合
    List<SaleQualification> saleQualifications = new ArrayList<>();
    List<Qualification> qualifications = customer.getQualifications();
    qualifications.forEach(qualification->{
      Customer c1 = new Customer();
      c1.setId(user1.getId());
      qualification.setCustomer(c1);

      SaleQualification saleQualification = new SaleQualification();
      saleQualification.setCustomerId(user1.getId());//客户id
      saleQualification.setQualificationCount(20);//限制客户抢购次数默认20次
      saleQualification.setSaleId(qualification.getSaleId());//活动id
      saleQualifications.add(saleQualification);

    });
    Customer customer1 = customerDao.save(customer);

    //更新客户id
    customerDao.updateCustomerIdUseUseId(user1.getId(),customer1.getId());

    //数据同步到sale 表中去
    houseOrderApi.updateSaleQualification(saleQualifications);
   //数据同步到sale 表中去
    return customer1;
  }

  @Override
  public Customer updateCustomer(Customer customer) {
    int id = customer.getId();
    if (customerDao.exists(id)) {
      return customerDao.save(customer);
    } else {
      throw new DataRetrievalFailureException("cannot update the non-existed customer");
    }
  }

  @Override
  public Customer findCustomer(int id) {
    return customerDao.findOne(id);
  }

  @Override
  public void removeCustomer(int id) {
    customerDao.delete(id);
  }

  @Override
  public List<Customer> indexCustomers() {
    return customerDao.findAll();
  }

  @Override
  public boolean updateCustomerQualifications(Customer customer, List<Qualification> qualifications) {
    customer.setQualifications(qualifications);
    qualifications.forEach(qualification -> qualification.setCustomer(customer));
    customerDao.saveAndFlush(customer);
    Map<Integer,Long> map = qualifications.stream().collect(Collectors.groupingBy(Qualification::getSaleId,Collectors.counting()));
    List<SaleQualification> saleQualifications = new ArrayList<>();
    map.forEach((k,v)->saleQualifications.add(new SaleQualification(customer.getId(),k,v.intValue())));
    houseOrderApi.updateSaleQualification(saleQualifications);
    return true;
  }

  @Override
  public int getQualificationsCount(int customerId, int saleId) {
    return qualificationDao.countByCustomerIdAndSaleId(customerId, saleId);
  }

}
