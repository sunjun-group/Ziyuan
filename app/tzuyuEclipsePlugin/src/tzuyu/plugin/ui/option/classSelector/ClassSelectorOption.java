/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.ui.option.classSelector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;

import tzuyu.plugin.command.gentest.GenTestPreferences;
import tzuyu.plugin.core.dto.MethodMnemonic;
import tzuyu.plugin.core.dto.TypeMnemonic;
import tzuyu.plugin.core.utils.ResourcesUtils;
import tzuyu.plugin.reporter.PluginLogger;
import tzuyu.plugin.ui.ClasspathLabelProvider;

/**
 * @author LLT
 * @author Peter Kalauskas [Randoop, ClassSelectorOption]
 */
public class ClassSelectorOption extends Option<GenTestPreferences> {
	private TreeInput fTreeInput;
	private CheckboxTreeViewer fTypeTreeViewer;
	private HashSet<String> fDeletedTypeNodes;
	private Map<IType, List<String>> fCheckedMethodsByType;
	private TreeLabelProvider fTreeLabelProvider;
	private ITreeContentProvider fTypeTreeContentProvider;
	private Shell fShell;
	private Button fClassAddFromSources;
	private Button fClassAddFromSystemLibraries;
	private Button fClassAddFromClasspaths;
	private Button fResolveClasses;
	private Button fSelectAll;
	private Button fSelectNone;
	private Button fClassRemove;

	private IJavaProject project;
	private IRunnableContext fRunnableContext;

	public ClassSelectorOption(Composite parent,
			IRunnableContext runableContext, IJavaProject project) {
		// fTypeTreeViewer = new CheckboxTreeViewer(parent);
		// please be careful with the flow
		initData(project, runableContext);
		initPanel(parent, false);
	}

	private void initData(IJavaProject project, IRunnableContext runnableContext) {
		this.project = project;
		fRunnableContext = runnableContext;
		fDeletedTypeNodes = new HashSet<String>();
	}

	@SuppressWarnings("restriction")
	private void initPanel(Composite parent, boolean hasResolveButton) {
		final SelectionListener listener = getTypeSelectionListener();

		Group comp = SWTFactory.createGroup(parent,
				"Classes/Methods Un&der Test", 2, 1, GridData.FILL_BOTH);

		fShell = comp.getShell();

		// package tree panel
		final Composite leftcomp = SWTFactory.createComposite(comp, 1, 1,
				GridData.FILL_BOTH);
		GridLayout ld = (GridLayout) leftcomp.getLayout();
		ld.marginWidth = 1;
		ld.marginHeight = 1;
		GridData leftPanelLayout = (GridData) leftcomp.getLayoutData();
		leftPanelLayout.grabExcessHorizontalSpace = true;
		leftPanelLayout.grabExcessVerticalSpace = true;

		// build tree
		initTreeView(listener, leftcomp);

		/* buttons panel */
		final Composite rightcomp = SWTFactory.createComposite(comp, 1, 1,
				SWT.END);
		// layout
		GridData rightPanelLayout = (GridData) rightcomp.getLayoutData();
		rightPanelLayout.horizontalAlignment = SWT.LEFT;
		rightPanelLayout.verticalAlignment = SWT.TOP;
		// title
		SWTFactory.createLabel(rightcomp, "Add classes from:", 1);

		initProjectSourcesBtn(listener, rightcomp);
		initSystemLibrariesBtn(listener, rightcomp);
		initReferencedClasspathsBtn(listener, rightcomp);

		// Create a spacer
		SWTFactory.createLabel(rightcomp, "", 1);
		initSelectAllBtn(listener, rightcomp);
		initDeselectAllBtn(listener, rightcomp);
		initRemoveBtn(listener, rightcomp);
	}

