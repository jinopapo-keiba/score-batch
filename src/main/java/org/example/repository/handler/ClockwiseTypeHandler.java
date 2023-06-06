package org.example.repository.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.example.valueobject.Clockwise;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClockwiseTypeHandler extends BaseTypeHandler<Clockwise> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Clockwise clockwise, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i,clockwise.getValue());
    }

    @Override
    public Clockwise getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return Clockwise.toEnum(resultSet.getInt(s));
    }

    @Override
    public Clockwise getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return Clockwise.toEnum(resultSet.getInt(i));
    }

    @Override
    public Clockwise getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return Clockwise.toEnum(callableStatement.getInt(i));
    }
}
