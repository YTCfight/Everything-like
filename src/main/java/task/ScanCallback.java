package task;

import java.io.File;

public interface ScanCallback {

    // 对于文件夹的扫描任务进行回调：处理文件夹，将文件夹下一级的
    void callback(File dir);

}
