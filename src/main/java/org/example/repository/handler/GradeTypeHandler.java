package org.example.repository.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.example.valueobject.Grade;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GradeTypeHandler extends BaseTypeHandler<Grade> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Grade grade, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i,grade.getValue());
    }

    @Override
    public Grade getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return Grade.toEnum(resultSet.getInt(s));
    }

    @Override
    public Grade getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return Grade.toEnum(resultSet.getInt(i));
    }

    @Override
    public Grade getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return Grade.toEnum(callableStatement.getInt(i));
    }
}
