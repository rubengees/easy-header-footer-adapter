package com.rubengees.easyheaderfooteradapter.sample;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class which contains the different LayoutManagers which are covered in the sample.
 *
 * @author Ruben Gees
 */
public final class LayoutManager {

    public static final int LINEAR = 0;
    public static final int GRID = 1;
    public static final int STAGGERED_GRID = 2;

    private LayoutManager() {
    }

    /**
     * Annotation to define the available options.
     */
    @IntDef({LINEAR, GRID, STAGGERED_GRID})
    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
    public @interface LayoutManagerType {
    }

}
