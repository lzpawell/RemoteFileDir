package xin.lzp.remotefiledir.util;

/**
 * Created by lzp on 2017/9/24.
 * from fileName's suffix get file type
 * and return a file header image resource id which match the file type
 */

public class FileHeaderMatcher {
    public static int match(String fileName){
        return matchImgResourceFromSuffix(getSuffix(fileName));
    }

    private static String getSuffix(String fileName){
        int dotIndex = fileName.lastIndexOf(".");
        if(dotIndex < 0){
            return null;
        }
        /* 获取文件的后缀名*/
        String suffix =fileName.substring(dotIndex,fileName.length()).toLowerCase();

        return suffix;
    }

    private static int matchImgResourceFromSuffix(String suffix){
        return 0;
    }
}
