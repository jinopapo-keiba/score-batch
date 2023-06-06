package org.example.repository.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.example.valueobject.RaceWeather;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RaceWeatherTypeHandler extends BaseTypeHandler<RaceWeather> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, RaceWeather raceWeather, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i,raceWeather.getValue());
    }

    @Override
    public RaceWeather getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return RaceWeather.toEnum(resultSet.getInt(s));
    }

    @Override
    public RaceWeather getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return RaceWeather.toEnum(resultSet.getInt(i));
    }

    @Override
    public RaceWeather getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return RaceWeather.toEnum(callableStatement.getInt(i));
    }
}