	/**
	 * @param listener
	 * @param rightcomp
	 */
	private void initRemoveBtn(final SelectionListener listener,
			final Composite rightcomp) {
		fClassRemove = SWTFactory.createPushButton(rightcomp, "&Remove", null);
		fClassRemove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				removeSelection();
			}
		});
		fClassRemove.addSelectionListener(listener);
		fClassRemove.setEnabled(false);
	}

	/**
	 * @param listener
	 * @param rightcomp
	 */
	private void initDeselectAllBtn(final SelectionListener listener,
			final Composite rightcomp) {
		fSelectNone = SWTFactory.createPushButton(rightcomp, "Select Non&e",
				null);
		fSelectNone.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (TreeNode node : fTreeInput.getRoots()) {
					node.setGrayed(false);
					node.setChecked(false);
					node.updateRelatives();
				}
				fTypeTreeViewer.refresh();
			}
		});
		fSelectNone.addSelectionListener(listener);
	}

	/**
	 * @param listener
	 * @param rightcomp
	 */
	private void initSelectAllBtn(final SelectionListener listener,
			final Composite rightcomp) {
		fSelectAll = SWTFactory
				.createPushButton(rightcomp, "Select &All", null);
		fSelectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (TreeNode node : fTreeInput.getRoots()) {
					node.setGrayed(false);
					node.setChecked(true);
					node.updateRelatives();
				}
				fTypeTreeViewer.refresh();
			}
		});
		fSelectAll.addSelectionListener(listener);
	}

	/**
	 * @param listener
	 * @param rightcomp
	 */
	private void initReferencedClasspathsBtn(final SelectionListener listener,
			final Composite rightcomp) {
		fClassAddFromClasspaths = SWTFactory.createPushButton(rightcomp,
				"Referenced Classpat&hs...", null);
		fClassAddFromClasspaths.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					IClasspathEntry[] classpathEntries = chooseClasspathEntry();
					if (classpathEntries != null) {
						ArrayList<IJavaElement> elements = new ArrayList<IJavaElement>();
						for (IClasspathEntry classpathEntry : classpathEntries) {
							IPackageFragmentRoot[] pfrs = ResourcesUtils
									.findPackageFragmentRoots(project,
											classpathEntry);
							elements.addAll(Arrays.asList(pfrs));
						}

						IJavaElement[] elementArray = (IJavaElement[]) elements
								.toArray(new IJavaElement[elements.size()]);
						IJavaSearchScope searchScope = SearchEngine
								.createJavaSearchScope(elementArray);
						handleSearchButtonSelected(searchScope);
					}
				} catch (JavaModelException jme) {
					PluginLogger.logEx(jme);
				}
			}
		});
		fClassAddFromClasspaths.addSelectionListener(listener);
	}

	private IClasspathEntry[] chooseClasspathEntry() throws JavaModelException {
		ILabelProvider labelProvider = new ClasspathLabelProvider(project);
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				fShell, labelProvider);
		dialog.setTitle("Classpath Selection");
		dialog.setMessage("Select a classpath to constrain your search.");

		IClasspathEntry[] classpaths = project.getRawClasspath();
		dialog.setElements(classpaths);
		dialog.setMultipleSelection(true);

		if (dialog.open() == Window.OK) {
			List<IClasspathEntry> cpentries = new ArrayList<IClasspathEntry>();
			for (Object obj : dialog.getResult()) {
				if (obj instanceof IClasspathEntry) {
					cpentries.add((IClasspathEntry) obj);
				}
			}
			return (IClasspathEntry[]) cpentries
					.toArray(new IClasspathEntry[cpentries.size()]);
		}
		return null;
	}

	/**
	 * @param listener
	 * @param rightcomp
	 */
	private void initSystemLibrariesBtn(final SelectionListener listener,
			final Composite rightcomp) {
		fClassAddFromSystemLibraries = SWTFactory.createPushButton(rightcomp,
				"System Libraries...", null);
		fClassAddFromSystemLibraries
				.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						IJavaElement[] elements = { project };
						IJavaSearchScope searchScope = SearchEngine
								.createJavaSearchScope(elements,
										IJavaSearchScope.SYSTEM_LIBRARIES);
						handleSearchButtonSelected(searchScope);
					}
				});
		fClassAddFromSystemLibraries.addSelectionListener(listener);
	}

	/**
	 * @param listener
	 * @param rightcomp
	 */
	private void initProjectSourcesBtn(final SelectionListener listener,
			final Composite rightcomp) {
		fClassAddFromSources = SWTFactory.createPushButton(rightcomp,
				"Project So&urces...", null);
		fClassAddFromSources.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IJavaElement[] elements = { project };
				IJavaSearchScope searchScope = SearchEngine
						.createJavaSearchScope(elements,
								IJavaSearchScope.SOURCES);
				handleSearchButtonSelected(searchScope);
			}
		});
		fClassAddFromSources.addSelectionListener(listener);
	}

	private void initTreeView(final SelectionListener listener,
			final Composite leftcomp) {
		fTreeLabelProvider = new TreeLabelProvider();
		// treeInput
		fTreeInput = new TreeInput();
		// treeContentProvider
		fTypeTreeContentProvider = new TreeContentProvider(fTreeInput);

		fTypeTreeViewer = new CheckboxTreeViewer(leftcomp, SWT.MULTI
				| SWT.BORDER);
		GridData pkgTreeLayout = new GridData(SWT.FILL, SWT.FILL, true, true);
		fTypeTreeViewer.getControl().setLayoutData(pkgTreeLayout);
		fTypeTreeViewer.setLabelProvider(fTreeLabelProvider);
		fTypeTreeViewer.setContentProvider(fTypeTreeContentProvider);
		fTypeTreeViewer.setInput(fTreeInput);
		fTypeTreeViewer.setSorter(new ViewerSorter());

		fTypeTreeViewer.getTree().addKeyListener(getTypeTreeKeyListener());

		fTypeTreeViewer
				.addSelectionChangedListener(getTypeTreeSelectionChangedListener());

		fTypeTreeViewer.setCheckStateProvider(new ICheckStateProvider() {

			public boolean isChecked(Object element) {
				TreeNode node = ((TreeNode) element);
				return node.isChecked();
			}

			public boolean isGrayed(Object element) {
				TreeNode node = ((TreeNode) element);
				return node.isGrayed();
			}

		});

		fTypeTreeViewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				TreeNode node = (TreeNode) event.getElement();
				node.setGrayed(false);
				node.setChecked(event.getChecked());
				node.updateRelatives();

				fTypeTreeViewer.refresh();
				listener.widgetSelected(null);
			}
		});
	}

	private ISelectionChangedListener getTypeTreeSelectionChangedListener() {
		return new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				ITreeSelection selection = (ITreeSelection) event
						.getSelection();

				Iterator<?> it = selection.iterator();
				while (it.hasNext()) {
					TreeNode node = (TreeNode) it.next();
					Object obj = node.getObject();
					if (obj instanceof TypeMnemonic || obj instanceof String) {
						fClassRemove.setEnabled(true);
						return;
					}
				}
				fClassRemove.setEnabled(false);
			}
		};
	}

	/**
	 * @return
	 */
	private KeyAdapter getTypeTreeKeyListener() {
		return new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.keyCode == SWT.DEL) {
					removeSelection();
				}
			}
		};
	}

	/**
	 * @return
	 */
	private SelectionListener getTypeSelectionListener() {
		return new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				notifyListeners();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
	}

	private void handleSearchButtonSelected(IJavaSearchScope searchScope) {
		try {
			// boolean ignoreJUnit = fIgnoreJUnitTestCases.getSelection();
			SelectionDialog dialog = JavaUI.createTypeDialog(fShell,
					fRunnableContext, searchScope,
					IJavaElementSearchConstants.CONSIDER_CLASSES_AND_ENUMS,
					true, "", //$NON-NLS-1$
					new ProjectTypeSelectionExtension());
			dialog.setTitle("Add Classes");
			dialog.setMessage("Enter type name prefix or pattern (*, ?, or camel case):");
			dialog.open();

			// Add all of the types to the type selector
			Object[] results = dialog.getResult();
			if (results != null && results.length > 0) {
				for (Object element : results) {
					if (element instanceof IType) {
						IType type = (IType) element;

						if (type != null) {
							String pfname = type.getPackageFragment()
									.getElementName();
							TreeNode packageNode = fTreeInput.addRoot(pfname);

							TypeMnemonic newTypeMnemonic = new TypeMnemonic(
									type);

							boolean typeAlreadyInTree = false;
							for (TreeNode node : packageNode.getChildren()) {
								TypeMnemonic otherMnemonic = (TypeMnemonic) node
										.getObject();
								if (otherMnemonic.getFullyQualifiedName()
										.equals(newTypeMnemonic
												.getFullyQualifiedName())) {
									if (!otherMnemonic.exists()
											|| !otherMnemonic.getJavaProject()
													.equals(project)) {
										// If it doesn't exist, simply replace
										// it with the new class
										setNewTypeMnemonic(node,
												newTypeMnemonic);
									}
									typeAlreadyInTree = true;
									break;
								}
							}

							if (!typeAlreadyInTree) {
								// Remove this from the list of deletes classes
								fDeletedTypeNodes.remove(newTypeMnemonic
										.toString());

								packageNode.addChild(newTypeMnemonic, true,
										false);

								fTypeTreeViewer.refresh();
								if (results.length < 3) {
									fTypeTreeViewer.setExpandedState(
											packageNode, true);
								}
							}
						}
					}
				}

				fTypeTreeViewer.refresh();
			}
		} catch (JavaModelException e) {
			PluginLogger.logEx(e);
		}
	}

	private void setNewTypeMnemonic(TreeNode node, TypeMnemonic newTypeMnemonic) {
		// TODO: Check if node or its object is null

		TypeMnemonic oldTypeMnemonic = (TypeMnemonic) node.getObject();
		fDeletedTypeNodes.add(oldTypeMnemonic.toString());

		List<String> checkedMethods;
		if (node.hasChildren()) {
			checkedMethods = new ArrayList<String>();
			for (TreeNode methodItem : node.getChildren()) {
				checkedMethods.add(((MethodMnemonic) methodItem.getObject())
						.toString());
			}
		} else {
			// Otherwise, the item probably hasn't been expanded. Move the
			// list of checked methods from the old key to the new one
			checkedMethods = fCheckedMethodsByType.get(oldTypeMnemonic
					.getType());
		}
		fCheckedMethodsByType.put(newTypeMnemonic.getType(), checkedMethods);
		fCheckedMethodsByType.remove(oldTypeMnemonic.getType());

		node.setObject(newTypeMnemonic);
		node.removeAllChildren();
	}

	/**
	 * 
	 */
	protected void notifyListeners() {
		// TODO Auto-generated method stub

	}

	private void removeSelection() {
		ITreeSelection selection = (ITreeSelection) fTypeTreeViewer
				.getSelection();

		Iterator<?> it = selection.iterator();
		while (it.hasNext()) {
			fTreeInput.remove((TreeNode) it.next(), fDeletedTypeNodes);
		}

		fTypeTreeViewer.refresh();
		fClassRemove.setEnabled(false);
		if (fResolveClasses != null) {
			fResolveClasses.setEnabled(project != null
					&& fTreeInput.hasMissingClasses(project));
		}
	}

	@Override
	public void initFrom(GenTestPreferences config) {
		fTreeInput.reset();
		fCheckedMethodsByType = new HashMap<IType, List<String>>();

		List<String> availableTypes = config.getAvailableTypes();
		List<String> grayedTypes = config.getGrayedTypes();
		List<String> checkedTypes = config.getCheckedTypes();

		for (String typeString : availableTypes) {
			TypeMnemonic typeMnemonic = new TypeMnemonic(typeString,
					getWorkspaceRoot());
			boolean typeIsChecked = checkedTypes.contains(typeString);
			boolean typeIsGrayed = grayedTypes.contains(typeString);

			String pfname = ResourcesUtils.getPackageName(typeMnemonic
					.getFullyQualifiedName());
			TreeNode pfNode = fTreeInput.addRoot(pfname);
			fTypeTreeViewer.refresh();

			List<String> checkedMethods = config.getCheckedMethods(typeString);
			if (typeMnemonic.getType() != null) {
				pfNode.addChild(typeMnemonic, typeIsChecked, typeIsGrayed);

				fCheckedMethodsByType.put(typeMnemonic.getType(),
						checkedMethods);
			} else {
				TreeNode typeNode = pfNode.addChild(typeMnemonic,
						typeIsChecked, typeIsGrayed);

				List<String> availableMethods = config
						.getAvailableMethods(typeString);

				for (String methodString : availableMethods) {
					MethodMnemonic methodMnemonic = new MethodMnemonic(
							methodString);

					if (typeIsChecked && !typeIsGrayed) {
						typeNode.addChild(methodMnemonic, true, false);
					} else {
						boolean methodIsChecked = checkedMethods
								.contains(methodString);
						boolean methodIsGrayed = false;

						typeNode.addChild(methodMnemonic, methodIsChecked,
								methodIsGrayed);
					}
				}
			}
		}

		fTypeTreeViewer.refresh();

		String projectName = config.getProjectName();
		setJavaProject(JavaCore.create(ResourcesUtils
				.getProjectFromName(projectName)));
	}

	private boolean setJavaProject(IJavaProject javaProject) {
		if (fTreeInput == null) {
			return false;
		}
		project = javaProject;
		fTreeLabelProvider.setProject(project);
		boolean hasMissingClasses = false;

		if (project == null) {
			fClassAddFromSources.setEnabled(false);
			fClassAddFromSystemLibraries.setEnabled(false);
			fClassAddFromClasspaths.setEnabled(false);

			hasMissingClasses = true;
		} else {
			for (TreeNode pfNode : fTreeInput.getRoots()) {
				for (TreeNode typeNode : pfNode.getChildren()) {
					TypeMnemonic oldMnemonic = (TypeMnemonic) typeNode
							.getObject();

					if (!project.equals(oldMnemonic.getJavaProject())) {
						try {
							TypeMnemonic newMnemonic = oldMnemonic
									.reassign(project);

							// If newMnemonic is not null, the IType was found
							// in a classpath
							// entry of the new Java project
							if (newMnemonic == null || !newMnemonic.exists()) {
								hasMissingClasses = true;
							} else {
								// Update the mnemonic for this TreeItem
								typeNode.setObject(newMnemonic);
							}
						} catch (JavaModelException e) {
							PluginLogger.logEx(e);
						}
					}
				}
			}
		}

		fTypeTreeViewer.refresh();
		return hasMissingClasses;
	}

	private IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
}
