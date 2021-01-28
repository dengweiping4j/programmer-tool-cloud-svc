package com.programmer.blog.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentLoopMerge;
import lombok.Data;

/**
 * 栗子
 *
 * @author dengweiping
 * @date 2021/1/28 16:05
 */
@Data
public class DemoData {
    // 这一列 每隔2行 合并单元格
    @ContentLoopMerge(eachRow = 2)
    @ExcelProperty("日期")
    private String date;
    @ExcelProperty("行程")
    private String name1;
    @ExcelProperty("里程")
    private String name2;
}
