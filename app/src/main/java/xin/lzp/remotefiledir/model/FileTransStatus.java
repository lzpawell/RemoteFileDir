package xin.lzp.remotefiledir.model;

/**
 * Created by lzp on 2017/9/22.
 */

public class FileTransStatus {
    private String fileName;
    private long currentLength;
    private long totalLength;
    private State state;

    public long getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(long totalLength) {
        this.totalLength = totalLength;
    }

    private String speed;
    private int currentPercentage;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getCurrentLength() {
        return currentLength;
    }

    public void setCurrentLength(long currentLength) {
        this.currentLength = currentLength;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public int getCurrentPercentage() {
        return currentPercentage;
    }

    public void setCurrentPercentage(int currentPercentage) {
        this.currentPercentage = currentPercentage;
    }


    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public static enum State{
        downloading,
        uploading,
        finish,
        stop_download,
        stop_upload
    }
}
