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

package org.apache.servicecomb.samples.practise.houserush.realestate.dao;

import org.apache.servicecomb.samples.practise.houserush.realestate.aggregate.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;

public interface HouseDao extends JpaRepository<House, Integer> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT h FROM org.apache.servicecomb.samples.practise.houserush.realestate.aggregate.House h WHERE h.id in (?1)")
  List<House> findAllByIdInForUpdate(List<Integer> ids);

  @Modifying
  @Query("UPDATE House h set h.state = 'locking' where h.id in (?1)")
  public int updateLockingStatesForHouses(List<Integer> ids);

  @Modifying
  @Query("UPDATE House h set h.state = 'new' where h.id in (?1)")
  public int updateReleaseLockingStatesForHouses(List<Integer> ids);
}
