/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.ui.widgets.connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import org.eclipse.dirigible.ide.ui.widgets.connection.spline.Spline;
import org.eclipse.dirigible.ide.ui.widgets.connection.spline.SplineRenderer;
import org.eclipse.dirigible.ide.ui.widgets.connection.spline.TwoPointSpline;

public class ConnectionViewer extends ContentViewer {

	private static final long serialVersionUID = 2227733381272538227L;

	private static final String INVALID_OR_MISSING_TARGET_ITEM_RESOLVER = Messages.ConnectionViewer_INVALID_OR_MISSING_TARGET_ITEM_RESOLVER;

	private static final String INVALID_OR_MISSING_SOURCE_ITEM_RESOLVER = Messages.ConnectionViewer_INVALID_OR_MISSING_SOURCE_ITEM_RESOLVER;

	private static final String INVALID_OR_MISSING_LABEL_PROVIDER = Messages.ConnectionViewer_INVALID_OR_MISSING_LABEL_PROVIDER;

	private static final String INVALID_OR_MISSING_CONTENT_PROVIDER = Messages.ConnectionViewer_INVALID_OR_MISSING_CONTENT_PROVIDER;

	private static final String INVALID_OR_NULL_SELECTION = Messages.ConnectionViewer_INVALID_OR_NULL_SELECTION;

	private static final String CONTENT_PROVIDER_MUST_NOT_RETURN_NULL = Messages.ConnectionViewer_CONTENT_PROVIDER_MUST_NOT_RETURN_NULL;

	private static final String COLOR_CANNOT_BE_NULL = Messages.ConnectionViewer_COLOR_CANNOT_BE_NULL;

	// TODO: Maybe configurable
	private static final int MARKER_WIDTH = 16;

	// TODO: Maybe configurable
	private static final int MARKER_HEIGHT = 16;

	/* Offset from the right side of the canvas */
	private static final int MARKER_MARGIN = 15;

	private static final int DEFAULT_LINE_WIDTH_DEFAULT = 1;

	private static final int DEFAULT_LINE_WIDTH_SELECTED = 2;

	private static final int SPLINE_PRECISION = 20;

	private final SplineRenderer splineRenderer = new SplineRenderer(
			SPLINE_PRECISION);

	private final Canvas canvas;

	private final ListenerList doubleClickListenerList = new ListenerList();

	private IConnectionItemResolver sourceItemResolver = null;

	private IConnectionItemResolver targetItemResolver = null;

	private final Set<Object> selection = new HashSet<Object>();

	private Object[] connections = new Object[0];

	private int lineWidthDefault = DEFAULT_LINE_WIDTH_DEFAULT;

	private int lineWidthSelected = DEFAULT_LINE_WIDTH_SELECTED;

	private Color colorDefault;

	private Color colorSelected;

