package cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.typehandler;

import cc.nekocc.cyanchatroomserver.domain.model.file.FileStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(FileStatus.class)
public class FileStatusTypeHandler extends BaseTypeHandler<FileStatus>
{
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, FileStatus parameter, JdbcType jdbcType) throws SQLException
    {
        ps.setString(i, parameter.name());
    }

    @Override
    public FileStatus getNullableResult(ResultSet rs, String columnName) throws SQLException
    {
        String value = rs.getString(columnName);
        if (value == null)
        {
            return null;
        }
        try
        {
            return FileStatus.valueOf(value);
        } catch (IllegalArgumentException e)
        {
            throw new SQLException("Invalid FileStatus value: " + value, e);
        }
    }

    @Override
    public FileStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException
    {
        String value = rs.getString(columnIndex);
        if (value == null)
        {
            return null;
        }
        try
        {
            return FileStatus.valueOf(value);
        } catch (IllegalArgumentException e)
        {
            throw new SQLException("Invalid FileStatus value: " + value, e);
        }
    }

    @Override
    public FileStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException
    {
        String value = cs.getString(columnIndex);
        if (value == null)
        {
            return null;
        }
        try
        {
            return FileStatus.valueOf(value);
        } catch (IllegalArgumentException e)
        {
            throw new SQLException("Invalid FileStatus value: " + value, e);
        }
    }
}