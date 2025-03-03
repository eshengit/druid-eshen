/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.druid.sql.calcite.planner;

import com.google.common.base.Supplier;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.druid.java.util.common.ISE;
import org.apache.druid.server.QueryResponse;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The result of planning an SQL query with {@link DruidPlanner} can be run to produce query result, and also includes
 * the output row type signature.
 */
public class PlannerResult
{
  private final Supplier<QueryResponse> resultsSupplier;
  private final RelDataType rowType;
  private final AtomicBoolean didRun = new AtomicBoolean();

  public PlannerResult(
      final Supplier<QueryResponse> resultsSupplier,
      final RelDataType rowType
  )
  {
    this.resultsSupplier = resultsSupplier;
    this.rowType = rowType;
  }

  public boolean runnable()
  {
    return !didRun.get();
  }

  /**
   * Run the query
   */
  public QueryResponse run()
  {
    if (!didRun.compareAndSet(false, true)) {
      // Safety check.
      throw new ISE("Cannot run more than once");
    }
    return resultsSupplier.get();
  }

  /**
   * Row type returned to the end user. Equivalent to {@link PrepareResult#getReturnedRowType()}.
   */
  public RelDataType rowType()
  {
    return rowType;
  }
}
