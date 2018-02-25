package xin.lzp.remotefiledir.util;

import android.content.Context;
import android.os.storage.StorageManager;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzp on 2017/8/20.
 * 用于获取系统所有可用的存储设备路径
 */

public class RootFileDirGetter {
    public static List<StorageInfo> listAllStorage(Context context) {
        ArrayList<StorageInfo> storages = new ArrayList<StorageInfo>();
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumeList = StorageManager.class.getMethod("getVolumeList", paramClasses);
            Object[] params = {};
            Object[] invokes = (Object[]) getVolumeList.invoke(storageManager, params);

            if (invokes != null) {
                StorageInfo info = null;
                for (int i = 0; i < invokes.length; i++) {
                    Object obj = invokes[i];
                    Method getPath = obj.getClass().getMethod("getPath", new Class[0]);
                    String path = (String) getPath.invoke(obj, new Object[0]);
                    info = new StorageInfo(path);

                    Method getVolumeState = StorageManager.class.getMethod("getVolumeState", String.class);
                    String state = (String) getVolumeState.invoke(storageManager, info.path);
                    info.state = state;

                    Method isRemovable = obj.getClass().getMethod("isRemovable", new Class[0]);
                    info.isRemoveable = ((Boolean) isRemovable.invoke(obj, new Object[0])).booleanValue();
                    storages.add(info);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        storages.trimToSize();
        return storages;
    }

    public static List<File> getAvaliableStorage(Context context) {

        List<StorageInfo> infos = listAllStorage(context);

        List<File> storages = new ArrayList<File>();
        for (StorageInfo info : infos) {
            File file = new File(info.path);
            if ((file.exists()) && (file.isDirectory()) && (file.canWrite())) {
                if (info.isMounted()) {
                    storages.add(file);
                }
            }
        }

        return storages;
    }


    static class StorageInfo {
        public String path;
        public String state;  //是否已经挂载
        public boolean isRemoveable;

        public StorageInfo(String path) {
            this.path = path;
        }

        public boolean isMounted() {
            return "mounted".equals(state);
        }

        @Override
        public String toString() {
            return "StorageInfo [path=" + path + ", state=" + state
                    + ", isRemoveable=" + isRemoveable + "]";
        }
    }
}
