package org.eclipse.dirigible.runtime.scripting;

public interface IStorage {

	public boolean exists(String path) throws EStorageException;
	
	public void clear() throws EStorageException;
	
	public void delete(String path) throws EStorageException;
	
	public void put(String path, byte[] data) throws EStorageException;
	
	public void put(String path, byte[] data, String contentType) throws EStorageException;
	
	public byte[] get(String path) throws EStorageException;
	
	
}
