package com.cobra.mytravo.models;

import java.util.ArrayList;
import java.util.List;

public class UploadLocation {

	/**
	 * @param args
	 */
	private List<Location> locations = new ArrayList<Location>();

	public List<Location> getLocations() {
		return locations;
	}

	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}

}
