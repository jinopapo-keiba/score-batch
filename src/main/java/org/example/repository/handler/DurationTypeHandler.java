package org.example.repository.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

public class DurationTypeHandler  extends BaseTypeHandler<Duration> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Duration duration, JdbcType jdbcType) throws SQLException {
        preparedStatement.setLong(i,duration.toMillis());
    }

    @Override
    public Duration getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return Duration.ofMillis(resultSet.getLong(s));
    }

    @Override
    public Duration getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return Duration.ofMillis(resultSet.getLong(i));
    }

    @Override
    public Duration getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return Duration.ofMillis(callableStatement.getLong(i));
    }
}
