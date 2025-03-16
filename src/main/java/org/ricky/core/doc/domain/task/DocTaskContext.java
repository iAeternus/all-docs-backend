package org.ricky.core.doc.domain.task;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ricky.common.lock.LockedMethod;
import org.ricky.core.doc.domain.Doc;
import org.ricky.core.doc.domain.DocTypeEnum;
import org.springframework.stereotype.Component;

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

    private Doc doc; // TODO get后写很危险！
    private String txtFilePath;
    private String thumbFilePath;
    private String previewFilePath;
    // private DocTypeEnum docType;

    @Getter(AccessLevel.NONE)
    private final Lock lock = new ReentrantLock();

    private DocTaskContext(Doc doc) {
        this.doc = doc;
        this.txtFilePath = "";
        this.thumbFilePath = "";
        this.previewFilePath = "";
        // this.docType = DocTypeEnum.getDocType(doc.getSuffix());
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

    // @LockedMethod
    // public void setDocType(DocTypeEnum docType) {
    //     this.docType = docType;
    // }

    public static String buildPath(String filename, String suffix) {
        return "./" + filename + suffix;
    }

}
