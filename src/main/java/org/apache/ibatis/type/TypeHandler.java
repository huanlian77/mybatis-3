/**
 * Copyright 2009-2019 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ibatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 类型转换处理器
 * <p>
 * 有很多类型转换器处理器子类：
 * 如 :
 * {@link IntegerTypeHandler}
 * {@link ObjectTypeHandler}
 * {@link DoubleTypeHandler}
 * ...
 *
 * @author Clinton Begin
 */
public interface TypeHandler<T> {

  /**
   * 设置 PreparedStatement 指定位置的参数
   *
   * @param ps
   * @param i         参数占位符位置
   * @param parameter 参数
   * @param jdbcType  JDBC类型
   * @throws SQLException
   */
  void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;

  /**
   * 获取 ResultSet 指定字段值
   *
   * @param columnName 字段名
   */
  T getResult(ResultSet rs, String columnName) throws SQLException;

  /**
   * 获取 ResultSet 指定字段值
   *
   * @param rs
   * @param columnIndex 字段位置
   * @return
   * @throws SQLException
   */
  T getResult(ResultSet rs, int columnIndex) throws SQLException;

  /**
   * 获取 CallableStatement 指定字段值
   *
   * @param cs          支持调用存储过程
   * @param columnIndex 字段位置
   * @return
   * @throws SQLException
   */
  T getResult(CallableStatement cs, int columnIndex) throws SQLException;

}
