/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.ide;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IBundleGroup;
import org.eclipse.core.runtime.IBundleGroupProvider;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.internal.BundleGroupProperties;
import org.eclipse.ui.internal.ProductProperties;

/**
 * The information within this object is obtained from the about INI file. This
 * file resides within an install configurations directory and must be a
 * standard java property file.
 * <p>
 * This class is not intended to be instantiated or subclassed by clients.
 * </p>
 */
@SuppressWarnings("restriction")
public final class AboutInfo {
	private static final String EMPTY_STRING = "";

	private ProductProperties productProperties;

	private BundleGroupProperties bundleGroupProperties;

	private Long featureImageCRC;

	private boolean calculatedImageCRC = false;

	/**
	 * The information contained in this info will apply to only the argument
	 * product.
	 *
	 * @param product
	 *            the product
	 */
	public AboutInfo(IProduct product) {
		this.productProperties = new ProductProperties(product);
	}

	/**
	 * This info object will apply to the argument bundle group.
	 * 
	 * @param bundleGroup
	 *            the bundle group
	 */
	public AboutInfo(IBundleGroup bundleGroup) {
		this.bundleGroupProperties = new BundleGroupProperties(bundleGroup);
	}

	/**
	 * Returns the configuration information for the feature with the given id.
	 *
	 * @param featureId
	 *            the feature id
	 * @param versionId
	 *            the version id (of the feature)
	 * @return the configuration information for the feature
	 */
	public static AboutInfo readFeatureInfo(String featureId, String versionId) {
		Assert.isNotNull(featureId);
		Assert.isNotNull(versionId);

		// first see if the id matches the product
		IProduct product = Platform.getProduct();
		if ((product != null) && featureId.equals(ProductProperties.getProductId(product))) {
			return new AboutInfo(product);
		}

		// then check the bundle groups
		IBundleGroup bundleGroup = getBundleGroup(featureId, versionId);
		if (bundleGroup != null) {
			return new AboutInfo(bundleGroup);
		}

		return null;
	}

	private static IBundleGroup getBundleGroup(String id, String versionId) {
		if ((id == null) || (versionId == null)) {
			return null;
		}

		IBundleGroupProvider[] providers = Platform.getBundleGroupProviders();
		for (IBundleGroupProvider provider : providers) {
			IBundleGroup[] groups = provider.getBundleGroups();
			for (IBundleGroup group : groups) {
				if (id.equals(group.getIdentifier()) && versionId.equals(group.getVersion())) {
					return group;
				}
			}
		}

		return null;
	}

	/**
	 * Returns the descriptor for an image which can be shown in an "about"
	 * dialog for this product. Products designed to run "headless" typically
	 * would not have such an image.
	 *
	 * @return the descriptor for an about image, or <code>null</code> if none
	 */
	public ImageDescriptor getAboutImage() {
		return productProperties == null ? null : productProperties.getAboutImage();
	}

	/**
	 * Returns the descriptor for an image which can be shown in an
	 * "about features" dialog. Products designed to run "headless" typically
	 * would not have such an image.
	 *
	 * @return the descriptor for a feature image, or <code>null</code> if none
	 */
	public ImageDescriptor getFeatureImage() {
		return bundleGroupProperties == null ? null : bundleGroupProperties.getFeatureImage();
	}

	/**
	 * Returns the simple name of the feature image file.
	 *
	 * @return the simple name of the feature image file, or <code>null</code>
	 *         if none
	 */
	public String getFeatureImageName() {
		if (bundleGroupProperties == null) {
			return null;
		}

		URL url = bundleGroupProperties.getFeatureImageUrl();
		return url == null ? null : new Path(url.getPath()).lastSegment();
	}

	/**
	 * Returns the CRC of the feature image as supplied in the properties file.
	 *
	 * @return the CRC of the feature image, or <code>null</code> if none
	 */
	public Long getFeatureImageCRC() {
		if (bundleGroupProperties == null) {
			return null;
		}

		if (!calculatedImageCRC) {
			featureImageCRC = calculateImageCRC(bundleGroupProperties.getFeatureImageUrl());
			calculatedImageCRC = featureImageCRC != null;
		}

		return featureImageCRC;
	}

