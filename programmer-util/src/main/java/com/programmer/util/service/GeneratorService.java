package com.programmer.util.service;

import com.programmer.util.domain.DatabaseColumn;
import com.programmer.util.domain.GeneratorParams;
import com.programmer.util.domain.Result;
import com.programmer.util.domain.dto.GeneratorParamsDTO;
import com.programmer.util.utils.GeneratorUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器
 *
 * @author dengweiping
 */
@Service
public class GeneratorService {
    @Autowired
    private DataConnectionService dataConnectionService;

    public byte[] generatorCode(GeneratorParamsDTO params) {
        if (params.getTables() == null || params.getTables().size() == 0) {
            return null;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        for (Map<String, Object> paramsMap : params.getTables()) {
            String tableName = paramsMap.get("tableName").toString();
            //查询列信息
            Result result = dataConnectionService.getTableColumns(params.getDataConnectionId(), tableName);
            if (result.errored()) {
                return null;
            }

            List<DatabaseColumn> columns = (List<DatabaseColumn>) result.getData();

            //生成代码
            GeneratorParams generatorParams = new GeneratorParams();
            generatorParams.setTableName(tableName);
            generatorParams.setTableComment(paramsMap.get("tableComment") + "");
            generatorParams.setAuthor(params.getAuthor());
            generatorParams.setModuleName(params.getModuleName());
            generatorParams.setPackageName(params.getPackageName());
            generatorParams.setColumns(columns);

            GeneratorUtil.generatorCode(generatorParams, zip);
        }

        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    public byte[] generatorCode(String dataConnectionId,String[] tableNames, String moduleName, String packageName, String author) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        for (String tableName : tableNames) {
            //查询列信息
            Result result = dataConnectionService.getTableColumns(dataConnectionId, tableName);
            if (result.errored()) {
                return null;
            }

            List<DatabaseColumn> columns = (List<DatabaseColumn>) result.getData();

            //生成代码
            GeneratorParams generatorParams = new GeneratorParams();
            generatorParams.setTableName(tableName);
            generatorParams.setAuthor(author);
            generatorParams.setModuleName(moduleName);
            generatorParams.setPackageName(packageName);
            generatorParams.setColumns(columns);

            GeneratorUtil.generatorCode(generatorParams, zip);
        }

        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }
}
