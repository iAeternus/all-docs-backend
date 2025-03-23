package org.ricky.core.common.domain.page;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.ricky.common.constants.ConfigConstant.*;
import static org.ricky.common.exception.MyException.requestValidationException;

@Getter
@EqualsAndHashCode
public class Pagination {

    private final int pageIndex;
    private final int pageSize;

    private Pagination(int pageIndex, int pageSize) {
        if (pageIndex < MIN_PAGE_INDEX) {
            throw requestValidationException("detail", "pageIndex不能小于" + MIN_PAGE_INDEX);
        }

        if (pageIndex > MAX_PAGE_INDEX) {
            throw requestValidationException("detail", "pageIndex不能大于" + MAX_PAGE_INDEX);
        }

        if (pageSize < MIN_PAGE_SIZE) {
            throw requestValidationException("detail", "pageSize不能小于" + MIN_PAGE_SIZE);
        }

        if (pageSize > MAX_PAGE_SIZE) {
            throw requestValidationException("detail", "pageSize不能大于" + MAX_PAGE_SIZE);
        }

        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public static Pagination pagination(int pageIndex, int pageSize) {
        return new Pagination(pageIndex, pageSize);
    }

    public int skip() {
        return (this.pageIndex - 1) * this.pageSize;
    }

    public int limit() {
        return this.pageSize;
    }

    public Pageable toPageable() {
        return PageRequest.of(pageIndex, pageSize);
    }

}
