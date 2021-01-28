package com.programmer.blog.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.enums.WriteDirectionEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.programmer.blog.domain.DemoData;

import java.util.*;

/**
 * excel工具类
 *
 * @author dengweiping
 * @date 2021/1/28 16:02
 */
public class ExcelUtil {
    public void simpleWrite(List<DemoData> demoData) {
        // 模板注意 用{} 来表示你要用的变量 如果本来就有"{","}" 特殊字符 用"\{","\}"代替
        // {} 代表普通变量 {.} 代表是list的变量
        String templateFileName = "D:\\demo.xlsx";
        String fileName = "D:\\" + new Date().getTime() + ".xlsx";
        ExcelWriter excelWriter = EasyExcel.write(fileName).withTemplate(templateFileName).build();
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        FillConfig fillConfig = FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL).build();

        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> dataItem1 = new HashMap<>();
        dataItem1.put("title", "行程");
        dataItem1.put("content", "{"+"data2.content1"+"}");
        data.add(dataItem1);

        Map<String, Object> dataItem2 = new HashMap<>();
        dataItem2.put("title", "里程");
        dataItem2.put("content", "{"+"data2.content2"+"}");
        data.add(dataItem2);

        Map<String, Object> dataItem3 = new HashMap<>();
        dataItem3.put("title", "加油量");
        dataItem3.put("content", "{"+"data2.content3"+"}");
        data.add(dataItem3);
        excelWriter.fill(new FillWrapper("data1",data),fillConfig, writeSheet);


        List<Map<String, Object>> data2 = new ArrayList<>();
        Map<String, Object> dataItem21 = new HashMap<>();
        dataItem21.put("content1", "123");
        dataItem21.put("content2", "234");
        dataItem21.put("content3", "345");
        data2.add(dataItem21);

        Map<String, Object> dataItem22 = new HashMap<>();
        dataItem22.put("content1", "6534");
        dataItem22.put("content2", "234");
        dataItem22.put("content3", "324");
        data2.add(dataItem22);

        Map<String, Object> dataItem23 = new HashMap<>();
        dataItem23.put("content1", "546");
        dataItem23.put("content2", "978");
        dataItem23.put("content3", "567");
        data2.add(dataItem23);

        Map<String, Object> dataItem24 = new HashMap<>();
        dataItem24.put("content1", "258");
        dataItem24.put("content2", "6342");
        dataItem24.put("content3", "54");
        data2.add(dataItem24);

        excelWriter.fill(new FillWrapper("data2",data2), writeSheet);

        List<Map<String, Object>> data3 = new ArrayList<>();
        Map<String, Object> dataItem31 = new HashMap<>();
        dataItem31.put("date", "2021-01-25");
        data3.add(dataItem31);

        Map<String, Object> dataItem32 = new HashMap<>();
        dataItem32.put("date", "2021-01-26");
        data3.add(dataItem32);

        Map<String, Object> dataItem33 = new HashMap<>();
        dataItem33.put("date", "2021-01-27");
        data3.add(dataItem33);

        excelWriter.fill(new FillWrapper("data3",data3), writeSheet);

        // 别忘记关闭流
        excelWriter.finish();

/*        Map<String, Object> map = new HashMap<String, Object>();
        map.put("date", "2019年10月9日13:28:28");
        excelWriter.fill(map, writeSheet);*/
    }
}
