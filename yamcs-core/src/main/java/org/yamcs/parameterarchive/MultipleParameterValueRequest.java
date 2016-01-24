package org.yamcs.parameterarchive;

import org.yamcs.protobuf.Yamcs.NamedObjectId;

public class MultipleParameterValueRequest {
    final NamedObjectId[] parameterNames;
    final int[] parameterIds;
    final int[] parameterGroupIds;
    final long start;
    final long stop;
    final boolean ascending;
    
    //these shall also be considered final - just that I didn't want the constructor to get very long
    boolean retrieveEngValues = true;
    boolean retrieveParamStatus = true;
    int limit = -1;
    
    //if true, then the generated ParameterValues will have the UTC string time set
    boolean storeUtcTime = false;
    
    public MultipleParameterValueRequest(long start, long stop, NamedObjectId[] parameterNames, int[] parameterIds, int[] parameterGroupIds,  boolean ascending) {
        if(parameterGroupIds.length!=parameterIds.length) throw new IllegalArgumentException("Different number of parameter ids than parameter group ids");
        if(parameterNames.length!=parameterIds.length) throw new IllegalArgumentException("Different number of parameter names than parameter ids");
        
        
        this.parameterNames = parameterNames;
        this.parameterIds = parameterIds;
        this.parameterGroupIds = parameterGroupIds;
        this.start = start;
        this.stop = stop;
        this.ascending = ascending;
    }
    
  
    boolean retrieveRawValues = false;
    public boolean isRetrieveRawValues() {
        return retrieveRawValues;
    }

    public void setRetrieveRawValues(boolean retrieveRawValues) {
        this.retrieveRawValues = retrieveRawValues;
    }

    public boolean isRetrieveEngValues() {
        return retrieveEngValues;
    }

    public void setRetrieveEngValues(boolean retrieveEngValues) {
        this.retrieveEngValues = retrieveEngValues;
    }

    public boolean isRetrieveParamStatus() {
        return retrieveParamStatus;
    }

    public void setRetrieveParamStatus(boolean retrieveParamStatus) {
        this.retrieveParamStatus = retrieveParamStatus;
    }

    public boolean isStoreUtcTime() {
        return storeUtcTime;
    }

    public void setStoreUtcTime(boolean storeUtcTime) {
        this.storeUtcTime = storeUtcTime;
    }

    public NamedObjectId[] getParameterNames() {
        return parameterNames;
    }

    public int[] getParameterIds() {
        return parameterIds;
    }

    public int[] getParameterGroupIds() {
        return parameterGroupIds;
    }

    public long getStart() {
        return start;
    }

    public long getStop() {
        return stop;
    }

    public boolean isAscending() {
        return ascending;
    }

    public int getLimit() {
        return limit;
    }

   
    
    
    /**
     * retrieve a limited number of "lines"
     * negative means no limit
     * @param limit
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }
}
