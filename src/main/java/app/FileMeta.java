package app;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import util.PinyinUtil;
import util.Util;

import java.io.File;
import java.util.Date;
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class FileMeta {

    // 文件名称
    private String name;
    // 文件所在的父目录的路径
    private String path;
    // 文件大小
    private Long size;
    // 文件上次修改时间
    private Date lastModified;
    // 是否是文件夹
    private Boolean isDirectory;

    // 给客户端控件使用，和 app.fxml中定义的名称要一致
    private String sizeText;
    // 和 app.fxml中定义的名称要一致
    private String lastModifiedText;
    // 文件名拼音
    private String pinyin;
    // 文件名拼音首字母
    private String pinyinFirst;

    // 通过文件设置属性
    public FileMeta(File file) {
        this(file.getName(), file.getParent(), file.isDirectory(),
                file.length(), new Date(file.lastModified()));
    }

    // 通过数据库获取的数据设置 FileMeta
    public FileMeta(String name, String path, boolean isDirectory, long size, Date lastModified) {
        this.name = name;
        this.path = path;
        this.isDirectory = isDirectory;
        this.size = size;
        this.lastModified = lastModified;
        if (PinyinUtil.containsChinese(name)) {
            String[] pinyins = PinyinUtil.get(name);
            pinyin = pinyins[0];
            pinyinFirst = pinyins[1];
        }
        // 客户端表格控件文件大小，上次文件修改时间的设置
        sizeText = Util.parseSize(size);
        lastModifiedText = Util.parseDate(lastModified);
    }
}
