package com.despegar.integration.mongo.entities;

public class BulkResult {
	
	private Integer modified;
	private Integer removed;
	private Integer inserted;
	
	public BulkResult(Integer modified, Integer removed, Integer inserted){
		this.modified = modified;
		this.removed = removed;
		this.inserted = inserted;
	}
	
	public Integer getModified() {
		return modified;
	}
	public void setModified(Integer modified) {
		this.modified = modified;
	}
	public Integer getRemoved() {
		return removed;
	}
	public void setRemoved(Integer removed) {
		this.removed = removed;
	}
	public Integer getInserted() {
		return inserted;
	}
	public void setInserted(Integer inserted) {
		this.inserted = inserted;
	}
	
}
