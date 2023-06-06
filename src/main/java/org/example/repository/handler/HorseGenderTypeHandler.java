package org.example.repository.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.example.valueobject.HorseGender;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HorseGenderTypeHandler extends BaseTypeHandler<HorseGender> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, HorseGender horseGender, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i,horseGender.getValue());
    }

    @Override
    public HorseGender getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return HorseGender.toEnum(resultSet.getInt(s));
    }

    @Override
    public HorseGender getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return HorseGender.valueOf(resultSet.getString(i));
    }

    @Override
    public HorseGender getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return HorseGender.toEnum(callableStatement.getInt(i));
    }
}