	/**
	 * Creates a new {@link ConnectionViewer} with the specified
	 * <code>parent</code>.
	 */
	public ConnectionViewer(Composite parent) {
		colorDefault = Display.getCurrent().getSystemColor(
				SWT.COLOR_WIDGET_BORDER);
		colorSelected = Display.getCurrent().getSystemColor(
				SWT.COLOR_LIST_SELECTION);

		canvas = new Canvas(parent, SWT.BORDER | SWT.DOUBLE_BUFFERED);
		canvas.addMouseListener(new MouseAdapter() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 7106341036462228033L;

			public void mouseDown(MouseEvent e) {
				handleMouseDown(e);
			}

			public void mouseDoubleClick(MouseEvent e) {
				handleMouseDoubleClick(e);
			}
		});
		canvas.addPaintListener(new PaintListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -4651208476754703341L;

			public void paintControl(PaintEvent event) {
				handleCanvasPaint(event.gc);
			}
		});
	}

	/**
	 * Adds a new double click listener to this viewer.
	 * 
	 * @param listener
	 *            listener that will be notified of double click events.
	 */
	public void addDoubleClickListener(IDoubleClickListener listener) {
		doubleClickListenerList.add(listener);
	}

	/**
	 * Removes the specified double click listener from this viewer.
	 * 
	 * @param listener
	 *            listener that will no longer receive double click events.
	 */
	public void removeDoubleClickListener(IDoubleClickListener listener) {
		doubleClickListenerList.remove(listener);
	}

	/**
	 * Sets a new source {@link IConnectionItemResolver} for this viewer.
	 * 
	 * @param itemResolver
	 *            new source item resolver
	 */
	public void setSourceItemResolver(IConnectionItemResolver itemResolver) {
		this.sourceItemResolver = itemResolver;
	}

	/**
	 * Returns this viewer's source {@link IConnectionItemResolver}.
	 * 
	 * @see IConnectionItemResolver
	 * @return the current source item resolver.
	 */
	public IConnectionItemResolver getSourceItemResolver() {
		return sourceItemResolver;
	}

	/**
	 * Sets a new target {@link IConnectionItemResolver} for this viewer.
	 * 
	 * @param itemResolver
	 *            new target item resolver
	 */
	public void setTargetItemResolver(IConnectionItemResolver itemResolver) {
		this.targetItemResolver = itemResolver;
	}

	/**
	 * Returns this viewer's target {@link IConnectionItemResolver}.
	 * 
	 * @see IConnectionItemResolver
	 * @return the current target item resolver.
	 */
	public IConnectionItemResolver getTargetItemResolver() {
		return targetItemResolver;
	}

	/**
	 * Sets a new color that will be used for drawing standard lines.
	 * <p>
	 * Note: When this viewer is disposed, it will not dispose of the color. It
	 * is up to the user to do it. One must make sure not to dispose of the
	 * color before the viewer has finished using it (i.e. is disposed).
	 * 
	 * @param color
	 *            new default line color
	 */
	public void setLineColorDefault(Color color) {
		if (color == null) {
			throw new IllegalArgumentException(COLOR_CANNOT_BE_NULL);
		}
		this.colorDefault = color;
		refresh();
	}

	/**
	 * Returns the color that will be used when doing default line drawing.
	 * 
	 * @see #getLineColorSelection()
	 * @return line color for default lines
	 */
	public Color getLineColorDefault() {
		return colorDefault;
	}

	/**
	 * Sets a new color that will be used for drawing lines that are selected.
	 * <p>
	 * Note: When this viewer is disposed, it will not dispose of the color. It
	 * is up to the user to do it. One must make sure not to dispose of the
	 * color before the viewer has finished using it (i.e. is disposed).
	 * 
	 * @param color
	 *            new selection line color
	 */
	public void setLineColorSelected(Color color) {
		if (color == null) {
			throw new IllegalArgumentException(COLOR_CANNOT_BE_NULL);
		}
		this.colorSelected = color;
		refresh();
	}

	/**
	 * Returns the color that will be sued when drawing selected lines.
	 * 
	 * @see #getLineColorDefault()
	 * @return line color for selected lines
	 */
	public Color getLineColorSelection() {
		return colorSelected;
	}

	/**
	 * Sets a new line width that will be used when drawing default lines.
	 * 
	 * @param width
	 *            width of default lines
	 */
	public void setLineWidthDefault(int width) {
		this.lineWidthDefault = width;
		refresh();
	}

	/**
	 * Returns the line width that is used when drawing default lines.
	 * 
	 * @return width of default lines
	 */
	public int getLineWidthDefault() {
		return lineWidthDefault;
	}

	/**
	 * Sets a new line width that will be used when drawing selected lines.
	 * 
	 * @param width
	 *            width of selected lines
	 */
	public void setLineWidthSelected(int width) {
		this.lineWidthSelected = width;
		refresh();
	}

	/**
	 * Returns the line width that is used when drawing selected lines.
	 * 
	 * @return width of selected lines
	 */
	public int getLineWidthSelected() {
		return lineWidthSelected;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Control getControl() {
		return canvas;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh() {
		updateViewModel();
		update();
	}

	private void updateViewModel() {
		assertContentProvider(getContentProvider());
		final IStructuredContentProvider contentProvider = (IStructuredContentProvider) getContentProvider();
		final Object[] elements = contentProvider.getElements(getInput());
		Assert.isNotNull(elements, CONTENT_PROVIDER_MUST_NOT_RETURN_NULL);
		connections = elements;
	}

	/**
	 * Updates this viewer.
	 * <p>
	 * This operation redraws the viewer but if new elements were added to the
	 * model, this change will not be visualized.
	 */
	public void update() {
		if (canvas.isVisible()) {
			canvas.redraw();
		}
	}

	private void handleMouseDown(MouseEvent e) {
		if (e.button != 1) {
			return;
		}

		final Object connection = getConnectionAtPosition(e.x, e.y);
		if (connection == null) {
			return;
		}

		if ((e.stateMask & SWT.CTRL) > 0) {
			if (selection.contains(connection)) {
				selection.remove(connection);
			} else {
				selection.add(connection);
			}
		} else {
			selection.clear();
			selection.add(connection);
		}

		final ISelection eventSelection = new StructuredSelection(
				selection.toArray());
		fireSelectionChanged(new SelectionChangedEvent(this, eventSelection));

		refresh();
	}

	private void handleMouseDoubleClick(MouseEvent e) {
		if (e.button != 1) {
			return;
		}

		final Object connection = getConnectionAtPosition(e.x, e.y);
		if (connection == null) {
			return;
		}

		fireDoubleClick(new DoubleClickEvent(this, getSelection()));
	}

	protected void fireDoubleClick(final DoubleClickEvent event) {
		final Object[] listeners = doubleClickListenerList.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			final IDoubleClickListener l = (IDoubleClickListener) listeners[i];
			SafeRunnable.run(new SafeRunnable() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 7402948979315925968L;

				public void run() {
					l.doubleClick(event);
				}
			});
		}
	}

	private Object getConnectionAtPosition(int x, int y) {
		for (Object connection : connections) {
			if (isConnectionMarkerAtPosition(connection, x, y)) {
				return connection;
			}
		}
		return null;
	}

	private boolean isConnectionMarkerAtPosition(Object connection, int x, int y) {
		final Rectangle markerBounds = getConnectionMarkerBounds(connection);
		return (markerBounds != null) && (markerBounds.contains(x, y));
	}

	private Rectangle getConnectionMarkerBounds(Object connection) {
		final IConnectionContentProvider contentProvider = (IConnectionContentProvider) getContentProvider();
		final Object targetItem = contentProvider.getTargetItem(connection);

		if (targetItem == null) {
			return null;
		}

		if (!targetItemResolver.isItemVisible(targetItem, true)) {
			return null;
		}

		final int x = canvas.getSize().x - MARKER_MARGIN - MARKER_WIDTH / 2;
		final int y = targetItemResolver.getItemLocation(targetItem, true)
				- MARKER_HEIGHT / 2;
		return new Rectangle(x, y, MARKER_WIDTH, MARKER_HEIGHT);
	}

	private void handleCanvasPaint(GC gc) {
		if (connections.length > 0) {
			paintConnections(gc);
		}
	}

	private void paintConnections(GC gc) {
		assertLabelProvider(getLabelProvider());
		assertSourceItemResolver(sourceItemResolver);
		assertTargetItemResolver(targetItemResolver);

		sourceItemResolver.clearCache();
		targetItemResolver.clearCache();

		paintConnections(gc, connections);
	}

	private void paintConnections(GC gc, Object[] connections) {
		for (Object connection : connections) {
			paintConnection(gc, connection);
		}
	}

	protected void paintConnection(GC gc, Object connection) {
		final IConnectionContentProvider contentProvider = (IConnectionContentProvider) getContentProvider();

		final Object targetItem = contentProvider.getTargetItem(connection);

		// We do not draw filtered or collapsed target items
		if (!targetItemResolver.isItemVisible(targetItem, true)) {
			return;
		}

		final int targetHeight = targetItemResolver.getItemLocation(targetItem,
				true);
		if (targetHeight < 0 || targetHeight > canvas.getBounds().height) {
			return;
		}

		final List<Integer> sourceHeights = new ArrayList<Integer>();
		for (Object sourceItem : contentProvider.getSourceItems(connection)) {
			final int sourceHeight = sourceItemResolver.getItemLocation(
					sourceItem, true);
			sourceHeights.add(sourceHeight);
		}

		// Paint connection
		final boolean selected = selection.contains(connection);
		final Image image = getConnectionImage(connection);
		paintConnection(gc, sourceHeights, targetHeight, image, selected);
	}

	private Image getConnectionImage(Object connection) {
		final ILabelProvider labelProvider = (ILabelProvider) getLabelProvider();
		return labelProvider.getImage(connection);
	}

	private void paintConnection(GC gc, Collection<Integer> sourceHeights,
			int targetHeight, Image markerImage, boolean selected) {
		final int markerX = canvas.getSize().x - MARKER_MARGIN;
		final int markerY = targetHeight;

		final int targetX = canvas.getSize().x;
		final int targetY = targetHeight;

		gc.setForeground(selected ? colorSelected : colorDefault);
		gc.setLineWidth(selected ? lineWidthSelected : lineWidthDefault);

		// Draw line between marker and target
		gc.drawLine(markerX, markerY, targetX, targetY);

		// Draw lines between sources and marker
		for (int sourceHeight : sourceHeights) {
			final int sourceX = 0;
			final int sourceY = sourceHeight;

			final Spline spline = new TwoPointSpline(sourceX, sourceY, markerX,
					markerY);
			splineRenderer.renderSpline(gc, spline);
		}

		// Draw marker image
		if (markerImage != null) {
			final int markerImageX = markerX - MARKER_WIDTH / 2;
			final int markerImageY = markerY - MARKER_HEIGHT / 2;
			gc.drawImage(markerImage, 0, 0, markerImage.getBounds().width,
					markerImage.getBounds().height, markerImageX, markerImageY,
					MARKER_WIDTH, MARKER_HEIGHT);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ISelection getSelection() {
		return new StructuredSelection(selection.toArray());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelection(ISelection selection, boolean reveal) {
		assertSelection(selection);
		final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		this.selection.clear();
		for (Object element : structuredSelection.toArray()) {
			this.selection.add(element);
		}
		refresh();
	}

	private void assertSelection(ISelection selection) {
		Assert.isTrue(selection instanceof IStructuredSelection,
				INVALID_OR_NULL_SELECTION);
	}

	private void assertContentProvider(IContentProvider provider) {
		Assert.isTrue(provider instanceof IConnectionContentProvider,
				INVALID_OR_MISSING_CONTENT_PROVIDER);
	}

	private void assertLabelProvider(IBaseLabelProvider provider) {
		Assert.isTrue(provider instanceof ILabelProvider,
				INVALID_OR_MISSING_LABEL_PROVIDER);
	}

	private void assertSourceItemResolver(IConnectionItemResolver resolver) {
		Assert.isNotNull(resolver, INVALID_OR_MISSING_SOURCE_ITEM_RESOLVER);
	}

	private void assertTargetItemResolver(IConnectionItemResolver resolver) {
		Assert.isNotNull(resolver, INVALID_OR_MISSING_TARGET_ITEM_RESOLVER);
	}

}