	/**
	 * Calculate a CRC for the feature image
	 */
	private static Long calculateImageCRC(URL url) {
		if (url == null) {
			return null;
		}

		InputStream in = null;
		try {
			CRC32 checksum = new CRC32();
			in = new CheckedInputStream(url.openStream(), checksum);

			// the contents don't matter, the read just needs a place to go
			byte[] sink = new byte[2048];
			while (true) {
				if (in.read(sink) <= 0) {
					break;
				}
			}

			return new Long(checksum.getValue());
		} catch (IOException e) {
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}

	/**
	 * Returns a label for the feature plugn, or <code>null</code>.
	 *
	 * @return label
	 */
	public String getFeatureLabel() {
		if (productProperties != null) {
			return productProperties.getProductName();
		}
		if (bundleGroupProperties != null) {
			return bundleGroupProperties.getFeatureLabel();
		}
		return null;
	}

	/**
	 * Returns the id for this feature.
	 *
	 * @return the feature id
	 */
	public String getFeatureId() {
		String id = null;
		if (productProperties != null) {
			id = productProperties.getProductId();
		} else if (bundleGroupProperties != null) {
			id = bundleGroupProperties.getFeatureId();
		}
		return id != null ? id : EMPTY_STRING;
	}

	/**
	 * Returns the text to show in an "about" dialog for this product. Products
	 * designed to run "headless" typically would not have such text.
	 *
	 * @return the about text, or <code>null</code> if none
	 */
	public String getAboutText() {
		return productProperties == null ? null : productProperties.getAboutText();
	}

	/**
	 * Returns the application name or <code>null</code>. Note this is never
	 * shown to the user. It is used to initialize the SWT Display.
	 * <p>
	 * On Motif, for example, this can be used to set the name used for resource
	 * lookup.
	 * </p>
	 *
	 * @return the application name, or <code>null</code>
	 * @see org.eclipse.swt.widgets.Display#setAppName
	 */
	public String getAppName() {
		// return productProperties == null ? null : productProperties
		// .getAppName();
		return null;
	}

	/**
	 * Returns the product name or <code>null</code>. This is shown in the
	 * window title and the About action.
	 *
	 * @return the product name, or <code>null</code>
	 */
	public String getProductName() {
		return productProperties == null ? null : productProperties.getProductName();
	}

	/**
	 * Returns the provider name or <code>null</code>.
	 *
	 * @return the provider name, or <code>null</code>
	 */
	public String getProviderName() {
		return bundleGroupProperties == null ? null : bundleGroupProperties.getProviderName();
	}

	/**
	 * Returns the feature version id.
	 *
	 * @return the version id of the feature
	 */
	public String getVersionId() {
		return bundleGroupProperties == null ? EMPTY_STRING : bundleGroupProperties.getFeatureVersion();
	}

	/**
	 * Returns a <code>URL</code> for the welcome page. Products designed to run
	 * "headless" typically would not have such an page.
	 *
	 * @return the welcome page, or <code>null</code> if none
	 */
	public URL getWelcomePageURL() {
		if (productProperties != null) {
			return productProperties.getWelcomePageUrl();
		}
		if (bundleGroupProperties != null) {
			return bundleGroupProperties.getWelcomePageUrl();
		}
		return null;
	}

	/**
	 * Returns the ID of a perspective in which to show the welcome page. May be
	 * <code>null</code>.
	 *
	 * @return the welcome page perspective id, or <code>null</code> if none
	 */
	public String getWelcomePerspectiveId() {
		return bundleGroupProperties == null ? null : bundleGroupProperties.getWelcomePerspective();
	}

	/**
	 * Returns a <code>String</code> for the tips and trick href.
	 *
	 * @return the tips and tricks href, or <code>null</code> if none
	 */
	public String getTipsAndTricksHref() {
		return bundleGroupProperties == null ? null : bundleGroupProperties.getTipsAndTricksHref();
	}

	/**
	 * Return an array of image descriptors for the window images to use for
	 * this product. The expectations is that the elements will be the same
	 * image rendered at different sizes. Products designed to run "headless"
	 * typically would not have such images.
	 *
	 * @return an array of the image descriptors for the window images, or
	 *         <code>null</code> if none
	 * @since 3.0
	 */
	public ImageDescriptor[] getWindowImages() {
		return productProperties == null ? null : productProperties.getWindowImages(); // NOPMD
	}
}
