/******************************************************************************* 
 * Copyright (c) 2008 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 *******************************************************************************/
package org.eclipse.dirigible.ide.ui.rap.managers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.internal.provisional.action.ToolBarManager2;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

@SuppressWarnings("restriction")
public class ViewToolBarManager extends ToolBarManager2 {

	private static final long serialVersionUID = 8700492995156762861L;
	private static final String STYLING_VARIANT = "viewToolbar"; //$NON-NLS-1$
	private ToolBar toolBar;
	private ToolBar fakeToolbar;

	@SuppressWarnings("deprecation")
	public ToolBar createControl(Composite parent) {
		if (!toolBarExist() && parent != null) {
			toolBar = new ToolBar(parent, SWT.NONE);
			toolBar.setData(WidgetUtil.CUSTOM_VARIANT, STYLING_VARIANT);
			toolBar.setMenu(getContextMenuControl());
			// create the fake Toolbar
			fakeToolbar = new ToolBar(parent, SWT.NONE);
			fakeToolbar.setVisible(false);
			update(true);
		}
		return toolBar;
	}

	public ToolBar getControl() {
		return toolBar;
	}

	private Menu getContextMenuControl() {
		Menu result = null;
		if ((getContextMenuManager() != null) && (toolBar != null)) {
			Menu menuWidget = getContextMenuManager().getMenu();
			if ((menuWidget == null) || (menuWidget.isDisposed())) {
				menuWidget = getContextMenuManager().createContextMenu(toolBar);
			}
			result = menuWidget;
		}
		return result;
	}

	private boolean toolBarExist() {
		return toolBar != null && !toolBar.isDisposed();
	}

	// TODO: Check why this method was overriden in the first place
	// and do the necessary modifications if necessary.
	/*
	 * (non-Javadoc) Method declared on IContributionManager.
	 */
	public void update(boolean force) {
		super.update(force);
		// if( isDirty() || force ) {
		// if( toolBarExist() ) {
		//
		// // clean contains all active items without separators
		// IContributionItem[] items = getItems();
		// ArrayList clean = new ArrayList( items.length );
		// for( int i = 0; i < items.length; ++i ) {
		// IContributionItem ci = items[ i ];
		// if( !ci.isSeparator() ) {
		// clean.add( ci );
		// }
		// }
		// // determine obsolete items (removed or non active)
		// ToolItem[] mi = toolBar.getItems();
		// ArrayList toRemove = new ArrayList();
		// for( int i = 0; i < mi.length; i++ ) {
		// toRemove.add( mi[ i ] );
		// }
		// // add fake toolbar items to the items to remove
		// for( int i = 0; i < fakeToolbar.getItemCount(); i++ ) {
		// toRemove.add( fakeToolbar.getItem( i ) );
		// }
		//
		// // Turn redraw off if the number of items to be added
		// // is above a certain threshold, to minimize flicker,
		// // otherwise the toolbar can be seen to redraw after each item.
		// // Do this before any modifications are made.
		// // We assume each contribution item will contribute at least one
		// // toolbar item.
		// boolean useRedraw
		// = ( clean.size() - ( mi.length - toRemove.size() ) ) >= 3;
		// try {
		// if( useRedraw ) {
		// toolBar.setRedraw( false );
		// }
		//
		// // remove obsolete items
		// for( int i = toRemove.size(); --i >= 0; ) {
		// ToolItem item = ( ToolItem ) toRemove.get(i);
		// if( !item.isDisposed() ) {
		// Control ctrl = item.getControl();
		// if( ctrl != null ) {
		// item.setControl( null );
		// ctrl.dispose();
		// }
		// item.dispose();
		// }
		// }
		//
		// // add new items
		// IContributionItem src, dest;
		// mi = toolBar.getItems();
		// int srcIx = 0;
		// int destIx = 0;
		//
		// for( Iterator e = clean.iterator(); e.hasNext(); ) {
		// src = ( IContributionItem ) e.next();
		//
		// // get corresponding item in SWT widget
		// if( srcIx < mi.length ) {
		// dest = ( IContributionItem ) mi[ srcIx ].getData();
		// } else {
		// dest = null;
		// }
		//
		// if( dest != null && src.equals( dest ) ) {
		// srcIx++;
		// destIx++;
		// continue;
		// }
		//
		// if( dest != null && dest.isSeparator()
		// && src.isSeparator() )
		// {
		// mi[ srcIx ].setData( src );
		// srcIx++;
		// destIx++;
		// continue;
		// }
		//
		// // fill item if visible, if not fill it into the fake toolbar
		// ToolBar tempToolBar = null;
		// if( src.isVisible() ) {
		// src.fill( toolBar, destIx );
		// tempToolBar = toolBar;
		// } else {
		// src.fill( fakeToolbar, destIx );
		// tempToolBar = fakeToolbar;
		// }
		// ToolItem toolItem = tempToolBar.getItem( destIx );
		// toolItem.setData( src );
		// toolItem.setData( WidgetUtil.CUSTOM_VARIANT, STYLING_VARIANT );
		// }
		//
		// // remove any old tool items not accounted for
		// for( int i = mi.length; --i >= srcIx; ) {
		// ToolItem item = mi[ i ];
		// if( !item.isDisposed() ) {
		// Control ctrl = item.getControl();
		// if( ctrl != null ) {
		// item.setControl( null );
		// ctrl.dispose();
		// }
		// item.dispose();
		// }
		// }
		//
		// setDirty( false );
		//
		// // turn redraw back on if we turned it off above
		// } finally {
		// if( useRedraw ) {
		// toolBar.setRedraw( true );
		// }
		// }
		// }
		// }
	}

