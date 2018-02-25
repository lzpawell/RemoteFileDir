package xin.lzp.remotefiledir.util;

/**
 * Created by lzp on 2017/8/21.
 */

public class FileSizeFormat {

    private static final long B = 1024;
    private static final long K = 1024*B;
    private static final long M = 1024*K;
    private static final long G = 1024*M;

    public static String format(long fileSize){
        String suf = "B";

        long ans;
        if((fileSize / B) > 0 ){
            ans = fileSize / B;
            suf = "KB";
        }else{
            return fileSize + suf;
        }

        if((ans / B) > 0){
            ans = ans / B;
            suf = "MB";
        }else{
            return ans + suf;
        }

        if((ans / B) > 0){
            ans = ans / B;
            suf = "GB";
        }else{
            return ans + suf;
        }

        return ans + suf;
    }
}
