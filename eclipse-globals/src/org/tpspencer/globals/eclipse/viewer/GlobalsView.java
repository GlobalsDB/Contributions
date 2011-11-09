/**
 * Copyright (C) 2011 Tom Spencer <thegaffer@tpspencer.com>
 *
 * eclipse-globals is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * eclipse-globals is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with eclipse-globals. If not, see <http://www.gnu.org/licenses/>.
 */
package org.tpspencer.globals.eclipse.viewer;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.tpspencer.globals.navigator.GlobalsNavigator;
import org.tpspencer.globals.navigator.GlobalsNavigatorImpl;
import org.tpspencer.globals.navigator.NodeInformation;

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */
public class GlobalsView extends ViewPart {
    /** The ID of the view as specified by the extension.*/
	public static final String ID = "org.tpspencer.globals.eclipse.viewer.GlobalsView";

	/** Our table */
	private TableViewer viewer;
	/** The previous node */
	private Action previousNodeAction;
	/** Selects that node */
	private Action doubleClickAction;
	
	/** The globals navigator we will use to get data */
	private GlobalsNavigator globalsNavigator = new GlobalsNavigatorImpl();
	
	/**
	 * This class provides the data for the view
	 */
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		
		public void dispose() {
		}
		
		public Object[] getElements(Object parent) {
		    List<NodeInformation> nodes = globalsNavigator.getPage();
		    return nodes.toArray();
		}
	}
	
	/**
	 * This class gets the text and/or image for each element
	 */
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
	    
	    public String getColumnText(Object obj, int index) {
		    String ret = null;
		    
		    if( obj instanceof NodeInformation ) {
    		    NodeInformation info = (NodeInformation)obj; 
    		    switch( index ) {
    		    case 0:
    		        ret = info.getFullPath();
    		        break;
    		        
    		    case 1:
    		        ret = info.getDataDisplay();
    		        break;
    		        
    		    }
		    }
		    else {
		        ret = "Unknown"; //getText(obj);
		    }
		    
		    return ret;
		}
		public Image getColumnImage(Object obj, int index) {
		    Image ret = null;
		    
		    if( obj instanceof NodeInformation ) {
                NodeInformation info = (NodeInformation)obj;
                
                switch( index ) {
                case 0:
                    if( info.isParentNode() ) ret = getImage(ISharedImages.IMG_OBJ_FOLDER);
                    else ret = getImage(ISharedImages.IMG_OBJ_FILE);
                }
		    }
		    
		    return ret;
		}
		
		/**
		 * Helper to get the image
		 * 
		 * @param image The image (from standard ones)
		 * @return
		 */
		public Image getImage(String image) {
			return PlatformUI.getWorkbench().
					getSharedImages().getImage(image);
		}
	}
	
	/**
	 * This class sorts the rows as necc
	 *
	 * @author Tom Spencer
	 */
	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public GlobalsView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());
		
		// Add in Columns
		Table table = viewer.getTable();
		TableColumn col = new TableColumn(table, SWT.RIGHT);
		col.setText("Name");
		col.setWidth(200);
		
		col = new TableColumn(table, SWT.LEFT);
        col.setText("Data");
        col.setWidth(800);
        
        table.setHeaderVisible(true);

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "eclipse-globals.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				GlobalsView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(previousNodeAction);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(previousNodeAction);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(previousNodeAction);
	}

	private void makeActions() {
	    // Go back
		previousNodeAction = new Action() {
			public void run() {
			    globalsNavigator.removeIndex();
			    viewer.refresh();
			}
		};
		previousNodeAction.setText("Back");
		previousNodeAction.setToolTipText("Show Parent");
		previousNodeAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_TOOL_UNDO));
		
		/*action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));*/
		
		// Selects the node
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				if( obj instanceof NodeInformation ) {
				    NodeInformation node = (NodeInformation)obj;
				    if( node.isParentNode() ) {
				        globalsNavigator.appendIndex(node.getName());
				        viewer.refresh();
				    }
				}
				else {
				    showMessage("Double-click detected on "+obj.toString());
				}
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Globals View",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}