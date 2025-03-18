package org.ricky.core.doc.domain.task;

import lombok.AccessLevel;
import lombok.Getter;
import org.ricky.common.lock.LockedMethod;
import org.ricky.core.doc.domain.Doc;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Ricky
 * @version 1.0
 * @date 2025/3/15
 * @className DocTaskContext
 * @desc
 */
@Getter
public class DocTaskContext {

    private Doc doc;
    private String txtFilePath;
    private String thumbFilePath;
    private String previewFilePath;

    @Getter(AccessLevel.NONE)
    private final Lock lock = new ReentrantLock();

    private DocTaskContext(Doc doc) {
        this.doc = doc;
        this.txtFilePath = "";
        this.thumbFilePath = "";
        this.previewFilePath = "";
    }

    public static DocTaskContext newInstance(Doc doc) {
        return new DocTaskContext(doc);
    }

    @LockedMethod
    public void setDoc(Doc doc) {
        this.doc = doc;
    }

    @LockedMethod
    public void setTxtFilePath(String txtFilePath) {
        this.txtFilePath = txtFilePath;
    }

    @LockedMethod
    public void setThumbFilePath(String thumbFilePath) {
        this.thumbFilePath = thumbFilePath;
    }

    @LockedMethod
    public void setPreviewFilePath(String previewFilePath) {
        this.previewFilePath = previewFilePath;
    }

}
