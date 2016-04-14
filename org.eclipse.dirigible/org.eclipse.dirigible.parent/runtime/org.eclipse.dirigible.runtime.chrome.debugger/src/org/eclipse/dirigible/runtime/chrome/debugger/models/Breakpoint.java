package org.eclipse.dirigible.runtime.chrome.debugger.models;

public class Breakpoint implements Comparable<Breakpoint> {

	private String id;
	private Location location;

	public Breakpoint() {
	}

	public Breakpoint(final String id, final Location location) {
		this.id = id;
		this.location = location;
	}

	public String getId() {
		return this.id;
	}

	public Location getLocation() {
		return this.location;
	}

	@Override
	public int compareTo(Breakpoint otherBreakpoint) {
		int idsCompared = id.compareTo(otherBreakpoint.id);
		if (idsCompared == 0) {
			Location otherLocation = otherBreakpoint.getLocation();
			return location.compareTo(otherLocation);
		}
		return idsCompared;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Breakpoint other = (Breakpoint) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Breakpoint [id=" + id + ", location=" + location + "]";
	}
}
