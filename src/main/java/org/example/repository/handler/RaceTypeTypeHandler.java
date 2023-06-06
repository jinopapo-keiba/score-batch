package org.example.repository.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.example.valueobject.RaceType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RaceTypeTypeHandler extends BaseTypeHandler<RaceType> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, RaceType raceType, JdbcType jdbcType) throws SQLException {
       preparedStatement.setInt(i,raceType.getValue());
    }

    @Override
    public RaceType getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return RaceType.toEnum(resultSet.getInt(s));
    }

    @Override
    public RaceType getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return RaceType.toEnum(resultSet.getInt(i));
    }

    @Override
    public RaceType getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return RaceType.toEnum(callableStatement.getInt(i));
    }
}
