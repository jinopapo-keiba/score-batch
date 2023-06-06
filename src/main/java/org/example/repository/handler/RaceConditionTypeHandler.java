package org.example.repository.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.example.valueobject.RaceCondition;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RaceConditionTypeHandler extends BaseTypeHandler<RaceCondition> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, RaceCondition raceCondition, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i,raceCondition.getValue());
    }

    @Override
    public RaceCondition getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return RaceCondition.toEnum(resultSet.getInt(s));
    }

    @Override
    public RaceCondition getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return RaceCondition.toEnum(resultSet.getInt(i));
    }

    @Override
    public RaceCondition getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return RaceCondition.toEnum(callableStatement.getInt(i));
    }
}
