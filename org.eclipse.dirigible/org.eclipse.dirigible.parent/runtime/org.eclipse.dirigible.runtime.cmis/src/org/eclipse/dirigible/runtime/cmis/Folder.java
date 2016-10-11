package org.eclipse.dirigible.runtime.cmis;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IResource;

public class Folder extends CmisObject {

	private CmisSession session;

	private ICollection internalFolder;

	private IRepository repository;

	private boolean rootFolder = false;

	public Folder(CmisSession session) throws IOException {
		super(session, "/");
		this.session = session;
		this.repository = (IRepository) session.getCmisRepository().getInternalObject();
		this.internalFolder = repository.getRoot();
		this.rootFolder = true;
	}

	public Folder(CmisSession session, ICollection internalCollection) throws IOException {
		super(session, internalCollection.getPath());
		this.session = session;
		this.repository = (IRepository) session.getCmisRepository().getInternalObject();
		this.internalFolder = internalCollection;
	}

	public ICollection getInternalFolder() {
		return internalFolder;
	}

	@Override
	protected boolean isCollection() {
		return true;
	}

	/**
	 * Returns the Path of this Folder
	 *
	 * @return
	 */
	public String getPath() {
		return this.getInternalEntity().getPath();
	}

	/**
	 * Creates a new folder under this Folder
	 *
	 * @param properties
	 * @return
	 * @throws IOException
	 */
	public Folder createFolder(Map<String, String> properties) throws IOException {
		String name = properties.get(CmisConstants.NAME);
		return new Folder(this.session, this.internalFolder.createCollection(name));
	}

	/**
	 * Creates a new document under this Folder
	 *
	 * @param properties
	 * @return
	 * @throws IOException
	 */
	public Document createDocument(Map<String, String> properties, ContentStream contentStream, String versioningState) throws IOException {
		String name = properties.get(CmisConstants.NAME);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtils.copy(contentStream.getStream(), out);
		return new Document(this.session, this.internalFolder.createResource(name, out.toByteArray(), true, contentStream.getMimetype()));
	}

	public List<CmisObject> getChildren() throws IOException {
		List<CmisObject> children = new ArrayList<CmisObject>();
		List<ICollection> collections = this.internalFolder.getCollections();
		for (ICollection collection : collections) {
			children.add(new Folder(this.session, collection));
		}
		List<IResource> resources = this.internalFolder.getResources();
		for (IResource resource : resources) {
			children.add(new Document(this.session, resource));
		}
		return children;
	}

	/**
	 * Returns true if this Folder is a root folder and false otherwise
	 *
	 * @return
	 */
	public boolean isRootFolder() {
		return rootFolder;
	}

	/**
	 * Returns the parent Folder of this Folder
	 *
	 * @param properties
	 * @return
	 * @throws IOException
	 */
	public Folder getFolderParent() throws IOException {
		return new Folder(this.session, this.internalFolder.getParent());
	}

}