	// I061150: This is the old update method. It would throw an index out
	// of bounds exception so I decided to refactor it. I am keeping it here
	// commented just in case (and for reference also)
	// /*
	// * (non-Javadoc) Method declared on IContributionManager.
	// */
	// public void update( boolean force ) {
	// if( isDirty() || force ) {
	// if( toolBarExist() ) {
	//
	// // clean contains all active items without separators
	// IContributionItem[] items = getItems();
	// ArrayList clean = new ArrayList( items.length );
	// for( int i = 0; i < items.length; ++i ) {
	// IContributionItem ci = items[ i ];
	// if( !ci.isSeparator() ) {
	// clean.add( ci );
	// }
	// }
	// // determine obsolete items (removed or non active)
	// ToolItem[] mi = toolBar.getItems();
	// ArrayList toRemove = new ArrayList();
	// for( int i = 0; i < mi.length; i++ ) {
	// toRemove.add( mi[ i ] );
	// }
	// // add fake toolbar items to the items to remove
	// for( int i = 0; i < fakeToolbar.getItemCount(); i++ ) {
	// toRemove.add( fakeToolbar.getItem( i ) );
	// }
	//
	// // Turn redraw off if the number of items to be added
	// // is above a certain threshold, to minimize flicker,
	// // otherwise the toolbar can be seen to redraw after each item.
	// // Do this before any modifications are made.
	// // We assume each contribution item will contribute at least one
	// // toolbar item.
	// boolean useRedraw
	// = ( clean.size() - ( mi.length - toRemove.size() ) ) >= 3;
	// try {
	// if( useRedraw ) {
	// toolBar.setRedraw( false );
	// }
	//
	// // remove obsolete items
	// for( int i = toRemove.size(); --i >= 0; ) {
	// ToolItem item = ( ToolItem ) toRemove.get(i);
	// if( !item.isDisposed() ) {
	// Control ctrl = item.getControl();
	// if( ctrl != null ) {
	// item.setControl( null );
	// ctrl.dispose();
	// }
	// item.dispose();
	// }
	// }
	//
	// // add new items
	// IContributionItem src, dest;
	// mi = toolBar.getItems();
	// int srcIx = 0;
	// int destIx = 0;
	//
	// for( Iterator e = clean.iterator(); e.hasNext(); ) {
	// src = ( IContributionItem ) e.next();
	//
	// // get corresponding item in SWT widget
	// if( srcIx < mi.length ) {
	// dest = ( IContributionItem ) mi[ srcIx ].getData();
	// } else {
	// dest = null;
	// }
	//
	// if( dest != null && src.equals( dest ) ) {
	// srcIx++;
	// destIx++;
	// continue;
	// }
	//
	// if( dest != null && dest.isSeparator()
	// && src.isSeparator() )
	// {
	// mi[ srcIx ].setData( src );
	// srcIx++;
	// destIx++;
	// continue;
	// }
	//
	// // fill item if visible, if not fill it into the fake toolbar
	// ToolBar tempToolBar = null;
	// if( src.isVisible() ) {
	// src.fill( toolBar, destIx );
	// tempToolBar = toolBar;
	// } else {
	// src.fill( fakeToolbar, destIx );
	// tempToolBar = fakeToolbar;
	// }
	// ToolItem toolItem = tempToolBar.getItem( destIx );
	// toolItem.setData( src );
	// toolItem.setData( WidgetUtil.CUSTOM_VARIANT, STYLING_VARIANT );
	// }
	//
	// // remove any old tool items not accounted for
	// for( int i = mi.length; --i >= srcIx; ) {
	// ToolItem item = mi[ i ];
	// if( !item.isDisposed() ) {
	// Control ctrl = item.getControl();
	// if( ctrl != null ) {
	// item.setControl( null );
	// ctrl.dispose();
	// }
	// item.dispose();
	// }
	// }
	//
	// setDirty( false );
	//
	// // turn redraw back on if we turned it off above
	// } finally {
	// if( useRedraw ) {
	// toolBar.setRedraw( true );
	// }
	// }
	// }
	// }
	// }

	public List<ToolItem> getToolItems() {
		List<ToolItem> result = new ArrayList<ToolItem>();
		for (int i = 0; i < toolBar.getItemCount(); i++) {
			result.add(toolBar.getItem(i));
		}
		for (int i = 0; i < fakeToolbar.getItemCount(); i++) {
			result.add(fakeToolbar.getItem(i));
		}
		return result;
	}

}
