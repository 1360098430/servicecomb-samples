package org.apache.servicecomb.samples.practise.houserush.customer.manage.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class CustomerDaoImpl implements CustomerDaoMore {
  @PersistenceContext
  private EntityManager em;
  @Override
  public void updateCustomerIdUseUseId(int userId,int cid) {
    em.createNativeQuery("UPDATE customers set  id=(?1) where id= (?2)").setParameter(1, userId).setParameter(2,cid).executeUpdate();
  }
}
