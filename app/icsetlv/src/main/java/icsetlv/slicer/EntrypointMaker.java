/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.slicer;

import icsetlv.common.exception.IcsetlvException;
import icsetlv.common.utils.Assert;

import java.util.Iterator;
import java.util.List;

import sav.common.core.utils.CollectionUtils;

import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.DefaultEntrypoint;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.Descriptor;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.strings.Atom;

/**
 * @author LLT
 *
 */
public abstract class EntrypointMaker<T> {
	private ClassLoaderReference loaderRef;
	private IClassHierarchy cha;
	private List<T> classEntryPoints;
	
	public EntrypointMaker(final ClassLoaderReference loaderRef,
			final IClassHierarchy cha,
			final List<T> classEntryPoints) {
		this.loaderRef = loaderRef;
		this.cha = cha;
		this.classEntryPoints = classEntryPoints;
	}
	
	public Iterable<Entrypoint> makeEntrypoints()
			throws IcsetlvException {
		Assert.assertTrue(!CollectionUtils.isEmpty(classEntryPoints),
				"no entrypoint detected!");
		for (T classEntry : classEntryPoints) {
			String classRef = getClassRef(classEntry);
			if (classRef.indexOf("L") != 0) {
				throw new IllegalArgumentException(
						"Expected class name to start with L " + classEntry);
			}
			if (classRef.indexOf(".") > 0) {
				throw new IcsetlvException(
						"Expected class name formatted with /, not . "
								+ classEntry);
			}
		}

		return new Iterable<Entrypoint>() {
			public Iterator<Entrypoint> iterator() {
				return new Iterator<Entrypoint>() {
					private int index = 0;

					public void remove() {
						Assert.assertFail("unsupported!!");
					}

					public boolean hasNext() {
						return index < classEntryPoints.size();
					}

					public Entrypoint next() {
						TypeReference T = TypeReference
								.findOrCreate(loaderRef,
										TypeName.string2TypeName(getClassRef(classEntryPoints
												.get(index))));
						Atom method = Atom
								.findOrCreateAsciiAtom(getMethodName(classEntryPoints
										.get(index)));
						MethodReference mainRef = MethodReference
								.findOrCreate(T, method, getMethodDescriptor());
						index++;
						return new DefaultEntrypoint(mainRef, cha);
					}
				};
			}
		};
	}
	
	protected Descriptor getMethodDescriptor() {
		return Descriptor.findOrCreate(new TypeName[0], TypeReference.VoidName);
	}
	
	protected abstract String getMethodName(T entry);

	protected abstract String getClassRef(T entry);
}