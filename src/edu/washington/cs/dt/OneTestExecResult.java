/** Copyright 2012 University of Washington. All Rights Reserved.
 *  @author Sai Zhang
 */
package edu.washington.cs.dt;

import edu.washington.cs.dt.main.ImpactMain;
import edu.washington.cs.dt.main.Main;
import edu.washington.cs.dt.util.TestExecUtils;
import edu.washington.cs.dt.util.Utils;

public class OneTestExecResult {

    public final RESULT result;

    private final String fullStackTrace;
    private final String filteredStackTrace;
    private long execTime = 0;


    private final boolean comparestacktrace;

    public OneTestExecResult(RESULT result) {
        this(result, null);
    }

    public OneTestExecResult(RESULT result, String fullStackTrace) {
        this(result, fullStackTrace, -1);
    }

    public OneTestExecResult(RESULT result, String fullStackTrace, long time) {
        Utils.checkNull(result, "result can not be null.");
        this.result = result;
        this.fullStackTrace = fullStackTrace;
        if(this.fullStackTrace == null) {
            this.filteredStackTrace = null;
        } else if(this.fullStackTrace.equals(TestExecUtils.noStackTrace)) {
            this.filteredStackTrace = fullStackTrace;
        } else {
            this.filteredStackTrace = TestExecUtils.flatFilteredStackTraces(this.fullStackTrace);
        }

        this.comparestacktrace = Main.comparestacktrace;
        this.execTime = time;
    }

    public String getFullStackTrace() {
        return this.fullStackTrace;
    }

    public String getFilteredStackTrace() {
        return this.filteredStackTrace;
    }

    @Override
    public String toString() {
        if (ImpactMain.useTimer) {
            return this.execTime + "";
        }
        return this.result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof OneTestExecResult)) {
            return false;
        }

        OneTestExecResult r = (OneTestExecResult)o;

        if(!comparestacktrace) {
            return r.result.equals(this.result);
        } else {
            return r.result.equals(this.result)
                    && (this.filteredStackTrace != null
                    ? this.filteredStackTrace.equals(r.filteredStackTrace)
                            : r.filteredStackTrace == null);
        }
    }

    @Override
    public int hashCode() {
        if(!comparestacktrace) {
            return 13*result.hashCode();
        }
        return 13*result.hashCode() + (this.filteredStackTrace != null ? 29*this.filteredStackTrace.hashCode() : 9);
    }

    public long getExecTime() {
        return execTime;
    }

    public void setExecTime(long execTime) {
        this.execTime = execTime;
    }

}